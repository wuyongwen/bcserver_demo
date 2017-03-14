package com.cyberlink.cosmetic.modules.mail.service;

public interface MailJoinEventHomeService {
	void send(Long eventUserId, Long brandEventId);
}