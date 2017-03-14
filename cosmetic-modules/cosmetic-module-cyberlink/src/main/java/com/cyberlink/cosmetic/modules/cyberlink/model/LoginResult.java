package com.cyberlink.cosmetic.modules.cyberlink.model;

import java.util.Map;

public class LoginResult {
	CyberLinkMemberStatus status;
	Map<String, Object> contentMap;
	public LoginResult(CyberLinkMemberStatus status, Map<String, Object> contentMap) {
		this.status = status;
		this.contentMap = contentMap;
	}
	public CyberLinkMemberStatus getStatus() {
		return status;
	}
	public void setStatus(CyberLinkMemberStatus status) {
		this.status = status;
	}
	public Map<String, Object> getContentMap() {
		return contentMap;
	}
	public void setContentMap(Map<String, Object> contentMap) {
		this.contentMap = contentMap;
	}
}
