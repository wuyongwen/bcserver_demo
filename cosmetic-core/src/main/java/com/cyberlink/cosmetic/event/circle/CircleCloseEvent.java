package com.cyberlink.cosmetic.event.circle;

import com.cyberlink.core.event.DurableEvent;

public class CircleCloseEvent extends DurableEvent {

    private static final long serialVersionUID = 1807985461885105035L;

    private Long creatorId;
    private Long circleId;

    public CircleCloseEvent() {
        super();
    }

    public CircleCloseEvent(Long creatorId, Long circleId) {
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
