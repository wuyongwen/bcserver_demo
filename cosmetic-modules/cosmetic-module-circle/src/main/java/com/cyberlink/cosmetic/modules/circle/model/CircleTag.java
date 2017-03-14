package com.cyberlink.cosmetic.modules.circle.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_CIRCLE_TAG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class CircleTag extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = -7169028711681460736L;
	private String circleTagName;
	private Long circleTagGroupId;
	
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }
    
    @JsonView(Views.Public.class)
    @Column(name = "CIRCLE_TAG_NAME")
	public String getCircleTagName() {
		return circleTagName;
	}

	public void setCircleTagName(String circleTagName) {
		this.circleTagName = circleTagName;
	}

	@Column(name = "CIRCLE_TAG_GROUP_ID")
	public Long getCircleTagGroupId() {
		return circleTagGroupId;
	}

	public void setCircleTagGroupId(Long circleTagGroupId) {
		this.circleTagGroupId = circleTagGroupId;
	}
	
	
}
