package com.cyberlink.cosmetic.event.user;

import com.cyberlink.core.event.DurableEvent;

/**
 * trigger if active user becomes inactive (not access my-feed during the past
 * three months)
 * 
 * @author steve_lee
 *
 */
public class UserLeaveEvent extends DurableEvent {

    private static final long serialVersionUID = -482854669031545259L;

    private Long userId;

    public UserLeaveEvent() {

    }

    public UserLeaveEvent(Long userId) {
        super(userId);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

}
