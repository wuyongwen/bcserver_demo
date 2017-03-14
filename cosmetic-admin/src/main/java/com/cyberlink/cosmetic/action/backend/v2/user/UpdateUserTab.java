package com.cyberlink.cosmetic.action.backend.v2.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.file.service.StorageService;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.User.LookStatus;
import com.cyberlink.cosmetic.modules.user.model.UserType;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/v2/user/update-user-tab.action")
public class UpdateUserTab extends AbstractAction {
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
	private List<String> visibleTabs;
	private List<String> invisibleTabs;
	private String visibleTabsString;
	
	@DefaultHandler
	public Resolution route() {
		user = getCurrentUser();
    	if (user == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
    	
    	if (user.getUserType() == UserType.Brand || user.getUserType() == UserType.Master) {
    		invisibleTabs = new ArrayList<String>(Arrays.asList("CIRCLE", "POST", "PRODUCT", "LIKE", "FOLLOWING", "FOLLOWER", "LOOK"));
    	}
    	else {
    		invisibleTabs = new ArrayList<String>(Arrays.asList("CIRCLE", "POST", "LIKE", "FOLLOWING", "FOLLOWER", "LOOK"));
    	}
    	
    	visibleTabs = user.getListInAttr("tab");
    	if (visibleTabs.size() <= 0) {
    		visibleTabs.add("CIRCLE");
    		visibleTabs.add("POST");
    		visibleTabs.add("FOLLOWER");
    	}
    	invisibleTabs.removeAll(visibleTabs);
    	

    	
        return forward();
    }
	
    public Resolution save() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
	    
	    if(visibleTabsString != null) {
	    	visibleTabs = Arrays.asList(visibleTabsString.split(","));
	        User oriUser = getCurrentUser();
	        if (visibleTabsString.indexOf("LOOK") >= 0)
	        	oriUser.setLookStatus(LookStatus.PUBLISHED);
	    	else
	    		oriUser.setLookStatus(LookStatus.HIDDEN);
	        oriUser.setListInAttr("tab", visibleTabs);
	        userDao.update(oriUser);
	    }
        return new RedirectResolution(UpdateUserTab.class, "route");
    }
    
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public List<String> getVisibleTabs() {
		return this.visibleTabs;
	}
	
	public void setVisibleTabs(List<String> visibleTabs) {
		this.visibleTabs = visibleTabs;
	}
	
	public List<String> getInvisibleTabs() {
		return this.invisibleTabs;
	}
	
	public void setInvisibleTabs(List<String> invisibleTabs) {
		this.invisibleTabs = invisibleTabs;
	}
	
	public String getVisibleTabsString() {
		return this.visibleTabsString;
	}
	
	public void setVisibleTabsString(String visibleTabsString) {
		this.visibleTabsString = visibleTabsString;
	}
}
