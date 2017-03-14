package com.cyberlink.cosmetic.modules.product.model.result;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.fasterxml.jackson.annotation.JsonView;

public class BrandWrapper {
	private Brand brand;
	
	public BrandWrapper( Brand brand ){
		this.brand = brand ;
	}
	
	@JsonView(Views.Public.class)
	public Long getId() {
		return brand.getId();
	}
	
	@JsonView(Views.Public.class)
	public String getBrandName(){
		return brand.getBrandName() ;
	}

}
