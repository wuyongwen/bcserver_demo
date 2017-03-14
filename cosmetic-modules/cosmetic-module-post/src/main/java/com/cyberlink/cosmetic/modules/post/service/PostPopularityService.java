package com.cyberlink.cosmetic.modules.post.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostNew;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool.NewPoolGroup;

public interface PostPopularityService {

    Post pushToNewImmediate(Post post, List<Long> circleTypeIds, Long basicSortBonus, Boolean forceHideInAll, Boolean skipPool, Boolean forceAdd);
    
    Boolean updatePostNew(Post post, List<PostNew> postNews, List<Long> circleTypeIds, Long basicSortBonus, Boolean adjustDate, Boolean skipPool, Boolean forceHideInAll, Boolean forceAdd);
    
    Integer releasePoolToNew(String locale, NewPoolGroup group, Calendar currentDate, Boolean mustPush, Map<String, Object> summary);
    
    Integer getPostNewMinThreshold();
    
    Long getPostBasicSortBonus(Post post);

    void setReleaseConfig(String content);

    String getReleaseConfig();

    Integer batchRealDelete(Date start, Date end);
}
