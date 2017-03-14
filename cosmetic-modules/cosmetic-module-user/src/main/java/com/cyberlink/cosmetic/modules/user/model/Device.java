package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_USER_DEVICE")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Device extends AbstractEntity<Long>{
    private static final long serialVersionUID = 1962076840917918250L;

    private Long userId;
    private DeviceType deviceType;
    private String apnsToken;
    private String uuid;
    private String app;

    @Transient
    private Boolean isBlocked;
    
    
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

    @Column(name = "DEVICE_OS")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)    
    public DeviceType getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    @Column(name = "APNS_TOKEN")
    @JsonView(Views.Public.class)    
    public String getApnsToken() {
        return apnsToken;
    }
    
    public void setApnsToken(String apnsToken) {
        this.apnsToken = apnsToken;
    }

    @Column(name = "DEVICE_UUID")
    @JsonView(Views.Public.class)    
    public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@Column(name = "DEVICE_APP")
    public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}
	
	@Transient
    public Boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

}
