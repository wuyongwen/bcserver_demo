package com.cyberlink.cosmetic.modules.user.model;

public enum UserStatus {
	Published(true), Hidden(false);
	
	final private Boolean needUpdateUsrAttr;
	
	UserStatus(Boolean needUpdateUsrAttr) {
	    this.needUpdateUsrAttr = needUpdateUsrAttr;
	}
	
	public Boolean needUpdateUsrAttr() {
	    return needUpdateUsrAttr;
	}
}
