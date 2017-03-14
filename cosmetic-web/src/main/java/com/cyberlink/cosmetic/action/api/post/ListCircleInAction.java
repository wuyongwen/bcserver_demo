package com.cyberlink.cosmetic.action.api.post;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.Circle.UserCicleView;
import com.cyberlink.cosmetic.modules.post.result.LikeDetailWrapper;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/api/post/list-circle-in.action")
public class ListCircleInAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
	@SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
	
	@SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
	
    private Long postId;
    private Integer offset = 0;
    private Integer limit = 10;
    private Long curUserId = null;
	private String responseType = "User"; // User, Circle

    @Validate(required = true, on = "route")
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    public Long getPostId() {
        return this.postId;
    }
    
    @Validate(minvalue = 0, required = false, on = "route")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    @Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
    
    @DefaultHandler
    public Resolution route() {
    	BlockLimit blockLimit = new BlockLimit(offset, limit);
    	
	    switch(responseType)
	    {
		case "Circle": {
			final PageResult<Circle> circles = postService.listCircleInCircle(postId, blockLimit);
			
			if (curUserId != null) {
				Set<Long> creatorIds = new HashSet<Long>();
				for (Circle circle : circles.getResults()) {
					creatorIds.add(circle.getCreatorId());
				}
				
				List<Subscribe> subscribeList = subscribeDao.findBySubscriberAndSubscribees(curUserId, SubscribeType.User, creatorIds.toArray(new Long[creatorIds.size()]));
				List<Long> subcribedCircleIds = null;
				if (subscribeList != null && subscribeList.size() > 0) {
					subcribedCircleIds = new ArrayList<Long>();
					for (Circle c : circles.getResults()) {
						subcribedCircleIds.add(c.getId());
					}
				} else {
					subcribedCircleIds = circleSubscribeDao.listSubcribeCircle(curUserId, circles.getResults());
				}
				circles.setResults(circleAttributeDao.getCircleAttribute(circles.getResults(), curUserId, subcribedCircleIds));
	    	}
			
			return json(circles, UserCicleView.class);
	    }
	    case "User":
        default:
            final PageResult<User> result = postService.listCircleInUser(postId, blockLimit);        
            final PageResult<LikeDetailWrapper> r = new PageResult<LikeDetailWrapper>();
            r.setTotalSize(result.getTotalSize());
            
            if(curUserId != null) {
                Set<Long> creatorIds = new HashSet<Long>();
                for(User u : result.getResults()) {
                    creatorIds.add(u.getId());
                }
                Set<Long> subcribeeIds = subscribeDao.findIdBySubscriberAndSubscribees(curUserId, SubscribeType.User, creatorIds.toArray(new Long[creatorIds.size()]));
                for(int idx = 0; idx < result.getResults().size(); idx++) {
                    User likeUser = result.getResults().get(idx);
                    likeUser.setCurUserId(curUserId);
                    LikeDetailWrapper ldw = new LikeDetailWrapper(likeUser);
                    if(subcribeeIds.contains(likeUser.getId()))
                        ldw.setIsFollowed(true);
                    r.add(ldw);
                }
            }
            else{
                for(int idx = 0; idx < result.getResults().size(); idx++) {
                    r.add(new LikeDetailWrapper(result.getResults().get(idx)));
                }
            }
            
            return json(r);
	    }
    }
}
