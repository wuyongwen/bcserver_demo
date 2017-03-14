package com.cyberlink.cosmetic.modules.post.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.cosmetic.modules.post.model.TrendPoolType;

public interface TrendingService {    
	Set<String> getTrendUserList(Long shardId);
	
	Boolean addPostCategory(Long userId, Long postId, String circleType);
	
	List<String> getPostCategoryList(Long userId);
	
    Boolean updateUserGroup();

    String getUserGroup(Long userId);
    
    void regenerateTrendList(String locale, List<TrendPoolType> pools, Date beginTime, Date endTime);
    
    void updateTrendList(String locale, List<TrendPoolType> pools, Date beginTime, Date endTime);
    
    void updateTrendListCursor(List<TrendPoolType> pools);
    
    Long listTrending(String sortBy, String locale, Long circleTypeId, String group, Long offset, Long limit, List<Long> result);
    
    Boolean isInWhiteList(Long userId);
    
    Long addToWhiteList(Long userId);
    
    Set<String> getJoinerList();
    
    Map<String, Map<Long, String>> getAllCircleNameMap();
    
    void importToPostScoreTrend(String locale, Long circleTypeId);
}