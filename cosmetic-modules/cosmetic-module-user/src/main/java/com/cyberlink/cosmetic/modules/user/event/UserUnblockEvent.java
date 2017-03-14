package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class UserUnblockEvent extends DurableEvent {

    private static final long serialVersionUID = -7841340098773107479L;
    private Long userId;

    public UserUnblockEvent() {
    }

    public UserUnblockEvent(Long userId) {
        super(userId);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
