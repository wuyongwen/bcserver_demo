package com.cyberlink.cosmetic.modules.search.model;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostViewFile {
	@JsonView(Views.Simple.class)
	private Long fileId;
	@JsonView(Views.Simple.class)
	private String fileType;
	@JsonView(Views.Simple.class)
	private Long downloadCount;
	@JsonView(Views.Simple.class)
	private String metadata;

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Long getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(Long downloadCount) {
		this.downloadCount = downloadCount;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
}
