package com.cyberlink.cosmetic.modules.post.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.ResultType;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "BC_POST_SCORE_TREND")
@DynamicUpdate
public class PostScoreTrend extends AbstractCoreEntity<Long>{

	private static final long serialVersionUID = -5014842295435493121L;
	
	private Long postId;
    private String postLocale;
    private String appName;
    private Long circleTypeId;
    private Date postCreateDate;
    private Integer score;
    private PoolType poolType;
    private ResultType resultType;
    private Long reviewerId;
    private Boolean isHandled;
    private String info;

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

    @Column(name = "POST_ID")
    public Long getPostId() {
        return postId;
    }
    
    public void setPostId(Long postId){
        this.postId = postId;
    }
    
    @Column(name = "POST_LOCALE")
    public String getPostLocale() {
        return postLocale;
    }
    
    public void setPostLocale(String postLocale){
        this.postLocale = postLocale;
    }
    
    @Column(name = "APP_NAME")
    public String getAppName() {
        return appName;
    }
    
    public void setAppName(String appName){
        this.appName = appName;
    }
    
    @Column(name = "CIRCLE_TYPE_ID")
    public Long getCircleTypeId() {
        return circleTypeId;
    }
    
    public void setCircleTypeId(Long circleTypeId){
        this.circleTypeId = circleTypeId;
    }
    
    @Column(name = "POST_CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPostCreateDate() {
        return postCreateDate;
    }
    
    public void setPostCreateDate(Date postCreateDate){
        this.postCreateDate = postCreateDate;
    }
    
    @Column(name = "SCORE")
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score){
        this.score = score;
    }
    
    @Enumerated(EnumType.STRING)
    @Column(name = "POOL_TYPE")
    public PoolType getPoolType() {
        return poolType;
    }
    
    public void setPoolType(PoolType poolType){
        this.poolType = poolType;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "RESULT_TYPE")
    public ResultType getResultType() {
        return resultType;
    }
    
    public void setResultType(ResultType resultType){
        this.resultType = resultType;
    }
    
    @Column(name = "REVIEWER_ID")
    public Long getReviewerId() {
        return reviewerId;
    }
    
    public void setReviewerId(Long reviewerId){
        this.reviewerId = reviewerId;
    }
    
    @Column(name = "IS_HANDLED")
    public Boolean getIsHandled() {
        return isHandled;
    }

    public void setIsHandled(Boolean isHandled) {
        this.isHandled = isHandled;
    }
    
    @Column(name = "INFO")
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    
}
