package com.cyberlink.cosmetic.action.api.post;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;

@UrlBinding("/api/post/report-inappropriate.action")
public class ReportPostAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    private String targetType = "Post";
    private Long targetId;
    private String reason;
        
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    @Validate(required = true, on = "route")
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    @Validate(required = true, on = "route")
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
    
    @Validate(required = true, on = "route")
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        if(!authenticate())
            return new ErrorResolution(authError); 
        
        Long userId = getCurrentUserId();
        PostApiResult <Boolean> result = null;
        switch(targetType)
        {
        case "Comment":
            result = commentService.reportComment(userId, targetId, reason);
            break;
        case "Post":
            result = postService.reportPost(userId, targetId, reason);
            break;
        default:
            {
                result = new PostApiResult<Boolean>();
                result.setErrorDef(ErrorDef.InvalidPostTargetType);
                break;
            }
        }
        
        if(result.success()) {
            /* Do not clean cache now */
            /*if(targetType.equals("Post") && Constants.getIsPostCacheView()) {
                SessionFactory sessionFactory = BeanLocator.getBean("core.sessionFactory");
                if(!sessionFactory.getCache().containsQuery("com.cyberlink.cosmetic.modules.post.model.Post.query.findNewPostView")) {
                    BlockLimit blockLimit = new BlockLimit(0, 1);
                    List<Long> resultList = new ArrayList<Long>();
                    List<String> locales = new ArrayList<String>();
                    locales.add("en_US");
                    postService.listPostByCircle_v3_1(null, null, locales, null, "Date", resultList, blockLimit);
                }
                    
                sessionFactory.getCache().evictQueryRegion("com.cyberlink.cosmetic.modules.post.model.Post.query.findNewPostView");
            }*/
            return success();
        }
        
        return new ErrorResolution(result.getErrorDef());
    }

}
