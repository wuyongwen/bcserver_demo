package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.lang.model.MailLang;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailSignUpService;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;

public class MailSignUpServiceImpl extends AbstractMailService implements
		MailSignUpService, ApplicationEventPublisherAware {

	private MemberDao memberDao;

	public MailSignUpServiceImpl() {
		super(MailType.MEMBER_CREATE_SUCCESSFULLY);
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
		data.put("content1", mailLang.getSignUpContent1(name));
		data.put("content2", mailLang.getSignUpContent2());
		data.put("content3", mailLang.getSignUpContent3());
		String removeUrl = String.format(
				"http://%s/api/user/confirm-mail.action?remove1&memberId=%s&locale=%s",
				Constants.getWebsiteDomain(), memberId.toString(), member.getLocale());
		data.put("anyProblem", mailLang.getSignUpAnyProblem(removeUrl));
		data.put("copyRight", mailLang.getCopyRight());
		data.put("mailWidth", 700);
		data.put("websiteDomain", Constants.getWebsiteDomain());
		final String subject = mailLang.getSignUpSubject();
		final String content = getContent(null, data);
		sendMail(subject, content, account.getAccount());
	}

	public MemberDao getMemberDao() {
		return memberDao;
	}

	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}

}
