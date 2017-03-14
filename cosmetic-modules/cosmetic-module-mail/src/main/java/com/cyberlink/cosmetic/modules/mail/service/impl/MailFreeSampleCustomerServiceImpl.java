package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.lang.model.MailLang;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailFreeSampleCustomerService;

public class MailFreeSampleCustomerServiceImpl extends AbstractMailService
		implements MailFreeSampleCustomerService,
		ApplicationEventPublisherAware {

	private BrandEventDao brandEventDao;

	protected MailFreeSampleCustomerServiceImpl() {
		super(MailType.FREE_SAMPLE_CUSTOMER);
	}

	@Override
	@BackgroundJob
	public void send(Long brandEventId) {
		if (!brandEventDao.exists(brandEventId)) {
			logger.error("[MailFreeSampleCustomerService] brandEvent not exists!");
			return;
		}
		BrandEvent brandEvent = brandEventDao.findById(brandEventId);

		final Map<String, Object> data = new HashMap<String, Object>();
		Locale locale = null;
		try {
			locale = new Locale(brandEvent.getLocale().substring(0, 2),
					brandEvent.getLocale().substring(3, 5));
		} catch (Exception e) {
			locale = new Locale("en", "US");
			logger.error(e.getMessage());
		}

		MailLang mailLang = LanguageCenter.getMailLang(brandEvent.getLocale());
		data.put("content1",
				mailLang.getFreeSampleCustomerContent1(brandEvent.getTitle()));
		data.put("content2", mailLang
				.getFreeSampleCustomerContent2(getTimeString(locale, brandEvent
						.getEventAttrJNode().getDrawTime())));
		data.put("content3", mailLang.getFreeSampleCustomerContent3());
		data.put("content4", mailLang.getFreeSampleCustomerContent4());
		data.put("copyRight", mailLang.getCopyRight());
		data.put("mailWidth", 700);
		data.put("websiteDomain", Constants.getWebsiteDomain());

		final Map<String, File> attachments = new HashMap<String, File>();
		try {
			File file = new File(Constants.getFreeSamplePath()
					+ String.format("/%s.xls", brandEventId.toString()));
			if (file != null && file.exists()) {
				attachments.put(brandEventId.toString() + ".xls", file);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		String subject = mailLang.getFreeSampleCustomerSubject();
		final String content = getContent(locale, data);
		String[] pfEmails = new String[0];
		String[] companyEmails = new String[0];

		String pfEmail = brandEvent.getEventAttrJNode().getPfEmail();
		if (pfEmail != null && !pfEmail.isEmpty()) {
			pfEmail += "," + "Roy_Lee@PerfectCorp.com";
			pfEmails = pfEmail.split("\\s*,\\s*");
		} else
			pfEmails = new String[] { "Roy_Lee@PerfectCorp.com" };

		if (Constants.getCustomerMailEnable()) {
			String companyEmail = brandEvent.getEventAttrJNode()
					.getCompanyEmail();
			if (companyEmail != null && !companyEmail.isEmpty())
				companyEmails = companyEmail.split("\\s*,\\s*");
		} else {
			companyEmails = new String[] { "Roy_Lee@PerfectCorp.com" };
			subject = subject + " - " + Constants.getWebsiteDomain();
		}

		sendMailwithBcc(subject, content, attachments, pfEmails, companyEmails);
	}

	@Override
	@BackgroundJob
	public void directSend(String address, String subject, String content) {
		sendMail(subject, content, address);
	}

	private String getTimeString(Locale locale, Date date) {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		try {
			if (locale == null) {
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				return dateFormatGmt.format(date) + " GMT";
			} else if (locale.getLanguage().equalsIgnoreCase("en")) {
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
				return dateFormatGmt.format(date) + " GMT-08:00";
			} else if (locale.getLanguage().equalsIgnoreCase("de")) {
				return dateFormatGmt.format(date) + " GMT+02:00";
			} else if (locale.getLanguage().equalsIgnoreCase("fr")) {
				return dateFormatGmt.format(date) + " GMT+02:00";
			} else if (locale.getLanguage().equalsIgnoreCase("ja")) {
				return dateFormatGmt.format(date) + " GMT+09:00";
			} else if (locale.getLanguage().equalsIgnoreCase("ko")) {
				return dateFormatGmt.format(date) + " GMT+09:00";
			} else if (locale.getCountry().equalsIgnoreCase("CN")) {
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
				return dateFormatGmt.format(date) + " GMT+08:00";
			} else if (locale.getCountry().equalsIgnoreCase("TW")) {
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
				return dateFormatGmt.format(date) + " GMT+08:00";
			} else if (locale.getLanguage().equalsIgnoreCase("pt")) {
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT-03:00"));
				return dateFormatGmt.format(date) + " GMT-03:00";
			} else {
				dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				return dateFormatGmt.format(date) + " GMT";
			}
		} catch (Exception e) {
			return "";
		}
	}

	public BrandEventDao getBrandEventDao() {
		return brandEventDao;
	}

	public void setBrandEventDao(BrandEventDao brandEventDao) {
		this.brandEventDao = brandEventDao;
	}

}