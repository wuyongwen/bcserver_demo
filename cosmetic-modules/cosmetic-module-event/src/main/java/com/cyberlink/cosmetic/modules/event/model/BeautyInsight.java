package com.cyberlink.cosmetic.modules.event.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_BEAUTY_INSIGHT")
@DynamicUpdate
public class BeautyInsight extends AbstractCoreEntity<Long>{
	
	private static final long serialVersionUID = 6214904826802974554L;
	private String locale;
	private String imgUrl ;
	private String description ;
	private String redirectUrl ;
	private String metadata;

	@Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
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
    @Column(name = "IMGURL")
	public String getImgUrl() {
		return imgUrl;
	}
	
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	@JsonView(Views.Public.class)
    @Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonView(Views.Public.class)
    @JoinColumn(name = "REDIRECTURL")
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "META_DATA")
	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
}
