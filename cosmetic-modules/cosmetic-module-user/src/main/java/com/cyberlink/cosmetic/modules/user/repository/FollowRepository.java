package com.cyberlink.cosmetic.modules.user.repository;

import java.util.Date;
import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.cosmetic.redis.CursorCallback;

public interface FollowRepository {

    void addUserFollowing(Long followerId, Long followeeId, Date created);

    void removeUserFollowing(Long followerId, Long followeeId);

    Set<String> getUserFollowing(Long followerId);
    
    Boolean getIsFollowing(Long followerId, Long followeeId);
    
    void addExplicitFollower(Long followerId, Long followeeId, Date created);

    void removeExplicitFollower(Long followerId, Long followeeId);

    void updateAllBetween(Date begin, Date end);
	
    void doWithExplicitFollower(Long followeeId,
            CursorCallback<TypedTuple<String>> callback);

    void deleteKey(String key);

    void doWithAllFollowers(Long followeeId, Long circleId,
            CursorCallback<TypedTuple<String>> callback);

    void deleteByUserId(Long userId);

}
