package com.cyberlink.cosmetic.action.api.post;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.dao.CommentDao;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetSubType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.AsyncPostUpdateService;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.LikeService.LikeServiceResult;
import com.cyberlink.cosmetic.modules.post.service.PostService;

@UrlBinding("/api/post/like.action")
public class LikeAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("post.PostDao")
    private PostDao postDao;
    
    @SpringBean("post.PostAttributeDao")
    private PostAttributeDao postAttributeDao;
    
    @SpringBean("post.asyncPostUpdateService")
    private AsyncPostUpdateService asyncPostUpdateService;
    
    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("post.CommentDao")
    private CommentDao commentDao;

    @SpringBean("post.LikeService")
    private LikeService likeService;

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
    
    public void setTargetSubType(TargetSubType targetSubType) {
        this.targetSubType = targetSubType;
    }
    
    public TargetType getTargetType() {
        return this.targetType;
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
    
    private Resolution doLike(Long userId) {
        PostApiResult <LikeServiceResult> result = likeService.likeTarget(userId, targetType, targetSubType, targetId);
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
                    asyncPostUpdateService.increaseUserAttr(userId, null, null, 1L, null);
                    break;
                }
                case HOW_TO: {
                    asyncPostUpdateService.increaseUserAttr(userId, 1L, null, null, null);
                    break;
                }
                default:
                    break;
                }
                break;
            }
            default:
                break;
        }
        return success();
    
    }
    
    @DefaultHandler
    public Resolution route() {
        RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
        if(!authenticate())
            return new ErrorResolution(authError); 
        
        Long userId = getCurrentUserId();
        return doLike(userId);
    }

}
