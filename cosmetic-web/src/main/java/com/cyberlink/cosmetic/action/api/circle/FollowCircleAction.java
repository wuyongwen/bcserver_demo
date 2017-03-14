package com.cyberlink.cosmetic.action.api.circle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.event.circle.CircleFollowEvent;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute.CircleAttrType;
import com.cyberlink.cosmetic.modules.circle.model.CircleSubscribe;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/api/circle/follow-circle.action")
public class FollowCircleAction extends AbstractCircleAction {
    
    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
    @SpringBean("circle.circleDao")
    private CircleDao circleDao;
    
    @SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;

    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;
    
    @SpringBean("user.userAttrDao")
    private UserAttrDao userAttrDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    private Long circleId;
    private Long circleCreatorId;
    
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    @Validate(required = true, on = "route")
    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }
    
    @Validate(required = true, on = "route")
    public void setCircleCreatorId(Long circleCreatorId) {
        this.circleCreatorId = circleCreatorId;
    }
    
    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
    	if (!authenticate())
    		return new ErrorResolution(authError); 
        
    	if(!circleDao.exists(circleId))
    	    return new ErrorResolution(ErrorDef.InvalidCircleId);
    	
		Circle relatedCircle = circleDao.findById(circleId, false);
		if (relatedCircle == null)
			return null; //the circle is already deleted
		
    	Circle userCircle = getUserAccessibleCircle(relatedCircle, circleCreatorId, true);
    	if(userCircle == null)
    	    return new ErrorResolution(ErrorDef.InvalidCircleId);
    	
    	if(getCurrentUserId() != null && circleCreatorId != null) {
    	    if(circleCreatorId.equals(getCurrentUserId()))
    	        return new ErrorResolution(ErrorDef.InvalidCircleNotAuth);
    	}
    	
    	List<Circle> circlesToCheck = new ArrayList<Circle>();
    	circlesToCheck.add(userCircle);
    	List<Circle> chekedCircles = circleAttributeDao.getCircleAttribute(circlesToCheck, getCurrentUserId(), null);
    	if(chekedCircles.size() <= 0)
    	    return new ErrorResolution(ErrorDef.UnknownCircleError);
    	if(chekedCircles.get(0).getPostCount() <= 0)
    	    return new ErrorResolution(ErrorDef.InvalidCircleNotAuth);
    	
    	circleId = userCircle.getId();
    	List<CircleSubscribe> circleSubscribes = circleSubscribeDao.getCircleSubscribe(getCurrentUserId(), circleId);
    	CircleSubscribe createdCircleSubcribe = null;
    	Boolean isAffected = false;
        if (circleSubscribes != null && circleSubscribes.size() > 0) {
            for(CircleSubscribe cs : circleSubscribes) {
                if(cs.getIsDeleted())
                    continue;
                if(createdCircleSubcribe == null) {
                    createdCircleSubcribe = cs;
                }
                else {
                    cs.setIsDeleted(Boolean.TRUE);
                    circleSubscribeDao.update(cs);
                }
            }
        } 
        
        if(createdCircleSubcribe == null) {
            CircleSubscribe cs = new CircleSubscribe();
            cs.setCircleId(circleId);
            cs.setUserId(getCurrentUserId());    
            cs.setShardId(getCurrentUserId());
            createdCircleSubcribe = circleSubscribeDao.create(cs);
            isAffected = true;
        }

        if(createdCircleSubcribe == null)
            return new ErrorResolution(ErrorDef.UnknownCircleError);
        
        if(isAffected) {
            Circle followedCircle = createdCircleSubcribe.getCircle();
            if(followedCircle == null)
                followedCircle = circleDao.findById(createdCircleSubcribe.getCircleId());
            notifyService.addNotifyByType(NotifyType.FollowCircle.toString(), followedCircle.getCreatorId(), 
            		getCurrentUserId(), followedCircle.getId(), followedCircle.getCircleName(), followedCircle.getIconUrl());
            notifyService.addFriendNotifyByType(NotifyType.FriendFollowCircle.toString(), getCurrentUserId(),
            		followedCircle.getId(), followedCircle.getCircleName(), followedCircle.getIconUrl());
            circleAttributeDao.createOrUpdateCircleAttr(followedCircle, CircleAttrType.FollowerCount, "1", true);
            Subscribe subs = subscribeDao.findBySubscriberAndSubscribee(getCurrentUserId(), circleCreatorId, SubscribeType.Circle);
            if(subs == null) {
                Subscribe newUserCircleSubs = new Subscribe();
                newUserCircleSubs.setShardId(getCurrentUserId());
                newUserCircleSubs.setSubscriberId(getCurrentUserId());
                newUserCircleSubs.setSubscribeeId(circleCreatorId);
                newUserCircleSubs.setSubscribeType(SubscribeType.Circle);
                String subscriberName = userDao.findById(getCurrentUserId()).getDisplayName();
                String subscribeeName = userDao.findById(circleCreatorId).getDisplayName();
                newUserCircleSubs.setSubscriberName(subscriberName);
                newUserCircleSubs.setSubscribeeName(subscribeeName);
                subscribeDao.create(newUserCircleSubs);
                userAttrDao.increaseNonNullValue(getCurrentUserId(), "FOLLOWING_COUNT");
                userAttrDao.increaseNonNullValue(circleCreatorId, "FOLLOWER_COUNT");
            }
        }
        publishDurableEvent(new CircleFollowEvent(getSession().getUserId(), circleId,
                circleCreatorId, new Date()));
        return success();
    }
    
}
