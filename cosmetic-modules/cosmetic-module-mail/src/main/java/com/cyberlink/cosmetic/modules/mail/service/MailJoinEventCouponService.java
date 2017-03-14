package com.cyberlink.cosmetic.modules.mail.service;

public interface MailJoinEventCouponService {
	void send(Long eventUserId, Long brandEventId);
}