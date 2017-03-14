package com.cyberlink.cosmetic.action.api.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserBlockedDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;

@UrlBinding("/api/user/list-blocked-user.action")
public class ListBlockedUserAction extends AbstractAction {
	@SpringBean("user.UserDao")
	private UserDao userDao;

	@SpringBean("user.UserBlockedDao")
	private UserBlockedDao userBlockedDao;
	
	@SpringBean("user.userAttrDao")
    private UserAttrDao userAttrDao;	
	
	private Long userId;
	private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
	
	@DefaultHandler
    public Resolution route() {
		final Map<String, Object> results = new HashMap<String, Object>();
		
		Long totalBlockCount = null;
        UserAttr attrObj = userAttrDao.findByUserId(userId);
        if (attrObj != null) {
        	totalBlockCount = attrObj.getBlockCount();
        }
		PageResult<Long> targetIds = new PageResult<Long>();
		if (totalBlockCount == null) {
			targetIds = userBlockedDao.findByUserOrderByName(userId, new BlockLimit(offset.intValue(), limit.intValue()), true);
		} else {
			targetIds = userBlockedDao.findByUserOrderByName(userId, new BlockLimit(offset.intValue(), limit.intValue()), false);
			targetIds.setTotalSize(totalBlockCount.intValue());
		}
		
		List<User> userList = userDao.findByIdsWithOrder(targetIds.getResults().toArray(new Long[targetIds.getResults().size()]));
		results.put("results", userList);
		results.put("totalSize", targetIds.getTotalSize());
		return json(results);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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
}