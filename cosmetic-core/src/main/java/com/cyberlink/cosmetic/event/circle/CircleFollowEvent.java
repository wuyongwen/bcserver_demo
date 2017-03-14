package com.cyberlink.cosmetic.event.circle;

import java.util.Date;

import com.cyberlink.core.event.DurableEvent;

public class CircleFollowEvent extends DurableEvent {

    private static final long serialVersionUID = 7700090742646872513L;
    private Long followerId;
    private Long circleId;
    private Long circleCreatorId;
    private Date created = new Date();

    public CircleFollowEvent() {
    }

    public CircleFollowEvent(Long followerId, Long circleId,
            Long circleCreatorId, Date created) {
        super(followerId + circleId + circleCreatorId);
        this.followerId = followerId;
        this.circleId = circleId;
        this.circleCreatorId = circleCreatorId;
        if(created != null)
            this.created = created;
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

    public Date getCreated() {
        if(created == null)
            return new Date();
        
        return created;
    }
}
