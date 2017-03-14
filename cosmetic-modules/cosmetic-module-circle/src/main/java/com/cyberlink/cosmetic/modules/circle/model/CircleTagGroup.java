package com.cyberlink.cosmetic.modules.circle.model;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_CIRCLE_TAG_GROUP")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class CircleTagGroup extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = -8654660673801276289L;

	private String circleTagGroupName;
	private Long circleId;
	private List<CircleTag> circleTag;
	
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }
    
    @JsonView(Views.Public.class)
    @Column(name = "CIRCLE_TAG_GROUP_NAME")
	public String getCircleTagGroupName() {
		return circleTagGroupName;
	}

	public void setCircleTagGroupName(String circleTagGroupName) {
		this.circleTagGroupName = circleTagGroupName;
	}

	@Column(name = "CIRCLE_ID")
	public Long getCircleId() {
		return circleId;
	}

	public void setCircleId(Long circleId) {
		this.circleId = circleId;
	}

	@Transient
	@JsonView(Views.Public.class)
	public List<CircleTag> getCircleTag() {
		return circleTag;
	}

	public void setCircleTag(List<CircleTag> circleTag) {
		this.circleTag = circleTag;
	}
}
