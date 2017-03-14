package com.cyberlink.cosmetic.action.api.circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleUserDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleUser;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;

//list user information by circle ID
@UrlBinding("/api/circle/list-user.action")
public class ListUserAction extends AbstractAction {
	@SpringBean("user.UserDao")
	private UserDao userDao;

	@SpringBean("circle.circleDao")
	private CircleDao circleDao;
	
	@SpringBean("circle.circleUserDao")
	private CircleUserDao circleUserDao;
	
	private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
	private Long circleId;
	
	@Validate(required = true, on = "route")
	public void setCircleId(Long circleId) {
		this.circleId = circleId;
	}

	@DefaultHandler
	public Resolution route() {
	    if(!circleDao.exists(circleId))
	        return new ErrorResolution(ErrorDef.InvalidCircleId);
	    
		final Map<String, Object> results = new HashMap<String, Object>();
		List<User> userList = new ArrayList<User>();
		List<CircleUser> circleUserList = new  ArrayList<CircleUser>();
		PageResult<CircleUser> pageResult = circleUserDao.findByCircleId(circleId, offset, limit);
		circleUserList = pageResult.getResults();
		for (int nIdx=0; nIdx < circleUserList.size(); nIdx++) {
			CircleUser circleUser = circleUserList.get(nIdx);
			Long userId = circleUser.getUserId();
			if (userDao.exists(userId)) {
				User user = userDao.findById(userId);
				userList.add(user);	
			}
		}
		results.put("results", userList);
		results.put("totalSize", pageResult.getTotalSize());
		return json(results);
	}
}
