package com.cyberlink.cosmetic.modules.product.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_BRAND_ALIAS")
@DynamicUpdate
public class BrandNameAlias extends AbstractCoreEntity<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1762002829389113297L;
	
	private Long id;
	private Brand refBrand ;
	private String aliasName ;

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

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRAND_ID")
	public Brand getRefBrand() {
		return refBrand;
	}

	public void setRefBrand(Brand refBrand) {
		this.refBrand = refBrand;
	}

	@JsonView(Views.Public.class)
    @Column(name = "ALIAS_NAME")
	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
}
