package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Cacheable;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.cyberlink.core.model.AbstractCoreEntity;

@Entity
@Table(name = "BC_ATTR")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Attribute extends AbstractCoreEntity<Long>{
    private static final long serialVersionUID = -7185054561878216977L;

    private AttributeType refType;
    private Long refId;
    private String attrName;
    private String attrValue;
    private User user;
    
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
    
    @Column(name = "REF_TYPE")
    @Enumerated(EnumType.STRING)
    public AttributeType getRefType() {
        return refType;
    }
    
    public void setRefType(AttributeType refType) {
        this.refType = refType;
    }
    
    @Column(name = "REF_ID")
    public Long getRefId() {
        return refId;
    }
    
    public void setRefId(Long refId) {
        this.refId = refId;
    }
    
    @Column(name = "ATTR_NAME")
    public String getAttrName() {
        return attrName;
    }
    
    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }
    
    @Column(name = "ATTR_VALUE")
    public String getAttrValue() {
        return attrValue;
    }
    
    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    @ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "REF_ID", insertable=false, updatable=false)
    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
}
