package com.cyberlink.cosmetic.event.post;

import java.util.Date;

import com.cyberlink.core.event.DurableEvent;

public class PostFanOutEvent extends DurableEvent {

    private Long postId;

    private Long circleId;

    private Long creatorId;

    private Long rootId;
    
    private Date created;

    public PostFanOutEvent() {
    }

    public PostFanOutEvent(Long postId, Long circleId, Long creatorId,
            Long rootId, Date created) {
        super(postId);
        this.postId = postId;
        this.circleId = circleId;
        this.creatorId = creatorId;
        this.rootId = rootId;
        this.created = created;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getCircleId() {
        return circleId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getCreatorId() {
        return creatorId;
    }
    
    public Long getRootId() {
        return rootId;
    }
    
    public Date getCreated() {
        return created;
    }

}
