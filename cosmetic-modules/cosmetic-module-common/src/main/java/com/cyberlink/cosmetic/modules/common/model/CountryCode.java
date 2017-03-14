package com.cyberlink.cosmetic.modules.common.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;

@Entity
@Table(name = "BC_COUNTRY_CODE")
public class CountryCode extends AbstractCoreEntity<Long> {
	private static final long serialVersionUID = 6055900844987175756L;

	private Long networkStart;
	private Long networkLast;
	private Long geonameId;
	private String countryCode;
	private Long shardId;

	@Id
	@GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
	@GeneratedValue(generator = "shardIdGenerator")
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	@Column(name = "NETWORK_START_INTEGER")
	public Long getNetworkStart() {
		return networkStart;
	}

	public void setNetworkStart(Long networkStart) {
		this.networkStart = networkStart;
	}

	@Column(name = "NETWORK_LAST_INTEGER")
	public Long getNetworkLast() {
		return networkLast;
	}

	public void setNetworkLast(Long networkLast) {
		this.networkLast = networkLast;
	}

	@Column(name = "GEONAME_ID")
	public Long getGeonameId() {
		return geonameId;
	}

	public void setGeonameId(Long geonameId) {
		this.geonameId = geonameId;
	}

	@Column(name = "COUNTRY_ISO_CODE")
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Column(name = "SHARD_ID")
	public Long getShardId() {
		return shardId;
	}

	public void setShardId(Long shardId) {
		this.shardId = shardId;
	}

}
