package com.cyberlink.cosmetic.modules.user.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.user.event.UserFollowEvent;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;

public class UserFollowEventListener extends
        AbstractEventListener<UserFollowEvent> {
    private FollowRepository followRepository;

    public void setFollowRepository(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public void onEvent(UserFollowEvent event) {
        followRepository.addUserFollowing(event.getFollowerId(),
                event.getFolloweeId(), event.getCreated());
        followRepository.addExplicitFollower(event.getFollowerId(),
                event.getFolloweeId(), event.getCreated());
    }

}
