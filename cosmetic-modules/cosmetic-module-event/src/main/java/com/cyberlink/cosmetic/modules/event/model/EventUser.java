package com.cyberlink.cosmetic.modules.event.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
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
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.restfb.json.JsonObject;

@Entity
@Table(name = "BC_EVENT_USER")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class EventUser extends AbstractCoreEntity<Long>{
	private static final long serialVersionUID = 686146989088206572L;

	private Long userId;
	private User creator;
	private Long eventId;
	private BrandEvent brandEvent;
	private String name;
	private String phone;
	private String mail;
	private String address;
	private EventUserStatus userStatus;
	private String code;
	private Boolean isInvalid = Boolean.FALSE;

	@Id
    @GenericGenerator(name = "shardIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.ShardIdGenerator")
    @GeneratedValue(generator = "shardIdGenerator")
    @JsonView(Views.Public.class)
    @Column(name = "ID", unique = true, nullable = false)
	public Long getId() {
        return id;
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
    public User getCreator() {
        return this.creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    } 
	
	@Column(name = "EVENT_ID")	
	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID", insertable=false, updatable=false)
	public BrandEvent getBrandEvent() {
		if(getEventId() == null)
	        return null;
		return brandEvent;
	}

	public void setBrandEvent(BrandEvent brandEvent) {
		this.brandEvent = brandEvent;
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
	@Column(name = "PHONE")	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "MAIL")	
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
	
	@JsonView(Views.Public.class)
	@Column(name = "ADDRESS")	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@JsonView(Views.Public.class)
	@Enumerated(EnumType.STRING)
	@Column(name = "Status")	
	public EventUserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(EventUserStatus userStatus) {
		this.userStatus = userStatus;
	}
	
	@Column(name = "CODE")	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "IS_INVALID", nullable=false)
	public Boolean getIsInvalid() {
		return isInvalid;
	}

	public void setIsInvalid(Boolean isInvalid) {
		this.isInvalid = isInvalid;
	}

	@Transient
	@JsonView(Views.Public.class)
	public String getAvatarUrl() {
		User creator = getCreator();
		if (creator != null) {
			return creator.getAvatarUrl();
		}
		return null;
	}
	
	@Transient
	@JsonView(Views.Public.class)
	public String getDisplayName() {
		User creator = getCreator();
		if (creator != null) {
			return creator.getDisplayName();
		}
		return null;
	}

	@Transient
	@JsonView(Views.Public.class)
	public String getImageUrl() {
		BrandEvent be = getBrandEvent();
		if (be != null) {
			return be.getImageUrl();
		}
		return null;
	}
	
	@Transient
	@JsonView(Views.Public.class)
	public String getProdName() {
		BrandEvent be = getBrandEvent();
		if (be != null) {
			return be.getProdName();
		}
		return null;
	}

	@Transient
	@JsonView(Views.Public.class)
	public String getReceiveTemplate() {
		BrandEvent be = getBrandEvent();
		if (be != null) {
			return be.getReceiveTemplate();
		}
		return null;
	}
	
	@Transient
	@Enumerated(EnumType.STRING)
	@JsonView(Views.Public.class)
	public ReceiveType getReceiveType() {
		BrandEvent be = getBrandEvent();
		if (be != null) {
			return be.getReceiveType();
		}
		return null;
	}
	
	@Transient
	@JsonView(Views.Public.class)
	public String getTitle() {
		BrandEvent be = getBrandEvent();
		if (be != null) {
			return be.getTitle();
		}
		return null;
	}
	
	@Transient
	@Enumerated(EnumType.STRING)
	@JsonView(Views.Public.class)
	public ServiceType getServiceType() {
		BrandEvent be = getBrandEvent();
		if (be != null) {
			return be.getServiceType();
		}
		return null;
	}
	
	@Transient
	@JsonView(Views.Public.class)
	public String getOrganizerName() {
		BrandEvent be = getBrandEvent();
		if (be != null) {
			return be.getOrganizerName();
		}
		return null;
	}
	
	@Transient
	@JsonView(Views.Public.class)
	public String getOrganizerLogo() {
		BrandEvent be = getBrandEvent();
		if (be != null) {
			return be.getOrganizerLogo();
		}
		return null;
	}
	
	@Transient
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="GMT+00")
    @JsonView(Views.Public.class)
    public Date getBirthDay() {
        return creator.getBirthDay();
    }
	
	@Transient
	public String getBirthDayString() {
        User creator = getCreator();
		if (creator != null) {
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd");
			try {
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				return dateFormatGmt.format(creator.getBirthDay());
			} catch (Exception e){
				return "";
			}
		}
		return "";
    }
	
	@Transient
	public String getUserAddress() {
		String address = getAddress();
		if (address != null && !address.isEmpty()) {
			try {
				JsonObject obj = new JsonObject(address);
				if (obj.has("userAddress"))
					return obj.getString("userAddress");
				else if (ReceiveType.Home.equals(getReceiveType())
						&& obj.has("address"))
					return obj.getString("address");
				else
					return "";
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	@Transient
	public String getStoreAddress() {
		String address = getAddress();
		if (address != null && !address.isEmpty()) {
			try {
				JsonObject obj = new JsonObject(address);
				if (ReceiveType.Store.equals(getReceiveType())
						&& obj.has("address"))
					return obj.getString("address");
				else
					return "";
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	@Transient
	public String getStoreLocation() {
		String address = getAddress();
		if (address != null && !address.isEmpty()) {
			try {
				JsonObject obj = new JsonObject(address);
				if (obj.has("location"))
					return obj.getString("location");
				else
					return "";
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	@Transient
	public String getStoreName() {
		String address = getAddress();
		if (address != null && !address.isEmpty()) {
			try {
				JsonObject obj = new JsonObject(address);
				if (obj.has("name"))
					return obj.getString("name");
				else
					return "";
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}
}
