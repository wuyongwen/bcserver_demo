package com.cyberlink.cosmetic.action.api.user;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserStatus;
import com.cyberlink.cosmetic.modules.user.model.UserSubType;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/update-type.action")
public class UpdateUserType extends AbstractAction {
	@SpringBean("user.UserDao")
	private UserDao userDao;
	
	@SpringBean("user.AccountDao")
	protected AccountDao accountDao;

	@SpringBean("post.PostDao")
	private PostDao postDao;

	private UserType userType;
	private UserSubType userSubType;
	private UserStatus userStatus;
	private String email;

	@DefaultHandler
	public Resolution route() {
		if (email == null) {
			return new ErrorResolution(ErrorDef.InvalidAccount);
		}
		
		RedirectResolution redirect = redirectWriteAPI();
		if (redirect != null)
			return redirect;

		Account account = accountDao.findBySourceAndReference(
				AccountSourceType.Email, email);
		if (account == null) {
			return new ErrorResolution(ErrorDef.InvalidAccount);
		}
		User user = account.getUser();
		if (userType != null || userSubType != null || userStatus != null) {
			if (userType != null)
				user.setUserType(userType);
			if (userSubType != null)
				user.setUserSubType(userSubType);
			UserStatus oriStatus = null;
			if (userStatus != null) {
				oriStatus = user.getUserStatus();
				user.setUserStatus(userStatus);
			}
			userDao.update(user);
			if (UserStatus.Hidden.equals(oriStatus)
					&& UserStatus.Published.equals(user.getUserStatus()))
				postDao.publishUnpublished(user.getId());
		}
		return success();
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public UserSubType getUserSubType() {
		return userSubType;
	}

	public void setUserSubType(UserSubType userSubType) {
		this.userSubType = userSubType;
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
