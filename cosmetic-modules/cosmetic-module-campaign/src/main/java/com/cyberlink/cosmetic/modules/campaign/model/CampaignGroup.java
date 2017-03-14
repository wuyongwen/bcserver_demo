package com.cyberlink.cosmetic.modules.campaign.model;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "BC_CAMPAIGN_GROUP")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class CampaignGroup extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = 752749602803296296L;
	
	private String name;
	private String locale;
	private List<Campaign> campaigns;
	private Long rotationPeriod;

    @Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
        return id;
    }    

	@JsonView(Views.Public.class)
	@Column(name = "GROUP_NAME")	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonView(Views.Public.class)
	@Column(name = "LOCALE")	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "group", cascade={CascadeType.ALL})
	@Where(clause = "IS_DELETED = 0")
    @JsonView(Views.Public.class)
	public List<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

	@JsonView(Views.Public.class)
	@Column(name = "PERIOD")	
	public Long getRotationPeriod() {
		return rotationPeriod;
	}

	public void setRotationPeriod(Long rotationPeriod) {
		this.rotationPeriod = rotationPeriod;
	}


}
