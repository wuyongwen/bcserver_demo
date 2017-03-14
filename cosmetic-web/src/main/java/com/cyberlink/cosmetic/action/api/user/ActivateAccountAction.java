package com.cyberlink.cosmetic.action.api.user;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/api/user/activate-account.action")
public class ActivateAccountAction extends SignInAction {
	@SpringBean("user.MemberDao")
	private MemberDao memberDao;

	@SpringBean("user.AccountDao")
	protected AccountDao accountDao;

	private Long memberId;
	private String activateCode;

	@DefaultHandler
	public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
		if (redirect != null)
			return redirect;
		
		if (memberId == null)
			return new ErrorResolution(ErrorDef.InvalidAccount);
		if (!memberDao.exists(memberId))
			return new ErrorResolution(ErrorDef.InvalidAccount);
		if (activateCode == null)
			return new ErrorResolution(ErrorDef.InvalidToken);

		Member userMember = memberDao.findByMemberId(Long.valueOf(memberId));
		Account userAccount = userMember.getAccount();
		if (!activateCode.equals(userMember.getActivateCode()))
			return new ErrorResolution(ErrorDef.InvalidToken);
		if (userAccount.getUserId() == null) {
			locale = "de_DE";
			User user = createNewUser();
			userAccount.setUserId(user.getId());
			userAccount = accountDao.update(userAccount);
		}

		return success();
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getActivateCode() {
		return activateCode;
	}

	public void setActivateCode(String activateCode) {
		this.activateCode = activateCode;
	}

}