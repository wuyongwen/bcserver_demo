package com.cyberlink.cosmetic.modules.product.model.result;

import java.util.List;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;
import com.fasterxml.jackson.annotation.JsonView;

public class BrandIndexWrapper {
	private BrandIndex brandIndex;
	private List<BrandWrapper> brandList ;
	
	public BrandIndexWrapper( BrandIndex bIndex ){
		this.brandIndex = bIndex;
	}

	@JsonView(Views.Public.class)
	public Long getId(){
		return brandIndex.getId();
	}
	
	@JsonView(Views.Public.class)
	public String getIndex(){
		return brandIndex.getIndex();
	}

	@JsonView(Views.Public.class)
	public List<BrandWrapper> getBrandList() {
		return brandList;
	}

	public void setBrandList(List<BrandWrapper> brandList) {
		this.brandList = brandList;
	}

	
}
