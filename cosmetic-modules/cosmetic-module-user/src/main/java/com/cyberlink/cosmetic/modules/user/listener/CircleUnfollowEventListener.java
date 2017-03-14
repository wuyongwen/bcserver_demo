package com.cyberlink.cosmetic.modules.user.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleUnfollowEvent;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;

public class CircleUnfollowEventListener extends
        AbstractEventListener<CircleUnfollowEvent> {
    private FollowRepository followRepository;

    public void setFollowRepository(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public void onEvent(CircleUnfollowEvent e) {
        followRepository.removeExplicitFollower(e.getFollowerId(), e.getCircleCreatorId());
        followRepository.removeUserFollowing(e.getFollowerId(), e.getCircleCreatorId());
    }
}
