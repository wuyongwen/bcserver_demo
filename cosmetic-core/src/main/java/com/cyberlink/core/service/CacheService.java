package com.cyberlink.core.service;

public interface CacheService<K, V> {
    V get(K k);

    void put(K k, V v);

    void remove(K k);

    void removeAll();
}
