package com.cyberlink.cosmetic.modules.user.repository;

public interface UserSessionRepository {
	
	void addSession(String token, Long userId);
	
	void removeSession (String token);
	
	Long authenticate (String token);
}