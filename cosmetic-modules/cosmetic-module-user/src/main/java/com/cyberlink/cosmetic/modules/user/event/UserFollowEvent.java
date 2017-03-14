package com.cyberlink.cosmetic.modules.user.event;

import java.util.Date;

import com.cyberlink.core.event.DurableEvent;

public class UserFollowEvent extends DurableEvent {

    private static final long serialVersionUID = -5673140876849709752L;
    private Long followerId;
    private Long followeeId;
    private Date created;

    public UserFollowEvent() {
        super(new Object());
    }
    
    public UserFollowEvent(Long followerId, Long followeeId, Date created) {
        super(followerId + followeeId);
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public Long getFolloweeId() {
        return followeeId;
    }

}
