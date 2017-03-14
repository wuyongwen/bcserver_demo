package com.cyberlink.cosmetic.action.backend.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.modules.post.model.PsTrendGroup;

public interface PsTrendScheduleService {

	void start();
	
	void stop();
	
	Map<String, Object> getStatus();

    void releaseFromTrendPool(final Date newDisplayDate, List<PsTrendGroup> groups, final Map<String, Object> info);
    
    void generateTrendPool(String locale, Date startTime, Date endTime, Date displayDate, final Map<String, Object> info);
    
    void purgeExpiredTrendPool(Date displayDate);
    
    void releaseOneSpecified();
    
    void reGenerate();

    void updateUsrGroupFromFile(File file);
}
