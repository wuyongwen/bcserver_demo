package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class UserSetRegionEvent extends DurableEvent {
	
	private static final long serialVersionUID = 7093704166393354076L;
	private Long userId;
    private String region;

    public UserSetRegionEvent() {
    }

    public UserSetRegionEvent(Long userId, String region) {
        super(userId);
        this.userId = userId;
        this.region = region;
    }

    public Long getUserId() {
        return userId;
    }
    
    public String getRegion() {
        return region;
    }

}
