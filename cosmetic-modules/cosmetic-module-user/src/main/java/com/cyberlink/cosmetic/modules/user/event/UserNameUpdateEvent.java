package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class UserNameUpdateEvent extends DurableEvent {

	private static final long serialVersionUID = 435200751616545162L;
	private Long userId;
	private String displayName;
	
	public UserNameUpdateEvent() {
		super(new Object());
    }

    public UserNameUpdateEvent(Long userId, String displayName) {
        super(userId);
        this.userId = userId;
        this.displayName = displayName;
    }

	public Long getUserId() {
		return userId;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public Boolean isGlobal() {
        return false;
    }
    
	@Override
    public Boolean toMaster() {
        return true;
    }
    
}