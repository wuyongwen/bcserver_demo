package com.cyberlink.cosmetic.modules.post.result;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonView;

public class LikeDetailWrapper {

    public Long userId = (long)1;
	public String displayName = "";
    public String avatar = "";
    public String cover = "";
    public UserType userType = UserType.Normal;
    public String description = "";
    private Boolean isFollowed = false;
    private Boolean starOfWeek = Boolean.FALSE;
	private String badge = "";

    @JsonView(Views.Public.class)
    public Long getUserId()
    {
        return userId;
    }
    
    @JsonView(Views.Public.class)
    public String getDisplayName()
    {
        return displayName;
    }
    
    @JsonView(Views.Public.class)
    public String getAvatar()
    {
        return avatar;
    }

    @JsonView(Views.Public.class)
    public String getCover()
    {
        return cover;
    }
    
    @JsonView(Views.Public.class)
    public UserType getUserType() {
		return userType;
	}

    @JsonView(Views.Public.class)
    public String getDescription() {
		return description;
	}
    
    public void setIsFollowed(Boolean isFollowed)
    {
        this.isFollowed = isFollowed;
    }
    
    @JsonView(Views.Simple.class)
    public Boolean getIsFollowed() {
        return isFollowed;
    }
    
    @JsonView(Views.Simple.class)
	public Boolean getStarOfWeek() {
		return starOfWeek;
	}

	public void setStarOfWeek(Boolean starOfWeek) {
		this.starOfWeek = starOfWeek;
	}

	@JsonView(Views.Simple.class)
	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}
    
    public LikeDetailWrapper(User creator) {
        userId = creator.getId();
        displayName = creator.getDisplayName();
        avatar = creator.getAvatarUrl();
        cover = creator.getCoverUrl();
        userType = creator.getUserType();
        description = creator.getDescription();      
        isFollowed = creator.getIsFollowed();
        starOfWeek = creator.getStarOfWeek();
        badge = creator.getBadge();
    }

}
