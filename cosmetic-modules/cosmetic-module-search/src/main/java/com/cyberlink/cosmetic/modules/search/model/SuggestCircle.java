package com.cyberlink.cosmetic.modules.search.model;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class SuggestCircle {
	@JsonView(Views.Simple.class)
	private Long id;
	@JsonView(Views.Simple.class)
	private String circleName;
	@JsonView(Views.Simple.class)
	private String creatorName;
	@JsonView(Views.Simple.class)
	private Integer postCount;
	@JsonView(Views.Simple.class)
	private String iconUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCircleName() {
		return circleName;
	}

	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}

	public Integer getPostCount() {
		return postCount;
	}

	public void setPostCount(Integer postCount) {
		this.postCount = postCount;
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
}
