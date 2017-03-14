package com.cyberlink.cosmetic.modules.search.model;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostViewCreator {
	@JsonView(Views.Simple.class)
	private Long userId;
	@JsonView(Views.Simple.class)
	private Boolean isFollowed;
	@JsonView(Views.Simple.class)
	private String displayName;
	@JsonView(Views.Simple.class)
	private String avatar;
	@JsonView(Views.Simple.class)
	private UserType userType;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getIsFollowed() {
		return isFollowed;
	}

	public void setIsFollowed(Boolean isFollowed) {
		this.isFollowed = isFollowed;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
}
