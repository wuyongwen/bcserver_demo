package com.cyberlink.cosmetic.modules.feed.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.feed.model.FeedPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.FeedRepository;
import com.cyberlink.cosmetic.modules.feed.service.PoolPostRetriever;
import com.cyberlink.cosmetic.modules.feed.service.SmartFeedContentGenerator;
import com.cyberlink.cosmetic.redis.KeyUtils;
import com.cyberlink.cosmetic.statsd.StatsD;

public class SmartFeedContentGeneratorImpl extends AbstractService implements
        SmartFeedContentGenerator {
    private static final Long DEFAULT_EXPIRATION = 90L;

    private Map<PoolType, PoolPostRetriever> retrievers = new LinkedHashMap<PoolType, PoolPostRetriever>();
    private FeedRepository feedRepository;
    private Integer maxIteration = 50;

    public void setMaxIteration(Integer maxIteration) {
        this.maxIteration = maxIteration;
    }

    public void setFeedRepository(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public void setRetrievers(Map<PoolType, PoolPostRetriever> retrievers) {
        this.retrievers = retrievers;
    }

    @Override
    @StatsD
    public void generate(String locale, String sourceHashKey,
            String targetHashKey, int numToRetrieve) {
        resetExpirationOfFeed(targetHashKey);
        int numRetrieved = 0;
        for (int i = 0; i < maxIteration; i++) {
            int t = generate(locale, sourceHashKey, targetHashKey);
            if (t == 0) {
                return;
            }
            numRetrieved += t;
            if (numRetrieved >= numToRetrieve) {
                return;
            }
        }
    }

    private void resetExpirationOfFeed(String hashKey) {
        String key = KeyUtils.userFeed(hashKey);
        feedRepository.expire(key, DEFAULT_EXPIRATION, TimeUnit.DAYS);
    }

    public Integer generate(String locale, String sourceHashKey,
            String targetHashKey) {
        final Map<PoolType, List<PoolPost>> t = retrieve(locale, sourceHashKey,
                targetHashKey);
        return generate(targetHashKey, t);
    }

    public void directToFeed(String targetHashKey, PoolType poolType, PoolPost post) {
        final Map<PoolType, List<PoolPost>> t = new HashMap<PoolType, List<PoolPost>>();
        List<PoolPost> p = new ArrayList<PoolPost>();
        p.add(post);
        t.put(poolType, p);
        generate(targetHashKey, t);
    }
    
    private Integer generate(String targetHashKey, final Map<PoolType, List<PoolPost>> t) {
        if (t.isEmpty()) {
            return 0;
        }
        final List<FeedPost> mixed = mix(t);
        if (mixed.isEmpty()) {
            return 0;
        }
        return fill(targetHashKey, mixed);
    }

    private Integer fill(String hashKey, List<FeedPost> posts) {
        feedRepository.add(hashKey, posts);
        return posts.size();
    }

    private List<FeedPost> mix(Map<PoolType, List<PoolPost>> m) {
        final List<FeedPost> r = new LinkedList<FeedPost>();
        final int maxlength = getMaxLength(m.values());
        for (int i = 0; i < maxlength; i++) {
            for (final PoolType pt : PoolType.values()) {
                if (!m.containsKey(pt)) {
                    continue;
                }
                final List<PoolPost> l = m.get(pt);
                if (l.isEmpty()) {
                    continue;
                }
                if (i >= l.size()) {
                    continue;
                }
                final PoolPost pp = l.get(i);
                r.add(new FeedPost(pt, pp.getPostId(), Boolean.FALSE));
                if (needToAddRootId(pp.getPostId(), pp.getRootId())) {
                    r.add(new FeedPost(pt, pp.getRootId(), Boolean.TRUE));
                }
            }
        }
        Collections.reverse(r);
        return r;
    }

    private boolean needToAddRootId(Long postId, Long rootId) {
        
        /* To sync with current feed implementation,
         * we should not add root post to feed. Otherwise,
         * the feed order will be changed. Enable this if we
         * would like to add root post to feed.
         */
        /*if (rootId == null) {
            return false;
        }
        return !rootId.equals(postId);*/
        return false;
    }

    private int getMaxLength(Collection<List<PoolPost>> values) {
        int max = 0;
        for (final List<PoolPost> pp : values) {
            if (max < pp.size()) {
                max = pp.size();
            }
        }

        return max;
    }

    private Map<PoolType, List<PoolPost>> retrieve(String locale,
            String sourceHashKey, String targetHashKey) {
        final Map<PoolType, List<PoolPost>> r = new LinkedHashMap<PoolType, List<PoolPost>>();
        for (final Map.Entry<PoolType, PoolPostRetriever> e : retrievers
                .entrySet()) {
            final List<PoolPost> posts = e.getValue().retrieve(e.getKey(),
                    locale, sourceHashKey, targetHashKey);
            if (!posts.isEmpty()) {
                r.put(e.getKey(), posts);
            }
        }
        return r;
    }

}
