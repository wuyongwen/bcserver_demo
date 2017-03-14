package com.cyberlink.cosmetic.action.api.user;


import java.util.ArrayList;
import java.util.List;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/unfollow.action")
public class UnFollowAction extends AbstractAction{
	@SpringBean("user.UserDao")
    private UserDao userDao;
	
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    @SpringBean("user.userAttrDao")
    private UserAttrDao userAttrDao;
	
    private Long userId;
    private SubscribeType targetType = null;

    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
    	if (!authenticateByRedis())
    		return new ErrorResolution(authError);
    	
    	User subscribee = userDao.findById(userId);
    	targetType = SubscribeType.getType(subscribee.getUserType());
        List<Subscribe> subscribeList = subscribeDao.findBySubscriberAndSubscribees(getSession().getUserId(), targetType, userId);
        List<Long> deleteList = new ArrayList<Long>();
        for (Subscribe subscribe : subscribeList) {
        	deleteList.add(subscribe.getId());
            userAttrDao.decreaseNonNullValue(getSession().getUserId(), "FOLLOWING_COUNT");
            userAttrDao.decreaseNonNullValue(subscribe.getSubscribeeId(), "FOLLOWER_COUNT");
            if (SubscribeType.LiveBrand.equals(targetType)) {
            	userAttrDao.decreaseNonNullValue(getSession().getUserId(), "LIVE_BRAND_COUNT");
            }
        }
        
        subscribeDao.batchDelete(deleteList);
        return success();
    }

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
    
}
