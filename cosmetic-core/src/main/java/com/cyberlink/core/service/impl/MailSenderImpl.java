package com.cyberlink.core.service.impl;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.service.MailSender;

public class MailSenderImpl extends AbstractService implements MailSender {
    private String from;
    private JavaMailSender mailSender;
    private String bcc;

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
    public void sendMimeMessage(final String subject, final String content,
            final String... email) {
        final Map<String, File> attachements = Collections.emptyMap();
        sendMimeMessage(subject, content, attachements, email);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
    public void sendMimeMessage(final String subject, final String content,
            final Map<String, File> attachments, final String... email) {
        final MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                final MimeMessageHelper helper = new MimeMessageHelper(
                        mimeMessage, !attachments.isEmpty(), "UTF-8");
                if (!StringUtils.isEmpty(from)) {
                    helper.setFrom(from);
                }
                if (!StringUtils.isEmpty(bcc)) {
                    helper.setBcc(bcc);
                }
                helper.setTo(email);
                helper.setSubject(subject);
                helper.setText(content, true);
                for (final Entry<String, File> a : attachments.entrySet()) {
                    helper.addAttachment(a.getKey(), a.getValue());
                }
            }
        };
        mailSender.send(preparator);
    }
}
