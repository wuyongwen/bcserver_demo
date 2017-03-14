package com.cyberlink.cosmetic.action.backend.service;

import java.util.Map;

public interface PostTopKeywordService {

	void start();
	
	void stop();
	
	Map<String, Object> getStatus();

    //For background invoke
    void exec();

    void exec2();
}
