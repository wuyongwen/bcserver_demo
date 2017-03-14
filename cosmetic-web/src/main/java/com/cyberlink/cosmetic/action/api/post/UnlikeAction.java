package com.cyberlink.cosmetic.action.api.post;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.lang3.StringUtils;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.event.post.CommentUnlikeEvent;
import com.cyberlink.cosmetic.event.post.PostUnlikeEvent;
import com.cyberlink.cosmetic.modules.post.dao.CommentDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetSubType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.AsyncPostUpdateService;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.LikeService.LikeServiceResult;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.event.UserBadgeEvent;

@UrlBinding("/api/post/unlike.action")
public class UnlikeAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("post.PostDao")
    private PostDao postDao;
    
    @SpringBean("post.asyncPostUpdateService")
    private AsyncPostUpdateService asyncPostUpdateService;
    
    @SpringBean("post.CommentService")
    private CommentService commentService;

    @SpringBean("post.CommentDao")
    private CommentDao commentDao;

    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    private TargetType targetType;
    private TargetSubType targetSubType = TargetSubType.YCL_LOOK;
    private Long targetId;
    
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }
    
    public TargetType getTargetType() {
        return this.targetType;
    }

    public void setTargetSubType(TargetSubType targetSubType) {
        this.targetSubType = targetSubType;
    }
    
    public TargetSubType getTargetSubType() {
        return this.targetSubType;
    }
    
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
    
    public Long getTargetId() {
        return this.targetId;
    }
    
    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        if(!authenticate())
            return new ErrorResolution(authError); 
        
        Long userId = getCurrentUserId();

        PostApiResult <LikeServiceResult> result = likeService.unlikeTarget(userId, targetType, targetId); 
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        if(result.getResult() == LikeServiceResult.LIKE_REL_FAILED)
            return new ErrorResolution(ErrorDef.UnknownPostError);
        
        if(result.getResult() == LikeServiceResult.LIKE_REL_NOT_AFFECTED)
            return success();
        
        switch(targetType) {
        case Post: {
            switch(targetSubType) {
            case YCL_LOOK: {
                asyncPostUpdateService.decreaseUserAttr(userId, null, null, 1L, null);
                break;
            }
            case HOW_TO: {
                asyncPostUpdateService.decreaseUserAttr(userId, 1L, null, null, null);
                break;
            }
            default:
                break;
            }
            publishDurableEvent(new PostUnlikeEvent(targetId, userId));
            final Post p = postDao.findById(targetId);
            publishDurableEvent(UserBadgeEvent.CreateLikeEvent(targetId, p.getCreatorId(), -1));
            break;
        }
        case Comment: {
            final Comment c = commentDao.findById(targetId);
            publishDurableEvent(new CommentUnlikeEvent(userId,
                    c.getRefId(), targetId));
            break;
        }
        default:
            break;
        }
        return success();
    }

}
