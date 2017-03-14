package com.cyberlink.cosmetic.modules.post.repository;

import java.util.Date;
import java.util.Map;

import com.cyberlink.cosmetic.core.repository.EsRepository;
import com.cyberlink.cosmetic.modules.post.model.PostHeat;

public interface PostHeatRepository extends EsRepository<PostHeat> {
    
    EsResult<Boolean> updateLikeCount(String id, Integer updateBy);
    
    EsResult<Boolean> updateCircleInCount(String id);
    
    EsResult<Map<Long, Map<String, Integer>>> findTopUser(String locale, Integer limit, Date begin, Date end);
    
}