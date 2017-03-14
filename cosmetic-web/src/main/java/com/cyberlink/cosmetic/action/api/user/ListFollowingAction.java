package com.cyberlink.cosmetic.action.api.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/list-following.action")
public class ListFollowingAction extends AbstractAction{
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("user.userAttrDao")
    private UserAttrDao userAttrDao;
    
    private Long userId;
    private int offset = 0;
    private int limit = 10;
    private Long curUserId;
    private SubscribeType targetType;
    
    private Map<String, Object> getFollowingUser() {
        final Map<String, Object> results = new HashMap<String, Object>();
        
        Long totalFollowingCount = null;
        UserAttr attrObj = userAttrDao.findByUserId(userId);
        if (attrObj != null) {
        	totalFollowingCount = attrObj.getFollowingCount();
        }
        PageResult<Long> subscribeeIds = new PageResult<Long>();
        if (totalFollowingCount == null)
        	subscribeeIds = subscribeDao.findBySubscriberOrderByName(userId, targetType, new BlockLimit(offset, limit), true);
        else {
        	subscribeeIds = subscribeDao.findBySubscriberOrderByName(userId, targetType, new BlockLimit(offset, limit), false);
        	subscribeeIds.setTotalSize(totalFollowingCount.intValue());
        }
        List<User> userList = userDao.findByIdsWithOrder(subscribeeIds.getResults().toArray(new Long[subscribeeIds.getResults().size()]));
        
        Set<Long> subscribeSet = null;
        if (curUserId != null) {
            subscribeSet = subscribeDao.findIdBySubscriberAndSubscribees(
                    curUserId, targetType, subscribeeIds.getResults().toArray(new Long[subscribeeIds.getResults().size()]));
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
        results.put("totalSize", subscribeeIds.getTotalSize());
        return results;
    }
    
	@DefaultHandler
    public Resolution route() {		
        return json(getFollowingUser());
    }
    
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
    public Long getCurUserId() {
		return curUserId;
	}
    
	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

	public SubscribeType getTargetType() {
		return targetType;
	}

	public void setTargetType(SubscribeType targetType) {
		this.targetType = targetType;
	}
	
}
