package com.cyberlink.cosmetic.modules.product.model.result;

import java.io.Serializable;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class ProdRatingCounter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1384657112630094721L;
	private String region;
	private int count ;
	private float rating ;
	private float avgRating ;
	
	public ProdRatingCounter( String r, float rating ){
		this.region = r ;
		this.rating = rating;
		count = 1 ;
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

	@JsonView(Views.Public.class)
	public float getAverageRating() {
		avgRating = rating / count ;
		return avgRating;
	}

	public void setAverageRating(float avgRating) {
		this.avgRating = avgRating;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}
	
	
}
