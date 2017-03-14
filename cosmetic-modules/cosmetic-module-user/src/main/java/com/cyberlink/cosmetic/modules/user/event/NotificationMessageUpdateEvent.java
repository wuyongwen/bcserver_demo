package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class NotificationMessageUpdateEvent extends DurableEvent {

    private static final long serialVersionUID = 5027496118880526666L;
    private Long userId;
    private String ap;
    private String uuid;
    private Boolean isDisabled;

    public NotificationMessageUpdateEvent() {
        super(new Object());
    }

    public NotificationMessageUpdateEvent(Long userId, String ap, String uuid,
            Boolean isDisabled) {
        super(userId);
        this.userId = userId;
        this.ap = ap;
        this.uuid = uuid;
        this.isDisabled = isDisabled;
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

    public Boolean getIsDisabled() {
        return isDisabled;
    }

    @Override
    public Boolean isGlobal() {
        return false;
    }
}
