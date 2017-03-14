package com.cyberlink.cosmetic.action.api.user;

import java.util.HashMap;
import java.util.Map;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;
import com.cyberlink.cosmetic.modules.user.service.UserService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/v5.0/user/list-user-by-badgeType.action")
public class ListUserByBadgeTypeAction extends ListUserByTypeAction {

	@SpringBean("user.userService")
	private UserService userService;

	private Long limit = Long.valueOf(20);
	private BadgeType badgeType = BadgeType.StarOfWeek;
	
	@Validate(maxvalue = 30, on = "route")
	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public BadgeType getBadgeType() {
		return badgeType;
	}
	
	public void setBadgeType(BadgeType badgeType) {
		this.badgeType = badgeType;
	}
	
	@DefaultHandler
	public Resolution route() {
		BlockLimit blockLimit = new BlockLimit(offset.intValue(), limit.intValue());
		blockLimit.addOrderBy("score", false);
		PageResult<User> users = userService.getUsersByBadgeType(locale, badgeType, blockLimit);
		setUserArribute(users);
		final Map<String, Object> results = new HashMap<String, Object>();
		results.put("results", users.getResults());
        results.put("totalSize", users.getTotalSize());
		return json(results);
	}
	
}
