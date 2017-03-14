package com.cyberlink.cosmetic.modules.mail.service;

public interface MailFreeSampleCustomerService {
    void send(Long brandEventId);
    void directSend(String address, String subject, String content);
}