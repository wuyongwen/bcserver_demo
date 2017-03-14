package com.cyberlink.cosmetic.event.post;

import com.cyberlink.core.event.DurableEvent;

public class CommentDeleteEvent extends DurableEvent {

    private static final long serialVersionUID = 2076710454352537070L;
    private Long commentId;
    private Long postId;
    private Long userId;

    public CommentDeleteEvent() {

    }

    public CommentDeleteEvent(Long id, Long postId, Long userId) {
        super(id);
        this.commentId = id;
        this.postId = postId;
        this.userId = userId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getUserId() {
        return userId;
    }

}
