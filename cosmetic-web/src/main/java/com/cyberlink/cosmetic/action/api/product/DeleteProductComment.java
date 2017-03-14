package com.cyberlink.cosmetic.action.api.product;

import java.util.List;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ProductCommentAttr;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;

import net.sourceforge.stripes.action.DefaultHandler;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/prod/DeleteComment.action")
public class DeleteProductComment extends AbstractAction{
	@SpringBean("product.ProductCommentDao")
	private ProductCommentDao commentDao;

	@SpringBean("user.AttributeDao")
    private AttributeDao attributeDao;
	
	private Long commentId;
	
	@DefaultHandler
	public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
		if (!authenticate()) {
			return new ErrorResolution(authError);
		}
		if (!commentDao.exists(commentId)) {
			return new ErrorResolution(ErrorDef.InvalidProdCommentId);
		}
		ProductComment comment = commentDao.findById(commentId);
		if (comment.getUser().getId().longValue() != getSession().getUserId().longValue()) {
			return new ErrorResolution(ErrorDef.Forbidden);
		}  
		if (comment.getIsDeleted()) {
			return success();
		}
		/*
		else if (comment.getRating() > 0) {
		
			deleteRatingValues(comment.getProduct().getId(), comment.getRating());
		}
		*/
		comment.setIsDeleted(Boolean.TRUE);
		commentDao.update(comment);
		return success();
	}
	
	public void deleteRatingValues(Long productId, Float oriRating) {
		List<Attribute> attrList = attributeDao.findByRefIdAndNames(AttributeType.Product, productId, 
				ProductCommentAttr.RATING_COUNT, ProductCommentAttr.RATING_VALUE);
		Attribute countAttr = null;
		Attribute valueAttr = null;
		Long count = Long.valueOf(0);
		Float value = Float.valueOf(0);

		for (Attribute attr : attrList) {
			if (attr.getAttrName().equals(ProductCommentAttr.RATING_COUNT)){
				countAttr = attr;
				count = Long.valueOf(countAttr.getAttrValue()) - 1;
				if (count <= 0) {
					count = Long.valueOf(0);
				}
				countAttr.setAttrValue(String.valueOf(count));
			} else if (attr.getAttrName().equals(ProductCommentAttr.RATING_VALUE)) {
				valueAttr = attr;
				value = Float.valueOf(valueAttr.getAttrValue()) - oriRating;
				if (value < Float.valueOf(0)) {
					value = Float.valueOf(0);
				}
				valueAttr.setAttrValue(String.valueOf(value));
			} 
		}
		if (countAttr != null && valueAttr != null) {
			attributeDao.update(countAttr);
			attributeDao.update(valueAttr);			
		}
		return;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
}
