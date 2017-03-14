package com.cyberlink.cosmetic.modules.user.repository.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.ScrollableResults;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class FollowRepositoryRedis extends AbstractRedisRepository implements
        FollowRepository {
    private static final Integer BATCH_SIZE = 1000;
    private SubscribeDao subscribeDao;

    public void setSubscribeDao(SubscribeDao subscribeDao) {
        this.subscribeDao = subscribeDao;
    }

    @Override
    public void addExplicitFollower(Long followerId, Long followeeId,
            Date created) {
        if (followerId == null) {
            return;
        }
        if (followeeId == null) {
            return;
        }
        opsForZSet().add(KeyUtils.userExplicitFollower(followeeId),
                followerId.toString(), created.getTime());
    }

    @Override
    public void addUserFollowing(Long followerId, Long followeeId, Date created) {
        if (followerId == null) {
            return;
        }
        if (followeeId == null) {
            return;
        }
        opsForZSet().add(KeyUtils.userFollowing(followerId),
                followeeId.toString(), created.getTime());
    }

    @Override
    public void removeUserFollowing(Long followerId, Long followeeId) {
        if (followerId == null) {
            return;
        }
        if (followeeId == null) {
            return;
        }
        opsForZSet().remove(KeyUtils.userFollowing(followerId),
                followeeId.toString());
    }

    @Override
    public Set<String> getUserFollowing(Long followerId) {
        return opsForZSet().range(KeyUtils.userFollowing(followerId), 0, -1);
    }

    @Override
    public Boolean getIsFollowing(Long followerId, Long followeeId) {
        return opsForZSet().score(KeyUtils.userFollowing(followerId), followeeId.toString()) != null;
    }
    
    @Override
    public void removeExplicitFollower(Long followerId, Long followeeId) {
        if (followerId == null) {
            return;
        }
        if (followeeId == null) {
            return;
        }
        opsForZSet().remove(KeyUtils.userExplicitFollower(followeeId),
                followerId.toString());
    }

    @Override
    public void deleteByUserId(final Long userId) {
        
        doWithExplicitFollower(userId,
                new CursorCallback<TypedTuple<String>>() {
                    @Override
                    public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                        while (cursor.hasNext()) {
                            removeUserFollowing(
                                    Long.valueOf(cursor.next().getValue()),
                                    userId);
                        }
                    }
                });
        doWithFollowing(userId, new CursorCallback<TypedTuple<String>>() {
            @Override
            public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                while (cursor.hasNext()) {
                    removeExplicitFollower(userId, Long.valueOf(cursor.next().getValue()));
                }
            }
        });
        delete(KeyUtils.userFollowing(userId));
        delete(KeyUtils.userExplicitFollower(userId));
    }

    private void doWithFollowing(Long userId, CursorCallback<TypedTuple<String>> callback) {
        zScan(KeyUtils.userFollowing(userId).getBytes(), ScanOptions.NONE, callback);
        /*Cursor<TypedTuple<String>> c = opsForZSet().scan(KeyUtils.userFollowing(userId),
                ScanOptions.NONE);
        execute(c, callback);*/
    }

    @Override
    public void doWithExplicitFollower(Long followeeId,
            CursorCallback<TypedTuple<String>> callback) {
        zScan(KeyUtils.userExplicitFollower(followeeId).getBytes(), ScanOptions.NONE, callback);
        /*Cursor<TypedTuple<String>> c = opsForZSet().scan(
                KeyUtils.userExplicitFollower(followeeId),
                ScanOptions.NONE);
        execute(c, callback);*/
    }

    @Override
    public void doWithAllFollowers(Long followeeId, Long circleId,
            CursorCallback<TypedTuple<String>> callback) {
        try {
            List<String> keys = generateKeys(followeeId, circleId);
            for(String key : keys) {
                zScan(key.getBytes(), ScanOptions.NONE, callback);
                //execute(opsForZSet().scan(key, ScanOptions.NONE), callback);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private List<String> generateKeys(Long followeeId, Long circleId) {
        List<String> keys = new ArrayList<String>();
        keys.add(KeyUtils.userExplicitFollower(followeeId));
        keys.add(KeyUtils.circleExplicitFollower(circleId));
        return keys;
    }

    @Override
    public void deleteKey(String key) {
        delete(key);
    }

	@Override
    public void updateAllBetween(Date begin, Date end) {
        logger.error("begin updateAllUserFollowships");
        updateAllUserFollowshipsBetween(begin, end);
        logger.error("end updateAllUserFollowships");
    }
	
    private void updateAllUserFollowshipsBetween(Date begin, Date end) {
        subscribeDao.doWithAllValidSubscribeBetween(begin, end, new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                int i = 0;
                long b = System.currentTimeMillis();
                while (sr.next()) {
                    if ((++i) % BATCH_SIZE == 0) {
                        logger.error("begin - end (" + i + "): "
                                + (System.currentTimeMillis() - b));
                        b = System.currentTimeMillis();
                        subscribeDao.clear();
                    }
                    final Object[] o = sr.get();
                    final Long subscriberId = (Long) o[0];
                    final Long subscribeeId = (Long) o[1];
                    final SubscribeType subscribeType = (SubscribeType) o[2];
                    final Date created = (Date) o[3];
                    if (subscribeType == null || subscribeType.isUser()) {
                        addExplicitFollower(subscriberId, subscribeeId, created);
                        addUserFollowing(subscriberId, subscribeeId, created);
                    }
                }
            }
        });
    }

}
