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
@Table(name = "BC_STORE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Store extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = -4530742642629382979L;

	private Long id;
	private String storeName;
	private String locale;
	private String storeLink ;
	private String storeIMG ;
	private String storeVeri ;

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
    @Column(name = "STORE_NAME")
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	@JsonView(Views.Public.class)   
    @Column(name = "LOCALE")
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Column(name = "STORE_LINK")
	public String getStoreLink() {
		return storeLink;
	}

	public void setStoreLink(String storeLink) {
		this.storeLink = storeLink;
	}

	@Column(name = "STORE_IMG")
	public String getStoreIMG() {
		return storeIMG;
	}

	public void setStoreIMG(String storeIMG) {
		this.storeIMG = storeIMG;
	}

	@Column(name = "STORE_VERIFICATION")
	public String getStoreVeri() {
		return storeVeri;
	}

	public void setStoreVeri(String storeVeri) {
		this.storeVeri = storeVeri;
	}
	
	
	
}
