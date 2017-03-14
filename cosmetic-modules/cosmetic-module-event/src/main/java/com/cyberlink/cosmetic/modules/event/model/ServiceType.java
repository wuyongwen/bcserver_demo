package com.cyberlink.cosmetic.modules.event.model;

import java.util.ArrayList;
import java.util.List;

public enum ServiceType {
	FREE_SAMPLE, CONSULTATION;
	
	 public static List<ServiceType> getSendCustomerType() {
		 List<ServiceType> list = new ArrayList<ServiceType>();
		 list.add(ServiceType.FREE_SAMPLE);
		 return list;
	 }
}