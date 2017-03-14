package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "BC_USER_REPORTED")
@DynamicUpdate
public class UserReported extends AbstractCoreEntity<Long> {

	public enum UserReportedReason {
		SPAMMING, GRAPHIC, ABUSIVE, PRETENDING
	}

	public enum UserReportedStatus {
		REPORTED, REVIEWING, REVIEWED, BANNED
	}

	private static final long serialVersionUID = -8332522391908512367L;
	private Long id;
	private Long reporterId;
	private Long reviewerId;
	private Long targetId;
	private User reporter;
	private User reviewer;
	private User target;
	private UserReportedReason reason;
	private UserReportedStatus status;

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

	@Column(name = "REPORTER_ID")	
	public Long getReporterId() {
		return reporterId;
	}

	public void setReporterId(Long reporterId) {
		this.reporterId = reporterId;
	}

	@Column(name = "REVIEWER_ID")	
	public Long getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(Long reviewerId) {
		this.reviewerId = reviewerId;
	}

	@Column(name = "TARGET_ID")	
	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REPORTER_ID", insertable = false, updatable = false)
	public User getReporter() {
		return reporter;
	}

	public void setReporter(User reporter) {
		this.reporter = reporter;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REVIEWER_ID", insertable = false, updatable = false)
	public User getReviewer() {
		return reviewer;
	}

	public void setReviewer(User reviewer) {
		this.reviewer = reviewer;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TARGET_ID", insertable = false, updatable = false)
	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}

	@Column(name = "REASON")
	@Enumerated(EnumType.STRING)
	public UserReportedReason getReason() {
		return reason;
	}

	public void setReason(UserReportedReason reason) {
		this.reason = reason;
	}

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	public UserReportedStatus getStatus() {
		return status;
	}

	public void setStatus(UserReportedStatus status) {
		this.status = status;
	}

}