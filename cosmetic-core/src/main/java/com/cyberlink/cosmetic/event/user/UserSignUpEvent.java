package com.cyberlink.cosmetic.event.user;

import com.cyberlink.core.event.DurableEvent;

/**
 * trigger when user first signs up
 * 
 * @author steve_lee
 *
 */
public class UserSignUpEvent extends DurableEvent {

    private static final long serialVersionUID = 3384290330492585737L;
    private Long userId;

    public UserSignUpEvent() {

    }

    public UserSignUpEvent(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

}
