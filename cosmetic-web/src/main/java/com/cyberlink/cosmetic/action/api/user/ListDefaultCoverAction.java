package com.cyberlink.cosmetic.action.api.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.user.model.UserType;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/list-default-cover.action")
public class ListDefaultCoverAction extends AbstractAction{
	@SpringBean("file.fileDao")
    protected FileDao fileDao;
	
    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
	private UserType userType;
	
	@DefaultHandler
    public Resolution route() {
        final Map<String, Object> results = new HashMap<String, Object>();
        PageResult<File> pageResult = null;
        if (userType != UserType.CL) {
			pageResult = fileDao.findByFileType(FileType.DefaultUserCover, offset, limit);			
		} else {
			pageResult = fileDao.findByFileType(FileType.DefaultCLCover, offset, limit);
		}
        
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	for (File f : pageResult.getResults()) {
    		List<FileItem> itemList = f.getListItems();
    		if (itemList.size() == 0) {
    			itemList = f.getQualityItems();
    			if (itemList.size() == 0) {
    				itemList = f.getFileItems();
    			}
    		}
    		Map<String, Object> wrapper = new HashMap<String, Object>();
			wrapper.put("id", f.getId());
    		if (itemList.size() > 0) {
    			wrapper.put("coverUrl", itemList.get(0).getOriginalUrl());
    		} else {
    			wrapper.put("coverUrl", "");    			
    		}
			list.add(wrapper);
    	}
		results.put("results", list);    	
		results.put("totalSize", pageResult.getTotalSize());
    	return json(results);
    }

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
}
