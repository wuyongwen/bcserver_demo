package com.cyberlink.cosmetic.core.service;

import java.io.File;
import java.util.Map;

import com.cyberlink.core.service.MailSender;

public interface BCMailSender extends MailSender {
	void sendMimeMessageWithBcc(String subject, String content, String[] bccEmail, String... email);

	void sendMimeMessageWithBcc(String subject, String content,
			Map<String, File> attachments, String[] bccEmail, String... email);
}
