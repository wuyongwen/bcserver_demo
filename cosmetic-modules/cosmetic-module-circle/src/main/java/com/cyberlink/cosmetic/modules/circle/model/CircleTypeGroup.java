package com.cyberlink.cosmetic.modules.circle.model;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@Table(name = "BC_CIRCLE_TYPE_GROUP")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class CircleTypeGroup extends AbstractCoreEntity<Long>{

	private static final long serialVersionUID = 500939102159337058L;

    private String groupName;
	private Integer sortOrder;
	private String imgUrl;
	private List<CircleType> circleTypes;
	
    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return id;
    }
    
    @Column(name = "GROUP_NAME")
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}	
	
    @Column(name = "SORT_ORDER")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
	
    @Column(name = "IMG_URL")
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="CIRCLE_TYPE_GROUP_ID")
	public List<CircleType> getCircleTypes() {
        return this.circleTypes;
    }
	
	public void setCircleTypes(List<CircleType> circleTypes) {
        this.circleTypes = circleTypes;
    }
	
	@Transient
	public String getDefaultTypeName() {
	    if(groupName == null)
	        return "";
	    return groupName.toUpperCase().replace(" ", "_");
	}
	
}
