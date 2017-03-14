package com.cyberlink.cosmetic.event.post;

import com.cyberlink.core.event.DurableEvent;

public class PostDeleteEvent extends DurableEvent {

    private static final long serialVersionUID = 117571122403062199L;
    private Long postId;
    private Long creatorId;
    private Long circleId;

    public PostDeleteEvent() {
    }

    public PostDeleteEvent(Long postId, Long creatorId, Long circleId) {
        super(postId);
        this.postId = postId;
        this.creatorId = creatorId;
        this.circleId = circleId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public Long getCircleId() {
        return circleId;
    }

    public Long getPostId() {
        return postId;
    }

}
