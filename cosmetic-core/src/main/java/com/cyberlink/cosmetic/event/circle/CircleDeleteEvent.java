package com.cyberlink.cosmetic.event.circle;

import java.util.List;

import com.cyberlink.core.event.DurableEvent;

public class CircleDeleteEvent extends DurableEvent {
    private static final long serialVersionUID = 404308169743098028L;

    private Long userId;
    private Long circleId;
    private List<Long> postIds;

    public CircleDeleteEvent() {
        super();
    }

    public CircleDeleteEvent(Long userId, Long circleId, List<Long> postIds) {
        super(userId + circleId);
        this.userId = userId;
        this.circleId = circleId;
        this.postIds = postIds;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCircleId() {
        return circleId;
    }
    
    public List<Long> getPostIds() {
        return postIds;
    }

}
