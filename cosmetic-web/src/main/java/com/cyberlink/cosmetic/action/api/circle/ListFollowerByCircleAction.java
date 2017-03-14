package com.cyberlink.cosmetic.action.api.circle;

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
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.Circle.ListCicleView;
import com.cyberlink.cosmetic.modules.circle.model.Circle.UserCicleView;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/api/circle/list-follower-by-circle.action")
public class ListFollowerByCircleAction extends AbstractCircleAction {
	
	@SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
	
	@SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
	
	@SpringBean("user.UserDao")
	private UserDao userDao;
	
	private int offset = 0;
    private int limit = 10;
    private Long circleId;
    private Long circleCreatorId;
    private Long curUserId;
    
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Validate(required = true, on = "route")
	public void setCircleId(Long circleId) {
		this.circleId = circleId;
	}

    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    public void setCircleCreatorId(Long circleCreatorId) {
        this.circleCreatorId = circleCreatorId;
    }
    
	@DefaultHandler
	public Resolution route() {
	    BlockLimit blockLimit = new BlockLimit(offset, limit);
	    if(!circleDao.exists(circleId))
	        return new ErrorResolution(ErrorDef.InvalidCircleId);
	    
		Circle relatedCircle = circleDao.findById(circleId, false);
		if (relatedCircle == null)
			return null; //the circle is already deleted
		
	    Circle userCircle = getUserAccessibleCircle(relatedCircle, circleCreatorId, false);
	    if(userCircle == null)
	        return new ErrorResolution(ErrorDef.InvalidCircleId);
	    
	    PageResult<User> results = new PageResult<User>();
	    List<User> users = new ArrayList<User>();
	    // Handle circle follow
	    PageResult<User> circleResults = circleSubscribeDao.findByCircleId(userCircle.getId(), blockLimit);
	    // Handle user follow
	    List<Long> userSubscriberIds = subscribeDao.findBySubscribee(userCircle.getCreatorId(), SubscribeType.User);
	    users.addAll(circleResults.getResults());
	    
	    if (circleResults.getResults().size() < limit) {
	    	int userOffset = offset - circleResults.getTotalSize();
	    	if (userOffset < 0) {
	    		limit = limit + userOffset;
	    		userOffset = 0;
	    		
	    	}
	    	int from = userOffset;
	    	int end = userOffset + limit;
	    	if (end > userSubscriberIds.size())
	    		end = userSubscriberIds.size();
	    	users.addAll(userDao.findByIds(userSubscriberIds.subList(from, end).toArray(new Long[userSubscriberIds.subList(from, end).size()])));
	    }
	    results.setResults(users);
	    results.setTotalSize(results.getTotalSize() + userSubscriberIds.size());
	    
        if (curUserId != null) {
            Long [] subscribeeIds = new Long[results.getResults().size()];
            Map<Long, Integer> subscribeeIdxMap = new HashMap<Long, Integer>();
            for(int idx = 0; idx < results.getResults().size(); idx++) {
                subscribeeIds[idx] = results.getResults().get(idx).getId();
                results.getResults().get(idx).setCurUserId(curUserId);
                subscribeeIdxMap.put(subscribeeIds[idx], idx);
            }
            List<Subscribe> subscribes =  subscribeDao.findBySubscriberAndSubscribees(curUserId, SubscribeType.User, subscribeeIds);
            for(Subscribe s : subscribes) {
                results.getResults().get(subscribeeIdxMap.get(s.getSubscribeeId())).setIsFollowed(true);
            }
        }
	    
	    return json(results);
	    
		
		
	}
}
