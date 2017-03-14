package com.cyberlink.cosmetic.modules.mail.service;

public interface MailJoinEventConsultationService {
	void send(Long eventUserId, Long brandEventId);
}