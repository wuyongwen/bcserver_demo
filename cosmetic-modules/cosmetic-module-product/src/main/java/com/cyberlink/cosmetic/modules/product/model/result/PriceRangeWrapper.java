package com.cyberlink.cosmetic.modules.product.model.result;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;
import com.fasterxml.jackson.annotation.JsonView;

public class PriceRangeWrapper {
	private StorePriceRange priceRange;
	
	public PriceRangeWrapper( StorePriceRange RangeItem ){
		setPriceRange(RangeItem); 
	}
	
	@JsonView(Views.Public.class)
	public Long getId(){
		return getPriceRange().getId();
	}
	
	@JsonView(Views.Public.class)
	public String getPriceRangeName(){
		if( getPriceRange().getRangeName() == null ){
			return "";
		}
		else{
			return getPriceRange().getRangeName();
		}
	}
	
	@JsonView(Views.Public.class)
	public String getPriceMin(){
		return String.format( "%.2f", getPriceRange().getPriceMin()) ;
	}
	
	@JsonView(Views.Public.class)
	public String getPriceMax(){
		return String.format( "%.2f", getPriceRange().getPriceMax()) ;
	}

	public StorePriceRange getPriceRange() {
		return priceRange;
	}

	public void setPriceRange(StorePriceRange priceRange) {
		this.priceRange = priceRange;
	}
}
