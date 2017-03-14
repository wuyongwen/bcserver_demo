package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class UserDeleteEvent extends DurableEvent {

    private static final long serialVersionUID = 5476419879287771455L;

    private Long userId;

    public UserDeleteEvent() {
    }

    public UserDeleteEvent(Long userId) {
        super(userId);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

}
