package com.cyberlink.cosmetic.action.backend.service;

import java.util.Date;

import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.user.model.FanPageUser;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

public interface AutoFanPagePostService {
	public static final String defaultKey = "MgATExEaNw==";
	
	void start();

	void stop();

	String getStatus();

	// For background invoke
	void exec();

	public String getFanpageId(String fanPageName);

	public JsonObject getFanpageObject(String fanPageLink);

	public JsonObject getFanpageObject(String fanPageName, String lastPostTime);

	public JsonObject doQuery(String fanPageId, FanPageUser fanPageUser,
			Integer limit, Date sinceTime, Date untilTime, String nextUrl,
			JsonArray dataArray);

	public JsonObject createPost(String postData, String lastPostTime,
			PostStatus postStatus);

}