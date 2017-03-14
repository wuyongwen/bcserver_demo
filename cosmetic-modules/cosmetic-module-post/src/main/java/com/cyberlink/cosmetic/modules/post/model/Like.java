package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.user.model.User;

@Entity
@DynamicUpdate
@Table(name = "BC_LIKE")
public class Like extends AbstractEntity<Long> {

    public enum TargetType {
        Post, Comment;
    }
    
    // TargetSubType should contains likeable PostType values
    public enum TargetSubType {
        YCL_LOOK, HOW_TO;
    }
    
    private static final long serialVersionUID = 2675802646624989076L;
    private TargetType refType;
    private TargetSubType refSubType;
    private Long refId;
    private Long userId;
    private User user;
    private Object refTarget;
    private Post refPost;
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    @Column(name = "USER_ID")
    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    
    @Column(name = "REF_TYPE")
    @Enumerated(EnumType.STRING)
    public TargetType getRefType() {
        return this.refType;
    }

    public void setRefType(TargetType refType) {
        this.refType = refType;
    }

    @Column(name = "REF_SUB_TYPE")
    @Enumerated(EnumType.STRING)
    public TargetSubType getRefSubType() {
        return this.refSubType;
    }

    public void setRefSubType(TargetSubType refSubType) {
        this.refSubType = refSubType;
    }
    
    @Column(name = "REF_ID")
    public Long getRefId() {
        return this.refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REF_ID", insertable=false, updatable=false)
    public Post getRefPost()
    {
        return  refPost;
    }
    
    public void setRefPost(Post refPost)
    {
        this.refPost = refPost;
    }
    
}
