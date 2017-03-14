package com.cyberlink.cosmetic.modules.product.model;

import java.io.Serializable;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class ProductFeatureMetadata implements Serializable {
	
	private static final long serialVersionUID = -2892022396883615358L;
	
	private String tryLink; 
	private String buyLink;
	private String infoLink;
	private Promotion promotion = new Promotion();
	
	public ProductFeatureMetadata() {
		
	}
	
	@JsonView(Views.Public.class)   
	public String getTryLink() {
		return tryLink;
	}
	
	public void setTryLink(String tryLink) {
		this.tryLink = tryLink;
	}
	
	@JsonView(Views.Public.class)   
	public String getBuyLink() {
		return buyLink;
	}
	
	public void setBuyLink(String buyLink) {
		this.buyLink = buyLink;
	}
	
	@JsonView(Views.Public.class)   
	public String getInfoLink() {
		return infoLink;
	}
	
	public void setInfoLink(String infoLink) {
		this.infoLink = infoLink;
	}
	
	@JsonView(Views.Public.class)   
	public Promotion getPromotion() {
		return promotion;
	}
	
	public void setPromotion(Promotion promotion) {
	    if(promotion != null)
	        this.promotion = promotion;
	}
	
}