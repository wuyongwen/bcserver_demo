package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.lang.model.MailLang;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailSignUpBCWService;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;

public class MailSignUpBCWServiceImpl extends AbstractMailService implements
		MailSignUpBCWService, ApplicationEventPublisherAware {

	private MemberDao memberDao;

	public MailSignUpBCWServiceImpl() {
		super(MailType.MEMBER_CREATE_SUCCESSFULLY_BCW);
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
		String meberLoacale = member.getLocale();
		if (meberLoacale == null || meberLoacale.isEmpty())
			meberLoacale = "en_US";
		MailLang mailLang = LanguageCenter.getMailLang(meberLoacale);
		String name = account.getAccount();
		try {
			name = name.split("@")[0];
		} catch (Exception e) {

		}
		data.put("content1", mailLang.getSignUpContent1(name));
		data.put("content2", mailLang.getSignUpContent2());
		data.put("content3", mailLang.getSignUpContent3());
		data.put("content4", mailLang.getSignUpContent4());
		data.put("content5", mailLang.getSignUpContent5());
		String exploring = String.format(
				"http://%s/?utm_source=email_signup_welcome&utm_medium=email",
				Constants.getBCWebsiteDomain());
		data.put("exploring", exploring);
		// Need change to BC wbsite page
		String removeUrl = String.format(
				"http://%s/api/user/confirm-mail.action?remove1&memberId=%s&locale=%s",
				Constants.getWebsiteDomain(), memberId.toString(), member.getLocale());
		data.put("anyProblem", mailLang.getSignUpAnyProblem(removeUrl));
		data.put("copyRight", mailLang.getCopyRight());
		data.put("mailWidth", 700);
		data.put("websiteDomain", Constants.getWebsiteDomain());
		String itunesUrl = String.format(
				"https://itunes.apple.com/%s/app/id1066152001?l=%s&mt=8",
				meberLoacale.substring(3, 5), meberLoacale.substring(0, 2));
		data.put("itunesUrl", itunesUrl);
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
