package com.cyberlink.cosmetic.event;

import com.cyberlink.core.event.Event;

public class CurrentUserAuthEvent extends Event {
    private static final long serialVersionUID = -24169628199715702L;
    
    private Long userId;
    
    public CurrentUserAuthEvent(Long userId) {
        super(userId);
        this.userId = userId;
    }
    
    public Long getUserId() {
        return this.userId;
    }

}
