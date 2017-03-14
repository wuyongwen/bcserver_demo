package com.cyberlink.cosmetic.modules.post.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.post.PostUnlikeEvent;
import com.cyberlink.cosmetic.modules.post.service.LikeService;

public class PostUnlikeEventListener extends AbstractEventListener<PostUnlikeEvent> {

    private LikeService likeService;
    
    public void setLikeService(LikeService likeService) {
        this.likeService = likeService;
    }
    
	@Override
	public void onEvent(PostUnlikeEvent event) {
	    if(event.getUserId() == null || event.getPostId() == null)
            return;
        
	    likeService.processUnlikeTarget(event.getUserId(), "Post", event.getPostId());
	}
}
