package com.cyberlink.cosmetic.action.api.event;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/api/event/get-receive-user-info.action")
public class GetReceiveUserInfo extends AbstractAction {
	@SpringBean("event.BrandEventDao")
	private BrandEventDao brandEventDao;
	
	@SpringBean("event.EventUserDao")
	private EventUserDao eventUserDao;
	
	private Long brandEventId;
	
	public Long getBrandEventId() {
		return brandEventId;
	}
	public void setBrandEventId(Long brandEventId) {
		this.brandEventId = brandEventId;
	}
	
	@DefaultHandler
    public Resolution route() {
		if (!authenticate()) {
    		return new ErrorResolution(authError); 
    	}
		
		if (!brandEventDao.exists(brandEventId))
			return new ErrorResolution(ErrorDef.InvalidBrandEventId);
		
		List<EventUserStatus> Status = new ArrayList<EventUserStatus>();
		Status.add(EventUserStatus.Selected);
		Status.add(EventUserStatus.Redeemed);
		
		EventUser eventUser = eventUserDao.findByUserIdAndEventId(getCurrentUserId(), brandEventId, Status);
		return json("result", eventUser);
	}
}