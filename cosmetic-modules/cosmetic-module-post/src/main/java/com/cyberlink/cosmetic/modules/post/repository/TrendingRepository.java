package com.cyberlink.cosmetic.modules.post.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.cosmetic.modules.post.model.TrendPoolInfo;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolType;

public interface TrendingRepository {

    enum PerType { 
        Gen(10000, 0.5D, 2000, "ge"), 
        GenCat(10000, 0.25D, 1500, "ge_ca"), 
        Cat(10000, 0.25D, 1500, "ca");

        final private Integer maxPoolSize;
        final private Double topRatio;
        final private Integer updateMax;
        final private String shortForm;
        
        PerType(Integer maxPoolSize, Double topRatio, Integer updateMax, 
                String shortForm) {
            this.maxPoolSize = maxPoolSize;
            this.topRatio = topRatio;
            this.updateMax = updateMax;
            this.shortForm = shortForm;
        }
        
        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }

        public Double getTopRatio() {
            return topRatio;
        }

        public Integer getUpdateMax() {
            return updateMax;
        }

        public String getShortForm() {
            return shortForm;
        }
    }
    
    TrendPoolInfo getTrendPoolInfo(TrendPoolType pt, String locale, String circleKey);
    
    Double getScore(TrendPoolType pt, String locale, String circleKey, Long idx, Long postId);
    
    Long addTrendPost(TrendPoolType pt, String locale, String circleKey, Long idx, Map<Long, Double> scoreMap);
    
    void removeTrendPost(TrendPoolType pt, String locale, String circleKey, Long idx, Long postId);
    
    void trimTrend(TrendPoolType pt, String locale, String circleKey, Long idx, Long maxSize);
    
    Long getTrendingList(TrendPoolType type, String locale, String circleKey, Long idx, Long offset, Long limit, List<Long> result);

    void swapTrend(TrendPoolType pt, String locale, String circleKey, Long oldIdx, Long newIdx);
    
    void mergeTrend(TrendPoolType pt, String locale, String circleKey, Long inIdx1, Long inIdx2, Long outIdx, Long outCursor);

    Map<Long, Double> shuffleTrendRange(TrendPoolType pt, String locale, String circleType, Long offset, Long count);

    void updateInfo(TrendPoolInfo info);
    
    Long getTrendPoolSize(TrendPoolType pt, String locale, String circleKey, Long idx);
    
	void setServiceHost(String hostName);

	String getServiceHost();

    Boolean isInWhiteList(Long userId);

    Long addToWhiteList(Long userId);

    Set<String> getJoinerList();
    
    Long addToUserList(Long userId);
    
    Set<String> getUserList(Long shardId);
    
    void removeUserList(Long shardId);
    
    Long addPostCategory(Long userId, String circleType);
    
    List<String> getCategoryList(Long userId);
    
    void updateUserGroup(Long userId, String userGroup);
    
    String getUserGroup(Long userId);
    
    List<Long> getShardList();
}
