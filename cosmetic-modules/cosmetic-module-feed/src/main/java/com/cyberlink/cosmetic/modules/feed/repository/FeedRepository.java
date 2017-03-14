package com.cyberlink.cosmetic.modules.feed.repository;

import java.util.List;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.cosmetic.modules.feed.model.FeedPost;
import com.cyberlink.cosmetic.redis.CursorCallback;
import com.cyberlink.cosmetic.redis.Repository;

public interface FeedRepository extends Repository {
    void add(String hashKey, List<FeedPost> posts);

    boolean exists(String hashKey, Long postId);

    boolean existsHashKey(String hashKey);

    List<FeedPost> range(String hashKey, int offset, int limit);
    
    List<FeedPost> rangeByScore(String hashKey, Double score, int offset, int limit);

    void doWithAllFeed(Long userId, CursorCallback<TypedTuple<String>> callback);

}
