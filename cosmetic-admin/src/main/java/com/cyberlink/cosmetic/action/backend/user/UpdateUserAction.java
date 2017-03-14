package com.cyberlink.cosmetic.action.backend.user;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.notify.service.NotifyService;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.event.UserNameUpdateEvent;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.GenderType;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/update.action")
public class UpdateUserAction extends AbstractAction {
	@SpringBean("user.UserDao")
	private UserDao userDao;

	@SpringBean("file.fileDao")
	private FileDao fileDao;

	@SpringBean("notify.NotifyService")
	private NotifyService notifyService;

	@SpringBean("file.fileItemDao")
	private FileItemDao fileItemDao;

	@SpringBean("user.AccountDao")
	protected AccountDao accountDao;

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
	private String email;
	private String moreInfo;

	@DefaultHandler
	public Resolution route() {
		if (email == null) {
			return new ErrorResolution(ErrorDef.InvalidAccount);
		}
		if (displayName != null
				&& (displayName.equalsIgnoreCase("Beauty Circle")
						|| displayName.equalsIgnoreCase("CyberLink")
						|| displayName.equalsIgnoreCase("YouCam Makeup") || displayName
							.equalsIgnoreCase("YouCam Perfect"))) {
			return new ErrorResolution(ErrorDef.InvalidName);
		}
		Account account = accountDao.findBySourceAndReference(
				AccountSourceType.Email, email);
		if (account == null) {
			return new ErrorResolution(ErrorDef.InvalidAccount);
		}
		User user = account.getUser();
		
		String oldName = user.getDisplayName();
		if (displayName != null)
			user.setDisplayName(displayName);
		Boolean isNameUpdated = (displayName != null && !displayName.equalsIgnoreCase(oldName)) ? true : false;

		if (avatarId != null && fileDao.exists(avatarId)) {
			Boolean needUpdate = (user.getAvatarId() == null);
			user.setAvatarId(avatarId);
			if (needUpdate)
				notifyService.updateSenderAvatar(user.getId(), avatarId);

			Long[] ids = new Long[1];
			ids[0] = avatarId;
			List<FileItem> listResults = fileItemDao.findThumbnails(ids,
					ThumbnailType.Avatar);
			if (listResults.size() > 0)
				user.setAvatarLink(listResults.get(0).getOriginalUrl());
			else
				user.setAvatarLink(null);
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
		if (region != null)
			user.setRegion(region);
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
		
		if (moreInfo != null)
			user.setStringInAttr("moreInfo", moreInfo);
			
		if (user.getUserType() == null)
			user.setUserType(UserType.Normal);

		if (tab != null) {
			user.setListInAttr("tab", tab);
		}
		userDao.update(user);
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(String moreInfo) {
		this.moreInfo = moreInfo;
	}
}
