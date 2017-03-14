package com.cyberlink.cosmetic.modules.search.model;

import java.util.ArrayList;
import java.util.List;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CircleView {
	@JsonView(Views.Simple.class)
	private Long id;
	@JsonView(Views.Simple.class)
	private Long circleCreatorId;
	@JsonView(Views.Simple.class)
	private Long lastModified;
	@JsonView(Views.Simple.class)
	private Long circleTypeId;
	@JsonView(Views.Simple.class)
	private String circleName;
	@JsonView(Views.Simple.class)
	private String description;
	@JsonView(Views.Simple.class)
	private List<String> postThumbnails = new ArrayList<String>();
	@JsonView(Views.Simple.class)
	private Boolean isEditable;
	@JsonView(Views.Simple.class)
	private Boolean isFollowed;
	@JsonView(Views.Simple.class)
	private Boolean isSecret;
	@JsonView(Views.Simple.class)
	private Long postCount;
	@JsonView(Views.Simple.class)
	private Long followerCount;
	@JsonView(Views.Simple.class)
	private String defaultType;
	@JsonView(Views.Simple.class)
	private String creatorName;
	@JsonView(Views.Simple.class)
	private String iconUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCircleCreatorId() {
		return circleCreatorId;
	}

	public void setCircleCreatorId(Long circleCreatorId) {
		this.circleCreatorId = circleCreatorId;
	}

	public Long getLastModified() {
		return lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	public Long getCircleTypeId() {
		return circleTypeId;
	}

	public void setCircleTypeId(Long circleTypeId) {
		this.circleTypeId = circleTypeId;
	}

	public String getCircleName() {
		return circleName;
	}

	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

	public Boolean getIsFollowed() {
		return isFollowed;
	}

	public void setIsFollowed(Boolean isFollowed) {
		this.isFollowed = isFollowed;
	}

	public Boolean getIsSecret() {
		return isSecret;
	}

	public void setIsSecret(Boolean isSecret) {
		this.isSecret = isSecret;
	}

	public Long getPostCount() {
		return postCount;
	}

	public void setPostCount(Long postCount) {
		this.postCount = postCount;
	}

	public Long getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(Long followerCount) {
		this.followerCount = followerCount;
	}

	public String getDefaultType() {
		return defaultType;
	}

	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public List<String> getPostThumbnails() {
		return postThumbnails;
	}

	public void setPostThumbnails(List<String> postThumbnails) {
		this.postThumbnails = postThumbnails;
	}

}
