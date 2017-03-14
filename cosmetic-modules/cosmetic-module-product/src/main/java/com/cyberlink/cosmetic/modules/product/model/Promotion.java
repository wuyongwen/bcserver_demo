package com.cyberlink.cosmetic.modules.product.model;

import java.io.Serializable;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class Promotion implements Serializable {
	        
	private static final long serialVersionUID = -1052255746515351357L;
	
	private String promotionBanner = "";
	private String promotionPage = "";
	
	public Promotion() {
		
	}
	
	@JsonView(Views.Public.class)   
	public String getPromotionBanner() {
		return promotionBanner;
	}

	public void setPromotionBanner(String promotionBanner) {
		this.promotionBanner = promotionBanner;
	}
	
	@JsonView(Views.Public.class)   
	public String getPromotionPage() {
		return promotionPage;
	}
	
	public void setPromotionPage(String promotionPage) {
		this.promotionPage = promotionPage;
	}
	
}