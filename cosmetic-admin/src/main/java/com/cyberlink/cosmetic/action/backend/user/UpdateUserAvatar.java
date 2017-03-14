package com.cyberlink.cosmetic.action.backend.user;

import java.util.List;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/user/update-user-avatar.action")
public class UpdateUserAvatar extends AbstractAction {
    @SpringBean("user.UserDao")
    private UserDao userDao;

    @SpringBean("file.fileItemDao")
    private FileItemDao fileItemDao;
    
    private Long limit = Long.valueOf(100);

	@DefaultHandler
    public Resolution route() {
		PageResult<User> page = null;
		do {
    		page = userDao.findUserWithoutAvatarUrl(limit);
    		for (User user : page.getResults()) {
    			Long [] ids = new Long[1];
    			ids[0] = user.getAvatarId();
    			List<FileItem> listResults = fileItemDao.findThumbnails(ids, ThumbnailType.Avatar);
    			if(listResults.size() > 0)
    				user.setAvatarLink(listResults.get(0).getOriginalUrl());
    			else {
    				user.setAvatarLink("http://" + Constants.getWebsiteDomain() + "/api/file/download-file.action?getFile&fileId="+ user.getAvatarId() +"&thumbnailType=Avatar");
    			}
    			userDao.update(user);
    		
    		}
    		if (page.getResults().size() < limit)
    			break;
    	} while(true);
		return new StreamingResolution("text/html", page.getTotalSize().toString());
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}


}
