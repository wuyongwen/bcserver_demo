package com.cyberlink.cosmetic.modules.post.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.modules.post.model.PsTrend;
import com.cyberlink.cosmetic.modules.post.model.PsTrend.PsTrendKey;
import com.cyberlink.cosmetic.modules.post.model.PsTrendGroup;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool;

public interface PsTrendService {

    interface ScanResultCallback<T> {
        void doWith(T r);
    }
    
    Set<Long> getRelatedPsTrendGroup(String locale, Long circleTypeId);

    Map<String, Map<Long, Set<Long>>> getPsTrendGroupMap();

    Boolean addGeneralPost(String locale, Long promoteScore, Date displayDate, Long postId);    
	
    Map<Long, Pair<Long, Date>> addGeneralPosts(String locale, Map<Long, Pair<Long, Date>> postMap);
    
    List<PsTrendGroup> findGroupsByStep(Integer step);
    
    Boolean addTrendPost(Long postId, String locale, List<Long> circleTypeIds, Long promoteScore, Boolean hideInAll, Date displayDate);

    String listPsTrend(String uuid, String groupIdVal, String locale, List<Long> resultList, BlockLimit blockLimit);
    
    void doWithBestPsTrend(String locale, Date startTime, Date endTime, final ScanResultCallback<Map<String, Map<String, Date>>> callback);
    
    Boolean batchCreateTrendPool(Date newDisplayDate, List<PsTrendPool> list, Map<String, Object> info);
    
    void purgeExpiredTrendPool(Date displayDate);
    
    Boolean releaseFromTrendPool(List<PsTrendGroup> groups, ScanResultCallback<Map<PsTrendKey, Long>> psTrendCallback, ScanResultCallback<List<PsTrendGroup>> psTGroupCallback);

    Boolean batchAddTrendPost(Date newDisplayDate, Map<PsTrendKey, Long> toAddPsTrend, Boolean updatePromoteScore, final Map<String, Object> info);
    
    Boolean updateTrendGroups(List<PsTrendGroup> trendGroups);
    
    Boolean createOrUpdateUserGroup(Map<String, String> userGroupMap);
}