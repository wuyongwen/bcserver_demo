package com.cyberlink.cosmetic.modules.post.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.post.result.AttachmentWrapper;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@DynamicUpdate
@Table(name = "BC_POST")
public class Post extends AbstractEntity<Long> {

    private static final long serialVersionUID = 3237513403541385804L;
    private Long parentId;
    private Long circleId;
    private Circle circle;
    private User creator;
    private Long creatorId;
    private String locale;
    private String countryCode;
    private String title;
    private String content;
    private String tags;
    private PostTags postTags;
    private Long lookTypeId;
    private String extLookUrl;
    private PostStatus postStatus;
    private Boolean gotProductTag;
    private List <Attachment> attachments = new ArrayList<Attachment>(0);
    private List <PostProduct> postProducts = new ArrayList<PostProduct>(0);
    private String postSource;
    private Long promoteScore;
    private Integer quality;
    private Long basicSortBonus;
    private AppName appName;
    private PostType postType = PostType.NORMAL;
    
    private List <AttachmentWrapper> attachmentsWrapper = new ArrayList<AttachmentWrapper>();
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    public User getCreator() {
        return this.creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
    
    @Column(name = "USER_ID")
    public Long getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    @Column(name = "PARENT_ID")
    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    @Column(name = "CIRCLE_ID")
    public Long getCircleId() {
        return this.circleId;
    }

    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CIRCLE_ID", insertable=false, updatable=false)
    public Circle getCircle() {
        return this.circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }
    
    @Column(name = "LOCALE", length = 8)
    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    @Column(name = "COUNTRY_CODE", length = 2)
    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    @Column(name = "TITLE", length = 100)
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "CONTENT", length = 16777215)
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "TAGS", length = 512)
    public String getTags() {
        return this.tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    @Column(name = "LOOK_TYPE_ID")
    public Long getLookTypeId() {
        return this.lookTypeId;
    }

    public void setLookTypeId(Long lookTypeId) {
        this.lookTypeId = lookTypeId;
    }
    
    @Column(name = "EXT_LOOK_URL", length = 256)
    public String getExtLookUrl() {
        return this.extLookUrl;
    }

    public void setExtLookUrl(String extLookUrl) {
        this.extLookUrl = extLookUrl;
    }

    @Column(name = "POST_STATUS")
    @Enumerated(EnumType.STRING)
    public PostStatus getPostStatus() {
        return this.postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }
    
    @Column(name = "GOT_PRODUCT_TAG")
    public Boolean getGotProductTag() {
        if(this.gotProductTag == null)
            return false;
        return this.gotProductTag;
    }

    public void setGotProductTag(Boolean gotProductTag) {
        this.gotProductTag = gotProductTag;
    }
    
    /*@Column(name = "CIRCLE_TAGS", length = 500)
    public String getCircleTags() {
        return this.circleTags;
    }

    public void setCircleTags(String circleTags) {
        this.circleTags = circleTags;
    }*/

    /*@Column(name = "LIKE_COUNT")
    public Long getLikeCount() {
        return this.likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    @Column(name = "COMMENT_COUNT")
    public Long getCommentCount() {
        return this.commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }*/
 
    @OneToMany(fetch = FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinColumn(name="POST_ID")
    @Where(clause="IS_DELETED=0")
    @JsonView(Views.Public.class)
    public List<Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
    
    @OneToMany(fetch = FetchType.LAZY, cascade={CascadeType.ALL})
	@Where(clause="IS_DELETED=0")
    @BatchSize(size = 180)
    @JoinColumn(name="POST_ID")
    @JsonView(Views.Public.class)
    public List<PostProduct> getPostProducts() {
        return this.postProducts;
    }

    public void setPostProducts(List<PostProduct> postProducts) {
        this.postProducts = postProducts;
    }
    
    @Column(name = "POST_SOURCE")
    public String getPostSource() {
        return this.postSource;
    }

    public void setPostSource(String postSource) {
        this.postSource = postSource;
    }
    
    @Column(name = "PROMOTE_SCORE")
    public Long getPromoteScore() {
        return this.promoteScore;
    }

    public void setPromoteScore(Long promoteScore) {
        this.promoteScore = promoteScore;
    }

    @Column(name = "QUALITY")
    public Integer getQuality() {
        return this.quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }
    
    @Column(name = "BASIC_SORT_BONUS")
    public Long getBasicSortBonus() {
        return this.basicSortBonus;
    }

    public void setBasicSortBonus(Long basicSortBonus) {
        this.basicSortBonus = basicSortBonus;
    }
    
    @Column(name = "APP_NAME")
    @Enumerated(EnumType.STRING)
    public AppName getAppName() {
        return appName;
    }

    public void setAppName(AppName appName) {
        this.appName = appName;
    }
    
    @Column(name = "POST_TYPE")
    @Enumerated(EnumType.STRING)
    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }
    
    @Transient
    public List<AttachmentWrapper> getAttachmentsWrapper() {
		return attachmentsWrapper;
	}

	public void setAttachmentsWrapper(List<AttachmentWrapper> attachmentsWrapper) {
		this.attachmentsWrapper = attachmentsWrapper;
	} 
	
    @Transient
    public String getRedirectUrl() {
    	JsonNode attrNode = attachments.get(0).getAttachmentFile().getFileItems().get(0).getMetadataJson().get("redirectUrl");
    	if(attrNode != null) {
    		try {
    		    return URLDecoder.decode(attrNode.asText(), "UTF-8");
    		} catch (UnsupportedEncodingException e) {
    		    // TODO Auto-generated catch block
    		    e.printStackTrace();
    		}
    	}
    	return "";
	}
    
    @Transient
    public Boolean getIsWidget() {
    	JsonNode attrNode = attachments.get(0).getAttachmentFile().getFileItems().get(0).getMetadataJson().get("isWidget");
    	if(attrNode != null) {
    		try {
    		    return attrNode.asBoolean();
    		} catch (Exception e) {
    		    // TODO Auto-generated catch block
    		    e.printStackTrace();
    		}
    	}
    	return Boolean.FALSE;
	}
 
    @Transient
    public PostTags getPostTags() {
        if(postTags != null)
            return postTags;
        if(tags == null || tags.length() <= 0)
            return null;
        try {
            ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            postTags = m.readValue(tags, PostTags.class);
            return postTags;
        } catch (Exception e) {
        }
        return null;
    }
}
