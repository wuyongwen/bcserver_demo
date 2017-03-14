package com.cyberlink.cosmetic.modules.product.model;

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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.cosmetic.modules.user.model.User;

@Entity
@Table(name = "BC_REPORTED_PROD_COMMENT")
@DynamicUpdate
public class ReportedProdComment extends AbstractCoreEntity<Long>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
    private User reporter;
    private User reviewer ;
    private ProductComment reportedComment ;
    private ReportedProdCommentReason reportReason ;
    private ReportedProdCommentStatus reviewStatus ;
    private ReportedProdCommentResult reviewResult ;
    
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTER_ID")
	public User getReporter() {
		return reporter;
	}
    
	public void setReporter(User reporter) {
		this.reporter = reporter;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
	public ProductComment getReportedComment() {
		return reportedComment;
	}
	
	public void setReportedComment(ProductComment reportedComment) {
		this.reportedComment = reportedComment;
	}
	
	@Column(name = "REVIEW_STATUS")
	@Enumerated(EnumType.STRING)
	public ReportedProdCommentStatus getReviewStatus() {
		return reviewStatus;
	}
	
	public void setReviewStatus(ReportedProdCommentStatus reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
	
	@Column(name = "REVIEW_RESULT", nullable=true)
	@Enumerated(EnumType.STRING)
	public ReportedProdCommentResult getReviewResult() {
		return reviewResult;
	}
	
	public void setReviewResult(ReportedProdCommentResult reviewResult) {
		this.reviewResult = reviewResult;
	}
	
	@Column(name = "REPORT_REASON")
	@Enumerated(EnumType.STRING)
	public ReportedProdCommentReason getReportReason() {
		return reportReason;
	}
	
	public void setReportReason(ReportedProdCommentReason reportReason) {
		this.reportReason = reportReason;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name = "REVIEWER_ID")
	public User getReviewer() {
		return reviewer;
	}
	
	public void setReviewer(User reviewer) {
		this.reviewer = reviewer;
	}
	
}
