package com.cyberlink.cosmetic.action.backend.service;

public interface CommentsMailService {
	
	void start();

	void stop();

	String getStatus();
	
	void exec();
	
	void execNow();

}
