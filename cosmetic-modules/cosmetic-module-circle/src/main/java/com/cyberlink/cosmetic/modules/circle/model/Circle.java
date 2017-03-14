package com.cyberlink.cosmetic.modules.circle.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.user.model.User;

@Entity
@Table(name = "BC_CIRCLE")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Circle extends AbstractCoreEntity<Long>{
    public static class ListCicleView extends Views.Public {
    }
    
    public static class UserCicleView extends ListCicleView {
    }
    
    public static int maxPostThumbnailSize = 3;
    
	private static final long serialVersionUID = 5361566760329657386L;

	private String circleName;
	private Long cricleTypeId;
	private CircleType circleType;
	private Long creatorId;
	private User creator;
	private Long iconId;
	private File iconFile;
	private String defaultType;
	private Boolean isSecret;
	private String description;
	private Long postCount = (long)0;
	private Long followerCount = (long)0;
	private List<String> postThumbnails = new ArrayList<String>();
	private Boolean isEditable = false;
	private Boolean isFollowed = false;
	private Boolean isCustomized = false;
	private Long circleCreatorId;
	private Long curUserId;
	private String translatedCircleName = null;
	
	@Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }    
        
	@JsonView(Views.Public.class)
	@Column(name = "CIRCLE_NAME")
	public String getCircleName() {
	    if(translatedCircleName != null)
	        return translatedCircleName;
	                
		return circleName;
	}

	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}

	@JsonView(Views.Public.class)
    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
	
	@JsonView(Views.Public.class)
	@Column(name = "CIRCLE_TYPE_ID")
	public Long getCricleTypeId() {
		return cricleTypeId;
	}

	public void setCricleTypeId(Long cricleTypeId) {
		this.cricleTypeId = cricleTypeId;
	}

	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CIRCLE_TYPE_ID", insertable=false, updatable=false)
    public CircleType getCircleType()
    {
        return circleType;
    }
	
	public void setCircleType(CircleType circleType)
    {
        this.circleType = circleType;
    }
	
	@Transient	
	@JsonView(Views.Public.class)
	public Long getCircleTypeId() {
		return getCricleTypeId();
	}
	
	@Transient	
	@JsonView(Views.Public.class)
	public String getIconUrl() {
		File f = getIconFile();
		if (f != null) {
    		List<FileItem> fl = f.getOriginalItems();
    		if (fl.size() > 0) {
    			return fl.get(0).getOriginalUrl();
    		} else {
    			fl = f.getFileItems();
    			if (fl.size() > 0){
    				return fl.get(0).getOriginalUrl();
    			}
    		}			
		}
		else {
		    if(postThumbnails != null && postThumbnails.size() > 0)
	            return postThumbnails.get(0);
		}
		return null;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID", insertable=false, updatable=false)
	public File getIconFile() {
	    if(getIconId() == null)
	        return null;
		return iconFile;
	}

	public void setIconFile(File iconFile) {
		this.iconFile = iconFile;
	}

	@Column(name = "FILE_ID")
    public Long getIconId() {
        return iconId;
    }

    public void setIconId(Long iconId) {
        this.iconId = iconId;
    }
	
    @Column(name = "USER_ID")
    public Long getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
    public User getCreator() {
        return this.creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }    

    @Transient 
    @JsonView(UserCicleView.class)
    public String getCreatorName() {
    	User user = getCreator();
    	if (user != null) {
    		return user.getDisplayName();
    	}
    	return null;
    }
    
    @Column(name = "IS_SECRET")
    @JsonView(Views.Public.class)
    public Boolean getIsSecret() {
        return this.isSecret == null ? false : this.isSecret;
    }

    public void setIsSecret(Boolean isSecret) {
        this.isSecret = isSecret;
    }
    
    @Column(name = "IS_CUSTOMIZED")
    public Boolean getIsCustomized() {
        return this.isCustomized == null ? false : this.isCustomized;
    }

    public void setIsCustomized(Boolean isCustomized) {
        this.isCustomized = isCustomized;
    }
    
    
    @Column(name = "DEFAULT_TYPE")
    @JsonView(Views.Public.class)
    public String getDefaultType() {
        return this.defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }
    
    @Transient 
    @JsonView(UserCicleView.class)
    public Long getPostCount() {
        return postCount < (long)0 ? (long)0 : postCount ;
    }
    
    public void setPostCount(Long postCount) {
        this.postCount += postCount;
    }
    
    @Transient 
    @JsonView(UserCicleView.class)
    public Long getFollowerCount() {
    	if (getIsSecret())
    		return (long)0;
        return followerCount;
    }
    
    public void setFollowerCount(Long followerCount) {
        this.followerCount += followerCount;
    }
    
    @Transient 
    @JsonView(ListCicleView.class)
    public List<String> getPostThumbnails() {
        return postThumbnails;
    }
    
    public void setPostThumbnails(String postThumbnail) {
        this.postThumbnails.add(postThumbnail);
    }
    
    @Transient 
    @JsonView(UserCicleView.class)
    public Boolean getIsEditable() {
        if(curUserId != null) {
            Long tmpCircleCreatorId = getCircleCreatorId();
            if(tmpCircleCreatorId != null)
                return tmpCircleCreatorId.equals(curUserId);
        }
        
        return false;
    }
    
    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }
    
    @Transient 
    @JsonView(UserCicleView.class)
    public Boolean getIsFollowed() {
        if(curUserId != null) {
            Long tmpCircleCreatorId = getCircleCreatorId();
            if(tmpCircleCreatorId != null)
                if(tmpCircleCreatorId.equals(curUserId))
                    return null;
        }
        
        if(isFollowed != null && isFollowed == true)
            return isFollowed;
        
        if(postCount <= 0)
            return null;
        
        return isFollowed == null ? false : isFollowed;
    }
    
    public void setIsFollowed(Boolean isFollowed) {
        this.isFollowed = isFollowed;
    }
    
    @Transient 
    @JsonView(Views.Public.class)
    public Long getCircleCreatorId() {
        if(creatorId == null && defaultType != null)
            return circleCreatorId;
        return creatorId;
    }
    
    public void setCircleCreatorId(Long circleCreatorId) {
        this.circleCreatorId = circleCreatorId;
    }
    
    @Transient 
    public Long getCurUserId() {
        return curUserId;
    }
    
    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    @Transient
    public void setTranslatedCircleName(String translatedCircleName) {
        this.translatedCircleName = translatedCircleName;
    }
}
