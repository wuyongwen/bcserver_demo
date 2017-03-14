package com.cyberlink.cosmetic.modules.mail.service;

public interface MailInappropPostCommentService {
    void send(String bannedTargetType, Long bannedTargetId, String reason);
    void directSend(String address, String subject, String content);
    void directSend(String[] address, String subject, String content);
}
