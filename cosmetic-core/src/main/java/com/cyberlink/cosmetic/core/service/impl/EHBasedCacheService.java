package com.cyberlink.cosmetic.core.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.core.service.CacheService;

public class EHBasedCacheService extends AbstractService implements CacheService {
    private final Map<String, Cache> caches = new HashMap<String, Cache>();

    public EHBasedCacheService(EhCacheCacheManager manager) {
        for(String cacheName : manager.getCacheNames()) {
            caches.put(cacheName, manager.getCache(cacheName));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public<V, K>  V get(String cacheName, K k) {
        if(!caches.containsKey(cacheName))
            return null;
        
        ValueWrapper vw = caches.get(cacheName).get(k);
        if (vw == null) {
            return null;
        }
        return (V) vw.get();
    }

    @Override
    public<V, K>  void put(String cacheName, K k, V v) {
        if(!caches.containsKey(cacheName))
            return;
        
        caches.get(cacheName).put(k, v);
    }

    @Override
    public<K>  void remove(String cacheName, K k) {
        if(!caches.containsKey(cacheName))
            return;
        
        caches.get(cacheName).evict(k);
    }

    @Override
    public void removeAll() {
        for(Cache cache : caches.values())
            cache.clear();
    }

}
