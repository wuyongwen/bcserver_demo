package com.cyberlink.cosmetic.modules.product.model.result;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.model.ProductType;
import com.fasterxml.jackson.annotation.JsonView;

public class ProductTypeWrapper {
	private ProductType productType ;
	
	public ProductTypeWrapper(ProductType type){
		productType = type ;
	}
	
	@JsonView(Views.Public.class)
	public Long gettypeId() {
		return productType.getId();
	}
	
	@JsonView(Views.Public.class)
	public String getTypeName(){
		return productType.getTypeName();
	}
}
