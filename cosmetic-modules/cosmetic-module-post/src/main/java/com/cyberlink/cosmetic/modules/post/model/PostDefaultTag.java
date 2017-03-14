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
@Table(name = "BC_POST_DEFAULT_TAG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class PostDefaultTag extends AbstractCoreEntity<Long>{
	
	private static final long serialVersionUID = 7762345445684435455L;
    
    private String locale;
	private String tagName;

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
    @Column(name = "TAG_NAME") 
    public String getTagName() {
        return tagName;
    }
	
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
