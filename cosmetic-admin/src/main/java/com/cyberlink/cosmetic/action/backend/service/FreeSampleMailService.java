package com.cyberlink.cosmetic.action.backend.service;

import java.util.Map;

public interface FreeSampleMailService {

	void start();

	void stop();

	String getStatus();

	// For background invoke
	void exec();

}