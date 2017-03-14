package com.cyberlink.cosmetic.action.api.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.utils.EncrUtil;

@UrlBinding("/api/user/update-password-CL.action")
public class UpdateCLPasswordAction extends AbstractAction{
	@SpringBean("user.MemberDao")
	protected MemberDao memberDao;

	@SpringBean("user.AccountDao")
    private AccountDao accountDao;

	private String oldPassword;
	private String newPassword;
	private Long memberId;
	
	@DefaultHandler
    public Resolution route() {
		if (!checkIpAddress()) {
			return new ErrorResolution(ErrorDef.InvalidIPAddress);
		}
		if (memberId == null || newPassword == null) {
			return new ErrorResolution(ErrorDef.Forbidden);
		}		
		/*Member member = memberDao.findByCLMemberId(memberId);
		if (member == null) {
			return success();
		}
		
		try {
			//if (PasswordHashUtil.validatePassword(oldPassword, member.getPassword())) {
				member.setPassword(PasswordHashUtil.createHash(newPassword));
				member.setEncryption(EncrUtil.encrypt(newPassword));
				member = memberDao.update(member);
			//}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
		}*/
		return success();
    }
	
    public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
