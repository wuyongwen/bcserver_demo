package com.cyberlink.cosmetic.event.user;

import com.cyberlink.core.event.DurableEvent;

/**
 * trigger when inactive user logins
 * 
 * @author steve_lee
 *
 */
public class UserComeBackEvent extends DurableEvent {

    private static final long serialVersionUID = -248579543372365299L;

    private Long userId;

    public UserComeBackEvent() {

    }

    public UserComeBackEvent(Long userId) {
        super(userId);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

}
