package com.cyberlink.cosmetic.action.backend.v2.post;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.PostService;

@UrlBinding("/v2/post/view-post.action")
public class ViewPostAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("circle.circleService")
    private CircleService circleService;
    
    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
    @SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;

    private Long postId;
    private List<Post> posts;
    private PostStatus postStatus;
    
    private final int maxSubPostCount = 100;
    
    public void setPostId(Long postId) {
    	this.postId = postId;
    }
    
    public Long getPostId() {
    	return this.postId;
    }
    
    public List<Post> getPosts() {
    	return this.posts;
    }
    
    public void setPostStatus(PostStatus postStatus) {
    	this.postStatus = postStatus;
    }
    
    @DefaultHandler
    public Resolution route() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
        
        BlockLimit blockLimit = new BlockLimit(0, maxSubPostCount);
        final PostApiResult <PageResult<Post>> result = postService.listAllRelatedPost(postId, blockLimit);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        posts = result.getResult().getResults();
        
        return forward();
    }
    
    public Resolution updateStatus() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
	    
        PostApiResult <Post> result = postService.updatePost(getCurrentUserId(), getCurrentUserLocale(), postId, null, null, null, null, null, null, null, postStatus, null, null, null, null);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        Post p = result.getResult();
        if(p != null) {
            return new RedirectResolution(ListPostByUserAction.class, "route");
        }
        
        return new ErrorResolution(ErrorDef.UnknownPostError);
    }

}
