package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.event.dao.BrandEventDao;
import com.cyberlink.cosmetic.modules.event.dao.EventUserDao;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventUser;
import com.cyberlink.cosmetic.modules.mail.model.event.ConsultationReceiveData;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventConsultationService;

public class MailJoinEventConsultationServiceImpl extends AbstractMailService
		implements MailJoinEventConsultationService,
		ApplicationEventPublisherAware {

	private BrandEventDao brandEventDao;
	private EventUserDao eventUserDao;

	protected MailJoinEventConsultationServiceImpl() {
		super(MailType.JOIN_EVENT_CONSULTATION);
	}

	@BackgroundJob
	public void send(Long eventUserId, Long brandEventId) {
		BrandEvent brandEvent = brandEventDao.findById(brandEventId);
		EventUser eventUser = eventUserDao.findById(eventUserId);
		final ConsultationReceiveData receiveData = new ConsultationReceiveData(brandEvent, eventUser);
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("eventUser", receiveData);
		Locale locale = new Locale(receiveData.getLocale().substring(0, 2),
				receiveData.getLocale().substring(3, 5));
		data.put("copyRight", receiveData.getCopyRight());
		data.put("mailWidth", 700);
		data.put("websiteDomain", Constants.getWebsiteDomain());
		String subject = receiveData.getConsultationSubject();
		final String content = getContent(locale, data);
		
		String companyEmail = brandEvent.getEventAttrJNode().getCompanyEmail();
		String[] companyEmails = new String[0];
		
		if (Constants.getCustomerMailEnable()) {
			if (companyEmail != null && !companyEmail.isEmpty())
				companyEmails = companyEmail.split("\\s*,\\s*");
		} else {
			companyEmails = new String[] { "Roy_Lee@PerfectCorp.com" };
			subject = subject + " - " + Constants.getWebsiteDomain();
		}
		
		if (brandEvent.getEventAttrJNode().getIsBcc() && companyEmails != null && companyEmails.length > 0)
			sendMailwithBcc(subject, content, companyEmails, eventUser.getMail());
		else
			sendMail(subject, content, eventUser.getMail());
	}

	public BrandEventDao getBrandEventDao() {
		return brandEventDao;
	}

	public void setBrandEventDao(BrandEventDao brandEventDao) {
		this.brandEventDao = brandEventDao;
	}

	public EventUserDao getEventUserDao() {
		return eventUserDao;
	}

	public void setEventUserDao(EventUserDao eventUserDao) {
		this.eventUserDao = eventUserDao;
	}
}