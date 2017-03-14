package com.cyberlink.core.service;

import java.io.File;
import java.util.Map;

public interface MailSender {
    void sendMimeMessage(String subject, String content, String... email);

    void sendMimeMessage(String subject, String content,
            Map<String, File> attachments, String... email);
}
