package com.cyberlink.cosmetic.action.api.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.cyberlink.cosmetic.utils.EncrUtil;
import com.restfb.json.JsonObject;

@UrlBinding("/api/user/reset-password-api.action")
public class ResetPasswordAPIAction extends AbstractAction {
	@SpringBean("user.MemberDao")
	private MemberDao memberDao;

	private Long memberId = null;
	private String password;
	private String memberCode;
	private static final int EXPIREDMIN = 30;

	@DefaultHandler
	public Resolution route() {
		RedirectResolution redirect = redirectWriteAPI();
		if (redirect != null)
			return redirect;
		
		if (memberId == null)
			return new ErrorResolution(ErrorDef.InvalidAccount);
		if (!memberDao.exists(memberId))
			return new ErrorResolution(ErrorDef.InvalidAccount);

		Member userMember = memberDao.findByMemberId(Long.valueOf(memberId));
		try {
			if (memberCode == null
					|| !PasswordHashUtil.validatePassword(memberId + "%"
							+ userMember.getAccount().getAccount(), memberCode))
				return new ErrorResolution(ErrorDef.InvalidToken);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return new ErrorResolution(ErrorDef.InvalidToken);
		}
		
		// check expired
		String codeObjStr = userMember.getMemberCode();
		if (codeObjStr != null && !codeObjStr.isEmpty()) {
			try {
				JsonObject codeObj = new JsonObject(codeObjStr);
				if (codeObj.has(memberCode)) {
					Long sendTime = codeObj.getLong(memberCode);
					sendTime += EXPIREDMIN * 60 * 1000;
					if (sendTime.compareTo(Calendar.getInstance()
							.getTimeInMillis()) < 0) {
						return new ErrorResolution(ErrorDef.ExpiredToken);
					}
				} else {
					return new ErrorResolution(ErrorDef.ExpiredToken);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}		

		try {
			userMember.setPassword(PasswordHashUtil.createHash(password));
			userMember.setEncryption(EncrUtil.encrypt(password));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			return new ErrorResolution(ErrorDef.InvalidPassword);
		}
		userMember = memberDao.update(userMember);

		return success();
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
}