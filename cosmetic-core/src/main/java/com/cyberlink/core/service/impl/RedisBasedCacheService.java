package com.cyberlink.core.service.impl;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.cache.RedisCacheManager;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.service.CacheService;

public class RedisBasedCacheService<K, V> extends AbstractService implements
        CacheService<K, V> {
    private final Cache cache;

    public RedisBasedCacheService(RedisCacheManager manager, String cacheName) {
        this.cache = manager.getCache(cacheName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(K k) {
        ValueWrapper vw = cache.get(k);
        if (vw == null) {
            return null;
        }
        return (V) vw.get();
    }

    @Override
    public void put(K k, V v) {
        cache.put(k, v);
    }

    @Override
    public void remove(K k) {
        cache.evict(k);
    }

    @Override
    public void removeAll() {
        cache.clear();
    }

}
