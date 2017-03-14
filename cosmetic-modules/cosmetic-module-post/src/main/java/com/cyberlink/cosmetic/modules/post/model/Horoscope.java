package com.cyberlink.cosmetic.modules.post.model;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_HOROSCOPE")
@DynamicUpdate
public class Horoscope extends AbstractCoreEntity<Long>{

	private static final long serialVersionUID = -2525903454353474319L;
	
	private String locale;
    private Long postId;
    private String title;
    private String imageUrl;

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
	@Column(name = "POST_ID")
	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}
    
	@JsonView(Views.Public.class)
    @Column(name = "TITLE") 
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@JsonView(Views.Public.class)
	@Column(name = "IMAGE_URL") 
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
