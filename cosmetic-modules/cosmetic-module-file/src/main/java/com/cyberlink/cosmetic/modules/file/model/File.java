package com.cyberlink.cosmetic.modules.file.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import com.cyberlink.cosmetic.core.model.AbstractEntity;

@Entity
@Table(name = "BC_FILE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class File extends AbstractEntity<Long> {
    private static final long serialVersionUID = 7666162427581097649L;

    private FileType fileType;
    
    private Long userId;
    
    private List<FileItem> fileItems = new ArrayList<FileItem>();
    private List<FileItem> avatarItems = new ArrayList<FileItem>();
    private List<FileItem> qualityItems = new ArrayList<FileItem>();
    private List<FileItem> listItems = new ArrayList<FileItem>();
    private List<FileItem> originalItems = new ArrayList<FileItem>();
  
	@Column(name = "FILE_TYPE")
    @Enumerated(EnumType.STRING)
    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Column(name = "USER_ID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        this.setShardId(userId);
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "file")
    @Where(clause = "IS_DELETED = 0")
    public List<FileItem> getFileItems() {
        return fileItems;
    }

    public void setFileItems(List<FileItem> fileItems) {
        this.fileItems = fileItems;
    }

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "file")
    @Where(clause = "IS_DELETED = 0 and THUMBNAIL_TYPE = 'Avatar'")
    public List<FileItem> getAvatarItems() {
		return avatarItems;
	}

	public void setAvatarItems(List<FileItem> avatarItems) {
		this.avatarItems = avatarItems;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "file")
    @Where(clause = "IS_DELETED = 0 and THUMBNAIL_TYPE = 'Quality65'")
	public List<FileItem> getQualityItems() {
		return qualityItems;
	}

	public void setQualityItems(List<FileItem> qualityItems) {
		this.qualityItems = qualityItems;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "file")
    @Where(clause = "IS_DELETED = 0 and THUMBNAIL_TYPE = 'List'")
	public List<FileItem> getListItems() {
		return listItems;
	}

	public void setListItems(List<FileItem> listItems) {
		this.listItems = listItems;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "file")
    @Where(clause = "IS_DELETED = 0 and IS_ORIGINAL = 1")
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<FileItem> getOriginalItems() {
		return originalItems;
	}

	public void setOriginalItems(List<FileItem> originalItems) {
		this.originalItems = originalItems;
	}
}
