package com.cyberlink.cosmetic.action.api;

import java.util.ArrayList;
import java.util.List;

public enum AppActionMap {
	COLLECT_RAW_IMAGE(0x0000000000000001L);

	private final long code;
	
	private AppActionMap(long code) {
		this.code = code;
	}	
	
	public long value() {
        return code;
    }	
	
	public static long getActionValue() {
		Long actionCode = Long.valueOf(0);		
		for (AppActionMap action : getActionList()) {
			actionCode = actionCode.longValue() | action.value();
		}
		return actionCode;
	}
	
	private static List<AppActionMap> getActionList() {
		List<AppActionMap> list = new ArrayList<AppActionMap>();
		return list;
	}
}