package com.cyberlink.cosmetic.modules.user.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.user.event.UserDeleteEvent;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;

public class UserDeleteEventListener extends
        AbstractEventListener<UserDeleteEvent> {
    private FollowRepository followRepository;

    public void setFollowRepository(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public void onEvent(UserDeleteEvent event) {
        followRepository.deleteByUserId(event.getUserId());
    }

}
