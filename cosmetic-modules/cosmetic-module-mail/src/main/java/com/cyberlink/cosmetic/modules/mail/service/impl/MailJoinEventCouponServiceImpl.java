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
import com.cyberlink.cosmetic.modules.mail.model.event.EventReceiveData;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailJoinEventCouponService;

public class MailJoinEventCouponServiceImpl extends AbstractMailService
		implements MailJoinEventCouponService, ApplicationEventPublisherAware {

	private BrandEventDao brandEventDao;
	private EventUserDao eventUserDao;

	protected MailJoinEventCouponServiceImpl() {
		super(MailType.JOIN_EVENT_COUPON);
	}

	@BackgroundJob
	public void send(Long eventUserId, Long brandEventId) {
		BrandEvent brandEvent = brandEventDao.findById(brandEventId);
		EventUser eventUser = eventUserDao.findById(eventUserId);
		final EventReceiveData receiveData = new EventReceiveData(brandEvent,
				eventUser);
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("eventUser", receiveData);
		Locale locale = new Locale(receiveData.getLocale().substring(0, 2),
				receiveData.getLocale().substring(3, 5));
		data.put("copyRight", receiveData.getCopyRight());
		data.put("mailWidth", 700);
		data.put("websiteDomain", Constants.getWebsiteDomain());
		final String subject = receiveData.getFreeSampleSubject();
		final String content = getContent(locale, data);
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