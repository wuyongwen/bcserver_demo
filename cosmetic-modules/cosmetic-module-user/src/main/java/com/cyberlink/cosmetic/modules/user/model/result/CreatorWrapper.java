package com.cyberlink.cosmetic.modules.user.model.result;

import java.io.Serializable;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.user.dao.DeviceDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonView;

public class CreatorWrapper implements Serializable{
	
	private static final long serialVersionUID = -6857359490858491839L;
	
	private Long userId ;
	private String avatar ;
	private String displayName ;
	private String cover ;
	private UserType userType ;
	private String description ;
	private Boolean isFollowed ;
	
	public CreatorWrapper ( User creator ){
		userId = creator.getId();
        avatar = creator.getAvatarUrl();
        displayName = creator.getDisplayName();
        cover = creator.getCoverUrl() ;
        userType = creator.getUserType();
        description = creator.getDescription();
        isFollowed = creator.getIsFollowed();
	}

	@JsonView(Views.Public.class)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@JsonView(Views.Public.class)
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@JsonView(Views.Public.class)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@JsonView(Views.Public.class)
	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	@JsonView(Views.Public.class)
	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	@JsonView(Views.Public.class)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonView(Views.Public.class)
	public Boolean getIsFollowed() {
		return isFollowed;
	}

	public void setIsFollowed(Boolean isFollowed) {
		this.isFollowed = isFollowed;
	}
	
	
	
}
