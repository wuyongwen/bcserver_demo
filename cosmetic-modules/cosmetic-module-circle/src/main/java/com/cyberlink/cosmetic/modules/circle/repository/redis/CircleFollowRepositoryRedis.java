package com.cyberlink.cosmetic.modules.circle.repository.redis;

import java.util.Date;

import org.hibernate.ScrollableResults;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.repository.CircleFollowRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class CircleFollowRepositoryRedis extends AbstractRedisRepository
        implements CircleFollowRepository {
    private static final Integer BATCH_SIZE = 1000;

    private CircleSubscribeDao circleSubscribeDao;

    public void setCircleSubscribeDao(CircleSubscribeDao circleSubscribeDao) {
        this.circleSubscribeDao = circleSubscribeDao;
    }

    @Override
    public void addCircleFollowing(Long followerId, Long circleId, Date created) {
        if (followerId == null) {
            return;
        }
        if (circleId == null) {
            return;
        }
        opsForZSet().add(KeyUtils.circleFollowing(followerId),
                circleId.toString(), created.getTime());
    }

    @Override
    public void removeCircleFollowing(Long followerId, Long circleId) {
        if (followerId == null) {
            return;
        }
        if (circleId == null) {
            return;
        }
        opsForZSet().remove(KeyUtils.circleFollowing(followerId),
                circleId.toString());
    }

    @Override
    public void doWithCircleFollowing(Long followerId,
            CursorCallback<TypedTuple<String>> callback) {
        zScan(KeyUtils.circleFollowing(followerId).getBytes(), ScanOptions.NONE, callback);
        /*execute(opsForZSet().scan(KeyUtils.circleFollowing(followerId),
                ScanOptions.NONE), callback);*/
    }

    @Override
    public void addExplicitFolower(Long followerId, Long circleId, Date created) {
        if (followerId == null) {
            return;
        }
        if (circleId == null) {
            return;
        }
        opsForZSet().add(KeyUtils.circleExplicitFollower(circleId),
                followerId.toString(), created.getTime());
    }

    @Override
    public void removeExplicitFollower(Long followerId, Long circleId) {
        if (followerId == null) {
            return;
        }
        if (circleId == null) {
            return;
        }
        opsForZSet().remove(KeyUtils.circleExplicitFollower(circleId),
                followerId.toString());
    }

    @Override
    public void deleteByCircleId(final Long circleId) {
        if (circleId == null) {
            return;
        }
        doWithExplicitFollower(circleId, new CursorCallback<TypedTuple<String>>() {
            @Override
            public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                while (cursor.hasNext()) {
                    opsForZSet().remove(KeyUtils.circleFollowing(cursor.next().getValue()),
                            circleId.toString());
                }
            }
        });
        delete(KeyUtils.circleExplicitFollower(circleId));
    }

    @Override
    public void deleteByUserId(Long userId) {
        if(userId == null)
            return;
        
        final String followerId = userId.toString();
        doWithCircleFollowing(userId, new CursorCallback<TypedTuple<String>>() {
            @Override
            public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                while (cursor.hasNext()) {
                    opsForZSet().remove(KeyUtils.circleExplicitFollower(cursor.next().getValue()),
                            followerId);
                }
            }
        });
        delete(KeyUtils.circleFollowing(userId));
    }

    @Override
    public void doWithExplicitFollower(Long circleId,
            CursorCallback<TypedTuple<String>> callback) {
        zScan(KeyUtils.circleExplicitFollower(circleId).getBytes(),
                ScanOptions.NONE, callback);
        /*execute(opsForZSet().scan(KeyUtils.circleExplicitFollower(circleId),
                ScanOptions.NONE), callback);*/
    }
    
    @Override
    public void updateCircleSubsBetween(Date begin, Date end) {
        logger.error("begin updateCircleSubsBetween");
        updateAllCircleSubscribeBetween(begin, end);
        logger.error("end updateCircleSubsBetween");
    }
    
    private void updateAllCircleSubscribeBetween(Date begin, Date end) {
        circleSubscribeDao
                .doWithAllCircleSubscribeBetween(begin, end, new ScrollableResultsCallback() {
                    @Override
                    public void doInHibernate(ScrollableResults sr) {
                        int i = 0;
                        long b = System.currentTimeMillis();
                        while (sr.next()) {
                            if ((++i) % BATCH_SIZE == 0) {
                                logger.error("begin - end (" + i + "): "
                                        + (System.currentTimeMillis() - b));
                                b = System.currentTimeMillis();
                                circleSubscribeDao.clear();
                            }
                            final Object[] o = sr.get();
                            final Long followerId = (Long) o[0];
                            final Long circleId = (Long) o[1];
                            final Date created = (Date) o[2];
                            addExplicitFolower(followerId, circleId, created);
                            addCircleFollowing(followerId, circleId, created);
                        }
                    }
                });
    }
    
}
