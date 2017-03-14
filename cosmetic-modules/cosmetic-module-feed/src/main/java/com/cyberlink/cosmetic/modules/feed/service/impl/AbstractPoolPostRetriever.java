package com.cyberlink.cosmetic.modules.feed.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.FeedRepository;
import com.cyberlink.cosmetic.modules.feed.repository.PoolRepository;
import com.cyberlink.cosmetic.modules.feed.service.PoolPostRetriever;

public abstract class AbstractPoolPostRetriever extends AbstractService
        implements PoolPostRetriever {
    private int maxIteration = 10;
    private int numToRetrieve = 2;
    protected PoolRepository poolRepository;
    private FeedRepository feedRepository;

    public void setNumToRetrieve(int numToRetrieve) {
        this.numToRetrieve = numToRetrieve;
    }

    public void setMaxIteration(int maxIterations) {
        this.maxIteration = maxIterations;
    }

    public void setFeedRepository(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public void setPoolRepository(PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    @Override
    public final List<PoolPost> retrieve(PoolType poolType, String locale,
            String sourceHashKey, String targetHashKey) {
        final List<PoolPost> r = new ArrayList<PoolPost>();
        int numRetrieved = 0;
        for (int i = 0; i < maxIteration; i++) {
            if (numToRetrieve == 0) {
                break;
            }
            if (numRetrieved >= numToRetrieve) {
                break;
            }

            final List<PoolPost> t = retrieveFromPool(poolType, locale,
                    sourceHashKey, numRetrieved, i, numToRetrieve);
            if (t.isEmpty()) {
                break;
            }

            for (final PoolPost pp : t) {
                if (exists(targetHashKey, pp.getPostId())) {
                    continue;
                }
                /* To sync current feed implementation, do not filter repeated circleIn post. 
                 * Enable this if we want to filter repeated circleIn post. */ 
                /*if (exists(targetHashKey, pp.getRootId())) {
                    continue;
                }*/
                r.add(pp);
                numRetrieved++;
                if (numRetrieved >= numToRetrieve) {
                    break;
                }
            }
        }

        return r;
    }

    protected abstract List<PoolPost> retrieveFromPool(PoolType poolType,
            String locale, String hashKey, Integer numRetrieved,
            Integer iteration, Integer numToRetrieve);

    private boolean exists(String targetHashKey, Long postId) {
        if (StringUtils.isBlank(targetHashKey)) {
            return false;
        }
        if (postId == null) {
            return false;
        }
        return feedRepository.exists(targetHashKey, postId);
    }
}
