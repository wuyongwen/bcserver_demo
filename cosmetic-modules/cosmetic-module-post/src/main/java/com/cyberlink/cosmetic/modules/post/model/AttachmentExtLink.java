package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.cosmetic.core.model.AbstractEntity;

@Entity
@Table(name = "BC_ATTACHMENT_EXT_LINK")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class AttachmentExtLink extends AbstractEntity<Long> {

    private static final long serialVersionUID = 9182232291899330612L;

    private String linkType;
    private Long userId;
    private String metadata;
    
    public void setId(Long id) {
        this.id = id;
    }
    
	@Column(name = "LINK_TYPE")
    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    @Column(name = "USER_ID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        this.setShardId(userId);
    }

    @Column(name = "METADATA", length = 65535)
    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
