package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailActivationService;
import com.cyberlink.cosmetic.modules.mail.service.MailSyncReportService;
import com.cyberlink.cosmetic.modules.user.model.Member;

public class MailSyncReportServiceImpl extends AbstractMailService implements MailSyncReportService,
ApplicationEventPublisherAware {
	public MailSyncReportServiceImpl() {
        super(MailType.SYNC_REPORT);
    }

	@Override
	public void send(String result) {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("result", result);
        final String subject = "Sync Result";
        Locale locale = new Locale("zh", "TW");
        final String content = getContent(locale, data);
        try {
        	sendMail(subject, content, "Ivon_Chang@cyberlink.com");
        } catch(Exception e) {}
        try {
        	sendMail(subject, content, "Frank_Chuang@cyberlink.com");
        } catch(Exception e) {}
	}

}
