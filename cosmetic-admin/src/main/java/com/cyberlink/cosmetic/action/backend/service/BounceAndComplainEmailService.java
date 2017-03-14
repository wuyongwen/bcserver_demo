package com.cyberlink.cosmetic.action.backend.service;

public interface BounceAndComplainEmailService {
	
	void start();

	void stop();

	void exec();
	
	Boolean IsReady();
	
	Boolean IsRunning();
	
	String getStatus();

	void setIsDebugMode();

	void setIsNotDebugMode();

	Boolean IsDebugMode();
}