package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class ProfileUpdateEvent extends DurableEvent {

    private static final long serialVersionUID = -9179337766192292025L;
    private Long userId;

    public ProfileUpdateEvent() {
        super(new Object());
    }

    public ProfileUpdateEvent(Long userId) {
        super(userId);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public Boolean isGlobal() {
        return false;
    }
}
