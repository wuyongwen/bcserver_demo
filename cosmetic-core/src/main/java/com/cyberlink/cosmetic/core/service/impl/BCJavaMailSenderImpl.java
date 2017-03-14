package com.cyberlink.cosmetic.core.service.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class BCJavaMailSenderImpl extends JavaMailSenderImpl {

	private static final String HEADER_MESSAGE_ID = "Message-ID";
	
	// Keep transport connected
	private Transport transport;

	@Override
	protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
		Map<Object, Exception> failedMessages = new LinkedHashMap<Object, Exception>();

		try {
			if (transport == null || !transport.isConnected()) {
				transport = getTransport(getSession());
				transport.connect(getHost(), getPort(), getUsername(), getPassword());
			}
		}
		catch (AuthenticationFailedException ex) {
			throw new MailAuthenticationException(ex);
		}
		catch (MessagingException ex) {
			// Effectively, all messages failed...
			for (int i = 0; i < mimeMessages.length; i++) {
				Object original = (originalMessages != null ? originalMessages[i] : mimeMessages[i]);
				failedMessages.put(original, ex);
			}
			throw new MailSendException("Mail server connection failed", ex, failedMessages);
		}

		try {
			for (int i = 0; i < mimeMessages.length; i++) {
				MimeMessage mimeMessage = mimeMessages[i];
				try {
					if (mimeMessage.getSentDate() == null) {
						mimeMessage.setSentDate(new Date());
					}
					String messageId = mimeMessage.getMessageID();
					mimeMessage.saveChanges();
					if (messageId != null) {
						// Preserve explicitly specified message id...
						mimeMessage.setHeader(HEADER_MESSAGE_ID, messageId);
					}
					transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
				}
				catch (MessagingException ex) {
					Object original = (originalMessages != null ? originalMessages[i] : mimeMessage);
					failedMessages.put(original, ex);
				}
			}
		}
		finally {
			/*try {
				transport.close();
			}
			catch (MessagingException ex) {
				if (!failedMessages.isEmpty()) {
					throw new MailSendException("Failed to close server connection after message failures", ex,
							failedMessages);
				}
				else {
					throw new MailSendException("Failed to close server connection after message sending", ex);
				}
			}*/
		}

		if (!failedMessages.isEmpty()) {
			throw new MailSendException(failedMessages);
		}
	}
}
