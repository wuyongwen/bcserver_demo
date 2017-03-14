package com.cyberlink.cosmetic.modules.user.model.result;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonView;

public class BCCUserInfoWrapper {
    
	private final User user;
	
	public BCCUserInfoWrapper(User user) {
        this.user = user;
    }
	
	@JsonView(Views.Public.class)
    public Long getId() {
		return user.getId();
	}
	
	@JsonView(Views.Public.class)
	public String getDisplayName(){
		return user.getDisplayName();
	} 

	@JsonView(Views.Public.class)
	public UserType getUserType() {
		return user.getUserType();
	}
	
	@JsonView(Views.Public.class)
	public String getLocale() {
		return user.getRegion();
	}
}
