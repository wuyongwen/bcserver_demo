package com.cyberlink.cosmetic.modules.post.repository;

import java.util.Map;

import com.cyberlink.cosmetic.modules.post.model.TrendPoolType;

public interface TrendingCacheRepository {

    Long addTrendPost(TrendPoolType pt, String locale, String circleKey, Map<Long, Double> scoreMap);

    Long getTrendPoolSize(TrendPoolType pt, String locale, String circleKey);

    void trimTrend(TrendPoolType pt, String locale, String circleKey, Long maxSize);

    Long getTrendListWithScore(TrendPoolType type, String locale, String circleKey, Long offset, Long limit, Boolean withSize, Map<Long, Double> result);
    
    void deleteTrend(TrendPoolType pt, String locale, String circleKey);
    
}
