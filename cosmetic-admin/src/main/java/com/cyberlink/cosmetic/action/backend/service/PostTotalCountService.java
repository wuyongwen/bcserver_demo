package com.cyberlink.cosmetic.action.backend.service;

public interface PostTotalCountService {

	void start();

	void stop();

	String getStatus();

	// For background invoke
	void exec();

}