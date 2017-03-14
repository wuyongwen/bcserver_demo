package com.cyberlink.cosmetic.modules.event.model;

import java.util.ArrayList;
import java.util.List;

public enum EventType {
	SelectUser, LimitProdNum;
	
	public static List<EventType> getSendCustomerType() {
		 List<EventType> list = new ArrayList<EventType>();
		 list.add(EventType.SelectUser);
		 list.add(EventType.LimitProdNum);
		 return list;
	 }
}
