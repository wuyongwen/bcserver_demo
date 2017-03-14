package com.cyberlink.cosmetic.modules.user.event;

import com.cyberlink.core.event.DurableEvent;

public class UserUnfollowEvent extends DurableEvent {

    private static final long serialVersionUID = -254191828928661014L;
    private Long followerId;
    private Long followeeId;

    public UserUnfollowEvent() {

    }

    public UserUnfollowEvent(Long followerId, Long followeeId) {
        super(followerId + followeeId);
        this.followerId = followerId;
        this.followeeId = followeeId;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public Long getFolloweeId() {
        return followeeId;
    }

}
