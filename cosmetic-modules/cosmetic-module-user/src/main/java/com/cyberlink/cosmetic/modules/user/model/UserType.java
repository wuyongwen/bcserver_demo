package com.cyberlink.cosmetic.modules.user.model;

import java.util.ArrayList;
import java.util.List;

public enum UserType {
    Normal, CL, Blogger, Expert, Master, Brand, Publisher, Celebrity, LiveBrand, Anchor;
    
    public static List<UserType> getTotalPostType() {
		List<UserType> list = new ArrayList<UserType>();
		list.add(UserType.Expert);
		list.add(UserType.Master);
		list.add(UserType.Brand);
		list.add(UserType.Publisher);
		list.add(UserType.Celebrity);
		return list;
	}
    
    public static List<UserType> getNonTotalPostType() {
		List<UserType> list = new ArrayList<UserType>();
		list.add(UserType.Normal);
		list.add(UserType.CL);
		list.add(UserType.Blogger);
		return list;
	}
    
    public static List<UserType> getAvailableBlockType() {
		List<UserType> list = new ArrayList<UserType>();
		list.add(UserType.Normal);
		list.add(UserType.Blogger);
		return list;
	}
    
    public static List<UserType> getChatableType() {
		List<UserType> list = new ArrayList<UserType>();
		list.add(UserType.Normal);
		list.add(UserType.Expert);
		return list;
	}
    
    public static List<UserType> getReceiveNotifyType() {
		List<UserType> list = new ArrayList<UserType>();
		list.add(UserType.Normal);
		list.add(UserType.CL);
		return list;
	}
    
    public static List<UserType> getBCConsoleUserType() {
		List<UserType> list = new ArrayList<UserType>();
		list.add(UserType.CL);
		list.add(UserType.Expert);
		list.add(UserType.Master);
		list.add(UserType.Brand);
		list.add(UserType.Publisher);
		list.add(UserType.Celebrity);
		return list;
	}
}
