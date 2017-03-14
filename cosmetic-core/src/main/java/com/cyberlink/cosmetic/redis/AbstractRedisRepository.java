package com.cyberlink.cosmetic.redis;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.core.service.AbstractService;

public abstract class AbstractRedisRepository extends AbstractService implements
        Repository {

    private StringRedisTemplate redisTemplate;

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    protected final HashOperations<String, String, String> opsForHash() {
        return redisTemplate.opsForHash();
    }

    public final boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    protected final SetOperations<String, String> opsForSet() {
        return redisTemplate.opsForSet();
    }

    protected final ListOperations<String, String> opsForList() {
        return redisTemplate.opsForList();
    }

    protected final Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    protected final ZSetOperations<String, String> opsForZSet() {
        return redisTemplate.opsForZSet();
    }

    protected final Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    public final void expireAt(String key, Date date) {
        redisTemplate.expireAt(key, date);
    }

    protected final ValueOperations<String, String> opsForValue() {
        return redisTemplate.opsForValue();
    }

    protected final boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    protected final void delete(String key) {
        redisTemplate.delete(key);
    }

    public final void expire(String key, Long timeout, TimeUnit unit) {
        final Date d = Calendar.getInstance().getTime();
        final Date expDate = DateUtils.addSeconds(d, (int) TimeoutUtils.toSeconds(timeout, unit));
        expireAt(key, expDate);
    }

    protected final <T> void execute(Cursor<T> c, CursorCallback<T> callback) {
        try {
            callback.doWithCursor(c);
        } finally {
            try {
                c.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    protected final String[] toString(Long... values) {
        if (values == null) {
            return null;
        }
        String[] l = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            l[i] = String.valueOf(values[i]);
        }
        return l;
    }

    protected final Object[] toObject(Long... values) {
        return (Object[]) toString(values);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected final <V> TypedTuple<V> deserializeTuple(Tuple tuple) {
        Object value = tuple.getValue();
        if (redisTemplate.getValueSerializer() != null) {
            value = redisTemplate.getValueSerializer().deserialize(tuple.getValue());
        }
        return new DefaultTypedTuple(value, tuple.getScore());
    }
    
    @SuppressWarnings("unchecked")
    <V> V deserializeValue(byte[] value) {
        if (redisTemplate.getValueSerializer() == null) {
            return (V) value;
        }
        return (V) redisTemplate.getValueSerializer().deserialize(value);
    }
    
    protected final void zScan(final byte[] key, final ScanOptions options, final CursorCallback<TypedTuple<String>> callback) {
        redisTemplate.execute(new RedisCallback<Boolean>() {

            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {

                Cursor<Tuple> cursor = connection.zScan(key, options);
                Cursor<TypedTuple<String>> tupleCursor = new ConvertingCursor<Tuple, TypedTuple<String>>(cursor, new Converter<Tuple, TypedTuple<String>>() {
                
                    @Override
                    public TypedTuple<String> convert(Tuple source) {
                        return deserializeTuple(source);
                    }
                });
                execute(tupleCursor, callback);
                return true;
            }
          });
        
    }
    
    protected final void sScan(final byte[] key, final ScanOptions options, final CursorCallback<String> callback) {
        redisTemplate.execute(new RedisCallback<Boolean>() {

            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {

                Cursor<byte[]> cursor = connection.sScan(key, options);
                Cursor<String> stringCursor = new ConvertingCursor<byte[], String>(cursor, new Converter<byte[], String>() {

                    @Override
                    public String convert(byte[] source) {
                        return deserializeValue(source);
                    }
                });
                execute(stringCursor, callback);
                return true;
            }
          });
        
    }
}
