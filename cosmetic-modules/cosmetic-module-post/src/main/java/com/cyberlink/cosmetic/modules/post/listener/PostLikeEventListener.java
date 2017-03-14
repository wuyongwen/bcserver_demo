package com.cyberlink.cosmetic.modules.post.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.post.PostLikeEvent;
import com.cyberlink.cosmetic.modules.post.service.LikeService;

public class PostLikeEventListener extends AbstractEventListener<PostLikeEvent> {

    private LikeService likeService;
    
    public void setLikeService(LikeService likeService) {
        this.likeService = likeService;
    }
    
	@Override
	public void onEvent(PostLikeEvent event) {
	    if(event.getUserId() == null || event.getPostId() == null || event.getCreatedTime() == null)
	        return;
	    
	    likeService.processLikeTarget(event.getUserId(), "Post", event.getPostId(), event.getCreatedTime());
	}

}
