package com.cyberlink.cosmetic.modules.circle.listener;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleCreateEvent;
import com.cyberlink.cosmetic.modules.circle.repository.CircleFollowRepository;
import com.cyberlink.cosmetic.modules.circle.repository.CircleRepository;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;

public class CircleCreateEventListener extends
        AbstractEventListener<CircleCreateEvent> {

    private CircleRepository circleRepository;

    public void setCircleRepository(CircleRepository circleRepository) {
        this.circleRepository = circleRepository;
    }

    @Override
    public void onEvent(final CircleCreateEvent event) {
        if (event.getIsSecret() == null) {
            return;
        }
        if (event.getIsSecret()) {
            return;
        }
        
        circleRepository.addCircle(event.getCreatorId(), event.getCircleId());
    }
}
