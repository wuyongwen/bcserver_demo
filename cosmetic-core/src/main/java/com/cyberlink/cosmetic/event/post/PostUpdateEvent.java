package com.cyberlink.cosmetic.event.post;

import com.cyberlink.core.event.DurableEvent;

public class PostUpdateEvent extends DurableEvent {

    private static final long serialVersionUID = -4847089248874041924L;

    private Long postId;

    private Long creatorId;

    private Long originalCircleId;

    private Long circleId;

    public PostUpdateEvent() {
    }

    public PostUpdateEvent(Long postId, Long creatorId, Long originalCircleId,
            Long circleId) {
        super(postId);
        this.postId = postId;
        this.creatorId = creatorId;
        this.originalCircleId = originalCircleId;
        this.circleId = circleId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getOriginalCircleId() {
        return originalCircleId;
    }

    public void setOriginalCircleId(Long originalCircleId) {
        this.originalCircleId = originalCircleId;
    }

    public Long getCircleId() {
        return circleId;
    }

    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }

}
