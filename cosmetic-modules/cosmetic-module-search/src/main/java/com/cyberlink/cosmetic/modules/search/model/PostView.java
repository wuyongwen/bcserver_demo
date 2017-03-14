package com.cyberlink.cosmetic.modules.search.model;

import java.util.ArrayList;
import java.util.List;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostView {
	@JsonView(Views.Simple.class)
	private Long postId;
	@JsonView(Views.Simple.class)
	private PostViewCreator creator;
	@JsonView(Views.Simple.class)
	private PostViewCreator sourcePostCreator;
	@JsonView(Views.Simple.class)
	private Long createdTime;
	@JsonView(Views.Simple.class)
	private Long lastModified;
	@JsonView(Views.Simple.class)
	private String status;
	@JsonView(Views.Simple.class)
	private String title;
	@JsonView(Views.Simple.class)
	private String content;
	@JsonView(Views.Simple.class)
	private PostViewAttachments attachments;
	@JsonView(Views.Simple.class)
	private Boolean gotProductTag;
	@JsonView(Views.Simple.class)
	private Long likeCount;
	@JsonView(Views.Simple.class)
	private Boolean isLiked;
	@JsonView(Views.Simple.class)
	private Long commentCount;
	@JsonView(Views.Simple.class)
	private Long circleInCount;
	@JsonView(Views.Simple.class)
	private Long lookDownloadCount;
	@JsonView(Views.Simple.class)
	private String extLookUrl;
	@JsonView(Views.Simple.class)
	private List<PostViewCircle> circles = new ArrayList<PostViewCircle>();

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public PostViewCreator getCreator() {
		return creator;
	}

	public void setCreator(PostViewCreator creator) {
		this.creator = creator;
	}

	public PostViewCreator getSourcePostCreator() {
		return sourcePostCreator;
	}

	public void setSourcePostCreator(PostViewCreator sourcePostCreator) {
		this.sourcePostCreator = sourcePostCreator;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	public Long getLastModified() {
		return lastModified;
	}

	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public PostViewAttachments getAttachments() {
		return attachments;
	}

	public void setAttachments(PostViewAttachments attachments) {
		this.attachments = attachments;
	}

	public Boolean getGotProductTag() {
		return gotProductTag;
	}

	public void setGotProductTag(Boolean gotProductTag) {
		this.gotProductTag = gotProductTag;
	}

	public Long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}

	public Boolean getIsLiked() {
		return isLiked;
	}

	public void setIsLiked(Boolean isLiked) {
		this.isLiked = isLiked;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	public Long getCircleInCount() {
		return circleInCount;
	}

	public void setCircleInCount(Long circleInCount) {
		this.circleInCount = circleInCount;
	}

	public Long getLookDownloadCount() {
		return lookDownloadCount;
	}

	public void setLookDownloadCount(Long lookDownloadCount) {
		this.lookDownloadCount = lookDownloadCount;
	}

	public String getExtLookUrl() {
		return extLookUrl;
	}

	public void setExtLookUrl(String extLookUrl) {
		this.extLookUrl = extLookUrl;
	}

	public List<PostViewCircle> getCircles() {
		return circles;
	}

	public void setCircles(List<PostViewCircle> circles) {
		this.circles = circles;
	}

}
