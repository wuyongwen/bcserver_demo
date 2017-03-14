package com.cyberlink.cosmetic.modules.look.model;

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
@Table(name = "BC_LOOK_TYPE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class LookType extends AbstractCoreEntity<Long>{

	private static final long serialVersionUID = 2557384961643291875L;

	private String name;
	private String codeName;
    private String locale;
	private Long bgImgId;
	private String bgImgUrl;
	private Boolean isVisible;

    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
        return id;
    }    
	
	@JsonView(Views.Public.class)
    @Column(name = "NAME") 
    public String getName() {
        return name;
    }
	
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonView(Views.Public.class)
    @Column(name = "CODE_NAME") 
    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
	    
	@Column(name = "LOCALE") 
    public String getLocale() {
        return locale;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    @JsonView(Views.Public.class)
    @Column(name = "BG_IMG_ID") 
    public Long getBgImgId() {
        return bgImgId;
    }
    
    public void setbgImgId(Long bgImgId) {
        this.bgImgId = bgImgId;
    }
    
    @JsonView(Views.Public.class)
    @Column(name = "BG_IMG_URL") 
    public String getBgImgUrl() {
        return bgImgUrl;
    }
    
    public void setBgImgUrl(String bgImgUrl) {
        this.bgImgUrl = bgImgUrl;
    }
    
    @Column(name = "IS_VISIBLE") 
    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }
}
