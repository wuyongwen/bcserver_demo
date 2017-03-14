package com.cyberlink.cosmetic.action.api.user;

import java.util.List;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/follow.action")
public class FollowAction extends AbstractAction {
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    @SpringBean("user.userAttrDao")
    private UserAttrDao userAttrDao;
    
    private List<Long> userId;
    private SubscribeType targetType;

    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
    	if (!authenticateByRedis())
    		return new ErrorResolution(authError);
        
    	if (userId == null || userId.isEmpty())
    		return new ErrorResolution(ErrorDef.InvalidUserId);
    	
    	List<User> userList = userDao.findByIds(userId.toArray(new Long[userId.size()]));
    	for (User subscribee : userList) {
    		if (getSession().getUserId() == subscribee.getId().longValue())
    			continue;
    		
    		targetType = SubscribeType.getType(subscribee.getUserType());
			Subscribe subscribe = subscribeDao.findBySubscriberAndSubscribee(getSession().getUserId(), subscribee.getId(), targetType);
            Boolean bIncrease = Boolean.FALSE;
            if (subscribe == null) {
            	subscribe = new Subscribe();
                subscribe.setSubscriberId(getSession().getUserId());
                subscribe.setSubscribeeId(subscribee.getId());   
                subscribe.setSubscribeType(targetType);
                String subscriberName = userDao.findById(getSession().getUserId()).getDisplayName();
                String subscribeeName = subscribee.getDisplayName();
                subscribe.setSubscriberName(subscriberName);
                subscribe.setSubscribeeName(subscribeeName);
                bIncrease = Boolean.TRUE;
                subscribeDao.create(subscribe);
            }
            
            if (bIncrease) {
            	userAttrDao.increaseNonNullValue(getSession().getUserId(), "FOLLOWING_COUNT");
            	userAttrDao.increaseNonNullValue(subscribee.getId(), "FOLLOWER_COUNT");
            	if (SubscribeType.LiveBrand.equals(targetType)) {
            		userAttrDao.increaseNonNullValue(getSession().getUserId(), "LIVE_BRAND_COUNT");
            	}
            }
    	}        
        return success();
    }

	public List<Long> getUserId() {
		return userId;
	}

	public void setUserId(List<Long> userId) {
		this.userId = userId;
	}
    
}
