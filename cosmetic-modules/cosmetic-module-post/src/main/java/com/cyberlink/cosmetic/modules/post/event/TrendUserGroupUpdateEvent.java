package com.cyberlink.cosmetic.modules.post.event;

import com.cyberlink.core.event.DurableEvent;

public class TrendUserGroupUpdateEvent extends DurableEvent {

	private static final long serialVersionUID = -4405598948548326924L;
	
	private Long userId;
	
	private String userGroup;
	
	public TrendUserGroupUpdateEvent() {
		super(new Object());
	}
	
	public TrendUserGroupUpdateEvent(Long userId, String userGroup) {
		super(userId + userGroup);
		this.userId = userId;
		this.userGroup = userGroup;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUserGroup() {
		return userGroup;
	}
	
}