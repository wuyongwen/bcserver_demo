package com.cyberlink.cosmetic.event.circle;

import com.cyberlink.core.event.DurableEvent;

public class CircleUnfollowEvent extends DurableEvent {

    private static final long serialVersionUID = 4861011701415756643L;
    private Long followerId;
    private Long circleId;
    private Long circleCreatorId;

    public CircleUnfollowEvent() {
    }

    public CircleUnfollowEvent(Long followerId, Long circleId, Long creatorId) {
        this.followerId = followerId;
        this.circleId = circleId;
        this.circleCreatorId = creatorId;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public Long getCircleId() {
        return circleId;
    }

    public Long getCircleCreatorId() {
        return circleCreatorId;
    }

}
