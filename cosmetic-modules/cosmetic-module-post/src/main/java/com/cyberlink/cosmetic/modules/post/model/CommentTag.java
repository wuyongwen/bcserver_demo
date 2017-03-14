package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.MetaValue;

import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.cosmetic.modules.user.model.User;

@Entity
@DynamicUpdate
@Table(name = "BC_COMMENT_TAG")
public class CommentTag extends AbstractEntity<Long> {

    private static final long serialVersionUID = 7055224306658678175L;
    private Long commentId;
    /*private String refType;
    private Long refId;*/
    private Object tagTarget;

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "COMMENT_ID")
    public Long getCommentId() {
        return this.commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    @Any(metaColumn = @Column(name = "REF_TYPE"))
    @AnyMetaDef(idType = "long", metaType = "string", 
            metaValues = { 
             @MetaValue(targetEntity = User.class, value = "Receiver")
       })
    @JoinColumn(name="REF_ID")
    public Object getTagTarget() {
        return tagTarget;
    }

    public void setTagTarget(Object tagTarget) {
        this.tagTarget = tagTarget;
    }
    
    /*@Column(name = "REF_TYPE", length = 5)
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
    }*/

}
