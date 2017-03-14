package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class UserUnblockUserEvent extends DurableEvent {
    private static final long serialVersionUID = 6401623581029639477L;

    private Long userId;
    
    private Long blockedUserId;

    public UserUnblockUserEvent() {
    }

    public UserUnblockUserEvent(Long userId, Long blockedUserId) {
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
