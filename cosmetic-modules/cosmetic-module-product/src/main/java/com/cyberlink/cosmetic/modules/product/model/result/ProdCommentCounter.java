package com.cyberlink.cosmetic.modules.product.model.result;

import java.io.Serializable;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class ProdCommentCounter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1384657112630094721L;
	private String region;
	private int count ;
	
	public ProdCommentCounter( String r ){
		region = r ;
		count = 1;
	}
	
	@JsonView(Views.Public.class)
	public String getRegion() {
		return region;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
	@JsonView(Views.Public.class)
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	
}
