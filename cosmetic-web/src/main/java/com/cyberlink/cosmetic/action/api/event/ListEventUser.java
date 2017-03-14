package com.cyberlink.cosmetic.action.api.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.EventUserStatus;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/api/event/list-event-user.action")
public class ListEventUser extends AbstractAction {
	
	@SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;

    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("event.BrandEventDao")
	private BrandEventDao brandEventDao;
	
	@SpringBean("event.EventUserDao")
	private EventUserDao eventUserDao;
    
    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
    private Long curUserId;
	private Long brandEventId;
	private List<EventUserStatus> eventUserStatus;

	private Boolean isIncludeRedeem = Boolean.FALSE;

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

	public Long getBrandEventId() {
		return brandEventId;
	}

	@Validate(required = true, on = "route")
	public void setBrandEventId(Long brandEventId) {
		this.brandEventId = brandEventId;
	}

	public List<EventUserStatus> getEventUserStatus() {
		return eventUserStatus;
	}

	@Validate(required = true, on = "route")
	public void setEventUserStatus(List<EventUserStatus> eventUserStatus) {
		this.eventUserStatus = eventUserStatus;
	}

	public Boolean getIsIncludeRedeem() {
		return isIncludeRedeem;
	}

	public void setIsIncludeRedeem(Boolean isIncludeRedeem) {
		this.isIncludeRedeem = isIncludeRedeem;
	}

	@DefaultHandler
    public Resolution route() {
		final Map<String, Object> results = new HashMap<String, Object>();
		BlockLimit blockLimit = new BlockLimit(offset.intValue(), limit.intValue());
		Set<Long> subscribeSet = null;
		PageResult<Long> userIds = null;
 
		/* Workaround for app's bug:
		 * When list event winner, app only attached
		 * one EventUserStatus "Selected" before apiVersion 4.0 (include 4.0).
		 * It will make "Redeemed" user not be shown on winner list.
		 */
		if (isIncludeRedeem && eventUserStatus != null
				&& eventUserStatus.size() == 1
				&& EventUserStatus.Selected.equals(eventUserStatus.get(0))) {
			eventUserStatus.add(EventUserStatus.Redeemed);
		}
		
		if (curUserId != null) {
			userIds = eventUserDao.findUserIdsByEventIdWithCurUser(brandEventId, eventUserStatus, curUserId, blockLimit);
        	subscribeSet = subscribeDao.findIdBySubscriberAndSubscribees(
        			curUserId, SubscribeType.User, userIds.getResults().toArray(new Long[userIds.getResults().size()]));
        } else 
        	userIds = eventUserDao.findUserIdsByEventId(brandEventId, eventUserStatus, blockLimit);
		
		User curUser = null;
		if (!userIds.getResults().isEmpty() && userIds.getResults().get(0).equals(curUserId)) {
			userIds.getResults().remove(curUserId);
			curUser = userDao.findById(curUserId);
		}		
		List<User> userList = new ArrayList<User>();
		if (!userIds.getResults().isEmpty())
			userList.addAll(userDao.findByIds(userIds.getResults().toArray(new Long[userIds.getResults().size()])));
		if (curUser != null) {
			userList.add(0, curUser);
		}
		
        for (User user : userList) {
            String mapAsJson = "{}";
            if (user.hasKeyInAttr("userAttr")) {
            	String userAttr = user.getStringInAttr("userAttr");
            	if (userAttr.length() > 0) {
            		mapAsJson = userAttr;
            	}
            } else {
            	final Map<String, Object> attributes = new HashMap<String, Object>();
            	for (Attribute attr : user.getAttributeList()) {
            		attributes.put(attr.getAttrName(), attr.getAttrValue());
            	}
            	try {
            		mapAsJson = new ObjectMapper().writeValueAsString(attributes);
            	} catch (JsonProcessingException e) {
            	}
            }
            user.setAttribute(mapAsJson);

            user.setCurUserId(curUserId);
            if (subscribeSet != null)
        		user.setIsFollowed(subscribeSet.contains(user.getId()));
        	
        }
        
        results.put("results", userList);
        results.put("totalSize", userIds.getTotalSize());
		return json(results);
	}
}