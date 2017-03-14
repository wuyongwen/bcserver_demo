package com.cyberlink.cosmetic.event.post;

import java.util.Date;

import com.cyberlink.core.event.DurableEvent;

public class OfficialPostCreateEvent extends DurableEvent {

    private static final long serialVersionUID = -502446756207785786L;

    private Long postId;

    private String region;
    
    private Long circleId;

    private Long creatorId;

    private Date created;

    public OfficialPostCreateEvent() {
        super(new Object());
    }

    public OfficialPostCreateEvent(Long postId, String region, Long circleId, Long creatorId,
            Date created) {
        super(postId);
        this.postId = postId;
        this.region = region;
        this.circleId = circleId;
        this.creatorId = creatorId;
        this.created = created;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    
    public void setCircleId(Long circleId) {
        this.circleId = circleId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
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

    public String getRegion() {
        return region;
    }
    
    public Long getCreatorId() {
        return creatorId;
    }

    public Date getCreated() {
        return created;
    }

}
