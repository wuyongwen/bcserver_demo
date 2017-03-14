package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.user.model.User;

@Entity
@DynamicUpdate
@Table(name = "BC_POST_CIRCLE_IN")
public class PostCircleIn extends AbstractEntity<Long> {

    private static final long serialVersionUID = -2827476118045799705L;

    private Long userId;
    private User user;
    private Long postId;
    private Post post;
	private Long circleId;
	private Circle circle;
    private Long sourceUserId;
    private User sourceUser;
    private Long sourcePostId;
    private Long rootPostId;
    
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "USER_ID")
    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    @Column(name = "POST_ID")
    public Long getPostId() {
        return this.postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", insertable=false, updatable=false)
    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
    
    @Column(name = "CIRCLE_ID")
	public Long getCircleId() {
		return circleId;
	}

	public void setCircleId(Long circleId) {
		this.circleId = circleId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CIRCLE_ID", insertable=false, updatable=false)
	public Circle getCircle() {
		return circle;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}

    @Column(name = "SOURCE_USER_ID")
    public Long getSourceUserId() {
        return this.sourceUserId;
    }

    public void setSourceUserId(Long sourceUserId) {
        this.sourceUserId = sourceUserId;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_USER_ID", insertable=false, updatable=false)
    public User getSourceUser() {
        return this.sourceUser;
    }

    public void setSourceUser(User sourceUser) {
        this.sourceUser = sourceUser;
    }
    
    @Column(name = "SOURCE_POST_ID")
    public Long getSourcePostId() {
        return this.sourcePostId;
    }

    public void setSourcePostId(Long sourcePostId) {
        this.sourcePostId = sourcePostId;
    }
    
    @Column(name = "ROOT_POST_ID")
    public Long getRootPostId() {
        return this.rootPostId;
    }

    public void setRootPostId(Long rootPostId) {
        this.rootPostId = rootPostId;
    }
}
