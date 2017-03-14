package com.cyberlink.cosmetic.event.user;

import com.cyberlink.core.event.DurableEvent;

/**
 * trigger when user signs in
 * 
 * @author steve_lee
 *
 */
public class UserSignInEvent extends DurableEvent {

    private static final long serialVersionUID = -3002844196190559211L;
    private Long userId;
    private String userRegion;

    public UserSignInEvent() {

    }

    public UserSignInEvent(Long userId, String userRegion) {
        this.userId = userId;
        this.userRegion = userRegion;
    }

    public Long getUserId() {
        return userId;
    }
    
    public String getUserRegion() {
        return userRegion;
    }

}
