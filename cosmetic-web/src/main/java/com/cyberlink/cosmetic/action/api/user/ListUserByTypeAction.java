package com.cyberlink.cosmetic.action.api.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/list-user-byType.action")
public class ListUserByTypeAction extends AbstractAction{
    @SpringBean("user.UserDao")
    protected UserDao userDao;

    @SpringBean("user.SubscribeDao")
    protected SubscribeDao subscribeDao;
    
    protected List<String> locale;
    protected Long offset = Long.valueOf(0);
    protected Long limit = Long.valueOf(10);
    protected List<UserType> userType;
    protected Long curUserId;
    
	@DefaultHandler
    public Resolution route() {
        final Map<String, Object> results = new HashMap<String, Object>();
        PageResult<User> pageResult;
        pageResult = userDao.findByUserType(userType, locale, offset, limit);
        setUserArribute(pageResult);
        
        results.put("results", pageResult.getResults());
        results.put("totalSize", pageResult.getTotalSize());
        return json(results);
    }
	
	protected void setUserArribute(PageResult<User> pageResult) {
		List<Long> userIdList = new ArrayList<Long>();
		for (User user : pageResult.getResults()) {
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
            userIdList.add(user.getId());
        }
		
		Map<Long, Long> followerCountMap = subscribeDao.getFollowerCountByUserIds(userIdList, null);
        
        Set<Long> subscribeSet = null;
        if (curUserId != null) {
        	subscribeSet = subscribeDao.findIdBySubscriberAndSubscribees(curUserId, null, userIdList.toArray(new Long[userIdList.size()]));        	
        }
    	for (User user : pageResult.getResults()) {        
    		if (subscribeSet != null) {
        		user.setIsFollowed(subscribeSet.contains(user.getId()));
    		}
        	if (followerCountMap.containsKey(user.getId())) {
        		user.setFollowerCount(followerCountMap.get(user.getId()));
        	}
        }
	}
    
    public List<UserType> getUserType() {
		return userType;
	}

	public void setUserType(List<UserType> userType) {
		this.userType = userType;
	}

	public List<String> getLocale() {
		return locale;
	}

	public void setLocale(List<String> locale) {
		this.locale = locale;
	}

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

}
