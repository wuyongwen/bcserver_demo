package com.cyberlink.cosmetic.action.api.post;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.result.CommentBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;

@UrlBinding("/api/post/update-comment.action")
public class UpdateCommentAction_v4_3 extends AbstractAction {

    @SpringBean("post.CommentService")
    protected CommentService commentService;

    @SpringBean("user.SessionDao")
    protected SessionDao sessionDao;
    
    protected Long commentId;
    protected String comment;
    
    @Validate(required = true, on = "route")
    public void setToken(String token) {
        super.setToken(token);
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getComment() {
        return this.comment;
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
		if (redirect != null)
			return redirect;

		if (!authenticate())
			return new ErrorResolution(authError);

		Long userId = getCurrentUserId();
		PostApiResult<Comment> result = commentService.updateComment(userId,
				commentId, comment, null);
		if (!result.success())
			return new ErrorResolution(result.getErrorDef());

		CommentBaseWrapper bcw = new CommentBaseWrapper(result.getResult());
		return json(bcw);
	}
}