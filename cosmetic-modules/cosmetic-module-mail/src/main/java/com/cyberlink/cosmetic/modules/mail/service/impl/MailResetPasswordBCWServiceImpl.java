package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.lang.model.MailLang;
import com.cyberlink.cosmetic.modules.cyberlink.model.PasswordHashUtil;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailResetPasswordBCWService;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;
import com.restfb.json.JsonObject;

public class MailResetPasswordBCWServiceImpl extends AbstractMailService
		implements MailResetPasswordBCWService, ApplicationEventPublisherAware {
	private MemberDao memberDao;

	public MailResetPasswordBCWServiceImpl() {
		super(MailType.MEMBER_RESET_PASSWORD_BCW);
	}

	@BackgroundJob
	public void send(Long memberId) {
		if (!memberDao.exists(memberId))
			return;

		final Member member = memberDao.findByMemberId(memberId);
		final Account account = member.getAccount();
		if (!AccountSourceType.Email.equals(account.getAccountSource()))
			return;

		final Map<String, Object> data = new HashMap<String, Object>();
		MailLang mailLang = LanguageCenter.getMailLang(member.getLocale());
		String name = account.getAccount();
		try {
			name = name.split("@")[0];
		} catch (Exception e) {

		}
		data.put("content1", mailLang.getForgotPasswordContent1(name));
		data.put("content2", mailLang.getForgotPasswordContent2());
		data.put("content3", mailLang.getForgotPasswordContent3());
		// BC wbsite page
		String resetUrl = String
				.format("http://%s/user/reset-password?utm_source=email_forgot_password&utm_medium=email&memberId=%s&memberCode=%s",
						Constants.getBCWebsiteDomain(), memberId.toString(),
						getMemberCode(member));
		data.put("resetUrl", resetUrl);
		data.put("pageName", mailLang.getForgotPasswordPageName());
		data.put("content5", mailLang.getForgotPasswordContent5());
		data.put("end", mailLang.getForgotPasswordEnd());
		data.put("anyProblem", mailLang.getForgotPasswordAnyProblem());
		data.put("copyRight", mailLang.getCopyRight());
		data.put("mailWidth", 700);
		final String subject = mailLang.getForgotPasswordSubject();
		final String content = getContent(null, data);
		sendMail(subject, content, account.getAccount());
	}

	private String getMemberCode(Member member) {
		String code = member.getId() + "%" + member.getAccount().getAccount();
		String memberCode = code;
		try {
			memberCode = PasswordHashUtil.createHash(code);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			memberCode = code;
		} finally {
			try {
				String codeObjStr = member.getMemberCode();
				JsonObject codeObj = null;
				if (codeObjStr == null || codeObjStr.isEmpty())
					codeObj = new JsonObject();
				else
					codeObj = new JsonObject(codeObjStr);

				if (codeObj.length() >= 10) {
					Iterator<?> keys = codeObj.keys();
					Long earliest = null;
					String removeKey = "";
					while (keys.hasNext()) {
						String key = (String) keys.next();
						Long tmpTime = codeObj.getLong(key);
						if (earliest == null) {
							earliest = tmpTime;
							removeKey = key;
						} else {
							if (earliest.compareTo(tmpTime) > 0) {
								earliest = tmpTime;
								removeKey = key;
							}
						}
					}
					codeObj.remove(removeKey);
				}

				codeObj.put(memberCode, Calendar.getInstance()
						.getTimeInMillis());
				member.setMemberCode(codeObj.toString());
				memberDao.update(member);
			} catch (Exception e1) {
			}
		}
		return memberCode;
	}

	public MemberDao getMemberDao() {
		return memberDao;
	}

	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}
}
