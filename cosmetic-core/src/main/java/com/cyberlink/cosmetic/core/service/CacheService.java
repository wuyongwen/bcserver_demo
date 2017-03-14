package com.cyberlink.cosmetic.core.service;

public interface CacheService {
    
    <V, K>  V get(String cacheName, K k);
    
    <V, K>  void put(String cacheName, K k, V v);
    
    <K>  void remove(String cacheName, K k);

    void removeAll();

}
