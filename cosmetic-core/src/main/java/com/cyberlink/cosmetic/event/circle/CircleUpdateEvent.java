package com.cyberlink.cosmetic.event.circle;

import com.cyberlink.core.event.DurableEvent;

public class CircleUpdateEvent extends DurableEvent {

    private static final long serialVersionUID = -2422915844623008898L;

    private Long circleId;

    private Long creatorId;

    public CircleUpdateEvent() {
        super();
    }

    public CircleUpdateEvent(Long circleId, Long creatorId) {
        super(circleId);
        this.circleId = circleId;
        this.creatorId = creatorId;
    }

    public Long getCircleId() {
        return circleId;
    }

    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

}
