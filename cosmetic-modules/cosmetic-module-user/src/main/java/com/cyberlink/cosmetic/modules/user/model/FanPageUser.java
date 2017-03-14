package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_USER_FAN_PAGE")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class FanPageUser extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = 6212445355361382082L;
	private Long id;
    private Long userId;
    private String locale;
    private String fanPage;
    private String tagCircleMap;
    private Boolean autoPost;

    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "USER_ID")
    @JsonView(Views.Public.class)    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "LOCALE")
    @JsonView(Views.Public.class)    
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Column(name = "FAN_PAGE")
    @JsonView(Views.Public.class) 
    public String getFanPage() {
		return fanPage;
	}
 
	public void setFanPage(String fanPage) {
		this.fanPage = fanPage;
	}

	@Column(name = "TAG_CIRCLE_MAP")
    @JsonView(Views.Public.class)    
    public String getTagCircleMap() {
        return tagCircleMap;
    }

    public void setTagCircleMap(String tagCircleMap){
        this.tagCircleMap = tagCircleMap;
    }
    
    @Column(name = "AUTOPOST")
    public Boolean getAutoPost() {
        return autoPost;
    }

    public void setAutoPost(Boolean autoPost) {
        this.autoPost = autoPost;
    }
}