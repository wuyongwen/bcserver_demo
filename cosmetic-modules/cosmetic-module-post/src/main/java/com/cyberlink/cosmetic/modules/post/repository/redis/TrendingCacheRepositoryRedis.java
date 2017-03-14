package com.cyberlink.cosmetic.modules.post.repository.redis;

import java.util.HashSet;

import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.cosmetic.modules.post.model.TrendPoolType;
import com.cyberlink.cosmetic.modules.post.repository.TrendingCacheRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class TrendingCacheRepositoryRedis extends AbstractRedisRepository implements
    TrendingCacheRepository {

    private Long idx = -1L;
    
    @Override
    public Long addTrendPost(TrendPoolType pt, String locale, String circleKey, Map<Long, Double> scoreMap) {
        Long added = 0L;
        String key = KeyUtils.trendPool(locale, circleKey, pt.toString(), idx);
        if(key == null)
            return added;
        
        Set<TypedTuple<String>> t = toTypedTuples(scoreMap);
        if(t.size() <= 0)
            return 0L;
        return opsForZSet().add(key, t);
    }
    
    @Override
    public void trimTrend(TrendPoolType pt, String locale, String circleKey, Long maxSize) {
        String key = KeyUtils.trendPool(locale, circleKey, pt.toString(), idx);
        if(key == null)
            return;
        opsForZSet().removeRange(key, 0, -1 * (maxSize + 1));
    }
    
    @Override
    public Long getTrendPoolSize(TrendPoolType pt, String locale, String circleKey) {
        String key = KeyUtils.trendPool(locale, circleKey, pt.toString(), idx);
        return opsForZSet().zCard(key);
    }
    
    @Override
    public Long getTrendListWithScore(TrendPoolType type, String locale, String circleKey, Long offset, 
            Long limit, Boolean withSize, Map<Long, Double> result) {
        if(result == null)
            return null;
        
        Set<TypedTuple<String>> postIdsStr = getTrendWithScore(type, locale, circleKey, idx, offset, limit);
        if(postIdsStr == null)
            return null;
        
        for(TypedTuple<String> ts : postIdsStr)
            result.put(Long.valueOf(ts.getValue()), ts.getScore());
        
        if(withSize)
            return getTrendPoolSize(type, locale, circleKey);
        return Long.valueOf(Integer.MAX_VALUE);
    }
    
    @Override
    public void deleteTrend(TrendPoolType pt, String locale, String circleKey) {
        String key = KeyUtils.trendPool(locale, circleKey, pt.toString(), idx);
        if(key == null)
            return;
        delete(key);
    }
    
    private Set<TypedTuple<String>> toTypedTuples(Map<Long, Double> scoreMap) {
        final Set<TypedTuple<String>> r = new HashSet<ZSetOperations.TypedTuple<String>>();
        for (final Long id : scoreMap.keySet()) {
            r.add(toTypedTuple(id, scoreMap.get(id)));
        }
        return r;
    }
    
    private TypedTuple<String> toTypedTuple(Long id, Double score) {
        return new DefaultTypedTuple<String>(id.toString(), score);
    }

    private Set<TypedTuple<String>> getTrendWithScore(TrendPoolType type, String locale, String circleKey, Long idx, Long start, 
            Long end) {
        
        String key = KeyUtils.trendPool(locale, circleKey, type.toString(), idx);
        if(key == null)
            return null;
         
        Set<TypedTuple<String>> postIdsStr = opsForZSet().reverseRangeByScoreWithScores(key, 0D, Double.MAX_VALUE, start, end);
        if(postIdsStr == null)
            return null;
        return postIdsStr;        
    }
}
