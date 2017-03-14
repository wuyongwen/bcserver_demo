package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class UserBlockUserEvent extends DurableEvent {
    private static final long serialVersionUID = -8629608834536646996L;

    private Long userId;
    
    private Long blockedUserId;

    public UserBlockUserEvent() {
    }

    public UserBlockUserEvent(Long userId, Long blockedUserId) {
        super(userId);
        this.userId = userId;
        this.blockedUserId = blockedUserId;
    }

    public Long getUserId() {
        return userId;
    }
    
    public Long getBlockedUserId() {
        return blockedUserId;
    }
    
}
