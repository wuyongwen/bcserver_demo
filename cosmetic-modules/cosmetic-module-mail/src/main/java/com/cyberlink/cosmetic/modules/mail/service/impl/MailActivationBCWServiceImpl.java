package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailActivationBCWService;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;

public class MailActivationBCWServiceImpl extends AbstractMailService implements
		MailActivationBCWService, ApplicationEventPublisherAware {

	private MemberDao memberDao;

	public MailActivationBCWServiceImpl() {
		super(MailType.MEMBER_ACTIVATION_BCW);
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
		String meberLoacale = "de_DE";
		Locale locale = new Locale(meberLoacale.substring(0, 2),
				meberLoacale.substring(3, 5));

		String name = account.getAccount();
		try {
			name = name.split("@")[0];
		} catch (Exception e) {

		}
		data.put("name", name);
		String activateUrl = String
				.format("https://%s/user/activate-account.action?memberId=%s&activateCode=%s&utm_source=email_deu_activation&utm_medium=email",
						Constants.getBCWebsiteDomain(), memberId.toString(),
						member.getActivateCode());
		data.put("activateUrl", activateUrl);
		data.put("copyRight", LanguageCenter.getMailLang(meberLoacale)
				.getCopyRight());
		data.put("mailWidth", 700);
		String itunesUrl = String.format(
				"https://itunes.apple.com/%s/app/id1066152001?l=%s&mt=8",
				meberLoacale.substring(3, 5), meberLoacale.substring(0, 2));
		data.put("itunesUrl", itunesUrl);
		final String subject = getSubject(locale);
		final String content = getContent(locale, data);
		sendMail(subject, content, account.getAccount());
	}

	public MemberDao getMemberDao() {
		return memberDao;
	}

	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}
}
