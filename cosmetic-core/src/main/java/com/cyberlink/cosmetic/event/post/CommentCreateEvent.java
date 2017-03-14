package com.cyberlink.cosmetic.event.post;

import com.cyberlink.core.event.DurableEvent;

public class CommentCreateEvent extends DurableEvent {

    private static final long serialVersionUID = 3212620166784576935L;
    private Long commentId;
    private Long postId;
    private Long userId;

    public CommentCreateEvent() {

    }

    public CommentCreateEvent(Long id, Long postId, Long userId) {
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
