package com.cyberlink.cosmetic.modules.post.repository.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.cosmetic.modules.common.service.GeoIPService;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolInfo;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolType;
import com.cyberlink.cosmetic.modules.post.repository.TrendingRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class TrendingRepositoryRedis extends AbstractRedisRepository implements
    TrendingRepository {

    private List<Long> shardList = null;
    
    @Override
    public TrendPoolInfo getTrendPoolInfo(TrendPoolType pt, String locale, String circleKey) {
        String key = KeyUtils.trendPoolInfo(locale, circleKey, pt.toString());
        String value = opsForValue().get(key);
        if(value == null || value.length() <= 0)
            return null;
        
        return new TrendPoolInfo(pt, locale, circleKey, value);
    }
    
    @Override
    public Double getScore(TrendPoolType pt, String locale, String circleKey, Long idx, Long postId) {
        String key = KeyUtils.trendPool(locale, circleKey, pt.toString(), idx);
        return opsForZSet().score(key, postId.toString());
    }
    
    @Override
    public Long addTrendPost(TrendPoolType pt, String locale, String circleKey, Long idx, Map<Long, Double> scoreMap) {
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
    public void removeTrendPost(TrendPoolType pt, String locale, String circleKey, Long idx, Long postId) {
        String key = KeyUtils.trendPool(locale, circleKey, pt.toString(), idx);
        if(key == null)
            return;
        
        opsForZSet().remove(key, postId.toString());
    }
    
    @Override
    public void trimTrend(TrendPoolType pt, String locale, String circleKey, Long idx, Long maxSize) {
        String key = KeyUtils.trendPool(locale, circleKey, pt.toString(), idx);
        if(key == null)
            return;
        opsForZSet().removeRange(key, 0, -1 * (maxSize + 1));
    }
    
    @Override
    public Long getTrendingList(TrendPoolType type, String locale, String circleKey, Long idx, Long offset, Long limit, List<Long> result) {
        if(result == null)
            return null;
        Long totalSize = Long.valueOf(Integer.MAX_VALUE);
        Long cursor = 0L;
        if(type.getGroup() == 2) {
            TrendPoolInfo pInfo = getTrendPoolInfo(type, locale, circleKey);
            if(pInfo != null) {
                cursor = pInfo.getPoolCursor();
                totalSize = pInfo.getPoolSize() - pInfo.getPoolCursor();
            }
        }
        
        if(totalSize <= 0L)
            return totalSize;
        String key = KeyUtils.trendPool(locale, circleKey, type.toString(), idx);
        if(key == null)
            return 0L;
        Set<String> postIdsStr = opsForZSet().reverseRangeByScore(key, 0, Double.MAX_VALUE, offset.intValue() + cursor.intValue(), limit.intValue());
        if(postIdsStr == null || postIdsStr.size() <= 0)
            return totalSize;
        
        for(String s : postIdsStr) {
            if(s == null)
                continue;
            result.add(Long.valueOf(s));
        }
        
        return totalSize;
    }
    
    @Override
    public void swapTrend(TrendPoolType pt, String locale, String circleKey, Long oldIdx, Long newIdx) {
        String oldKey = KeyUtils.trendPool(locale, circleKey, pt.toString(), oldIdx);
        String newKey = KeyUtils.trendPool(locale, circleKey, pt.toString(), newIdx);
        if(!exists(newKey))
            return;
        TrendPoolInfo info = new TrendPoolInfo(pt, locale, circleKey);
        Long totalSize = opsForZSet().zCard(newKey);
        info.setPoolSize(totalSize);
        info.setIdx(newIdx);
        updateInfo(info);
        if(!oldIdx.equals(newIdx))
            delete(oldKey);
    }
    
    @Override
    public void mergeTrend(TrendPoolType pt, String locale, String circleKey, Long inIdx1, Long inIdx2, 
            Long outIdx, Long outCursor) {
        String srcKey1 = KeyUtils.trendPool(locale, circleKey, pt.toString(), inIdx1);
        String srcKey2 = KeyUtils.trendPool(locale, circleKey, pt.toString(), inIdx2);
        if(!exists(srcKey1) || !exists(srcKey2))
            return;
        
        String newKey = KeyUtils.trendPool(locale, circleKey, pt.toString(), outIdx);
        TrendPoolInfo info = new TrendPoolInfo(pt, locale, circleKey);
        opsForZSet().unionAndStore(srcKey1, srcKey2, newKey);
        Long totalSize = opsForZSet().zCard(newKey);
        info.setPoolSize(totalSize);
        info.setIdx(outIdx);
        if(outCursor != null && outCursor < totalSize)
            info.setPoolCursor(outCursor);
        updateInfo(info);
        if(!inIdx1.equals(outIdx))
            delete(srcKey1);
        if(!inIdx2.equals(outIdx))
            delete(srcKey2);
    }
    
    @Override
    public Map<Long, Double> shuffleTrendRange(TrendPoolType pt, String locale, String circleType, Long offset, Long count) {
        TrendPoolInfo info = getTrendPoolInfo(pt, locale, circleType);
        if(info == null)
            return null;
        
        Set<TypedTuple<String>> postIdsStr = getTrendWithScore(pt, info.getLocale(), info.getCircleType(), info.getIdx(), offset, count);
        if(postIdsStr == null)
            return null;
        
        List<Long> postIds = new ArrayList<Long>();
        List<Double> scores = new ArrayList<Double>();
        for(TypedTuple<String> ts : postIdsStr) {
            postIds.add(Long.valueOf(ts.getValue()));
            scores.add(ts.getScore());
        }
        Collections.shuffle(postIds);
        Map<Long, Double> result = new HashMap<Long, Double>();
        for(int i = 0; i < postIds.size(); i++) {
            if(i >= scores.size())
                break;
            result.put(postIds.get(i), scores.get(i));
        }
        
        return result;
    }
    
    @Override
    public void updateInfo(TrendPoolInfo info) {
        String infoKey = KeyUtils.trendPoolInfo(info.getLocale(), info.getCircleType(), info.getPerType().toString().toLowerCase());
        opsForValue().set(infoKey, info.toString());
    }
    
    @Override
    public Long getTrendPoolSize(TrendPoolType pt, String locale, String circleKey, Long idx) {
        String key = KeyUtils.trendPool(locale, circleKey, pt.toString(), idx);
        return opsForZSet().zCard(key);
    }
    
    @Override
    public void setServiceHost(String hostName) {
        String key = KeyUtils.trendServiceHost();
        opsForValue().set(key, hostName);
    }
    
    @Override
    public String getServiceHost() {
        String key = KeyUtils.trendServiceHost();
        return opsForValue().get(key);
    }
    
    @Override
    public Boolean isInWhiteList(Long userId) {
        return opsForSet().isMember(KeyUtils.trendPoolJoiner(), userId.toString());
    }
    
    @Override
    public Long addToWhiteList(Long userId) {
        return opsForSet().add(KeyUtils.trendPoolJoiner(), userId.toString());
    }
    
    @Override
    public Set<String> getJoinerList() {
        return opsForSet().members(KeyUtils.feedJoiner());
    }
    
    @Override
    public Long addToUserList(Long userId) {
    	return opsForSet().add(KeyUtils.trendPoolUsers(getShardId(userId)), userId.toString());
    }
    
    @Override
    public Set<String> getUserList(Long shardId) {
    	return opsForSet().members(KeyUtils.trendPoolUsers(getShardId(shardId)));
    }
    
    @Override
    public void removeUserList(Long shardId) {
    	delete(KeyUtils.trendPoolUsers(getShardId(shardId)));
    }
    
    @Override
    public Long addPostCategory(Long userId, String circleType) {
    	if (opsForList().size(KeyUtils.trendPoolUserCategories(getShardId(userId), userId)) >= 20)
    		opsForList().leftPop(KeyUtils.trendPoolUserCategories(getShardId(userId), userId));
    	return opsForList().rightPush(KeyUtils.trendPoolUserCategories(getShardId(userId), userId), circleType);
    }
    
    @Override
    public List<String> getCategoryList(Long userId) {
    	Long size = opsForList().size(KeyUtils.trendPoolUserCategories(getShardId(userId), userId));
    	return opsForList().range(KeyUtils.trendPoolUserCategories(getShardId(userId), userId), 0l, size);
    }
    
    @Override
    public void updateUserGroup(Long userId, String userGroup) {
    	opsForValue().set(KeyUtils.trendPoolUserGroup(getShardId(userId), userId), userGroup);
    }
    
    @Override
    public String getUserGroup(Long userId) {
    	return opsForValue().get(KeyUtils.trendPoolUserGroup(getShardId(userId), userId));
    }
    
    @Override
    public List<Long> getShardList() {
    	if (shardList == null || shardList.isEmpty()) {
    		shardList = new ArrayList<Long>();
        	shardList.add(1l);
        	shardList.add(2l);
        	shardList.add(3l);
        	shardList.add(4l);
        	shardList.add(5l);
        	shardList.add(6l);
        	shardList.add(GeoIPService.SHARD_OTHERS);
        	shardList.addAll(GeoIPService.SHARDINGMAP.values());
    	}
    	return shardList;
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

    private Set<TypedTuple<String>> getTrendWithScore(TrendPoolType type, String locale, String circleKey, Long idx, Long offset, 
            Long count) {
        
        String key = KeyUtils.trendPool(locale, circleKey, type.toString(), idx);
        if(key == null)
            return null;
         
        Set<TypedTuple<String>> postIdsStr = opsForZSet().reverseRangeByScoreWithScores(key, 0D, Double.MAX_VALUE, offset, count);
        if(postIdsStr == null)
            return null;
        return postIdsStr;        
    }
    private Long getShardId(Long userId) {
    	Long shardId = userId % 2000;
    	
    	if (getShardList().contains(shardId))
    		return shardId;
    	else
    		return GeoIPService.SHARD_OTHERS;
    }

}
