package com.cyberlink.cosmetic.modules.search.model;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostViewCircle {
	@JsonView(Views.Simple.class)
	private Long circleId;
	@JsonView(Views.Simple.class)
	private String circleName;

	public Long getCircleId() {
		return circleId;
	}

	public void setCircleId(Long circleId) {
		this.circleId = circleId;
	}

	public String getCircleName() {
		return circleName;
	}

	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}
}
