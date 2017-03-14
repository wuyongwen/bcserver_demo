package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.cosmetic.core.model.AbstractEntity;

@Entity
@DynamicUpdate
@Table(name = "BC_POST_PRODUCT")
public class PostProduct extends AbstractEntity<Long> {

    private static final long serialVersionUID = 1920583157365214151L;
    private Post post;
    private Long productId;
    private String tagAttrs;

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "POST_ID")
    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Column(name = "PRODUCT_ID")
    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Column(name = "TAG_ATTRS", length = 65535)
    public String getTagAttrs() {
        return this.tagAttrs;
    }

    public void setTagAttrs(String tagAttrs) {
        this.tagAttrs = tagAttrs;
    }

}
