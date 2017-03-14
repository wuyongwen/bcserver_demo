package com.cyberlink.cosmetic.modules.file.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.slf4j.LoggerFactory;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "BC_FILE_ITEM")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class FileItem extends AbstractEntity<Long> {
    private static final long serialVersionUID = -4490142754517063352L;
   
    private File file;
    
    private String filePath;
    
    private String fileName;
    
    private Long fileSize;
    
    private String contentType;
    
    private String md5;
    
    private byte[] md5Bytes;
    
    private Integer width;
    
    private Integer height;
    
    private Integer orientation;
    
    private String metadata;
    
    private Boolean isOriginal;
    
    private ThumbnailType thumbnailType;
   
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "FILE_ID")
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Column(name = "FILE_PATH")
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Column(name = "FILE_NAME")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Column(name = "FILE_SIZE")
    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Column(name = "CONTENT_TYPE")
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Column(name = "MD5")
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Transient
    public byte[] getMd5Bytes() {
        return md5Bytes;
    }

    @Transient
    public void setMd5Bytes(byte[] md5Bytes) {
        this.md5Bytes = md5Bytes;
    }

    @Column(name = "WIDTH")
    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    @Column(name = "HEIGHT")
    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Column(name = "ORIENTATION")
    public Integer getOrientation() {
        return orientation;
    }

    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }

    @Column(name = "META_DATA")
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Column(name = "IS_ORIGINAL")
    public Boolean getIsOriginal() {
        return isOriginal;
    }

    public void setIsOriginal(Boolean isOriginal) {
        this.isOriginal = isOriginal;
    }
    
    @Column(name = "THUMBNAIL_TYPE")
    @Enumerated(EnumType.STRING)
    public ThumbnailType getThumbnailType() {
        return thumbnailType;
    }

    public void setThumbnailType(ThumbnailType thumbnailType) {
        this.thumbnailType = thumbnailType;
    }

    @Transient
    public JsonNode getMetadataJson() {
        if (StringUtils.isBlank(metadata))
            return null;
      
        try {
            ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            return m.readValue(metadata, JsonNode.class);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("", e);
        }
        return null;
    }
    
    @Transient
    public String getLocalFilePath() {
       return Constants.getStorageLocalRoot() + "/" + filePath; 
    }
    
    @Transient
    public String getLocalFilePath(String filePath) {
       return Constants.getStorageLocalRoot() + "/" + filePath; 
    }
    
    @Transient
    public String getOriginalUrl() {
    	String cdnDomain;
    	if (Constants.getIsCN())
    		cdnDomain = Constants.getOSSDomain();
    	else {
    		cdnDomain = Constants.getBcCdnDomain();
    		if(cdnDomain == null || cdnDomain.length() <= 0)
                cdnDomain = Constants.getCdnDomain();
    	}    		
        return "http://" + cdnDomain + "/" + filePath; 
    }
    
    @Transient
    public Double aspectRatio() {
        if (width != null && height != null && !height.equals(0))
            return (double) width / (double) height;
        else
            return null;
    }
}
