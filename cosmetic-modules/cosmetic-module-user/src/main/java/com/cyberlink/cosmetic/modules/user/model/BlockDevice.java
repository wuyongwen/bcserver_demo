package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_BLOCK_DEVICE")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class BlockDevice extends AbstractEntity<Long> {
	private static final long serialVersionUID = -3677968615971167047L;

	private String uuid;
    
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "DEVICE_UUID")
    @JsonView(Views.Public.class)    
    public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}	
}
