package com.cyberlink.cosmetic.action.api.user;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.post.event.PostViewUpdateEvent;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.event.ProfileUpdateEvent;
import com.cyberlink.cosmetic.modules.user.event.UserNameUpdateEvent;
import com.cyberlink.cosmetic.modules.user.event.UserSetRegionEvent;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.GenderType;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/update.action")
public class UpdateUserAction extends AbstractAction{
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("file.fileDao")
    private FileDao fileDao;

    @SpringBean("notify.NotifyService")
    private NotifyService notifyService;

    @SpringBean("file.fileItemDao")
    private FileItemDao fileItemDao;
    
    private String displayName;
    private String avatarUrl;
    private String coverUrl;
    private String websiteUrl;
	private String description = "";
    private GenderType gender;
    private String region;
    private String birthDay;
    private String attribute;
	private Long avatarId;
    private Long coverId;
	private Long bgImageId;
	private Long iconId;
    private List<String> tab;
    private String uniqueId;
    private AccountMailStatus mailStatus;
   
    // personal information
    private String name;
	private String phone;
	private String receiveEmail;
	private String address;
    
	@DefaultHandler
    public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
        if(redirect != null)
            return redirect;
        
    	if (!authenticateByRedis()) {
    		return new ErrorResolution(authError); 
    	} 
    	if (displayName != null && (displayName.equalsIgnoreCase("Beauty Circle") || 
    		displayName.equalsIgnoreCase("CyberLink") || displayName.equalsIgnoreCase("YouCam Makeup") ||
    		displayName.equalsIgnoreCase("YouCam Perfect"))) {
    		return new ErrorResolution(ErrorDef.InvalidName);
    	}
    	
        User user = getSession().getUser();
        String oldName = user.getDisplayName();
    	if(displayName != null && !user.setDisplayNameNotInBlackList(displayName)){ //check and set displayName
    		return new ErrorResolution(ErrorDef.InvalidName);
    	}
    	Boolean isNameUpdated = (displayName != null && !displayName.equalsIgnoreCase(oldName)) ? true : false;
    	
    	if (uniqueId != null) {
    		user.setUniqueId(uniqueId);
    	}
        
        if (avatarId != null && fileDao.exists(avatarId)) {
        	Boolean needUpdate = (user.getAvatarId() == null); 
        	user.setAvatarId(avatarId);
        	if (needUpdate)
        		notifyService.updateSenderAvatar(user.getId(), avatarId);
            
        	Long [] ids = new Long[1];
            ids[0] = avatarId;
            List<FileItem> listResults = fileItemDao.findThumbnails(ids, ThumbnailType.Avatar);
            if(listResults.size() > 0)
            	user.setAvatarLink(listResults.get(0).getOriginalUrl());
            else
            	user.setAvatarLink(null);
            
            // avatar detail
            FileItem ori = fileItemDao.findOriginal(avatarId);
            if (ori != null && ori.getIsOriginal())
            	user.setAvatarOri(ori.getOriginalUrl());
            else
            	user.setAvatarOri(null);
        }
        if (coverId != null && fileDao.exists(coverId)) {
        	user.setCoverId(coverId);
        }
        if (description == null) {
        	// App clear the description
        	user.setDescription("");
        } else if (description.length() != 0) {
            user.setDescription(description);
        }
        if (gender != null)
            user.setGender(gender);
        if (region != null && user.getUserType() == UserType.Normal) {
            user.setRegion(region);
            publishDurableEvent(new UserSetRegionEvent(user.getId(), region));
        }
        if (birthDay != null) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                user.setBirthDay(format.parse(birthDay));         
            } catch (ParseException e) {
            }
        }
        if (attribute != null && attribute.length() > 0) {
        	user.setStringInAttr("userAttr", attribute);
        }
		if (bgImageId != null && fileDao.exists(bgImageId)) {
			FileItem bgImage = fileItemDao.findOriginal(bgImageId);
			String bgImageUrl = bgImage.getOriginalUrl();
			user.setStringInAttr("bgImageUrl", bgImageUrl);
		}
		if (iconId != null && fileDao.exists(iconId)) {
			FileItem icon = fileItemDao.findOriginal(iconId);
			String iconUrl = icon.getOriginalUrl();
			user.setStringInAttr("iconUrl", iconUrl);
		}
        if (websiteUrl != null)
			user.setStringInAttr("websiteUrl", websiteUrl);
        if (user.getUserType() == null)
        	user.setUserType(UserType.Normal);
        
        if (tab != null) {
        	user.setListInAttr("tab", tab);
        }       
        if (name != null)
        	user.setName(name);
        if (phone != null)
        	user.setPhone(phone);
        if (receiveEmail != null)
        	user.setMail(receiveEmail);
        if (address != null)
        	user.setAddress(address);
        if (mailStatus != null) {
        	for (Account account : user.getAllEmailAccountList()) {
        		if (AccountMailStatus.INVALID.equals(account.getMailStatus()))
        			continue;
        		account.setMailStatus(mailStatus);
        	}
        }
        
		try {
			userDao.update(user);
		} catch (DataIntegrityViolationException e) {
			// Handle duplicated uniqueId
			Throwable tb = e.getCause();
			if (tb instanceof ConstraintViolationException) {
				if (tb.getCause() instanceof SQLException) {
					SQLException sqle = (SQLException) tb.getCause();
					if ("23000".equalsIgnoreCase(sqle.getSQLState()) && sqle.getErrorCode() == 1062)
							return new ErrorResolution(ErrorDef.DuplicatedUniqueId);
				}
			}
			return new ErrorResolution(ErrorDef.BadRequest);
		}
        publishDurableEvent(new PostViewUpdateEvent(user.getId(), user.getAvatarLink(), user.getUserType(), user.getCoverUrl(), user.getDescription(), user.getDisplayName()));
        publishDurableEvent(new ProfileUpdateEvent(user.getId()));
        if (isNameUpdated)
        	publishDurableEvent(new UserNameUpdateEvent(user.getId(), user.getDisplayName()));
        final Map<String, Object> results = new HashMap<String, Object>();
        results.put("userId", user.getId());
        return json(results);   
    }
    
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}
	
    public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		if (websiteUrl == null)
			this.websiteUrl = "";
		else
			this.websiteUrl = websiteUrl;
	}

	public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public GenderType getGender() {
        return gender;
    }
    public void setGender(GenderType gender) {
        this.gender = gender;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getBirthDay() {
        return birthDay;
    }
    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }
    public String getAttrs() {
        return attribute;
    }
    public void setAttrs(String attrs) {
        this.attribute = attrs;
    }
    public Long getAvatarId() {
		return avatarId;
	}
	public void setAvatarId(Long avatarId) {
		this.avatarId = avatarId;
	}
	public Long getCoverId() {
		return coverId;
	}
	public void setCoverId(Long coverId) {
		this.coverId = coverId;
	}
	public Long getBgImageId() {
		return bgImageId;
	}
	public void setBgImageId(Long bgImageId) {
		this.bgImageId = bgImageId;
	}
	public Long getIconId() {
		return iconId;
	}
	public void setIconId(Long iconId) {
		this.iconId = iconId;
	}
    public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public List<String> getTab() {
		if (tab == null)
			this.tab = new ArrayList<String>();
		return tab;
	}
	public void setTab(List<String> tab) {
		this.tab = tab;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public AccountMailStatus getMailStatus() {
		return mailStatus;
	}

	public void setMailStatus(AccountMailStatus mailStatus) {
		this.mailStatus = mailStatus;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if (name == null)
			this.name = "";
		else
			this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		if (phone == null)
			this.phone = "";
		else
			this.phone = phone;
	}
	public String getReceiveEmail() {
		return receiveEmail;
	}
	public void setReceiveEmail(String receiveEmail) {
		if (receiveEmail == null)
			this.receiveEmail = "";
		else
			this.receiveEmail = receiveEmail;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		if (address == null)
			this.address = "";
		else
			this.address = address;
	}
	
}
