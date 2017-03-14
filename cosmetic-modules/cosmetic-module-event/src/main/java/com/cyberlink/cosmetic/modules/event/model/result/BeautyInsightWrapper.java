package com.cyberlink.cosmetic.modules.event.model.result;

import java.util.Date;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.event.model.BeautyInsight;
import com.fasterxml.jackson.annotation.JsonView;

public class BeautyInsightWrapper {
	
	private BeautyInsight beautyInsight;
	
	public BeautyInsightWrapper(BeautyInsight beautyInsight){
		this.beautyInsight = beautyInsight;
	}

	@JsonView(Views.Public.class)
	public Long getId() {
		return beautyInsight.getId();
	}

	@JsonView(Views.Public.class)
	public String getLocale() {
		return beautyInsight.getLocale();
	}
	
	@JsonView(Views.Public.class)
	public String getImgUrl() {
		return beautyInsight.getImgUrl();
	}
	
	@JsonView(Views.Public.class)
	public String getDescription() {
		return beautyInsight.getDescription();
	}
	
	@JsonView(Views.Public.class)
	public String getRedirectUrl() {
		return beautyInsight.getRedirectUrl();
	}
	
	@JsonView(Views.Public.class)
	public String getMetadata() {
		return beautyInsight.getMetadata();
	}
	
	@JsonView(Views.Public.class)
	public Date getCreatedTime() {
		return beautyInsight.getCreatedTime();
	}
}
