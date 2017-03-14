package com.cyberlink.cosmetic.action.api.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.Circle.UserCicleView;
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

@UrlBinding("/api/v3.0/user/list-following.action")
public class ListFollowingAction_v3 extends AbstractAction{
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;

    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
    @SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("user.userAttrDao")
    private UserAttrDao userAttrDao;
    
    private Long userId;
    private int offset = 0;
    private int limit = 10;
    private Long curUserId;
    private String targetType = "User";//"Circle"; "User"; "All"
    
    private Map<String, Object> getFollowingUser() {
        final Map<String, Object> results = new HashMap<String, Object>();
        
        Long totalFollowingCount = null;
        UserAttr attrObj = userAttrDao.findByUserId(userId);
        if (attrObj != null) {
        	totalFollowingCount = attrObj.getFollowingCount();
        }
        PageResult<Long> subscribeeIds = new PageResult<Long>();
        if (totalFollowingCount == null)
        	subscribeeIds = subscribeDao.findBySubscriberOrderByName(userId, SubscribeType.User, new BlockLimit(offset, limit), true);
        else {
        	subscribeeIds = subscribeDao.findBySubscriberOrderByName(userId, SubscribeType.User, new BlockLimit(offset, limit), false);
        	subscribeeIds.setTotalSize(totalFollowingCount.intValue());
        }
        
        Set<Long> subscribeSet = null;
        if (curUserId != null) {
            subscribeSet = subscribeDao.findIdBySubscriberAndSubscribees(
                    curUserId, SubscribeType.User, subscribeeIds.getResults().toArray(new Long[subscribeeIds.getResults().size()]));
        }

        List<User> userList = userDao.findByIdsWithOrder(subscribeeIds.getResults().toArray(new Long[subscribeeIds.getResults().size()]));
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
    
    private Map<String, Object> getFollowingCircle() {
        final Map<String, Object> results = new HashMap<String, Object>();
        PageResult<Circle> subscribeCircles = circleSubscribeDao.findByUserId(userId, new BlockLimit(offset, limit));
        List<Long> subcribedCircleIds = circleSubscribeDao.listSubcribeCircle(curUserId, subscribeCircles.getResults());
        List<Circle> circleList = circleAttributeDao.getCircleAttribute(subscribeCircles.getResults(), curUserId, subcribedCircleIds);
        results.put("results", circleList);
        results.put("totalSize", subscribeCircles.getTotalSize());
        return results;
    }
    
	@DefaultHandler
    public Resolution route() {
        Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();
        switch(targetType) {
        case "All":
        {
            resultMap.put("circles", getFollowingCircle());
            resultMap.put("users", getFollowingUser());
            break;
        }
        case "Circle":
        {
            resultMap.put("circles", getFollowingCircle());
            break;
        }
        case "User":
            default:
            {
                resultMap.put("users", getFollowingUser());
                break;
            }
        }
        return json(resultMap, UserCicleView.class);
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
	public void setTargetType(String targetType) {
	    this.targetType = targetType;
	}
}
