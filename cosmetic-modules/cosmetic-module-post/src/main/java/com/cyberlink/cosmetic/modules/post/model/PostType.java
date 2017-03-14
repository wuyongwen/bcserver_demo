package com.cyberlink.cosmetic.modules.post.model;

import java.util.HashMap;
import java.util.Map;

public enum PostType {
    NORMAL			(false, false, false, false, ""), 
    YCL_LOOK		(true, false, false, true, "YCL"),
    HOW_TO          (true, false, false, true, "YCL");

	PostType(Boolean hasExtLookUrl, Boolean hasLookTag, Boolean hasHoroscopeTag, Boolean hasLookTypeId, String withAppName){
		this.hasExtLookUrl = hasExtLookUrl;
		this.hasLookTag = hasLookTag;
		this.hasHoroscopeTag = hasHoroscopeTag;
		this.hasLookTypeId = hasLookTypeId;
		this.withAppName = withAppName;
	}
	
	private Boolean hasExtLookUrl = false;
	private Boolean hasLookTag = false;
	private Boolean hasHoroscopeTag = false;
	private Boolean hasLookTypeId = false;
	private String withAppName = "";

	public Boolean getHasExtLookUrl() {
		return hasExtLookUrl;
	}

	public Boolean getHasLookTag() {
		return hasLookTag;
	}

	public Boolean getHasHoroscopeTag() {
		return hasHoroscopeTag;
	}
	
	public Boolean getHasLookTypeId() {
		return hasLookTypeId;
	}

	public String getWithAppName() {
		return withAppName;
	}
	
	public Map<String, Object> getSerializedPostType(){ //for creating & modifying post in v1 backend
    	Map<String, Object> postTypeMap = new HashMap<String, Object>();
    	postTypeMap.put("hasExtLookUrl", getHasExtLookUrl());
    	postTypeMap.put("hasLookTag", getHasLookTag());
    	postTypeMap.put("hasHoroscopeTag", getHasHoroscopeTag());
    	postTypeMap.put("hasLookTypeId", getHasLookTypeId());
    	postTypeMap.put("withAppName", getWithAppName());
    	return postTypeMap;
    }
	
}
