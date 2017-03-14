package com.cyberlink.cosmetic.action.api.user;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;

@UrlBinding("/api/user/update-email-CL.action")
public class UpdateCLEmailAction extends AbstractAction{
	@SpringBean("user.MemberDao")
	protected MemberDao memberDao;

	@SpringBean("user.AccountDao")
    private AccountDao accountDao;

	private String oldEmail;
	private String newEmail;
	private Long memberId;
	
	@DefaultHandler
    public Resolution route() {
		if (!checkIpAddress()) {
			return new ErrorResolution(ErrorDef.InvalidIPAddress);
		}
		if (memberId == null || oldEmail == null || newEmail == null) {
			return new ErrorResolution(ErrorDef.Forbidden);
		}
		/*Member member = memberDao.findByCLMemberId(memberId);
		if (member == null) {
			return success();
		}
		
		Account account = accountDao.findById(member.getAccountId());
		if (account == null) {
			return success();
		}	
		if (account.getAccount().equalsIgnoreCase(oldEmail)) {
			if (accountDao.findBySourceAndReference(AccountSourceType.Email, newEmail) == null) {
				account.setAccount(newEmail);
				accountDao.update(account);
				return success();
			}
		}*/
		return success();
    }
	
	public String getOldEmail() {
		return oldEmail;
	}
	public void setOldEmail(String oldEmail) {
		this.oldEmail = oldEmail;
	}
	public String getNewEmail() {
		return newEmail;
	}
	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}
    public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
}
