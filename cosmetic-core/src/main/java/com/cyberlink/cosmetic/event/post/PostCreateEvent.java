package com.cyberlink.cosmetic.event.post;

import java.util.Date;

import com.cyberlink.core.event.DurableEvent;

public class PostCreateEvent extends DurableEvent {

    private static final long serialVersionUID = -6331829250954993286L;

    private Long postId;

    private Long circleId;

    private String locale;
    
    private Long creatorId;

    private Long rootId;
    
    private Date created;

    public PostCreateEvent() {
    }

    public PostCreateEvent(Long postId, Long circleId, String locale, 
            Long creatorId, Long rootId, Date created) {
        super(postId);
        this.postId = postId;
        this.circleId = circleId;
        this.locale = locale;
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

    public String getLocale() {
        return locale;
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
