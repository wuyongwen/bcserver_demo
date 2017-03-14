package com.cyberlink.cosmetic.modules.user.repository;

import com.cyberlink.cosmetic.core.repository.EsRepository;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;
import com.cyberlink.cosmetic.modules.user.model.UserHeat;

public interface UserHeatRepository extends EsRepository<UserHeat> {
    
    EsResult<Boolean> updatePostCount(String id);
    
    EsResult<Boolean> updateLikeCount(String id, Integer updateBy);
    
    EsResult<Boolean> updateCircleInCount(String id);
    
    EsResult<Boolean> updateFollowerCount(String id, Integer updateBy);

    EsResult<Boolean> updateBadge(String id, BadgeType badgeType);
    
}