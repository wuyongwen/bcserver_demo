package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;

@Entity
@Table(name = "BC_USER_BLOCKED")
@DynamicUpdate
public class UserBlocked extends AbstractCoreEntity<Long> {

	private static final long serialVersionUID = 4926212959339100671L;
	private Long id;
	private Long userId;
	private Long targetId;
	private User creator;
	private User target;
	private String userName;
	private String targetName;

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

	@Column(name = "USER_ID")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "TARGET_ID")
	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	@Column(name = "USER_NAME")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "TARGET_NAME")
	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", insertable = false, updatable = false)
	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TARGET_ID", insertable = false, updatable = false)
	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}

}