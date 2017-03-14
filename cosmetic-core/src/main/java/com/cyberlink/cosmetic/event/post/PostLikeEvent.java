package com.cyberlink.cosmetic.event.post;

import com.cyberlink.core.event.DurableEvent;

public class PostLikeEvent extends DurableEvent {

    private static final long serialVersionUID = -1022830529898663083L;

    private Long postId;
    private Long userId;
    private Long createdTime;

    public PostLikeEvent() {
    }

    public PostLikeEvent(Long postId, Long userId, Long createdTime) {
        super(postId + userId);
        this.postId = postId;
        this.userId = userId;
        this.createdTime = createdTime;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCreatedTime() {
        return createdTime;
    }
}
