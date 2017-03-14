package com.cyberlink.cosmetic.modules.product.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "BC_PRODUCT_EFFECT")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class ProductEffect extends AbstractCoreEntity<Long>{

    private static final long serialVersionUID = -4071159485997359647L;
    private Long id;
	private String effectName;
	private Long productGroupId;

	@Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "PRODUCT_GROUP_ID")
    @JsonView(Views.Public.class)       
    public Long getProductGroupId() {
        return productGroupId;
    }
    
    public void setProductGroupId(Long productGroupId) {
        this.productGroupId = productGroupId;
    }
    
    @Column(name = "EFFECT_NAME")
    @JsonView(Views.Public.class)    	
	public String getEffetcName() {
		return effectName;
	}
	
	public void setEffetcName(String effectName) {
		this.effectName = effectName;
	}
}
