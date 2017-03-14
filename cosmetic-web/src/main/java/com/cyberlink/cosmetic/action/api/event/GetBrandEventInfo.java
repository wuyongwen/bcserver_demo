package com.cyberlink.cosmetic.action.api.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent.InfoBrandEventView_v4;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;

@UrlBinding("/api/event/get-brand-event-info.action")
public class GetBrandEventInfo extends AbstractAction {
	
	@SpringBean("event.BrandEventDao")
	private BrandEventDao brandEventDao;
	
	@SpringBean("event.EventUserDao")
	private EventUserDao eventUserDao;
	
	@SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
	
	protected Long curUserId;
	protected Long brandEventId;
	protected String apiVersion;
	
	public Long getCurUserId() {
		return curUserId;
	}
	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}
	public Long getBrandEventId() {
		return brandEventId;
	}
	public void setBrandEventId(Long brandEventId) {
		this.brandEventId = brandEventId;
	}
	public String getApiVersion() {
		return apiVersion;
	}
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
	
	protected Map<String, Object> getEventInfo(Long brandEventId, Long curUserId) {
	    BrandEvent brandEvent = brandEventDao.findByBrandEventId(brandEventId);
        if (brandEvent == null)
            return null;
        
        final Map<String, Object> results = new HashMap<String, Object>();
        if (curUserId != null) {
            List<Long> eventIds = new ArrayList<Long>();
            eventIds.add(brandEvent.getId());
            Map<Long, EventUserStatus> statusMap = eventUserDao.getEventUserStatusByEventIds(curUserId, eventIds);      
            if (statusMap.containsKey(brandEvent.getId()))
                brandEvent.setUserStatus(statusMap.get(brandEvent.getId()));
            
            Subscribe subscribe = subscribeDao.findBySubscriberAndSubscribee(curUserId, brandEvent.getBrandId(), null);
            if (subscribe != null)
                brandEvent.setIsFollowed(Boolean.TRUE);
        }
        
        // Handle old build. 
        Double ver = 0.0;
        try {
        	ver = Double.valueOf(apiVersion);
        } catch (Exception e) {
        }
        brandEvent.setApiVersion(ver);
        
        Date currentTime = new Date();
        results.put("currentTime", currentTime.getTime());
        results.put("result", brandEvent);
        return results;
	}
	
	@DefaultHandler
    public Resolution route() {
		final Map<String, Object> results = getEventInfo(brandEventId, curUserId);
		return json(results, InfoBrandEventView_v4.class);
	}
}