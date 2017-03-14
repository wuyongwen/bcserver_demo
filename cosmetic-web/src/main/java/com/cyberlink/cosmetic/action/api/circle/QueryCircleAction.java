package com.cyberlink.cosmetic.action.api.circle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleUserDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;

@UrlBinding("/api/circle/query-circle.action")
public class QueryCircleAction extends AbstractAction {
	@SpringBean("circle.circleTypeDao")
	private CircleTypeDao circleTypeDao;

	@SpringBean("circle.circleDao")
	private CircleDao circleDao;

	@SpringBean("circle.circleTagGroupDao")
	private CircleTagGroupDao circleTagGroupDao;

	@SpringBean("circle.circleTagDao")
	private CircleTagDao circleTagDao;

	@SpringBean("circle.circleUserDao")
	private CircleUserDao circleUserDao;
	
	@SpringBean("user.UserDao")
	private UserDao userDao;

	private String tableName;
	private String circleTypeName;
	private String circleName;
	private String circleTagGroupName;
	private String circleTagName;
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setCircleTypeName(String circleTypeName) {
		this.circleTypeName = circleTypeName;
	}	
	
	public void setCircleName(String circleName) {
		this.circleName = circleName;
	}
		
	public void setCircleTagGroupName(String circleTagGroupName) {
		this.circleTagGroupName = circleTagGroupName;
	}
	
	public void setCircleTagName(String circleTagName) {
		this.circleTagName = circleTagName;
	}	

	@DefaultHandler
	public Resolution route() {
		if (tableName.equals("BC_CIRCLE_TYPE"))
			return queryCircleType();
		else if (tableName.equals("BC_CIRCLE"))
			return queryCircle();
		else if (tableName.equals("BC_CIRCLE_TAG_GROUP"))
			return queryCircleTagGroup();
		else if (tableName.equalsIgnoreCase("BC_CIRCLE_TAG"))
			return queryCircleTag();
		
		return new StreamingResolution("text/plain", "No Implement");
	}

	public Resolution queryCircleType() {
		if (circleTypeName == null) 
			return new StreamingResolution("text/plan", "circleTypeName cannot be empty.");
		
		final Map<String, Object> results = new HashMap<String, Object>();
		List<CircleType> listCircleType = circleTypeDao.findByName(circleTypeName);
		results.put("results", listCircleType);
		results.put("resultSize", listCircleType.size());
		return json(results);
	}

	public Resolution queryCircle() {
		if (circleName == null)
			return new StreamingResolution("text/plan", "circleName cannot be empty.");
		
		final Map<String, Object> results = new HashMap<String, Object>();
		return json(results);
	}

	public Resolution queryCircleTagGroup() {
		if (circleTagGroupName == null)
			return new StreamingResolution("text/plan", "circleTagGroupName cannot be empty.");

		final Map<String, Object> results = new HashMap<String, Object>();
		return json(results);
	}

	public Resolution queryCircleTag() {
		if (circleTagName == null)
			return new StreamingResolution("text/plan", "circleTagName cannot be empty.");
		
		final Map<String, Object> results = new HashMap<String, Object>();
		return json(results);
	}
}
