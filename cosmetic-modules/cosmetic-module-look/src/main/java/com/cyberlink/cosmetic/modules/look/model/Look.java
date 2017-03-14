package com.cyberlink.cosmetic.modules.look.model;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_LOOK")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Look extends AbstractCoreEntity<Long>{

	private static final long serialVersionUID = 139764891356225880L;
	
	private Long userId;
	private Long postId;
	private Long typeId;
	private String featureRoomId;
	private String name;
	private String description;
	private String imageUrls;
	private String attachmentUrl;
	private Long likeCount = 0L;
	private Long downloadCount = 0L;
	
	@Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
        return id;
    }    
	
	@JsonView(Views.Public.class)
	@Column(name = "USER_ID")	
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@JsonView(Views.Public.class)
	@Column(name = "POST_ID")
	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "TYPE_ID")
	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "FEATURE_ROOM_ID")	
    public String getFeatureRoomId() {
		return featureRoomId;
	}

	public void setFeatureRoomId(String featureRoomId) {
		this.featureRoomId = featureRoomId;
	}

	@JsonView(Views.Public.class)
	@Column(name = "NAME")	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	@Column(name = "IMAGE_URLS")	
	public String getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(String imageUrls) {
		this.imageUrls = imageUrls;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "ATTACHMENT_URL")	
	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "LIKE_COUNT")
	public Long getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "DOWNLOAD_COUNT")
	public Long getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(Long downloadCount) {
		this.downloadCount = downloadCount;
	}

}
