package com.cyberlink.cosmetic.action.api.post;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;

@UrlBinding("/api/post/delete-comment.action")
public class DeleteCommentAction extends AbstractAction {
    @SpringBean("post.CommentService")
    private CommentService commentService;

    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;

    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;
        
    private Long commentId;
    
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
    
    public Long getCommentId() {
        return this.commentId;
    }
    
    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        if(!authenticate())
            return new ErrorResolution(authError); 
        
        Long userId = getCurrentUserId();
        PostApiResult <Boolean> result = commentService.deleteComment(userId, commentId); 
        if(result.success()) {
        	notifyService.updateByDeleteComment(commentId);
            return success();
        }
        
        return new ErrorResolution(result.getErrorDef());
    }

}
