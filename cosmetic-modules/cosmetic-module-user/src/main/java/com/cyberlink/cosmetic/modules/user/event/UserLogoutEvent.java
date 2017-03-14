package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class UserLogoutEvent extends DurableEvent {

    private static final long serialVersionUID = -5792484475839219643L;
    private Long userId;
    private String ap;
    private String uuid;
    private String token;

    public UserLogoutEvent() {
        super(new Object());
    }

    public UserLogoutEvent(Long userId, String ap, String uuid) {
        super(userId);
        this.userId = userId;
        this.ap = ap;
        this.uuid = uuid;
        this.token = "";
    }

    public UserLogoutEvent(Long userId, String ap, String uuid, String token) {
        super(userId);
        this.userId = userId;
        this.ap = ap;
        this.uuid = uuid;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getAp() {
        return ap;
    }

    public String getUuid() {
        return uuid;
    }

}
