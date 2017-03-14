package com.cyberlink.cosmetic.modules.feed.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.redis.CursorCallback;
import com.cyberlink.cosmetic.redis.Repository;

public interface PoolRepository extends Repository {
    void add(PoolType poolType, String hashKey, Map<String, Double> values);

    void add(PoolType poolType, String userId, String postId, Long score);

    void deleteByCreatorId(PoolType poolType, Long followerId, Long followeeId);

    void deleteByCircleId(PoolType poolType, Long followerId, Long circleId);

    void deleteByValue(PoolType poolType, Long followerId, String value);

    List<PoolPost> pop(PoolType poolType, String hashKey, Integer numToRetrieve);

    List<PoolPost> range(PoolType poolType, String hashkey, int pageIndex,
            int pageSize);

    Map<PoolPost, Double> rangeWithScores(PoolType poolType, String hashkey,
            int pageIndex, int pageSize);

    void doWithAllPostAscendingly(PoolType poolType, String hashKey,
            CursorCallback<TypedTuple<String>> callback);

    void clean(PoolType poolType, String hashKey);
}
