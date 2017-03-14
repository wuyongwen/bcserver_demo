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
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserBlocked;

@UrlBinding("/api/user/block-user.action")
public class BlockUserAction extends AbstractAction {
	@SpringBean("user.UserDao")
	private UserDao userDao;

	@SpringBean("user.UserBlockedDao")
	private UserBlockedDao userBlockedDao;
	
	@SpringBean("user.userAttrDao")
	private UserAttrDao userAttrDao;

	private Long targetId;
	private Boolean bIncrease = Boolean.FALSE;

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

		User targetUser = userDao.findById(targetId);
		
		// Current allow block all user type
		/*if (!UserType.getAvailableBlockType().contains(
				targetUser.getUserType()))
			return new ErrorResolution(ErrorDef.BlockCLAccount);*/

		UserBlocked userBlocked = userBlockedDao.findByTargetAndCreater(
				targetId, getCurrentUserId());

		if (userBlocked == null) {
			userBlocked = new UserBlocked();
			userBlocked.setUserId(getCurrentUserId());
			userBlocked.setTargetId(targetId);
			userBlocked.setUserName(getSession().getUser().getDisplayName());
			userBlocked.setTargetName(targetUser.getDisplayName());
			userBlockedDao.create(userBlocked);
			bIncrease = Boolean.TRUE;
		} else if (userBlocked.getIsDeleted()) {
			userBlocked.setIsDeleted(Boolean.FALSE);
			userBlocked.setUserName(getSession().getUser().getDisplayName());
			userBlocked.setTargetName(targetUser.getDisplayName());
			userBlockedDao.update(userBlocked);
			bIncrease = Boolean.TRUE;
		}
		
		if (bIncrease)
			userAttrDao.increaseNonNullValue(getCurrentUserId(), "BLOCK_COUNT");

		return success();
	}
}