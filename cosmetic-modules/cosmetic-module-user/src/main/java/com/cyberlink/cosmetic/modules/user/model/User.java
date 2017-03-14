package com.cyberlink.cosmetic.modules.user.model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Where;

import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

@Entity
@Table(name = "BC_USER")
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
public class User extends AbstractCoreEntity<Long> implements Comparable<User>{
    private static final long serialVersionUID = 6776945907862240811L;

    public enum LookSource {
		ALL, YCL;
	}

	public enum LookStatus {
		PUBLISHED, HIDDEN
	}
	
    private Long id;
    private String displayName;
	private GenderType gender;
    private Date birthDay;
    private UserType userType;
    private UserSubType userSubType;
    private UserStatus userStatus;
    private String description;

    private String region;
    //private String mappedRegion;

	private File avatarFile;
    private File coverFile;
    private Long avatarId;
    private String avatarLink;
    private String avatarOri;
    

	private Long coverId;
    private String os;
    private String app;
    private String ipAddress;
    
	private List<Account> accountList = new ArrayList<Account>();
    private List<Account> allEmailAccountList = new ArrayList<Account>();
	private List<Attribute> attributeList = new ArrayList<Attribute>();
	private List<Session> sessionList = new ArrayList<Session>();
	private String attr;
	
	// personal information
	private String name;
	private String phone;
	private String mail;
	private String address;
	
	private LookSource lookSource;
    private LookStatus lookStatus;
    
    private String uniqueId;
    private Long level = 0l;
    private Boolean certify = Boolean.FALSE;
	
	@Transient
    private Long curUserId;
	
	@Transient
    private String token;
	
	@Transient
    private String encryption;
    
	@Transient
    private String attribute = "{}";
	
	@Transient
    private String moreInfo = "{}";
	
	@Transient
	private Boolean starOfWeek = Boolean.FALSE;

	@Transient
	private String badge = "";

	@Transient
    private Boolean isFollowed = Boolean.FALSE;
	
	@Transient
    private Boolean isBlocked = Boolean.FALSE;
	
	@Transient
	private Boolean isChatable = null;

	@Transient
    private Long followerCount = Long.valueOf(0);

	@Transient
    private Long sortValue = Long.valueOf(0);

	@Id
    @GenericGenerator(name = "userIdGenerator", strategy = "com.cyberlink.cosmetic.hibernate.id.UserIdGenerator")
    @GeneratedValue(generator = "userIdGenerator")
    @JsonView(Views.Public.class)   
    @Column(name = "ID", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "DISPLAY_NAME")
    @JsonView(Views.Public.class)    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public Boolean setDisplayNameNotInBlackList(String displayName) {
		if(displayName == null)
			return Boolean.FALSE;
		String userDisplayName =  displayName.replaceAll("\\s+","");
		try {
			final java.io.File file = new java.io.File(getClass().getClassLoader().getResource("celebrityBlacklist.csv").getFile());
			final Charset charset = Charset.defaultCharset();
			final CSVFormat csvFormat = CSVFormat.RFC4180;
			CSVParser parser = CSVParser.parse(file,charset,csvFormat);
			Set<String> blackNamesSet = new HashSet<String>();
	        for (CSVRecord record : parser) {
	            for (String value : record) {
	            	blackNamesSet.add(value.toLowerCase());
	            }
	        }
	        if(blackNamesSet.contains(userDisplayName.toLowerCase())){
	        	return Boolean.FALSE;
	        }
		} catch (IOException e){
			return Boolean.FALSE;
		}
		this.displayName = displayName;
		return Boolean.TRUE;
    }
    
    @Transient    
    @JsonView(Views.Public.class)   
    public String getAvatarUrl() {
    	String link = getAvatarLink();
    	if (link != null && link.length() > 0) {
    		return link;
    	} else {
    		if (getAvatarId() != null)
    			return "http://" + Constants.getWebsiteDomain() + "/api/file/download-file.action?getFile&fileId="+ getAvatarId() +"&thumbnailType=Avatar";
    		else
    			return null;
    	}
	}
    
    @Transient    
    public String getAvatarDetail() {
    	String ori = getAvatarOri();
    	if (ori != null && ori.length() > 0) {
    		return ori;
    	} else {
	    	if (getAvatarId() != null)
				return "http://" + Constants.getWebsiteDomain() + "/api/file/download-file.action?fileId="+ getAvatarId();
			else
				return null;
    	}
	}

    @Transient    
    @JsonView(Views.Public.class)   
	public String getCoverUrl() {
    	if (getCoverId() != null)
    		return "http://" + Constants.getWebsiteDomain() + "/api/file/download-file.action?getFile&fileId="+ getCoverId() + "&thumbnailType=Quality65";
    	else
    		return null;
	}

	@Transient
	@JsonView(Views.Public.class)
	public String getBgImageUrl() {
		if (hasKeyInAttr("bgImageUrl"))
			return getStringInAttr("bgImageUrl");
		else
			return null;
	}
	
	@Transient
	@JsonView(Views.Public.class)
	public String getWebsiteUrl() {
		if (hasKeyInAttr("websiteUrl"))
			return getStringInAttr("websiteUrl");
		else
			return null;
	}
	
	@Transient
	@JsonView(Views.Public.class)
	public String getIconUrl() {
		if (hasKeyInAttr("iconUrl"))
			return getStringInAttr("iconUrl");
		else
			return null;
	}

    @Column(name = "AVATAR_URL")
    @JsonView(Views.Public.class)
    public String getAvatarLink() {
		return avatarLink;
	}

	public void setAvatarLink(String avatarLink) {
		this.avatarLink = avatarLink;
	}

	@Column(name = "AVATAR_ORI")
	public String getAvatarOri() {
		return avatarOri;
	}

	public void setAvatarOri(String avatarOri) {
		this.avatarOri = avatarOri;
	}

	@Column(name = "GENDER")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    public GenderType getGender() {
        return gender;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    @Column(name = "BIRTH_DAY")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="GMT+00")
    @JsonView(Views.Public.class)
    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    @Column(name = "USER_TYPE")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Column(name = "USER_SUBTYPE")
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Public.class)
    public UserSubType getUserSubType() {
		return userSubType;
	}

	public void setUserSubType(UserSubType userSubType) {
		this.userSubType = userSubType;
	}

	@Column(name = "USER_STATUS")
    @Enumerated(EnumType.STRING)
	// This info is not necessary to the app on current version.
    //@JsonView(Views.Public.class) 
    public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	@Column(name = "DESCRIPTION")
    @JsonView(Views.Public.class)            
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "REGION")
    @JsonView(Views.Public.class)
    public String getRegion() {
    	return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVATAR_ID", insertable=false, updatable=false)
    public File getAvatarFile() {
        if(getAvatarId() == null)
            return null;
		return avatarFile;
	}

	public void setAvatarFile(File avatarFile) {
		this.avatarFile = avatarFile;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COVER_ID", insertable=false, updatable=false)
	public File getCoverFile() {
	    if(getCoverId() == null)
	        return null;
		return coverFile;
	}

	public void setCoverFile(File coverFile) {
		this.coverFile = coverFile;
	}

	@Column(name = "AVATAR_ID")
	public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    @Column(name = "COVER_ID")
    public Long getCoverId() {
        return coverId;
    }

    public void setCoverId(Long coverId) {
        this.coverId = coverId;
    }

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade={CascadeType.ALL})
	@Where(clause = "IS_DELETED = 0 and ACCOUNT_SOURCE = 'Email'")
	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	@Where(clause = "IS_DELETED = 0 and (EMAIL is not null or ACCOUNT_SOURCE = 'Email')")
	public List<Account> getAllEmailAccountList() {
		return allEmailAccountList;
	}

	public void setAllEmailAccountList(List<Account> allEmailAccountList) {
		this.allEmailAccountList = allEmailAccountList;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	@Where(clause = "IS_DELETED = 0 and REF_TYPE = 'User'")
	public List<Attribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<Attribute> attributeList) {
		this.attributeList = attributeList;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade={CascadeType.ALL})
	@Where(clause = "IS_DELETED = 0 and STATUS = 'SignIn'")
	@OrderBy("CREATED_TIME ASC")
	public List<Session> getSessionList() {
		return sessionList;
	}

	public void setSessionList(List<Session> sessionList) {
		this.sessionList = sessionList;
	}
	
    @Transient
    @JsonView(Views.Public.class) 
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attributes) {
        this.attribute = attributes;
    }

    @Transient
    @JsonView(Views.Public.class) 
    public Boolean getIsFollowed() {
    	UserType type = getUserType();
    	if((type != null && type == UserType.CL) || (curUserId != null && curUserId == getId().longValue()))
    		return null;
    	return isFollowed;
	}

	public void setIsFollowed(Boolean isFollowed) {
		this.isFollowed = isFollowed;
	}
	
	@Transient
	@JsonView(Views.Public.class)
	public Boolean getIsBlocked() {
		// Current allow block all user type
		/*UserType type = getUserType();
		if (type != null && !UserType.getAvailableBlockType().contains(type)
				|| (curUserId != null && curUserId == getId().longValue()))
			return null;*/
		if ((curUserId != null && curUserId == getId().longValue()))
			return null;
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	@Transient
	@JsonView(Views.Public.class)
	public Boolean getIsChatable() {
		UserType type = getUserType();
		if (isChatable != null)
			return isChatable;
		
		if (type == null)
			type = UserType.Normal;
		if (UserType.getChatableType().contains(type))
			return true;
		else
			return false;
	}

	public void setIsChatable(Boolean isChatable) {
		this.isChatable = isChatable;
	}

	@Transient
	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}
	
	@Column(name = "DEVICE_OS")
	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}
	
	@Column(name = "DEVICE_APP")
	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	@Column(name = "IP_ADDRESS")
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Column(name = "ATTRIBUTE")
	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}
	
	@Column(name = "LOOK_SOURCE")
	@Enumerated(EnumType.STRING)
    public LookSource getLookSource() {
		return lookSource;
	}

	public void setLookSource(LookSource lookSource) {
		this.lookSource = lookSource;
	}

	@Column(name = "LOOK_STATUS")
	@Enumerated(EnumType.STRING)
	public LookStatus getLookStatus() {
		return lookStatus;
	}

	public void setLookStatus(LookStatus lookStatus) {
		this.lookStatus = lookStatus;
	}

	@Column(name = "UNIQUE_ID")
	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Column(name = "LEVEL")
	@JsonView(Views.Public.class)
	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	@Column(name = "CERTIFY")
	@JsonView(Views.Public.class)
	public Boolean getCertify() {
		return certify;
	}

	public void setCertify(Boolean certify) {
		this.certify = certify;
	}

	@Transient
	public Boolean hasKeyInAttr(String key) {
		if (getAttr() != null){
			try {
				JsonObject obj = new JsonObject(attr);
				return obj.has(key);
			} catch (Exception e) {
				return Boolean.FALSE;
			}
		}
		return Boolean.FALSE;
	}
	
	@Transient
	public List<String> getListInAttr(String key) {
		List<String> list = new ArrayList<String>();
		if (getAttr() != null){
			try {
				JsonObject obj = new JsonObject(attr);
				JsonArray array = obj.getJsonArray(key);
				for (int i =0;i < array.length();i++) {
					list.add(array.getString(i));
				}
			} catch (Exception e) {
				return list;
			}
		}
		return list;
	}

	@Transient
	public Boolean setListInAttr(String key, List<String> list) {
		if (getAttr() != null) {
			JsonObject object = new JsonObject(getAttr());
			object.put(key, list);
			setAttr(object.toString());
		} else {
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put(key, list);
	    	try {
	    		setAttr(new ObjectMapper().writerWithView(Views.Public.class).writeValueAsString(attributes));
			} catch (JsonProcessingException e) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	@Transient
	public String getStringInAttr(String key) {
		String str = "";
		if (getAttr() != null){
			try {
				JsonObject obj = new JsonObject(attr);
				str = obj.getString(key);
			} catch (Exception e) {
				return "";
			}
		}
		return str;
	}

	@Transient
	public Boolean setStringInAttr(String key, String str) {
		if (getAttr() != null) {
			JsonObject object = new JsonObject(getAttr());
			object.put(key, str);
			setAttr(object.toString());
		} else {
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put(key, str);
	    	try {
	    		setAttr(new ObjectMapper().writerWithView(Views.Public.class).writeValueAsString(attributes));
			} catch (JsonProcessingException e) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
	
	@Transient
	@JsonView(Views.Public.class)
	public String getMoreInfo() {
		if (hasKeyInAttr("moreInfo"))
			moreInfo = getStringInAttr("moreInfo");
		return moreInfo;
	}

	@Transient
	@JsonView(Views.Public.class)
	public Boolean getStarOfWeek() {
		if (hasKeyInAttr("sow")) {
			String isStarOfWeek = getStringInAttr("sow");
			switch(isStarOfWeek) {
			case "1" :
				starOfWeek = Boolean.TRUE;
				break;
			case "0" :
			default :
				starOfWeek = Boolean.FALSE;
				break;
			}
		}
		return starOfWeek;
	}

	@Transient
	@JsonView(Views.Public.class)
	public String getBadge() {
		if (hasKeyInAttr("bdl")) {
			String badgeType = getStringInAttr("bdl");
			switch(badgeType) {
				case "Di" :
					badge = BadgeType.Diamond.name();
					break;
				case "Pl" :
					badge = BadgeType.Platinum.name();
					break;
				case "Go" :
					badge = BadgeType.Gold.name();
					break;
				case "Si" :
					badge = BadgeType.Silver.name();
					break;
				case "Nor" :
				default :
					badge = BadgeType.Normal.name();
					break;
			}
		}
		return badge;
	}
	
	@Transient
    @JsonView(Views.Public.class) 
	public Long getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(Long followerCount) {
		this.followerCount = followerCount;
	}
	
	@Transient
	public Long getSortValue() {
		return sortValue;
	}

	public void setSortValue(Long sortValue) {
		this.sortValue = sortValue;
	}

	@Override
	public int compareTo(User o) {
		if (this.getSortValue() > o.getSortValue()) {
			return 1;
		} else if (this.getSortValue() == o.getSortValue()) {
			return 0;
		} else {
			return -1;
		}		
	}

	@Column(name = "NAME")	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "PHONE")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "MAIL")
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Column(name = "ADDRESS")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Transient
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Transient
	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}	
}
