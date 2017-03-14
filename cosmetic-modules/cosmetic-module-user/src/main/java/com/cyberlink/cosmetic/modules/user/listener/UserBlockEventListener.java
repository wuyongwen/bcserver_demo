package com.cyberlink.cosmetic.modules.user.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.user.event.UserBlockEvent;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;

public class UserBlockEventListener extends
        AbstractEventListener<UserBlockEvent> {

    private FollowRepository followRepository;

    public void setFollowRepository(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public void onEvent(UserBlockEvent event) {
        followRepository.deleteByUserId(event.getUserId());
    }

}
