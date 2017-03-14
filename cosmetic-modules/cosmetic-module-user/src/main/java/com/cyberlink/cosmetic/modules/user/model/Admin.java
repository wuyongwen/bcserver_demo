package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.cyberlink.cosmetic.core.model.AbstractEntity;

@Entity
@DynamicUpdate
@Table(name = "BC_ADMIN")
public class Admin extends AbstractEntity<Long> {
	public enum UserEvent {
		ExternalPost;
	}
	
	private static final long serialVersionUID = -8224264115083762254L;
	private User creator;
    private Long creatorId;
    private UserEvent event;
    private String refInfo;
    private String attribute;
    
    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	@Column(name = "USER_ID")
	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	@Column(name = "EVENT")
	@Enumerated(EnumType.STRING)
	public UserEvent getEvent() {
		return event;
	}

	public void setEvent(UserEvent event) {
		this.event = event;
	}

	@Column(name = "REF_INFO", length = 200)
	public String getRefInfo() {
		return refInfo;
	}

	public void setRefInfo(String refInfo) {
		this.refInfo = refInfo;
	}

	@Column(name = "ATTRIBUTE", length = 2048)
	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
    
    
}