package com.cyberlink.cosmetic.modules.post.model;

import java.util.Date;

import javax.persistence.Cacheable;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.cosmetic.modules.user.model.User;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "BC_POST_SCORE")
@DynamicUpdate
public class PostScore extends AbstractCoreEntity<Long>{

    private static final long serialVersionUID = 989249166711543514L;

    private Long postId;
    private String postLocale;
    private CreatorType creatorType;
    private String appName;
    private Long circleTypeId;
    private Date postCreateDate;
    private Integer score;
    private PoolType poolType;
    private ResultType resultType;
    private Long reviewerId;
    private User Curator;
    private Boolean isHandled;
    private String info;

    public enum PoolType {
        Qualified(false, false, true, true, false, true, "Approve UGC Post", "Disapprove", true, null, false), 
        QualifiedNail(false, false, false, true, false, true, "Approve UGC Nail", "Disapprove", true, null, false), 
        RevQualified(false, true, true, true, false, true, "Review Disapproved UGC", "Disapprove", true, null, false), 
        RevQualifiedNail(false, true, false, true, false, true, "Review Disapproved UGC Nail", "Disapprove", true, null, false), 
        Disqualified(false, false, true, true, false, true, "Approve UGC Post (Low)", "Disapprove", true, null, false), 
        Retag(true, true, false, false, false, false, "Retag Approved Post", "Undecided", false, null, false),
        RetagScraped(true, true, true, true, false, true, "Cat/Tag Scraped Post", "No Change", true, 2, true),
        Violate(false, false, false, false, false, false, "Review Violated Post", "Undecided", false, null, false),
        NewCat(true, true, true, false, false, false, "Review New Cat. Post", "Undecided", false, null, false),
        Trending(true, true, true, false, false, false, "Review Trending Post", "Undecided", false, null, false),
        TrendingTest(true, true, true, false, false, false, "List Trending Post", "Undecided", false, null, false),
        Pgc(false, false, false, true, true, true, "Cat/Tag PGC Post", "OwnZone Only", true, null, false);
        
        private Boolean revived = false;
        private Boolean reviewed = false;
        private Boolean filterable = false;
        private Boolean multiCategory = false;
        private Boolean filterCreatorType = false;
        private Boolean enablePush = false;
        private String description = "";
        private String undecidedWord = "Undecided";
        private Boolean enableNewInfo = false;
        private Integer preSelectQuality = null;
        private Boolean preSelectCircle = false;
        
        PoolType(Boolean revived, Boolean reviewed, Boolean filterable, 
                Boolean multiCategory, Boolean filterCreatorType, Boolean enablePush,
                String description, String undecidedWord, Boolean enableNewInfo, 
                Integer preSelectQuality, Boolean preSelectCircle) {
            this.revived = revived;
            this.reviewed = reviewed;
            this.filterable = filterable;
            this.multiCategory = multiCategory;
            this.filterCreatorType = filterCreatorType;
            this.enablePush = enablePush;
            this.description = description;
            this.undecidedWord = undecidedWord;
            this.enableNewInfo = enableNewInfo;
            this.preSelectQuality = preSelectQuality;
            this.preSelectCircle = preSelectCircle;
        }
        
        public Boolean getRevived() {
            return revived;
        }
        
        public Boolean getReviewed() {
            return reviewed;
        }
        
        public Boolean getFilterable() {
            return filterable;
        }
        
        public Boolean getMultiCategory() {
            return multiCategory;
        }
        
        public Boolean getFilterCreatorType() {
            return filterCreatorType;
        }
        
        public Boolean getEnablePush() {
            return enablePush;
        }

        public String getDescription() {
            return description;
        }
        
        public String getUndecidedWord() {
            return undecidedWord;
        }
        
        public Boolean getEnableNewInfo() {
			return enableNewInfo;
		}
        
        public Integer getPreSelectQuality() {
            return preSelectQuality;
        }
        
        public Boolean getPreSelectCircle() {
            return preSelectCircle;
        }
    }
    
    public enum ResultType {
        Revive, Abandon, Selfie, SelfieDiscover, CatAndTrend, CatOnly, SelfieOnly, ChangeCircle, ChangeKeyWord, Remove, Reviewed;
    }
    
    public enum CreatorType {
        Publication, Beautyist, Brand;
    }
    
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
    
    @Column(name = "USER_TYPE")
    @Enumerated(EnumType.STRING)
    public CreatorType getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(CreatorType creatorType) {
        this.creatorType = creatorType;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEWER_ID", insertable=false, updatable=false)
	public User getCurator() {
		return Curator;
	}

	public void setCurator(User curator) {
		Curator = curator;
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
