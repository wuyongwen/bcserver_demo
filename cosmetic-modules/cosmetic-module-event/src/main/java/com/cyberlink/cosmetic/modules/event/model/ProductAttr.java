package com.cyberlink.cosmetic.modules.event.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class ProductAttr {
    
    private Long bcProductId;
    private String thumbnailUrl;
    private String productLink;
    private String brandName;
    private String category;
    private String name;
    private String price;
    private float rating = -1;
    private Long commentCount = -1L;
    
    public ProductAttr() {
    }
    
    @JsonView(Views.Public.class)
	public Long getBcProductId() {
        return bcProductId;
    }
    
    public void setBcProductId(Long bcProductId) {
        this.bcProductId = bcProductId;
    }
    
    @JsonView(Views.Public.class)
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    @JsonView(Views.Public.class)
    public String getProductLink() {
        return productLink;
    }
    
    public void setProductLink(String productLink) {
        this.productLink = productLink;
    }
    
    @JsonView(Views.Public.class)
    public String getBrandName() {
        return brandName;
    }
    
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    @JsonView(Views.Public.class)
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    @JsonView(Views.Public.class)
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @JsonView(Views.Public.class)
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    @Transient 
    public float getRating() {
    	return rating;
    }
    
    public void setRating(float rating) {
    	this.rating = rating;
    }
    
    @Transient 
    public Long getCommentCount() {
    	return commentCount;
    }
    
    public void setCommentCount(Long commentCount) {
    	this.commentCount = commentCount;
    }
}
