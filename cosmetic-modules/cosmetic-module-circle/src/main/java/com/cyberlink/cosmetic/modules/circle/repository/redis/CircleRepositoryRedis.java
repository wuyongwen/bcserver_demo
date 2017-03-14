package com.cyberlink.cosmetic.modules.circle.repository.redis;

import org.hibernate.ScrollableResults;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;

import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.repository.CircleRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class CircleRepositoryRedis extends AbstractRedisRepository implements
        CircleRepository {
    private static final Integer BATCH_SIZE = 1000;
    private CircleDao circleDao;

    public void setCircleDao(CircleDao circleDao) {
        this.circleDao = circleDao;
    }

    @Override
    public void doWithCircleIds(Long circleCreatorId,
            CursorCallback<String> callback) {
        sScan(KeyUtils.userCirclePublic(circleCreatorId).getBytes(), ScanOptions.NONE, callback);
        /*Cursor<String> c = opsForSet().scan(
                KeyUtils.userCirclePublic(circleCreatorId), ScanOptions.NONE);
        execute(c, callback);*/
    }

    @Override
    public void addCircle(Long userId, Long circleId) {
        opsForSet().add(KeyUtils.userCirclePublic(userId), circleId.toString());
    }

    @Override
    public void deleteByUserId(Long userId) {
        delete(KeyUtils.userCirclePublic(userId));
    }

    @Override
    public void deleteCircle(Long userId, Long circleId) {
        opsForSet().remove(KeyUtils.userCirclePublic(userId),
                circleId.toString());
    }

    @Override
    public void updateAll() {
        logger.error("begin updateAll");
        circleDao.doWithAllPublicCircle(new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                int i = 0;
                long b = System.currentTimeMillis();
                while (sr.next()) {
                    if ((++i) % BATCH_SIZE == 0) {
                        logger.error("begin - end (" + i + "): "
                                + (System.currentTimeMillis() - b));
                        b = System.currentTimeMillis();
                        circleDao.clear();
                    }
                    final Object[] o = sr.get();
                    final Long creatorId = (Long) o[0];
                    final Long circleId = (Long) o[1];
                    addCircle(creatorId, circleId);
                }
                logger.error("end updateAll");
            }
        });
    }

}
