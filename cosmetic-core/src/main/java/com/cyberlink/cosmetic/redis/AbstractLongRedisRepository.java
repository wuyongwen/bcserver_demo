package com.cyberlink.cosmetic.redis;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.cyberlink.core.service.AbstractService;

public abstract class AbstractLongRedisRepository extends AbstractService {

    private RedisTemplate<String, Long> redisTemplate;


	public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		this.redisTemplate.setKeySerializer(stringSerializer);
    }

    protected final SetOperations<String, Long> opsForSet() {
        return redisTemplate.opsForSet();
    }

    protected final HashOperations<String, Long, Long> opsForHash() {
        return redisTemplate.opsForHash();
    }
    
    protected final ListOperations<String, Long> opsForList() {
        return redisTemplate.opsForList();
    }


    protected final Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    protected final Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    protected final void expireAt(String key, Date date) {
        redisTemplate.expireAt(key, date);
    }

    protected final ValueOperations<String, Long> opsForValue() {
        return redisTemplate.opsForValue();
    }

    protected final boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    protected final void delete(String key) {
        redisTemplate.delete(key);
    }

    protected final void expire(String key, Long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
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
}
