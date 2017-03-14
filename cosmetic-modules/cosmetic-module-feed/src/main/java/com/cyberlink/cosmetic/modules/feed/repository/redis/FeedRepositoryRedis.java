package com.cyberlink.cosmetic.modules.feed.repository.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.cosmetic.modules.feed.model.FeedPost;
import com.cyberlink.cosmetic.modules.feed.repository.FeedRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;
import com.cyberlink.cosmetic.redis.KeyUtils;
import com.cyberlink.cosmetic.statsd.StatsD;

public class FeedRepositoryRedis extends AbstractRedisRepository implements
        FeedRepository {
    private static final int MAX_LENGTH = 1000;

    @Override
    public void add(String hashKey, List<FeedPost> posts) {
        if (posts.isEmpty()) {
            return;
        }
        
        updateScore(posts);
        final String key = getKey(hashKey);
        opsForZSet().add(key, toTypedTuples(posts));
        trimFeed(key, -1 * MAX_LENGTH);
    }

    private void updateScore(List<FeedPost> l) {
        for (final FeedPost fp : l) {
            fp.calculate();
        }
    }
    
    private void trimFeed(String key, int i) {
        opsForZSet().removeRange(key, 0, i);
    }

    private Set<TypedTuple<String>> toTypedTuples(List<FeedPost> posts) {
        final Set<TypedTuple<String>> r = new HashSet<ZSetOperations.TypedTuple<String>>();
        for (final FeedPost fp : posts) {
            r.add(toTypedTuple(fp));
        }
        return r;
    }

    private TypedTuple<String> toTypedTuple(FeedPost fp) {
        return new DefaultTypedTuple<String>(fp.getPostId().toString(),
                fp.getScore());
    }

    private List<FeedPost> toFeedPosts(Set<TypedTuple<String>> tts) {
        final List<FeedPost> r = new ArrayList<FeedPost>();
        for (final TypedTuple<String> tt : tts) {
            r.add(new FeedPost(tt));
        }
        return r;
    }

    @Override
    public boolean exists(String hashKey, Long postId) {
        if (StringUtils.isBlank(hashKey)) {
            return false;
        }
        if (postId == null) {
            return false;
        }
        return opsForZSet().score(getKey(hashKey), postId.toString()) != null;
    }

    @Override
    public boolean existsHashKey(String hashKey) {
        return super.exists(getKey(hashKey));
    }

    private String getKey(Object hashKey) {
        return KeyUtils.userFeed(hashKey);
    }

    @Override
    @StatsD
    public List<FeedPost> range(String hashKey, int offset, int limit) {
        final Set<TypedTuple<String>> r = opsForZSet().reverseRangeWithScores(
                getKey(hashKey), offset, offset + limit - 1);
        return removeIgnored(toFeedPosts(r));
    }

    @Override
    @StatsD
    public List<FeedPost> rangeByScore(String hashKey, Double score, int offset, int limit) {
        final Set<TypedTuple<String>> r = opsForZSet().reverseRangeByScoreWithScores(
                getKey(hashKey), 0, score, offset, limit);
        return removeIgnored(toFeedPosts(r));
    }
    
    private List<FeedPost> removeIgnored(List<FeedPost> posts) {
        final List<FeedPost> r = new ArrayList<FeedPost>();
        for (final FeedPost p : posts) {
            if (p.isIgnore()) {
                continue;
            }
            r.add(p);
        }
        return r;
    }

    @Override
    public void doWithAllFeed(Long userId, CursorCallback<TypedTuple<String>> callback) {
        zScan(getKey(userId.toString()).getBytes(), ScanOptions.NONE, callback);
    }
}
