package com.cyberlink.cosmetic.modules.post.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.redis.CursorCallback;

public interface LikeRepository {
	
    List<Long> getLikes(Long userId, String targetType, List<Long> targetIds);
    
    PageResult<Long> getLikers(String targetType, Long targetId, BlockLimit blockLimit);
    
    PageResult<Long> getLikedTarget(Long userId, String targetType, BlockLimit blockLimit);
    
    void like(Long userId, String targetType, Long targetId, Long createdTime);
    
    void unlike(Long userId, String targetType, Long targetId);
    
    void deleteByTargetId(String targetType, Long targetId);
    
    void deleteByUserId(Long userId);
    
    void setServiceHost(String hostName);
    
    String getServiceHost();
    
    void setPromotionalLikeTarget(String locale, Long postId, Date createdTime);
    
    Map<Integer, String> getAvailablePromitionalLikeKey(String locale, Integer slotCount);
    
    void doWithPromotionalLikeTargets(String key, CursorCallback<String> callback);
    
    void setPromotionalLikeCount(String postId, Integer incr);
    
    Integer getPromotionalLikeCount(String postId);

    void setLastDayPostLikeCount(String locale, Integer totalPostCount, Integer totalLikeCount);
    
    Pair<Integer, Integer> getLastDayPostLikeCount(String locale);
    
    // For initialize using. Should remove after initialization
    void cleanOldPromoteTarget(String locale);
}
