package com.cyberlink.cosmetic.action.api.product;

import com.cyberlink.cosmetic.modules.product.model.ProductComment;

import net.sourceforge.stripes.action.DefaultHandler;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/api/prod/UpdateComment.action")
public class UpdateCommentAction extends CreateProductCommentAction {
	static final Integer commentLength = Integer.valueOf(2048) ;
	private Long commentId;
	private String apiVersion = "" ;
	
	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
		if (!authenticate()) {
			return new ErrorResolution(authError);
		}
		if (comment == null) {
			comment = "";
		}
		if ( comment.length() > commentLength ) {
			return new ErrorResolution(ErrorDef.InvalidProdComment);
		}
		if(apiVersion != null && apiVersion.length() != 0 && !apiVersion.equals("1.0")){
			ProductComment currentComment = commentDao.findByProductIdAndUserId(getSession().getUserId(), productId) ;
			if( currentComment == null ){
				return new ErrorResolution(ErrorDef.InvalidProductId);
			}
			currentComment.setIsDeleted(Boolean.FALSE);
			currentComment.setComment(comment);
			currentComment.setRating(rating);
			currentComment = commentDao.update(currentComment);
			return json(currentComment);
		}
		if (!commentDao.exists(commentId)) {
			return new ErrorResolution(ErrorDef.InvalidCommentId);
		}		
		ProductComment prodComment = commentDao.findById(commentId);
		if (prodComment.getUser().getId().longValue() != getSession().getUserId().longValue()) {
			return new ErrorResolution(ErrorDef.Forbidden);
		}
		if (prodComment.getIsDeleted()) {
			return new ErrorResolution(ErrorDef.InvalidCommentId);
		}
		
		//Float oriRating = prodComment.getRating();
		prodComment.setIsDeleted(Boolean.FALSE);
		prodComment.setComment(comment);
		//updateRatingValues(prodComment.getProduct().getId(), rating, oriRating, false);
		prodComment.setRating(rating);
		prodComment = commentDao.update(prodComment);
		return json(prodComment);
	}
	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	public String getApiVersion() {
		return apiVersion;
	}
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

}
