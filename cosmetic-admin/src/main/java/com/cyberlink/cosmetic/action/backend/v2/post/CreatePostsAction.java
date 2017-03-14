package com.cyberlink.cosmetic.action.backend.v2.post;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.result.MainPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.result.PostsWrapper;
import com.cyberlink.cosmetic.modules.post.result.SubPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.model.UserStatus;

@UrlBinding("/v2/post/create-posts.action")
public class CreatePostsAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;
    
    @SpringBean("circle.circleService")
    private CircleService circleService;
    
    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
    @SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
    
    private String mainPost = "";
    private List<String> subPosts; 
    private String postSource = "native_posting";
    private AppName appName = AppName.BACKEND_V2;
    private Long circleId;
    PageResult<Circle> circles = new PageResult<Circle>();
    private Long userId;
    private String title;
    
    public void setMainPost(String mainPost) {
        this.mainPost = mainPost;
    }
    
    public void setSubPosts(List<String> subPosts) {
        this.subPosts = subPosts;
    }
    
    public void setPostSource(String postSource) {
        this.postSource = postSource;
    }
    
    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }
    
    public Long getCircleId() {
        return this.circleId;
    }
    
    public PageResult<Circle> getCircles() {
        return this.circles;
    }
    
    public String getTitle() {
    	return this.title;
    }
    
    private void loadAvailableCircles() {
	    if(userId == null)
	        userId = getCurrentUserId();

        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit((pageLimit.getPageIndex() - 1 ) * pageLimit.getPageSize(), pageLimit.getPageSize());
        circles = circleService.listUserCircle(userId, true, getCurrentUserLocale(), true, blockLimit);
        List<Long> subcribedCircleIds = circleSubscribeDao.listSubcribeCircle(userId, circles.getResults());
        circles.setResults(circleAttributeDao.getCircleAttribute(circles.getResults(), userId, subcribedCircleIds));
    }
    

    
    @DefaultHandler
    public Resolution route() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
    	loadAvailableCircles();
        return forward();
    }
    
    public Resolution create() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
        
        Long userId = getCurrentUserId();
        PostApiResult <List<Post>> result = postService.createPosts(userId, getCurrentUserLocale(), null, mainPost, postSource, appName, null, subPosts, null);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        List<Post> resultList = result.getResult();        
        PostsWrapper r = new PostsWrapper();
        r.mainPost = new MainPostBaseWrapper(resultList.get(0));
        for(int idx = 1; idx < resultList.size(); idx++) {
            r.subPosts.add(new SubPostBaseWrapper(resultList.get(idx)));
        }
        
        return json("Done");
    }

}
