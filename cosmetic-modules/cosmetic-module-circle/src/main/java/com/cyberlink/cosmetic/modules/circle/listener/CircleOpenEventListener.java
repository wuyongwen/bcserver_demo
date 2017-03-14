package com.cyberlink.cosmetic.modules.circle.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleOpenEvent;
import com.cyberlink.cosmetic.modules.circle.repository.CircleRepository;

public class CircleOpenEventListener extends
        AbstractEventListener<CircleOpenEvent> {

    private CircleRepository circleRepository;

    public void setCircleRepository(CircleRepository circleRepository) {
        this.circleRepository = circleRepository;
    }

    @Override
    public void onEvent(final CircleOpenEvent event) {
        if(event.getCircleId() == null || event.getCreatorId() == null)
            return;
        
        circleRepository.addCircle(event.getCreatorId(), event.getCircleId());
    }
}
