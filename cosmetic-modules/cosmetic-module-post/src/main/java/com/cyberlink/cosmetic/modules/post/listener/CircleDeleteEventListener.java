package com.cyberlink.cosmetic.modules.post.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleDeleteEvent;
import com.cyberlink.cosmetic.modules.post.service.LikeService;

public class CircleDeleteEventListener extends
        AbstractEventListener<CircleDeleteEvent> {
    
    private LikeService likeService;
    
    public void setLikeService(LikeService likeService) {
        this.likeService = likeService;
    }

    @Override
    public void onEvent(final CircleDeleteEvent event) {
        if(event.getPostIds() == null || event.getPostIds().size() <= 0)
            return;
        likeService.processUnlikeTargets(null, "Post", event.getPostIds());
    }

}
