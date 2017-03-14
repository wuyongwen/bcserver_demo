package com.cyberlink.cosmetic.modules.circle.service;

public interface CircleFollowReCountService {
	void startReCountThread();
	void stopReCountThread();
	
	String getStatus();
	String getParam();
	void setSleep(int sleep);
	void setOffset(int offset);
	void setLimit(int limit);
}