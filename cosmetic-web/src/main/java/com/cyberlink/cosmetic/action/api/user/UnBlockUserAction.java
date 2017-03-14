package com.cyberlink.cosmetic.action.api.user;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserBlockedDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.UserBlocked;

@UrlBinding("/api/user/unblock-user.action")
public class UnBlockUserAction extends AbstractAction {
	@SpringBean("user.UserDao")
	private UserDao userDao;

	@SpringBean("user.UserBlockedDao")
	private UserBlockedDao userBlockedDao;

	@SpringBean("user.userAttrDao")
    private UserAttrDao userAttrDao;
	
	private Long targetId;

	@Validate(required = true, on = "route")
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	@DefaultHandler
	public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
		if (redirect != null)
			return redirect;

		if (!authenticateByRedis()) {
			return new ErrorResolution(authError);
		}

		if (!userDao.exists(targetId))
			return new ErrorResolution(ErrorDef.InvalidUserTargetId);
		else if (getCurrentUserId().equals(targetId))
			return new ErrorResolution(ErrorDef.InvalidUserTargetId);

		UserBlocked userBlocked = userBlockedDao.findByTargetAndCreater(
				targetId, getCurrentUserId());

		if (userBlocked != null && !userBlocked.getIsDeleted()) {
			userBlocked.setIsDeleted(Boolean.TRUE);
			userBlockedDao.update(userBlocked);
			userAttrDao.decreaseNonNullValue(getCurrentUserId(), "BLOCK_COUNT");
		}
		return success();
	}
}