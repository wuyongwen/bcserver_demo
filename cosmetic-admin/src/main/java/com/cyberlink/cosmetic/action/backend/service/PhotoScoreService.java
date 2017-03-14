package com.cyberlink.cosmetic.action.backend.service;

import java.util.Date;
import java.util.Map;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool.NewPoolGroup;

public interface PhotoScoreService {

    Throwable runFor(Date beginDate, Date endDate, Long minBasicScore, final Boolean checkPostRescue, final Map<String, Object> summary, BlockLimit blockLimit);
    
	void start();
	
	void stop();
	
	Map<String, Object> getStatus();

    //For background invoke
    void exec();
    
    void releaseUgcPostNew();
    
    void releasePgcPostNew();
    
    void CleanOldRecord();
    
    void HandleUnhandledPostScore(Date begin, Date end);

    Integer releaseFromPoolToNew(NewPoolGroup group, Boolean mustPush, Map<String, Object> releaseCountMap);
}
