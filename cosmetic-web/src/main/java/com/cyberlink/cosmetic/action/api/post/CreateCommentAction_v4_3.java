package com.cyberlink.cosmetic.action.api.post;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.result.CommentBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.CommentService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/v4.3/post/create-comment.action")
public class CreateCommentAction_v4_3 extends AbstractAction {

    @SpringBean("post.CommentService")
    protected CommentService commentService;
    
    @SpringBean("notify.NotifyService")
    protected NotifyService notifyService;
    
    protected String targetType;
    protected Long targetId;
    protected String comment;    

    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    @Validate(maxlength = 2048, on = "route")
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public String getTargetType() {
        return this.targetType;
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
		if (redirect != null)
			return redirect;

		if (!authenticate())
			return new ErrorResolution(authError);

		Long userId = getCurrentUserId();
		PostApiResult<Comment> resultC = commentService.createComment(userId,
				targetType, targetId, comment, null);
		if (!resultC.success())
			return new ErrorResolution(resultC.getErrorDef());

		Comment comment = resultC.getResult();
		if (comment != null) {
			if(targetType.equals(PostTargetType.POST)){
				notifyService.addNotifyByType(NotifyType.CommentPost.toString(),
						null, getSession().getUserId(), targetId, getComment());
			} else if (targetType.equals(PostTargetType.COMMENT)) {
				if (Constants.enableReplyCommentNotify())
					notifyService.addNotifyByType(NotifyType.ReplyComment.toString(), 
							targetId, getSession().getUserId(), comment.getId(), getComment());
			}
			CommentBaseWrapper bcw = new CommentBaseWrapper(comment);
			return json(bcw);
		}

		return new ErrorResolution(ErrorDef.UnknownPostError);
	}
}