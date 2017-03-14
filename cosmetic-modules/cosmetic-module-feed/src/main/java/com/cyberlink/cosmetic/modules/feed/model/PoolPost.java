package com.cyberlink.cosmetic.modules.feed.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.cyberlink.cosmetic.event.post.PostCreateEvent;
import com.cyberlink.cosmetic.event.post.PostDeleteEvent;
import com.cyberlink.cosmetic.event.post.PostFanOutEvent;

public class PoolPost implements Serializable {

    private static final long serialVersionUID = -8630269956108053382L;

    private final Long postId;
    private final Long creatorId;
    private final Long circleId;
    private Long rootId = null;

    public PoolPost(Long postId, Long creatorId, Long circleId) {
        this.postId = postId;
        this.creatorId = creatorId;
        this.circleId = circleId;
    }

    public PoolPost(PostCreateEvent pce) {
        this.postId = pce.getPostId();
        this.creatorId = pce.getCreatorId();
        this.circleId = pce.getCircleId();
        this.rootId = pce.getRootId();
    }

    public PoolPost(PostFanOutEvent ffoe) {
        this.postId = ffoe.getPostId();
        this.rootId = ffoe.getRootId();
        this.creatorId = ffoe.getCreatorId();
        this.circleId = ffoe.getCircleId();
    }

    public PoolPost(PostDeleteEvent pde) {
        this.postId = pde.getPostId();
        this.creatorId = pde.getCreatorId();
        this.circleId = pde.getCircleId();
    }

    public PoolPost(String input) {
        final String[] buff = input.split(":");
        if (buff == null || buff.length != 4) {
            throw new IllegalArgumentException(input);
        }
        try {
            this.creatorId = Long.valueOf(buff[0]);
            this.postId = Long.valueOf(buff[1]);
            if (StringUtils.isNotBlank(buff[2])
                    && !"null".equalsIgnoreCase(buff[2])) {
                this.rootId = Long.valueOf(buff[2]);
            }
            if (StringUtils.isNotBlank(buff[3])
                    && !"null".equalsIgnoreCase(buff[3])) {
                this.circleId = Long.valueOf(buff[3]);
            }
            else
                this.circleId = null;
        } catch (Exception e) {
            throw new IllegalArgumentException(input);
        }
    }

    public Long getPostId() {
        return postId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public Long getCircleId() {
        return circleId;
    }

    public String getValueInPool() {
        if (rootId == null) {
            return creatorId + ":" + postId + "::" + circleId;
        }
        return creatorId + ":" + postId + ":" + rootId + ":" + circleId;
    }

    public Long getRootId() {
        return rootId;
    }

    public Boolean isValid() {
        return creatorId != null && postId != null && circleId != null;
    }
}
