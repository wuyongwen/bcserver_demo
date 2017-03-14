package com.cyberlink.cosmetic.modules.search.model;

import java.util.ArrayList;
import java.util.List;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostViewAttachments {
	@JsonView(Views.Simple.class)
	public List<PostViewFile> files = new ArrayList<PostViewFile>();

	public List<PostViewFile> getFiles() {
		return files;
	}

	public void setFiles(List<PostViewFile> files) {
		this.files = files;
	}
}
