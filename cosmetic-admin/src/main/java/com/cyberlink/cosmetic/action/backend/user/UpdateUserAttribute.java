package com.cyberlink.cosmetic.action.backend.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/user/update-user-attribute.action")
public class UpdateUserAttribute extends AbstractAction{
    @SpringBean("user.UserDao")
    private UserDao userDao;

    @SpringBean("user.AttributeDao")
    protected AttributeDao attributeDao;
	
    @DefaultHandler
    public Resolution route() {
		do {
			List<Long> ids = attributeDao.findAttributeUser(Long.valueOf(0), Long.valueOf(100));
			if (ids.size() == 0)
				break;
			List<User> userList = userDao.findByIds(ids.toArray(new Long[ids.size()]));
			for (User user : userList) {
		        String mapAsJson = "{}";
				final Map<String, Object> attributes = new HashMap<String, Object>();
	        	for (Attribute attr : user.getAttributeList()) {
	        		attributes.put(attr.getAttrName(), attr.getAttrValue());
	        	}
	        	try {
	        		mapAsJson = new ObjectMapper().writeValueAsString(attributes);
	        	} catch (JsonProcessingException e) {
	        	}	
	        	if (mapAsJson != null) {
	        		user.setStringInAttr("userAttr", mapAsJson);
	        	}
	        	userDao.update(user);
	        	try {
	        		Thread.sleep(500);
	        	}catch (Exception e) {
	        	}
			}
			attributeDao.deleteUserAttribute(ids);
    		if (ids.size() < 100)
    			break;
    	} while(true);
		return new StreamingResolution("text/html", "");
	}

}
