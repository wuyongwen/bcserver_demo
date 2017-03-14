package com.cyberlink.cosmetic.modules.product.model.result;


import java.util.Date;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.modules.user.model.result.CreatorWrapper;
import com.fasterxml.jackson.annotation.JsonView;

public class ProductCommentWrapper {
	private ProductComment productComment ;
	private CreatorWrapper creator;
	
	public ProductCommentWrapper( ProductComment productComment ){
		this.productComment = productComment ;
		this.setCreator(new CreatorWrapper( productComment.getUser()));
	}

	@JsonView(Views.Public.class)
	public Long getId() {
		return productComment.getId();
	}

	@JsonView(Views.Public.class)
	public String getComment() {
		return productComment.getComment();
	}
	
	@JsonView(Views.Public.class)
	public float getRating() {
		return productComment.getRating();
	}

	@JsonView(Views.Public.class)
	public Date getLastModified() {
        return productComment.getLastModified();
    }
	
	@JsonView(Views.Public.class)
	public CreatorWrapper getCreator() {
		return creator;
	}

	public void setCreator(CreatorWrapper creator) {
		this.creator = creator;
	}

	
	
}
