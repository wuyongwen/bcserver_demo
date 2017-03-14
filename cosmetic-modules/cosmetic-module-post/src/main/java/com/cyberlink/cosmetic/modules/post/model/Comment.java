package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@DynamicUpdate
@Table(name = "BC_COMMENT")
public class Comment extends AbstractEntity<Long> {

    public enum CommentStatus {
        Published, Reviewing, Banned;
    }
    
    private static final long serialVersionUID = 5707799728908720285L;
    private String refType;
    private Long refId;
    private User creator;
    private Long creatorId;
    private String commentText;
    private CommentStatus commentStatus;
    private Long latestSubCommentId;
	private Long subCommentCount = (long)0;
	
	public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "REF_TYPE")
    @JsonView(Views.Public.class)
    public String getRefType() {
        return this.refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    @Column(name = "REF_ID")
    @JsonView(Views.Public.class)
    public Long getRefId() {
        return this.refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    public User getCreator() {
        return this.creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    @Column(name = "USER_ID", insertable=false, updatable=false)
    public Long getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    @Column(name = "COMMENT_TEXT", length = 2048)
    @JsonView(Views.Public.class)
    public String getCommentText() {
        return this.commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    @Column(name = "COMMENT_STATUS")
    @Enumerated(EnumType.STRING)
    public CommentStatus getCommentStatus() {
        return this.commentStatus;
    }

    public void setCommentStatus(CommentStatus commentStatus) {
        this.commentStatus = commentStatus;
    }
    
	@Column(name = "LATEST_SUBCOMMENT_ID")
    public Long getLatestSubCommentId() {
		return latestSubCommentId;
	}

	public void setLatestSubCommentId(Long latestSubCommentId) {
		this.latestSubCommentId = latestSubCommentId;
	}
	
    @Column(name = "SUBCOMMENT_COUNT")
    public Long getSubCommentCount() {
		return subCommentCount;
	}
    
	public void setSubCommentCount(Long subCommentCount) {
		this.subCommentCount = subCommentCount;
	}
}
