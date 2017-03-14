package com.cyberlink.cosmetic.modules.product.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_PROD_SEARCH_LOG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class SearchLog extends AbstractEntity<Long>{

	private static final long serialVersionUID = 1L;
	private String locale ;
	private Long appliedProdTypeID ;
	private String keyword ;
	
	@JsonView(Views.Public.class)   
	@Column(name = "LOCALE")
	public String getLocale() {
		return locale;
	}
	
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	@JsonView(Views.Public.class)   
    @Column(name = "KEYWORD")
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "APPLIED_TYPE_ID")
	public Long getAppliedProdTypeID() {
		return appliedProdTypeID;
	}

	public void setAppliedProdTypeID(Long appliedProdTypeID) {
		this.appliedProdTypeID = appliedProdTypeID;
	}

}
