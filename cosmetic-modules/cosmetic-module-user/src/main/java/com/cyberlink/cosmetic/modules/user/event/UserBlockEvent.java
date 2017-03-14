package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class UserBlockEvent extends DurableEvent {

    private static final long serialVersionUID = 6715071949055207906L;
    private Long userId;

    public UserBlockEvent() {
    }

    public UserBlockEvent(Long userId) {
        super(userId);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
