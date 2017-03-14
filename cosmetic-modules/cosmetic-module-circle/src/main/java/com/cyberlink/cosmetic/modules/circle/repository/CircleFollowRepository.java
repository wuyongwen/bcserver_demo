package com.cyberlink.cosmetic.modules.circle.repository;

import java.util.Date;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.cosmetic.redis.CursorCallback;

public interface CircleFollowRepository {

    void addCircleFollowing(Long followerId, Long circleId, Date created);

    void removeCircleFollowing(Long followerId, Long circleId);

    void doWithCircleFollowing(Long followerId, CursorCallback<TypedTuple<String>> callback);

    void addExplicitFolower(Long followerId, Long circleId, Date created);

    void removeExplicitFollower(Long followerId, Long circleId);

    void doWithExplicitFollower(Long circleId, CursorCallback<TypedTuple<String>> callback);

    void deleteByCircleId(Long circleId);

    void deleteByUserId(Long userId);

    void updateCircleSubsBetween(Date begin, Date end);

}
