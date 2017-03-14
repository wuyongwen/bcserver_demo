package com.cyberlink.cosmetic.action.api.post;

import java.util.List;

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
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.MainPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.result.PostsWrapper;
import com.cyberlink.cosmetic.modules.post.result.SubPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.service.PostService;

@UrlBinding("/api/post/create-posts.action")
public class CreatePostsAction_v3 extends AbstractAction {
    
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;
    
    @SpringBean("circle.circleDao")
    private CircleDao circleDao;
    
    private String mainPost = "";
    private String locale;
    private String postSource;
    private AppName appName = AppName.YCL;
    private String countryCode;
    private List<String> subPosts;
    
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    @Validate(required = true, on = "route")
    public void setMainPost(String mainPost) {
        this.mainPost = mainPost;
    }
    
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public void setPostSource(String postSource) {
        this.postSource = postSource;
    }
    
    public void setAppName(AppName appName) {
        this.appName = appName;
    }
    
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    public void setSubPosts(List<String> subPosts) {
        this.subPosts = subPosts;
    }
    
    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        if (!authenticateByRedis())
            return new ErrorResolution(authError);
        
        Long userId = getCurrentUserId();
        PostApiResult <List<Post>> result = postService.createPosts(userId, locale, countryCode, mainPost, postSource, appName, null, subPosts, null);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        List<Post> resultList = result.getResult();        
        PostsWrapper r = new PostsWrapper();
        r.mainPost = new MainPostBaseWrapper(resultList.get(0));
        for(int idx = 1; idx < resultList.size(); idx++) {
            r.subPosts.add(new SubPostBaseWrapper(resultList.get(idx)));
        }
        if (resultList.get(0).getPostStatus() == PostStatus.Published) {
        	Long circleId  = resultList.get(0).getCircleId();
        	if (circleId != null) {
	        	Circle circle = circleDao.findById(circleId);
	        	if (circle != null && !circle.getIsSecret())
	        		notifyService.addFriendNotifyByType(NotifyType.AddPost.toString(), userId, resultList.get(0).getId(), null);
        	}
        }
        
        return json(r);
    }

}
