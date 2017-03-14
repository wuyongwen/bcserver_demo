package com.cyberlink.cosmetic.action.api.post;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.result.MainPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.PostService;

@UrlBinding("/api/post/circle-in-post.action")
public class CircleInAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;
    
    @SpringBean("circle.circleDao")
    private CircleDao circleDao;
    
    private String title;
    private Long circleId;
    private Long postId;
    private PostType postType;
    private String countryCode;
    
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Validate(required = true, on = "route")
    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }
    
    @Validate(required = true, on = "route")
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }
    
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        if(!authenticate())
            return new ErrorResolution(authError); 
        
        Long userId = getCurrentUserId();
        PostApiResult <Post> result = postService.circleInPost(userId, countryCode, postId, circleId, title, postType);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        Circle circle = circleDao.findById(circleId);
        if (circle != null && !circle.getIsSecret())
	        notifyService.addNotifyByType(NotifyType.CircleInPost.toString(), 
	        			null, userId, postId, null);
        
        MainPostBaseWrapper pw = new MainPostBaseWrapper(result.getResult());
        return json(pw);
    }

}
