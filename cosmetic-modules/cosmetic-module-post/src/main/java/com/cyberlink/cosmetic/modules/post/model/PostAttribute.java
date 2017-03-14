package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;

@Entity
@DynamicUpdate
@Table(name = "BC_POST_ATTR")
public class PostAttribute extends AbstractCoreEntity<Long> {

    private static final long serialVersionUID = 2529280425132332954L;

    public enum PostAttrType {
        PostLikeCount, PostCommentCount, PostCircleInCount, CommentLikeCount, PromoteScore, PostTotalCount, LookDownloadCount, PromoteLikeCount;
    }
    
    private String refType;
    private Long refId;
    private PostAttrType attrType;
    private Long attrValue;   
    private Post post;   

	@Override
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }
    
    @Column(name = "REF_TYPE")
    public String getRefType() {
        return this.refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    @Column(name = "REF_ID")
    public Long getRefId() {
        return this.refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }
    
    @Column(name = "ATTR_TYPE")
    @Enumerated(EnumType.STRING)
    public PostAttrType getAttrType() {
        return this.attrType;
    }

    public void setAttrType(PostAttrType attrType) {
        this.attrType = attrType;
    }
    
    @Column(name = "ATTR_VALUE")
    public Long getAttrValue() {
        return this.attrValue;
    }

    public void setAttrValue(Long attrValue) {
        this.attrValue = attrValue;
    }

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REF_ID", insertable=false, updatable=false)
    public Post getPost() {
		if (!getRefType().equals("Post"))
			return null;
    	return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
    
}
