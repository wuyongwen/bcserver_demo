package com.cyberlink.cosmetic.modules.product.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "BC_REDIRECT_LOG")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class RedirectLog extends AbstractCoreEntity<Long>{
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private Integer testGroup ;
	private String link;

	@Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonView(Views.Public.class)
    @Column(name = "TESTGROUP")
	public Integer getTestGroup() {
		return testGroup;
	}

	public void setTestGroup(Integer testGroup) {
		this.testGroup = testGroup;
	}

	@JsonView(Views.Public.class)
	@Column(name = "LINK")
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}
