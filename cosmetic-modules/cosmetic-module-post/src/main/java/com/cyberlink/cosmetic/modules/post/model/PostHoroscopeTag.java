package com.cyberlink.cosmetic.modules.post.model;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class PostHoroscopeTag {

	@JsonView(Views.Public.class)
	public String horoscopeType;

	@JsonView(Views.Public.class)
	public Object horoscopeMaster;

}