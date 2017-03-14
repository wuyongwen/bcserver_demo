package com.cyberlink.cosmetic.action.api.circle;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.event.circle.CircleUnfollowEvent;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute;
import com.cyberlink.cosmetic.modules.circle.model.CircleSubscribe;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute.CircleAttrType;
import com.cyberlink.cosmetic.modules.post.service.FeedService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/api/circle/unfollow-circle.action")
public class UnfollowCircleAction extends AbstractCircleAction {
    
    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
    @SpringBean("circle.circleDao")
    private CircleDao circleDao;
    
    @SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
    
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    @SpringBean("post.feedService")
    private FeedService feedService;
    
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
    
    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }
    
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
        
        Boolean isAffected = false;
        List<Subscribe> subscribeList = subscribeDao.findBySubscriberAndSubscribees(getCurrentUserId(), SubscribeType.User, userCircle.getCreatorId());
        List<Long> deleteList = new ArrayList<Long>();
    	if(subscribeList != null && subscribeList.size() > 0) {
    	    isAffected = true;
            for (Subscribe subscribe : subscribeList) {
            	deleteList.add(subscribe.getId());
            }
            
            List<Long> userIds = new ArrayList<Long>();
            userIds.add(userCircle.getCreatorId());
            int offset = 0;
            int limit = 100;
            int circleFollowCount = 0;
            do {
                PageResult<Circle> circles = circleDao.findByUserIds(userIds, true, new BlockLimit(offset, limit));
                if(circles.getResults().size() <= 0)
                    break;
                List<Long> subcribeCircles = circleSubscribeDao.listSubcribeCircle(getCurrentUserId(), circles.getResults());
                List<Circle> chekedCircles = circleAttributeDao.getCircleAttribute(circles.getResults(), getCurrentUserId(), null);
                
                for(Circle c : chekedCircles) {
                    if(c.getIsDeleted() || c.getPostCount() <= 0)
                        continue;
                    circleAttributeDao.createOrUpdateCircleAttr(c, CircleAttrType.FollowerCount, "-1", false);
                    if(c.getId().equals(userCircle.getId()))
                    	continue;
                    if(subcribeCircles.contains(c.getId()))
                        continue;
                    CircleSubscribe cs = new CircleSubscribe();
                    cs.setShardId(getCurrentUserId());
                    cs.setCircleId(c.getId());
                    cs.setUserId(getCurrentUserId());
                    circleSubscribeDao.create(cs);
                    circleAttributeDao.createOrUpdateCircleAttr(c, CircleAttrType.FollowerCount, "1", true);
                    circleFollowCount++;
                }
                
                offset += limit;
                if(offset > circles.getTotalSize())
                    break;
            } while(true);
            
            if(circleFollowCount > 0) {
                Subscribe subs = subscribeDao.findBySubscriberAndSubscribee(getCurrentUserId(), userCircle.getCreatorId(), SubscribeType.Circle);
                if(subs == null) {
                    Subscribe newUserCircleSubs = new Subscribe();
                    newUserCircleSubs.setShardId(getCurrentUserId());
                    newUserCircleSubs.setSubscriberId(getCurrentUserId());
                    newUserCircleSubs.setSubscribeeId(userCircle.getCreatorId());
                    newUserCircleSubs.setSubscribeType(SubscribeType.Circle);
                    String subscriberName = userDao.findById(getCurrentUserId()).getDisplayName();
                    String subscribeeName = userDao.findById(userCircle.getCreatorId()).getDisplayName();
                    newUserCircleSubs.setSubscriberName(subscriberName);
                    newUserCircleSubs.setSubscribeeName(subscribeeName);
                    subscribeDao.create(newUserCircleSubs);
                }
            } else {
            	userAttrDao.decreaseNonNullValue(getCurrentUserId(), "FOLLOWING_COUNT");
                userAttrDao.decreaseNonNullValue(userCircle.getCreatorId(), "FOLLOWER_COUNT");
            }
    	}
    	else {
            List<CircleSubscribe> circleSubscribes = circleSubscribeDao.getCircleSubscribe(getCurrentUserId(), userCircle.getId());
            if (circleSubscribes != null) {
                for(CircleSubscribe circleSubscribe : circleSubscribes) {
                    if(!circleSubscribe.getIsDeleted()) {
                    	isAffected = true;
                        circleSubscribe.setIsDeleted(true);
                        if(circleSubscribeDao.update(circleSubscribe) == null)
                        	return new ErrorResolution(ErrorDef.UnknownCircleError);
                    }
                }
                if(isAffected)
                    circleAttributeDao.createOrUpdateCircleAttr(userCircle, CircleAttrType.FollowerCount, "-1", false);
            }
    	}

    	if(isAffected) {
            Long circleCreatorId = userCircle.getCreatorId();
            if(circleSubscribeDao.getSubscribeCountByCircleCreator(getCurrentUserId(), circleCreatorId) <= 0) {
                Subscribe subs = subscribeDao.findBySubscriberAndSubscribee(getCurrentUserId(), circleCreatorId, SubscribeType.Circle);
                if(subs != null) {
                	deleteList.add(subs.getId());
                    userAttrDao.decreaseNonNullValue(getCurrentUserId(), "FOLLOWING_COUNT");
                    userAttrDao.decreaseNonNullValue(circleCreatorId, "FOLLOWER_COUNT");
                }
            }

    	}
    	
    	subscribeDao.batchDelete(deleteList);
    	
        publishDurableEvent(new CircleUnfollowEvent(getSession().getUserId(),
                circleId, userCircle.getCreatorId()));
        return success();
    }
    
}
