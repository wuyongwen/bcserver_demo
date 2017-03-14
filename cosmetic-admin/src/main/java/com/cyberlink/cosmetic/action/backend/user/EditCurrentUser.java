package com.cyberlink.cosmetic.action.backend.user;


import java.util.List;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.IndexAction;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.file.service.StorageService;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.event.UserNameUpdateEvent;
import com.cyberlink.cosmetic.modules.user.model.User;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/user/editCurrentUser.action")
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
    
	private User user;
	
	static final String editCurrentUser = "/user/editCurrentUser-route.jsp" ;
	
	@DefaultHandler
	public Resolution route() {
		user = getCurrentUser();
    	if (user == null) {
    		return new StreamingResolution("text/html", "Need Login");
        } 

        return forward();
    }
	
    public Resolution save() {
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
        oriUser.setRegion(user.getRegion());
        oriUser.setBirthDay(user.getBirthDay());
        oriUser.setDescription(user.getDescription());
        oriUser.setGender(user.getGender());
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
        userDao.update(oriUser);
        if (isNameUpdated)
        	publishDurableEvent(new UserNameUpdateEvent(oriUser.getId(), oriUser.getDisplayName()));
        return new RedirectResolution(IndexAction.class, "route");
    }
    
    public Resolution cancel() {/* (3) */
    	return new RedirectResolution(IndexAction.class, "route");
    }	
    
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
}
