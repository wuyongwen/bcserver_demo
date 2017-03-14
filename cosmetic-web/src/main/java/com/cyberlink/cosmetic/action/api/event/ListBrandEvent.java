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
import com.cyberlink.cosmetic.modules.event.model.ServiceType;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent.ListBrandEventView;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;

@UrlBinding("/api/event/list-brand-event.action")
public class ListBrandEvent extends AbstractAction {
	
	@SpringBean("event.BrandEventDao")
	private BrandEventDao brandEventDao;
	
	@SpringBean("event.EventUserDao")
	private EventUserDao eventUserDao;
	
	private String locale;
	private Long curUserId;
	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public Long getCurUserId() {
		return curUserId;
	}
	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}
	
	@DefaultHandler
    public Resolution route() {
		final Map<String, Object> results = new HashMap<String, Object>();
		
		List<BrandEvent> beventList = brandEventDao.listBrandEvent(locale, ServiceType.FREE_SAMPLE);
		
		if (curUserId != null) {
			List<Long> eventIds = new ArrayList<Long>();
			for (BrandEvent bevent : beventList) {
				eventIds.add(bevent.getId());
			}
			Map<Long, EventUserStatus> statusMap = eventUserDao.getEventUserStatusByEventIds(curUserId, eventIds);
			for (BrandEvent bevent : beventList) {
				if (statusMap.containsKey(bevent.getId()))
					bevent.setUserStatus(statusMap.get(bevent.getId()));
			}
		}
		

		Date currentTime = new Date();
		results.put("currentTime", currentTime.getTime());
		results.put("results", beventList);
		results.put("totalSize", beventList.size());
		return json(results, ListBrandEventView.class);
	}
}