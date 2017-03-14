package com.cyberlink.cosmetic.modules.feed.service;

public interface InterestPoolGenerator {
	void generate();
	void generateForUser(Long userId, boolean doClean);
}
