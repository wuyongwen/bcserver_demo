package com.cyberlink.cosmetic.modules.feed.event;

import com.cyberlink.core.event.DurableEvent;

public class FeedVisitEvent extends DurableEvent {

    private static final long serialVersionUID = -7011954923414713955L;

    private Long userId;

    public FeedVisitEvent() {

    }

    public FeedVisitEvent(Long userId) {
        super(userId);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

}
