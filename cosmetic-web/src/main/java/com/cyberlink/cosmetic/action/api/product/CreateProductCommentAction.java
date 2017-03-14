package com.cyberlink.cosmetic.action.api.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.product.model.ProductCommentAttr;
import com.cyberlink.cosmetic.modules.product.model.ProductCommentStatus;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;

@UrlBinding("/api/prod/CreateComment.action")
public class CreateProductCommentAction extends AbstractAction{
	static final Integer commentLength = Integer.valueOf(2048) ; 
	@SpringBean("product.ProductCommentDao")
	protected ProductCommentDao commentDao;

	@SpringBean("user.AttributeDao")
	protected AttributeDao attributeDao;

	@SpringBean("product.ProductDao")
	protected ProductDao productDao;

	@SpringBean("user.UserDao")
    protected UserDao userDao;
	
	protected Long productId;
	protected String comment ;
	protected Float rating = Float.valueOf(0);
    
	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
		if (!authenticate()) {
			return new ErrorResolution(authError);
		}
		if (!productDao.exists(productId)) {
			return new ErrorResolution(ErrorDef.InvalidProductId);
		}  
		Long userId = getSession().getUserId();
		if (!userDao.exists(userId)) {
			return new ErrorResolution(ErrorDef.InvalidUserId);
		}
		if (comment == null) {
			comment = "";
		}
		if ( comment.length() > commentLength ) {
			return new ErrorResolution(ErrorDef.InvalidProdComment);
		}
		Float oriRating = Float.valueOf(0);
		boolean is1stCommentofUser = Boolean.FALSE ;
		ProductComment prodComment = commentDao.findByProductIdAndUserId(userId, productId);
		if (prodComment == null) {
			prodComment = new ProductComment();
			is1stCommentofUser = Boolean.TRUE ;
		}
		else {
			oriRating = prodComment.getRating();
			is1stCommentofUser = Boolean.FALSE ;
		}
		prodComment.setShardId(userId);
		prodComment.setIsDeleted(Boolean.FALSE);
		prodComment.setComment(comment);
		prodComment.setUser(userDao.findById(userId));
		prodComment.setProduct(productDao.findById(productId));
		//do not update attr since we changed to use real-time calculation on rating & comment number
		//updateRatingValues(productId, rating, oriRating, is1stCommentofUser);

		prodComment.setRating(rating);
		prodComment.setStatus(ProductCommentStatus.Published);
		prodComment = commentDao.update(prodComment);
		final Map<String, Object> results = new HashMap<String, Object>();
		results.put("commentId", prodComment.getId());
		return json(results);
	}
	
	protected Float updateRatingValues(Long productId, Float rating, Float oriRating, boolean is1stCommentofUser) {
		List<Attribute> attrList = attributeDao.findByRefIdAndNames(AttributeType.Product, productId, 
				ProductCommentAttr.RATING_COUNT, ProductCommentAttr.RATING_VALUE);
		Attribute countAttr = null;
		Attribute valueAttr = null;
		for (Attribute attr : attrList) {
			if (attr.getAttrName().equals(ProductCommentAttr.RATING_COUNT)){
				countAttr = attr;
			} else if (attr.getAttrName().equals(ProductCommentAttr.RATING_VALUE)) {
				valueAttr = attr;
			} 
		}
		
		Long count = Long.valueOf(0);
		Float value = Float.valueOf(0);
		if (countAttr != null) {
			count = Long.valueOf(countAttr.getAttrValue());
		} else {
			countAttr = new Attribute();
			countAttr.setAttrName(ProductCommentAttr.RATING_COUNT);
			countAttr.setRefId(productId);
			countAttr.setRefType(AttributeType.Product);
		}
		if (valueAttr != null) {
			value = Float.valueOf(valueAttr.getAttrValue());
		} else {
			valueAttr = new Attribute();
			valueAttr.setAttrName(ProductCommentAttr.RATING_VALUE);
			valueAttr.setRefId(productId);
			valueAttr.setRefType(AttributeType.Product);
		}
		
		if( count == 0 ){//never received comment on this product...
			count++;
			value = rating ;
		}
		else if( count != 0 && is1stCommentofUser ){
			count++;
			value += rating ;
		}
		else{//count !=0 && !is1stCommentofUser
			value = value - oriRating + rating ; 
		}
			
		countAttr.setAttrValue(String.valueOf(count));
		valueAttr.setAttrValue(String.valueOf(value));
		attributeDao.update(countAttr);
		attributeDao.update(valueAttr);
		
		return Float.valueOf( value /Float.valueOf(count));
	}
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Float getRating() {
		return rating;
	}
	public void setRating(Float rating) {
		this.rating = rating;
	}
	
}
