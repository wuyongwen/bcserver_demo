package com.cyberlink.cosmetic.action.api.user;

import java.util.List;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/remove-account-byEmail.action")
public class RemoveByEmailAction extends AbstractAction{
    @SpringBean("user.AccountDao")
    protected AccountDao accountDao;

    @SpringBean("user.UserDao")
    private UserDao userDao;
	
	private String email;

	@DefaultHandler
    public Resolution route() {
		Account account = accountDao.findBySourceAndReference(AccountSourceType.Email, email);
		if (account != null) {
			List<Session> sessionList = sessionDao.findByUserId(account.getUserId());
			for (Session session : sessionList) {
				session.setStatus(SessionStatus.Invalied);
				sessionDao.update(session);
			}
			account.setAccount(null);
			accountDao.update(account);
		}
		return success();
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
