package com.cyberlink.cosmetic.modules.mail.service.impl;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//import net.sourceforge.stripes.integration.spring.SpringBean;

import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.cosmetic.lang.LanguageCenter;
import com.cyberlink.cosmetic.modules.mail.model.MailType;
import com.cyberlink.cosmetic.modules.mail.service.MailActivationService;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Member;

public class MailActivationServiceImpl extends AbstractMailService implements MailActivationService,
        ApplicationEventPublisherAware {

	private MemberDao memberDao;
	
	public MailActivationServiceImpl() {
        super(MailType.MEMBER_ACTIVATION);
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
        data.put("member", member);
        data.put("activationCode",
        		member.getActivateCode());
        Locale locale = new Locale(member.getLocale().substring(0, 2), member.getLocale().substring(3, 5));
        
        String name = account.getAccount();
		try {
			name = name.split("@")[0];
		} catch (Exception e) {

		}
		data.put("name", name);
		if ("de_DE".equalsIgnoreCase(member.getLocale()))
			data.put("copyRight", LanguageCenter.getMailLang(member.getLocale()).getCopyRight());
		else
			data.put("copyRight", getCopyRight(locale));
        data.put("mailWidth", 700);
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
