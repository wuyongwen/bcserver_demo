package com.cyberlink.cosmetic.event.circle;

import com.cyberlink.core.event.DurableEvent;

public class CircleOpenEvent extends DurableEvent {

    private static final long serialVersionUID = 8191856113652887034L;

    private Long creatorId;
    private Long circleId;

    public CircleOpenEvent() {

    }

    public CircleOpenEvent(Long creatorId, Long circleId) {
        super(circleId);
        this.creatorId = creatorId;
        this.circleId = circleId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public Long getCircleId() {
        return circleId;
    }

}
