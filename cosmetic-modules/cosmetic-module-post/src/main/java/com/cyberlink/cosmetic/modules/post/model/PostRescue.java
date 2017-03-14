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
@Table(name = "BC_POST_RESCUE")
@DynamicUpdate
public class PostRescue extends AbstractCoreEntity<Long>{

    private static final long serialVersionUID = -5284561006180316631L;
    private Long postId;
    private Post post;
    private Long reviewerId;
    private RescueType rescueType;
    private String remark;
    private Boolean isHandled;

    public enum RescueType {
        Revive, Abandon, Selfie, SelfieDiscover;
    }
    
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "POST_ID")
    public Long getpostId() {
        return postId;
    }
    
    public void setpostId(Long postId){
        this.postId = postId;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", insertable=false, updatable=false)
    public Post getPost()
    {
        return post;
    }
    
    public void setPost(Post post)
    {
        this.post = post;
    }
    
    @Column(name = "REVIEWER_ID")
    public Long getReviewerId() {
        return reviewerId;
    }
    
    public void setReviewerId(Long reviewerId){
        this.reviewerId = reviewerId;
    }
    
    
    @Enumerated(EnumType.STRING)
    @Column(name = "RESCUE_TYPE")
    public RescueType getRescueType() {
        return rescueType;
    }
    
    public void setRescueType(RescueType rescueType){
        this.rescueType = rescueType;
    }

    @Column(name = "REMARK")
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark){
        this.remark = remark;
    }
    
    @Column(name = "IS_HANDLED")
    public Boolean getIsHandled() {
        return isHandled;
    }

    public void setIsHandled(Boolean isHandled) {
        this.isHandled = isHandled;
    }
    
}
