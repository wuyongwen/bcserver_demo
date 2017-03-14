package com.cyberlink.cosmetic.event.post;

import com.cyberlink.core.event.DurableEvent;

public class PostUnlikeEvent extends DurableEvent {

    private static final long serialVersionUID = 6753138598854331710L;
    private Long postId;
    private Long userId;

    public PostUnlikeEvent() {
    }

    public PostUnlikeEvent(Long postId, Long userId) {
        super(postId + userId);
        this.postId = postId;
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getUserId() {
        return userId;
    }

}
