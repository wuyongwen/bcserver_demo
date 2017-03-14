package com.cyberlink.cosmetic.event.post;

import java.util.Date;
import java.util.List;

import com.cyberlink.core.event.DurableEvent;

public class DiscoverPostCreateEvent extends DurableEvent {

    private static final long serialVersionUID = -6041527224570737448L;

    private Long postId;

    private String region;

    private Long circleId;
    
    private Long circleTypeId;

    private List<Long> circleTypeIds;

    private Long creatorId;

    private Boolean hideInAll;
    
    private Date created;

    private Long promoteScore;
    
    public DiscoverPostCreateEvent() {
        super(new Object());
    }

    public DiscoverPostCreateEvent(Long postId, String region, List<Long> circleTypeIds,
            Long creatorId, Long promoteScore, Boolean hideInAll, Date created) {
        super(postId);
        this.postId = postId;
        this.region = region;
        this.circleTypeIds = circleTypeIds;
        if(circleTypeIds != null && circleTypeIds.size() > 0)
            this.circleTypeId = circleTypeIds.get(0);
        this.creatorId = creatorId;
        this.created = created;
        this.hideInAll = hideInAll;
        this.promoteScore = promoteScore;
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

    public void setCircleTypeId(Long circleTypeId) {
        this.circleTypeId = circleTypeId;
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

    public Long getCircleTypeId() {
        return circleTypeId;
    }

    public List<Long> getCircleTypeIds() {
        return circleTypeIds;
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

    public Boolean getHideInAll() {
        return hideInAll;
    }

    public Date getCreated() {
        return created;
    }

    public Long getpromoteScore() {
        return promoteScore;
    }
    
    @Override
    public Boolean isGlobal() {
        return false;
    }
}
