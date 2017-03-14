package com.cyberlink.cosmetic.modules.post.service;

public interface DeleteUserService {
	void startAutoPostThread();
	void stopAutoPostThread();
	void pushUser(Long userId);
	
	String getStatus();
}