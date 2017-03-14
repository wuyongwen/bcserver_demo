package com.cyberlink.cosmetic.modules.post.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.post.repository.LikeRepository;
import com.cyberlink.cosmetic.modules.user.event.UserDeleteEvent;

public class UserDeleteEventListener extends
        AbstractEventListener<UserDeleteEvent> {
    
    private LikeRepository likeRepository;

    public void setLikeRepository(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public void onEvent(UserDeleteEvent event) {
        if(event.getUserId() == null)
            return;
        
        likeRepository.deleteByUserId(event.getUserId());
    }

}
