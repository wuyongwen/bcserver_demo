package com.cyberlink.cosmetic.event.circle;

import com.cyberlink.core.event.DurableEvent;

public class CircleCreateEvent extends DurableEvent {

    private static final long serialVersionUID = -8338247484458863019L;
    private Long circleId;
    private Long creatorId;
    private Boolean isSecret;

    public CircleCreateEvent() {
    }

    public CircleCreateEvent(Long circleId, Long creatorId, Boolean isSecret) {
        super(circleId);
        this.circleId = circleId;
        this.creatorId = creatorId;
        this.isSecret = isSecret;
    }

    public Long getCircleId() {
        return circleId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public Boolean getIsSecret() {
        return isSecret;
    }

}
