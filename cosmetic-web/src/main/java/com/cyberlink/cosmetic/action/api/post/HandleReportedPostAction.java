package com.cyberlink.cosmetic.action.api.post;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/api/post/handle-reported-inappropriate.action")
public class HandleReportedPostAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;

    private Long targetId;
    
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }

    @Validate(required = true, on = "route")
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        if(!authenticate())
            return new ErrorResolution(authError); 
        
        Long userId = getCurrentUserId();
        User reviewer = userDao.findById(userId);
        Throwable postError = postService.handleReportPost(targetId, reviewer, "Published", "");
        if(postError.getMessage().contains("Succeed to handle the post")) {
        	if(postError.getMessage().contains("failed to report contest post"))
        		return new StreamingResolution("text/html", postError.getMessage());
        	return success();
        }
        return new ErrorResolution(ErrorDef.InvalidPostTargetType);
    }
}
