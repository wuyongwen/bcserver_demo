package com.cyberlink.cosmetic.modules.product.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_ATTR_PROD_CHANGE_LOG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class ProductChangeLogAttr extends AbstractCoreEntity<Long>{
    private static final long serialVersionUID = -7185054561878216977L;

    private Long id;
    private Long prodChangeLogId ;
    private ProductChangeLogAttrStatus status ;
    private String attrName;
    private String attrValue;
    
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
    
    @Column(name = "ATTR_NAME")
    public String getAttrName() {
        return attrName;
    }
    
    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }
    
    @Column(name = "ATTR_VALUE", nullable=true)
    public String getAttrValue() {
        return attrValue;
    }
    
    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    @Column(name = "PROD_CHANGE_LOG_ID")
	public Long getProdChangeLogId() {
		return this.prodChangeLogId;
	}

	public void setProdChangeLogId(Long prodChangeLogId) {
		this.prodChangeLogId = prodChangeLogId;
	}

	@Column(name = "ATTR_STATUS")
	@Enumerated(EnumType.STRING)
	public ProductChangeLogAttrStatus getStatus() {
		return status;
	}

	public void setStatus(ProductChangeLogAttrStatus status) {
		this.status = status;
	}
}
