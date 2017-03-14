package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Cacheable;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_POST_TOP_KW")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class PostTopKeyword extends AbstractCoreEntity<Long>{

	private static final long serialVersionUID = -959351398377497077L;
	
	private String locale;
	private String keyword;
	private Long frequency;
	private String postIds;
	private Boolean isTop = false;
	private Integer bucketId;

	@Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
        return id;
    }    
	
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
    @Column(name = "FREQUENCY") 
    public Long getFrequency() {
        return frequency;
    }
    
    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }
	
    @JsonView(Views.Public.class)
    @Column(name = "POST_IDS")
	public String getPostIds() {
		return postIds;
	}

	public void setPostIds(String postIds) {
		this.postIds = postIds;
	}

	@JsonView(Views.Public.class)
    @Column(name = "IS_TOP")
	public Boolean getIsTop() {
		return isTop;
	}

	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}
	
	@JsonView(Views.Public.class)
    @Column(name = "BUCKET_ID")
	public Integer getBucketId() {
		return bucketId;
	}

	public void setBucketId(Integer bucketId) {
		this.bucketId = bucketId;
	}
}
