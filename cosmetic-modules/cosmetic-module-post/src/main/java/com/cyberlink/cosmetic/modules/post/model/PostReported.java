package com.cyberlink.cosmetic.modules.post.model;

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

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.MetaValue;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.cosmetic.modules.user.model.User;

@Entity
@Table(name = "BC_POST_REPORTED")
@DynamicUpdate
public class PostReported extends AbstractCoreEntity<Long>{

    public enum PostReportedReason {
        Inappropriate, Copyright, Other;
    }
    
    public enum PostReportedStatus {
        NewReported, Reviewed;
    }
    
    public enum PostReportedResult {
        Published, Banned;
    }
    
    private static final long serialVersionUID = 8980817291746827920L;
    private Long id;
    private User reporter;
    private User reviewer;
    private Object target ;
    private PostReportedReason reason ;
    private PostReportedStatus status ;
    private PostReportedResult result ;
    private String remark;
    
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
    @JoinColumn(name = "REVIEWER_ID")
    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }
    
	@Any(metaColumn = @Column(name = "REF_TYPE"))
    @AnyMetaDef(idType = "long", metaType = "string", 
            metaValues = { 
             @MetaValue(targetEntity = Post.class, value = "Post"),
             @MetaValue(targetEntity = Comment.class, value = "Comment")
       })
    @JoinColumn(name="REF_ID")
	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	@Column(name = "REASON")
    @Enumerated(EnumType.STRING)
    public PostReportedReason getReason() {
        return reason;
    }

    public void setReason(PostReportedReason reason) {
        this.reason = reason;
    }
	
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    public PostReportedStatus getStatus() {
        return status;
    }

    public void setStatus(PostReportedStatus status) {
        this.status = status;
    }

	@Column(name = "RESULT")
    @Enumerated(EnumType.STRING)
    public PostReportedResult getResult() {
        return result;
    }

    public void setResult(PostReportedResult result) {
        this.result = result;
    }
    
    @Column(name = "REMARK")
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark){
        this.remark = remark;
    }
    
}
