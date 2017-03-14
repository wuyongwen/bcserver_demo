package com.cyberlink.cosmetic.modules.feed.repository.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.PoolRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class PoolRepositoryRedis extends AbstractRedisRepository implements
        PoolRepository {

    @Override
    public void add(PoolType poolType, String hashKey,
            Map<String, Double> values) {
        final String key = getKey(poolType, hashKey);
        for(String k : values.keySet()) {
            opsForZSet().add(key, k, values.get(k).longValue());
        }
        trimPool(key, -1 * poolType.getMaxlength());
    }

    private void trimPool(String key, int i) {
        opsForZSet().removeRange(key, 0, i);
    }

    private Set<TypedTuple<String>> toTuples(Map<String, Double> values) {
        final Set<TypedTuple<String>> r = new LinkedHashSet<TypedTuple<String>>();
        for (final Map.Entry<String, Double> e : values.entrySet()) {
            r.add(new DefaultTypedTuple<String>(e.getKey(), e.getValue()));
        }
        return r;
    }

    @Override
    public void add(PoolType poolType, String userId, String id, Long score) {
        final String key = getKey(poolType, userId);
        opsForZSet().add(key, id, score);
        trimPool(key, -1 * poolType.getMaxlength());
    }

    @Override
    public void deleteByCreatorId(PoolType poolType, Long followerId,
            Long followeeId) {
        deleteByPattern(getKey(poolType, followerId.toString()), followeeId
                + ":*");
    }

    private String getKey(PoolType poolType, String hashKey) {
        return KeyUtils.pool(poolType.name().toLowerCase(), hashKey);
    }

    @Override
    public void deleteByCircleId(PoolType poolType, Long followerId,
            Long circleId) {
        deleteByPattern(getKey(poolType, followerId.toString()), "*:"
                + circleId);
    }

    private void deleteByPattern(final String key, String pattern) {
        zScan(key.getBytes(), new ScanOptions.ScanOptionsBuilder().match(pattern).build(), new CursorCallback<TypedTuple<String>>() {
            @Override
            public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                while (cursor.hasNext()) {
                    final TypedTuple<String> tt = cursor.next();
                    opsForZSet().remove(key, tt.getValue());
                }
            }
        });
        /*execute(opsForZSet().scan(key,
                new ScanOptions.ScanOptionsBuilder().match(pattern).build()),
                new CursorCallback<TypedTuple<String>>() {
                    @Override
                    public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                        while (cursor.hasNext()) {
                            final TypedTuple<String> tt = cursor.next();
                            opsForZSet().remove(key, tt.getValue());
                        }
                    }
                });*/
    }

    @Override
    public void deleteByValue(PoolType poolType, Long followerId, String value) {
        opsForZSet().remove(getKey(poolType, followerId.toString()), value);
    }

    @Override
    public List<PoolPost> pop(PoolType poolType, String hashKey,
            Integer numToRetrieve) {
        final String key = getKey(poolType, hashKey);
        final Set<String> r = opsForZSet().reverseRange(key, 0,
                numToRetrieve - 1);
        if (!r.isEmpty()) {
            opsForZSet().remove(key, r.toArray());
        }
        return toPoolPost(r);
    }

    private List<PoolPost> toPoolPost(Set<String> l) {
        final List<PoolPost> r = new ArrayList<PoolPost>();
        for (final String s : l) {
            r.add(new PoolPost(s));
        }
        return r;
    }

    @Override
    public List<PoolPost> range(PoolType poolType, String hashkey,
            int pageIndex, int pageSize) {
        final Set<String> r = opsForZSet().reverseRange(
                getKey(poolType, hashkey), getStart(pageIndex, pageSize),
                getEnd(pageIndex, pageSize));
        return toPoolPost(r);
    }

    private long getStart(int pageIndex, int pageSize) {
        return (pageIndex - 1) * pageSize;
    }

    private long getEnd(int pageIndex, int pageSize) {
        return pageIndex * pageSize - 1;
    }

    @Override
    public Map<PoolPost, Double> rangeWithScores(PoolType poolType,
            String hashkey, int pageIndex, int pageSize) {
        final Set<TypedTuple<String>> r = opsForZSet().reverseRangeWithScores(
                getKey(poolType, hashkey), getStart(pageIndex, pageSize),
                getEnd(pageIndex, pageSize));
        final Map<PoolPost, Double> m = new HashMap<PoolPost, Double>();
        for (final TypedTuple<String> tt : r) {
            final PoolPost pp = new PoolPost(tt.getValue());
            m.put(pp, tt.getScore());
        }
        return m;
    }

    @Override
    public void doWithAllPostAscendingly(PoolType poolType, String hashKey,
            CursorCallback<TypedTuple<String>> callback) {
        zScan(getKey(poolType, hashKey).getBytes(), ScanOptions.NONE, callback);
        /*execute(opsForZSet().scan(getKey(poolType, hashKey), ScanOptions.NONE),
                callback);*/
    }

    @Override
    public void clean(PoolType poolType, String hashKey) {
        opsForZSet().removeRange(getKey(poolType, hashKey), 0, -1);
    }
}
