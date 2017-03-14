	package com.cyberlink.cosmetic.modules.mail.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.core.service.BCMailSender;
import com.cyberlink.cosmetic.modules.mail.model.MailSendEvent;
import com.cyberlink.cosmetic.modules.mail.model.MailType;

//import com.directorzone.mail.event.MailSendEvent;




import com.cyberlink.cosmetic.modules.mail.service.SMTPMXLookup;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.BlockMailDomainDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.BlockMailDomain;

import freemarker.template.Configuration;
import freemarker.template.Template;

public abstract class AbstractMailService extends AbstractService implements
        ApplicationEventPublisherAware {
    private BCMailSender mailService;
    private final MailType mailType;
    private ApplicationEventPublisher publisher;
    private Configuration configuration;
    protected AccountDao accountDao; 
    private BlockMailDomainDao blockMailDomainDao;
    
    public void setBlockMailDomainDao(BlockMailDomainDao blockMailDomainDao) {
		this.blockMailDomainDao = blockMailDomainDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setMailService(BCMailSender mailService) {
        this.mailService = mailService;
    }

    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    private void publishMailSendEvent() {
        publisher.publishEvent(new MailSendEvent(mailType.toString()));
    }

    protected AbstractMailService(MailType mailType) {
        this.mailType = mailType;
    }

    protected void sendMail(String subject, String content, String... emails) {
    	String[] validEmails = validateEmailAddress(emails);
    	if (validEmails == null || validEmails.length == 0)
    		return;
    	
        mailService.sendMimeMessage(subject, content, validEmails);
        publishMailSendEvent();
    }
    
    protected void sendMailwithBcc(String subject, String content, String[] bcc, String... emails) {
    	String[] validEmails = validateEmailAddress(emails);
    	if (validEmails == null || validEmails.length == 0)
    		return;
    	
        mailService.sendMimeMessageWithBcc(subject, content, bcc, validEmails);
        publishMailSendEvent();
    }
    
    protected void sendMailwithBcc(String subject, String content, Map<String, File> attachments, String[] bcc, String... emails) {
    	String[] validEmails = validateEmailAddress(emails);
    	if (validEmails == null || validEmails.length == 0)
    		return;
    	
        mailService.sendMimeMessageWithBcc(subject, content, attachments, bcc, validEmails);
        publishMailSendEvent();
    }

    private Template getTemplate(Locale locale) throws IOException {
        return configuration.getTemplate(mailType.getTemplatePath(), locale);
    }

    protected String getContent(Locale locale, Map<String, Object> data) {
        try {
            if (locale == null) {
                return FreeMarkerTemplateUtils.processTemplateIntoString(
                        getTemplate(Locale.US), data);
            } else {
                return FreeMarkerTemplateUtils.processTemplateIntoString(
                        getTemplate(locale), data);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException("Fail to generate mail content", e);
        }
    }

    protected String getSubject(Locale locale) {
        if (locale.getLanguage().equalsIgnoreCase("en")) {
        	return "Welcome to Beauty Circle!";        	
        } else if (locale.getLanguage().equalsIgnoreCase("de")){
        	return "Willkommen beim Beauty Circle!";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("es")){
        	return "¡Bienvenidas a Beauty Circle!";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("fr")){
        	return "Bienvenue sur Sphère Beauté!";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("it")){
        	return "Ti diamo il benvenuto nel Beauty Circle!";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("ja")){
        	return "ビューティーサークルをご利用いただきありがとうございます。";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("ko")){
        	return "Beauty Circle 에 오신 것을 환영합니다!";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("ru")){
        	return "Добро пожаловать в Beauty Circle!";        	        	
        } else if (locale.getCountry().equalsIgnoreCase("CN")){
        	return "欢迎来到玩美圈！";        	        	
        } else if (locale.getCountry().equalsIgnoreCase("TW")){
        	return "歡迎來到玩美圈！";        	        	
        } else {
        	return "Welcome to Beauty Circle!";   
        }
    }
    
    protected String getCopyRight(Locale locale) {
        if (locale.getLanguage().equalsIgnoreCase("en")) {
        	return "© 2015 CyberLink Corp. All Rights Reserved.";        	
        } else if (locale.getLanguage().equalsIgnoreCase("de")){
        	return "© 2015 CyberLink Corp. Alle Rechte vorbehalten.";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("es")){
        	return "Copyright © 2015 CyberLink Corp. Todos los derechos reservados.";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("fr")){
        	return "© Copyright 2015 Groupe CyberLink. Tous droits réservés.";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("it")){
        	return "© 2015 CyberLink Corp. All Rights Reserved.";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("ja")){
        	return "© 2015 CyberLink Corp. 無断複写・複製・転載を禁ず";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("ko")){
        	return "© 2015 CyberLink Corp. All Rights Reserved.";        	        	
        } else if (locale.getLanguage().equalsIgnoreCase("ru")){
        	return "© 2015 CyberLink Corp. All Rights Reserved.";        	        	
        } else if (locale.getCountry().equalsIgnoreCase("CN")){
        	return "© 2015 讯连科技. 保留所有权利";        	        	
        } else if (locale.getCountry().equalsIgnoreCase("TW")){
        	return "© 2015 訊連科技。保留所有權利。";        	        	
        } else {
        	return "© 2015 CyberLink Corp. All Rights Reserved.";   
        }
    }

    protected MailType getMailType() {
        return this.mailType;
    }
    
	protected boolean MXChecking(String address, final ArrayList mxList, Boolean isMXonly) {
		// Find the separator for the domain name
		int pos = address.indexOf('@');

		// If the address does not contain an '@', it's not valid
		if (pos == -1)
			return false;

		// Isolate the domain/machine name and get a list of mail exchangers
		String domain = address.substring(++pos);
		if(blockMailDomainDao.isBlockedDomain(domain))
			return false;		
		try {
			if (isMXonly)
				mxList.addAll(SMTPMXLookup.getMXOnly(domain));
			else
				mxList.addAll(SMTPMXLookup.getMX(domain));
		} catch (NamingException ex) {
			blockDomain(domain);
			return false;
		}

		// Just because we can send mail to the domain, doesn't mean that the
		// address is valid, but if we can't, it's a sure sign that it isn't
		if (mxList.size() == 0) {
			blockDomain(domain);
			return false;
		}

		return true;
	}
	
	protected String[] validateEmailAddress(String... emails) {
		if (emails == null || emails.length == 0)
			return null;
		
		List<String> validEmail = new ArrayList<String>(); 
		for (int i = 0; i < emails.length; i++) {
			String address = emails[i];
			
			List<Account> accountList = accountDao.findByEmail(address);
			if (accountList.size() > 0) {
				if (AccountMailStatus.INVALID.equals(accountList.get(0).getMailStatus()))
					continue;
				else if (accountList.get(0).getIsVerified()) {
					validEmail.add(address);
					continue;
				}
			}
			
			final ArrayList mxList = new ArrayList();
						
			Boolean isInvalid = Boolean.FALSE;
			if (MXChecking(address, mxList, true)) {
				validEmail.add(address);
				isInvalid = Boolean.FALSE;
			} else
				isInvalid = Boolean.TRUE;
			
			// update account status
			if (accountList.size() > 0) {
				if (isInvalid)
					accountDao.updateStatus(address, AccountMailStatus.INVALID);
				else
					accountDao.updateStatus(address, null);
			}
		}
		
		if (validEmail.size() == 0)
			return null;
		
		String[] result = new String[validEmail.size()];
		return validEmail.toArray(result);
	}
	
	private void blockDomain(String domain) {
		BlockMailDomain bockdomain = new BlockMailDomain();
		bockdomain.setDomain(domain);
		blockMailDomainDao.create(bockdomain);
	}
}
