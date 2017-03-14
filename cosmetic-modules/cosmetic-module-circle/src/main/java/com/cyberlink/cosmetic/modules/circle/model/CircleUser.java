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
@Table(name = "BC_CIRCLE_USER")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class CircleUser extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = 1086093100588709349L;
	
	private Long userId;
	private Long circleId;
	
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
	@Column(name = "CIRCLE_ID")
	public Long getCircleId() {
		return circleId;
	}

	public void setCircleId(Long circleId) {
		this.circleId = circleId;
	}
}
