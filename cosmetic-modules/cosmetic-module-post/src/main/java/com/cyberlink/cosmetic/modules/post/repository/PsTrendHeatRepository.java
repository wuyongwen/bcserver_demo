package com.cyberlink.cosmetic.modules.post.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.core.repository.EsRepository;
import com.cyberlink.cosmetic.modules.post.model.PsTrendHeat;

public interface PsTrendHeatRepository extends EsRepository<PsTrendHeat> {
    
    interface LoopCallback<ResultType> {
        void doWith(ResultType r);
    }
    
    EsResult<Boolean> updateLikeCount(String id, Integer updateBy);
    
    EsResult<Boolean> updateCircleInCount(String id);
    
    void findTopTrend(List<String> circleTypeIds, Double topRatio, Date begin, Date end, LoopCallback<Map<String, Map<String, Date>>> callback);
    
}