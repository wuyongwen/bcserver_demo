package com.cyberlink.cosmetic.modules.user.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.user.event.UserUnfollowEvent;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;

public class UserUnfollowEventListener extends
        AbstractEventListener<UserUnfollowEvent> {
    private FollowRepository followRepository;

    public void setFollowRepository(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public void onEvent(UserUnfollowEvent event) {
        followRepository.removeUserFollowing(event.getFollowerId(),
                event.getFolloweeId());
        followRepository.removeExplicitFollower(event.getFollowerId(),
                event.getFolloweeId());
    }

}
