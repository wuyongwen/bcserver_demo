package com.cyberlink.cosmetic.modules.product.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
@Table(name = "BC_STORE_PRICE_RANGE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class StorePriceRange extends AbstractCoreEntity<Long>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1555866776280933313L;
	private Long id;
	private String rangeName;
	private float priceMin ;
	private float priceMax ;
	private String locale ;
	
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
	
	

	@JsonView(Views.Public.class)
    @Column(name = "PRICE_MIN")
	public float getPriceMin() {
		return priceMin;
	}

	public void setPriceMin(float priceMin) {
		this.priceMin = priceMin;
	}

	@JsonView(Views.Public.class)
    @Column(name = "PRICE_MAX")
	public float getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(float priceMax) {
		this.priceMax = priceMax;
	}

	@JsonView(Views.Public.class)
    @Column(name = "LOCALE")
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@JsonView(Views.Public.class)
    @Column(name = "RANGE_NAME")
	public String getRangeName() {
		return rangeName;
	}

	public void setRangeName(String rangeName) {
		this.rangeName = rangeName;
	}
	
	/**
	 * Determine whether the price is in price range
	 * @param price
	 * @return 
	 */
	public boolean isInPriceRange(float price){
		return (priceMin <= price && price < priceMax);
	}
}
