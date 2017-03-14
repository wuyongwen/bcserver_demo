package com.cyberlink.cosmetic.modules.post.listener;

import java.util.ArrayList;
import java.util.List;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.post.PostDeleteEvent;
import com.cyberlink.cosmetic.modules.post.service.LikeService;

public class PostDeleteEventListener extends
        AbstractEventListener<PostDeleteEvent> {
    
    private LikeService likeService;
    
    public void setLikeService(LikeService likeService) {
        this.likeService = likeService;
    }

    @Override
    public void onEvent(PostDeleteEvent event) {
        if(event.getPostId() == null)
            return;
        
        List<Long> toDeletedPostId = new ArrayList<Long>();
        toDeletedPostId.add(event.getPostId());
        likeService.processUnlikeTargets(null, "Post", toDeletedPostId );
    }
    
}
