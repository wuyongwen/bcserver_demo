package com.cyberlink.cosmetic.modules.mail.service;

public interface MailJoinEventStoreService {
	void send(Long eventUserId, Long brandEventId);
}