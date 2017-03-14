package com.cyberlink.cosmetic.action.backend.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.result.UserLogWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/user/list-all-user.action")
public class ListAllUserAction extends AbstractAction{
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
    private String startTime;
    private String endTime;
    private String[] parseString = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"};
	@DefaultHandler
    public Resolution route() {
        final Map<String, Object> results = new HashMap<String, Object>();
        
        Date start = null;
        Date end = null;
        if (startTime != null) {
        	for (String parse : parseString) {
        		SimpleDateFormat parserSDF = new SimpleDateFormat(parse);
        		parserSDF.setTimeZone(TimeZone.getTimeZone("GMT+00"));

            	try {
            		start = parserSDF.parse(startTime);
            		break;
            	} catch (Exception e){
            	}        		
        	}
        }
        if (endTime != null) {
        	for (String parse : parseString) {
        		SimpleDateFormat parserSDF = new SimpleDateFormat(parse);
        		parserSDF.setTimeZone(TimeZone.getTimeZone("GMT+00"));
        		try {
        			end = parserSDF.parse(endTime);
        			break;
        		} catch (Exception e){
        		}
        	}
        }
               
        PageResult<User> pageResult = userDao.findUserByParameters(null, null, null, null, start, end, offset, limit);
        List<UserLogWrapper> list = new ArrayList<UserLogWrapper>();
        for (User user : pageResult.getResults()) {
            String mapAsJson = "{}";
            if (user.hasKeyInAttr("userAttr")) {
            	String userAttr = user.getStringInAttr("userAttr");
            	if (userAttr.length() > 0) {
            		mapAsJson = userAttr;
            	}
            } else {
            	final Map<String, Object> attributes = new HashMap<String, Object>();
            	for (Attribute attr : user.getAttributeList()) {
            		attributes.put(attr.getAttrName(), attr.getAttrValue());
            	}
            	try {
            		mapAsJson = new ObjectMapper().writeValueAsString(attributes);
            	} catch (JsonProcessingException e) {
            	}
            }
            user.setAttribute(mapAsJson);
            list.add(new UserLogWrapper(user));
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
