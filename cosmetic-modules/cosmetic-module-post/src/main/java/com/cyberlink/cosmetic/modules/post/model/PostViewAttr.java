package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;

import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class PostViewAttr implements Serializable {
	
	private static final long serialVersionUID = 1487497114486261896L;
	
    private Long likeCount = (long)0;
	private Long commentCount = (long)0;
    private Long circleInCount = (long)0;  
	private Long lookDownloadCount = (long)0;

    public PostViewAttr() {
        
    }
    
    @JsonView(Views.Public.class)
	public Long getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
    
    @JsonView(Views.Public.class)
    public Long getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
    
    @JsonView(Views.Public.class)
    public Long getCircleInCount() {
        return circleInCount;
    }
    
    public void setCircleInCount(Long circleInCount) {
        this.circleInCount = circleInCount;
    }
	
    @JsonView(Views.Public.class)
    public Long getLookDownloadCount() {
        return lookDownloadCount;
    }

    public void setLookDownloadCount(Long lookDownloadCount) {
        this.lookDownloadCount = lookDownloadCount;
    }
    
}