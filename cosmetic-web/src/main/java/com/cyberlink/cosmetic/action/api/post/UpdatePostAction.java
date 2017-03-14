package com.cyberlink.cosmetic.action.api.post;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.MainPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;

@UrlBinding("/api/post/update-post.action")
public class UpdatePostAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    private Long postId;
    private String title;
    private String content;
    private List<Long> circleIds;
    private String tags;
    private String attachments;
    private String locale;
    private PostStatus postStatus = null;
    
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    @Validate(required = true, on = "route")
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }
    
    public void setCircleIds(List<Long> circleIds) {
        this.circleIds = circleIds;
    }
    
    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }
    
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        if(!authenticate())
            return new ErrorResolution(authError); 
        
        Long userId = getCurrentUserId();
        PostApiResult <Post> result = postService.updatePost(userId, locale, postId, null, null, title, content, circleIds, attachments, tags, postStatus, null, null, null, null);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        Post p = result.getResult();
        if(p != null) {
            MainPostBaseWrapper pw = new MainPostBaseWrapper(p);
            return json(pw);
        }
        
        return new ErrorResolution(ErrorDef.UnknownPostError);
    }

}
