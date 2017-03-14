package com.cyberlink.cosmetic.action.backend.v2.user;

import java.util.List;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.IndexAction;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.file.service.StorageService;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.event.UserNameUpdateEvent;
import com.cyberlink.cosmetic.modules.user.model.User;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/v2/user/editCurrentUser.action")
public class EditCurrentUser extends AbstractAction {
    @SpringBean("user.UserDao")
    private UserDao userDao;

    @SpringBean("file.fileService")
	private FileService fileService;

    @SpringBean("file.fileDao")
    private FileDao fileDao;
    
    @SpringBean("file.storageService")
	private StorageService storageService;

    @SpringBean("file.fileItemDao")
    private FileItemDao fileItemDao;
    
    @SpringBean("user.MemberDao")
	protected MemberDao memberDao;
    
	private User user;
	private String bgImageUrl;
	private String iconUrl;
    private Long bgImageId;
    private Long iconId;
	
	static final String editCurrentUser = "/v2/user/editCurrentUser-route.jsp" ;
	
	@DefaultHandler
	public Resolution route() {
		user = getCurrentUser();
    	if (user == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
    	bgImageUrl = user.getBgImageUrl();
    	iconUrl = user.getIconUrl();
        return forward();
    }
	
    public Resolution save() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
    	
    	Long avatarId = null;
        if (user.getAvatarId() != null)
        	avatarId = user.getAvatarId();
        Long coverId = null;
        if (user.getCoverId() != null)
        	coverId = user.getCoverId();
        User oriUser = getCurrentUser();
        String oldName = oriUser.getDisplayName();
        oriUser.setDisplayName(user.getDisplayName());
        Boolean isNameUpdated = (user.getDisplayName() != null && !user.getDisplayName().equalsIgnoreCase(oldName)) ? true : false;
        oriUser.setDescription(user.getDescription());
        if (avatarId != null) {
        	oriUser.setAvatarId(avatarId);
        	Long [] ids = new Long[1];
            ids[0] = avatarId;
            List<FileItem> listResults = fileItemDao.findThumbnails(ids, ThumbnailType.Avatar);
            if(listResults.size() > 0)
            	oriUser.setAvatarLink(listResults.get(0).getOriginalUrl());
            else
            	oriUser.setAvatarLink(null);
            
            // avatar detail
            FileItem ori = fileItemDao.findOriginal(avatarId);
            if (ori != null && ori.getIsOriginal())
            	oriUser.setAvatarOri(ori.getOriginalUrl());
            else
            	oriUser.setAvatarOri(null);

        }
        if (coverId != null) {
        	oriUser.setCoverId(coverId);
        }
    	if (bgImageId != null) {
    		FileItem bgImage = fileItemDao.findOriginal(bgImageId);
			bgImageUrl = bgImage.getOriginalUrl();
			oriUser.setStringInAttr("bgImageUrl", bgImageUrl);
			bgImageId = null;
    	} else if (bgImageUrl != null){
    		oriUser.setStringInAttr("bgImageUrl", bgImageUrl);
    	}
    	if (iconId != null) {
    		FileItem icon = fileItemDao.findOriginal(iconId);
    		iconUrl = icon.getOriginalUrl();
			oriUser.setStringInAttr("iconUrl", iconUrl);
			iconId = null;
    	} else if (iconUrl != null){
    		oriUser.setStringInAttr("iconUrl", iconUrl);
    	}
        userDao.update(oriUser);
        if (isNameUpdated)
        	publishDurableEvent(new UserNameUpdateEvent(oriUser.getId(), oriUser.getDisplayName()));
        return new RedirectResolution(EditCurrentUser.class, "route");
    }
    
    public Resolution cancel() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
    	
    	return new RedirectResolution(IndexAction.class, "route");
    }	
    
    public Resolution changePassword() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
    	
		return new RedirectResolution(ChangePasswordAction.class, "route");
    }	
    
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public String getBgImageUrl() {
		return bgImageUrl;
	}

	public void setBgImageUrl(String bgImageUrl) {
		this.bgImageUrl = bgImageUrl;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
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
}
