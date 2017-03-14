package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_USER_ATTR")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class UserAttr extends AbstractCoreEntity<Long> {

    private static final long serialVersionUID = 1457917179229920599L;
    
    private Long userId;
    private Long likeHowToCount;
    private Long likeYclCount;
    private Long followerCount;
    private Long followingCount;
    private Long howToCount;
    private Long yclLookCount;
    private Long blockCount;
    private Long liveBrandCount;
    
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name = "USER_ID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "LIKE_HOW_TO_COUNT")
    public Long getLikeHowToCount() {
        return likeHowToCount;
    }

    public void setLikeHowToCount(Long likeHowToCount) {
        this.likeHowToCount = likeHowToCount;
    }
    
    @Column(name = "LIKE_COUNT")
    public Long getLikeCount() {
        return likeYclCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeYclCount = likeCount;
    }

    @Column(name = "FOLLOWER_COUNT")
    public Long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Long followerCount) {
        this.followerCount = followerCount;
    }

    @Column(name = "FOLLOWING_COUNT")
    public Long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Long followingCount) {
        this.followingCount = followingCount;
    }

    @Column(name = "POST_COUNT")
    public Long getHowToCount() {
        return howToCount;
    }

    public void setHowToCount(Long howToCount) {
        this.howToCount = howToCount;
    }

    @Column(name = "YCL_LOOK_COUNT")
    public Long getYclLookCount() {
        return yclLookCount;
    }

    public void setYclLookCount(Long yclLookCount) {
        this.yclLookCount = yclLookCount;
    }
    
    @Column(name = "BLOCK_COUNT")
    public Long getBlockCount() {
		return blockCount;
	}

	public void setBlockCount(Long blockCount) {
		this.blockCount = blockCount;
	}

	@Column(name = "LIVE_BRAND_COUNT")
	public Long getLiveBrandCount() {
		return liveBrandCount;
	}

	public void setLiveBrandCount(Long liveBrandCount) {
		this.liveBrandCount = liveBrandCount;
	}

	@Transient
    public Long getValidLikeYclCount() {
        return likeYclCount == null ? 0L : likeYclCount;
    }
	
	@Transient
    public Long getValidLikeHowToCount() {
        return likeHowToCount == null ? 0L : likeHowToCount;
    }
}
