package com.cyberlink.cosmetic.action.api.post;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;

@UrlBinding("/api/post/delete-post.action")
public class DeletePostAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    private Long postId;
    
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    @Validate(required = true, on = "route")
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    public Long getPostId() {
        return this.postId;
    }
    
    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        if(!authenticate())
            return new ErrorResolution(authError); 
        
        Long userId = getCurrentUserId();
        PostApiResult <Boolean> result = postService.deletePost(userId, postId);
        if(result.success()) {
        	return success();
        }
        
        return new ErrorResolution(result.getErrorDef());
    }

}
