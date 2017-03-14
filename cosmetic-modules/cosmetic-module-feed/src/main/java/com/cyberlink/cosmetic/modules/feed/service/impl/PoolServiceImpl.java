package com.cyberlink.cosmetic.modules.feed.service.impl;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.circle.repository.CircleFollowRepository;
import com.cyberlink.cosmetic.modules.circle.repository.CircleRepository;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.PoolRepository;
import com.cyberlink.cosmetic.modules.feed.service.PoolService;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;

public class PoolServiceImpl extends AbstractService implements PoolService {
    private CircleFollowRepository circleFollowRepository;
    private PoolRepository poolRepository;
    private FollowRepository followRepository;
    private CircleRepository circleRepository;

    public void setFollowRepository(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void setCircleFollowRepository(
            CircleFollowRepository circleFollowRepository) {
        this.circleFollowRepository = circleFollowRepository;
    }

    public void setPoolRepository(PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    public void setCircleRepository(CircleRepository circleRepository) {
        this.circleRepository = circleRepository;
    }
    
    @Override
    public void deleteByCreatorId(final Long creatorId) {
        final CursorCallback<TypedTuple<String>> userCallback = new CursorCallback<TypedTuple<String>>() {
            @Override
            public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                while (cursor.hasNext()) {
                    final Long followerId = Long.valueOf(cursor.next().getValue());
                    poolRepository.deleteByCreatorId(PoolType.Following,
                            followerId, creatorId);
                }
            }
        };
        followRepository.doWithExplicitFollower(creatorId, userCallback);
        
        final CursorCallback<TypedTuple<String>> circleCallback = new CursorCallback<TypedTuple<String>>() {
            @Override
            public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                while (cursor.hasNext()) {
                    final Long followerId = Long.valueOf(cursor.next().getValue());
                    poolRepository.deleteByCreatorId(PoolType.Following,
                            followerId, creatorId);
                }
            }
        };
        circleRepository.doWithCircleIds(creatorId, new CursorCallback<String>() {
            @Override
            public void doWithCursor(Cursor<String> cursor) {
                while (cursor.hasNext()) {
                    circleFollowRepository.doWithExplicitFollower(Long.valueOf(cursor.next()), circleCallback);
                }
            }
        });
    }

    @Override
    public void deleteByCircleId(final Long creatorId, final Long circleId) {
        poolRepository.deleteByCircleId(PoolType.PublicCreation, creatorId,
                circleId);

        final CursorCallback<TypedTuple<String>> callback = new CursorCallback<TypedTuple<String>>() {
            @Override
            public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                while (cursor.hasNext()) {
                    final String followerId = cursor.next().getValue();
                    poolRepository.deleteByCircleId(PoolType.Following,
                            Long.valueOf(followerId), circleId);
                }
            }
        };
        deleteFromExplicitFollower(circleId, callback);
        deleteFromImplicitFollower(circleId, callback);
    }

    private void deleteFromImplicitFollower(final Long circleId,
            CursorCallback<TypedTuple<String>> callback) {
        circleFollowRepository.doWithExplicitFollower(circleId, callback);

    }

    private void deleteFromExplicitFollower(final Long circleId,
            final CursorCallback<TypedTuple<String>> callback) {
        circleFollowRepository.doWithExplicitFollower(circleId, callback);
    }
}
