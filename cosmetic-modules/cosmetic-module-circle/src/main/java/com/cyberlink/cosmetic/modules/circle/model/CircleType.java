package com.cyberlink.cosmetic.modules.circle.model;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_CIRCLE_TYPE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class CircleType extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = -8539595970328603443L;
	private String circleTypeName;
	private String locale;
	private Long fileId;
	private File file;
	private Long circleTypeGroupId;
	private Boolean isVisible;
	private CircleTypeGroup circleTypeGroup;
	private String imgUrl;

    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }
    
    @JsonView(Views.Public.class)
    @Column(name = "CIRCLE_TYPE_NAME")
	public String getCircleTypeName() {
		return circleTypeName;
	}

	public void setCircleTypeName(String circleTypeName) {
		this.circleTypeName = circleTypeName;
	}	
	
    @Column(name = "LOCALE")
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID", insertable=false, updatable=false)
    public File getFile() {
        if(getFileId() == null)
            return null;
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Column(name = "FILE_ID")
    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
    
    @Column(name = "CIRCLE_TYPE_GROUP_ID")
    public Long getCircleTypeGroupId() {
        return circleTypeGroupId;
    }

    public void setCircleTypeGroupId(Long circleTypeGroupId) {
        this.circleTypeGroupId = circleTypeGroupId;
    }
   
    @Column(name = "IS_VISIBLE")
    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }
    
    @JsonView(Views.Public.class)
    @Column(name = "IMG_URL")
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CIRCLE_TYPE_GROUP_ID", insertable=false, updatable=false)
    public CircleTypeGroup getCircleTypeGroup() {
        return circleTypeGroup;
    }

    public void setCircleTypeGroup(CircleTypeGroup circleTypeGroup) {
        this.circleTypeGroup = circleTypeGroup;
    }
    
    @Transient  
    @JsonView(Views.Public.class)
    public String getIconUrl() {
        File f = getFile();
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
        return null;
    }
    
    @Transient  
    @JsonView(Views.Public.class)
    public String getDefaultType() {
        if(circleTypeGroup == null)
            return null;
        return circleTypeGroup.getDefaultTypeName();
    }

}
