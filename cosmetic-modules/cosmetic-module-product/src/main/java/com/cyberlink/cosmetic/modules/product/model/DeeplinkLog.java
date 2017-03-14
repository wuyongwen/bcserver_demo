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
@Table(name = "BC_DEEPLINK_LOG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class DeeplinkLog extends AbstractCoreEntity<Long>{
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String platform ;
	private String browser;
	private String appName;
	private String appUrl;
	private String referrer;
	private String fry;

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

    @JsonView(Views.Public.class)
    @Column(name = "DEVICE_OS")
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	@JsonView(Views.Public.class)
	@Column(name = "DEVICE_BROWSER")
	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "DEVICE_APP")
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "APP_URL")
	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "REFERRER")
	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	@JsonView(Views.Public.class)
	@Column(name = "FRY")
	public String getFry() {
		return fry;
	}

	public void setFry(String fry) {
		this.fry = fry;
	}
}
