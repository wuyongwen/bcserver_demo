package com.cyberlink.cosmetic.modules.post.event;

import com.cyberlink.core.event.DurableEvent;

public class PostCategoryUpdateEvent extends DurableEvent {

	private static final long serialVersionUID = 2163785920140379719L;
	
	private Long userId;
	
	private String circleType;
	
	public PostCategoryUpdateEvent() {
		super(new Object());
	}
	
	public PostCategoryUpdateEvent(Long userId, String circleType) {
		super(userId + circleType);
		this.userId = userId;
		this.circleType = circleType;
	}

	public Long getUserId() {
		return userId;
	}

	public String getCircleType() {
		return circleType;
	}
	
	@Override
	public Boolean toMaster() {
        return true;
    }
	
	@Override
	public Boolean isGlobal() {
        return false;
    }
}