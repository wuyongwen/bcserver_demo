package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailImpersonationInvestigationService;

public class MailImpersonationInvestigationServiceImpl extends
		AbstractMailService implements MailImpersonationInvestigationService,
		ApplicationEventPublisherAware {

	protected MailImpersonationInvestigationServiceImpl() {
		super(MailType.IMPERSONATION_INVESTIGATION);
	}

	@BackgroundJob
	public void send(String email, String region) {
		final Map<String, Object> data = new HashMap<String, Object>();
		Locale locale = new Locale(region.substring(0, 2), region.substring(3,
				5));
		data.put("copyRight", getCopyRight(locale));
		data.put("mailWidth", 700);
		data.put("websiteDomain", Constants.getWebsiteDomain());
		final String subject = getLocalizedMailSubject(locale);
		final String content = getContent(locale, data);
		sendMail(subject, content, email);
	}

	private String getLocalizedMailSubject(Locale locale) {
		try {
			String region = locale.getLanguage() + "_" + locale.getCountry();
			if (locale.getLanguage().equalsIgnoreCase("en")) {
				return LanguageCenter.getMailLang(region)
						.getSubjectImpersonationInvestigation();
			}
		} catch (Exception e) {
		}
		return LanguageCenter.getMailLang("en_US")
				.getSubjectImpersonationInvestigation();
	}

	@Override
	protected String getCopyRight(Locale locale) {
		try {
			String region = locale.getLanguage() + "_" + locale.getCountry();
			if (locale.getLanguage().equalsIgnoreCase("en")) {
				return LanguageCenter.getMailLang(region).getCopyRight();
			}
		} catch (Exception e) {
		}
		return LanguageCenter.getMailLang("en_US").getCopyRight();
	}
}