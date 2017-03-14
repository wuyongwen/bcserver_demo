package com.cyberlink.cosmetic.modules.campaign.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_CAMPAIGN")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class Campaign extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = -6182536757214648579L;

	private String link;
	private File cover_1080;	
	private File cover_720;
	private CampaignGroup group;
	private Date endDate;
	
	private Long fileId;
	private Long file720Id;
	private Long file1080Id;
	private Long groupId;

    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
        return id;
    }    

	@JsonView(Views.Public.class)
	@Column(name = "LINK_URL")
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name = "GROUP_ID", insertable=false, updatable=false)
	public CampaignGroup getGroup() {
		return group;
	}

	public void setGroup(CampaignGroup group) {
		this.group = group;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name = "FILE_ID_1080", insertable=false, updatable=false)
	public File getCover_1080() {
		return cover_1080;
	}

	public void setCover_1080(File cover_1080) {
		this.cover_1080 = cover_1080;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name = "FILE_ID_720", insertable=false, updatable=false)
	public File getCover_720() {
		return cover_720;
	}

	public void setCover_720(File cover_720) {
		this.cover_720 = cover_720;
	}	
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE")
    @JsonView(Views.Public.class)	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Column(name = "FILE_ID")
	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	@Column(name = "FILE_ID_720")
	public Long getFile720Id() {
		return file720Id;
	}

	public void setFile720Id(Long file720Id) {
		this.file720Id = file720Id;
	}

	@Column(name = "FILE_ID_1080")
	public Long getFile1080Id() {
		return file1080Id;
	}

	public void setFile1080Id(Long file1080Id) {
		this.file1080Id = file1080Id;
	}

	@Column(name = "GROUP_ID")
	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
}
