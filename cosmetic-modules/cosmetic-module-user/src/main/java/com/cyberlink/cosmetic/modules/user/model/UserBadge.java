package com.cyberlink.cosmetic.modules.user.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;

@Entity
@Table(name = "BC_USER_BADGE")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class UserBadge extends AbstractCoreEntity<Long> {

	private static final long serialVersionUID = 7017550790784520760L;

	public enum BadgeType {
		StarOfWeek(true, "SoW", 0),
		Diamond(false, "Di", 5),
		Platinum(false, "Pl", 4),
		Gold(false, "Go", 3),
		Silver(false, "Si", 2),
		Normal(false, "Nor", 1);

		BadgeType(Boolean isStar, String badgeTypeSign, Integer priority){
			this.isStar = isStar;
			this.badgeTypeSign = badgeTypeSign;
			this.priority = priority;
		}
		
		private Boolean isStar = false;
		private String badgeTypeSign = "Nor";
		private Integer priority = 0;

		public Boolean getIsStar() {
			return isStar;
		}
		
		public String getBadgeTypeSign() {
			return badgeTypeSign;
		}
		
		public Integer getPriority() {
			return priority;
		}
	}
	
	private Long userId;
	private User user;
	private String locale;
	private BadgeType badgeType;
	private Long score;

	@Id
	@GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
	@GeneratedValue(generator = "shardIdGenerator")
	@Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	@Column(name = "USER_ID")
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", insertable=false, updatable=false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Column(name = "LOCALE")
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "BADGE_TYPE")
	public BadgeType getBadgeType() {
		return badgeType;
	}

	public void setBadgeType(BadgeType badgeType) {
		this.badgeType = badgeType;
	}
	
	@Column(name = "SCORE")
	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}
}