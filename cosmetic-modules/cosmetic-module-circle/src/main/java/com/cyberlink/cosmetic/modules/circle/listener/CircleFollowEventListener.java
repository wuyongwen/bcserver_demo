package com.cyberlink.cosmetic.modules.circle.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleFollowEvent;
import com.cyberlink.cosmetic.modules.circle.repository.CircleFollowRepository;

public class CircleFollowEventListener extends
        AbstractEventListener<CircleFollowEvent> {
    private CircleFollowRepository circleFollowRepository;

    public void setCircleFollowRepository(
            CircleFollowRepository circleFollowRepository) {
        this.circleFollowRepository = circleFollowRepository;
    }

    @Override
    public void onEvent(CircleFollowEvent e) {
        circleFollowRepository.addExplicitFolower(e.getFollowerId(),
                e.getCircleId(), e.getCreated());
        circleFollowRepository.addCircleFollowing(e.getFollowerId(),
                e.getCircleId(), e.getCreated());
    }

}
