package com.cyberlink.cosmetic.modules.circle.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleDeleteEvent;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;

public class CircleDeleteEventListener extends
        AbstractEventListener<CircleDeleteEvent> {
    private CircleService circleService;

    public void setCircleService(CircleService circleService) {
        this.circleService = circleService;
    }

    @Override
    public void onEvent(CircleDeleteEvent event) {
        circleService.deleteByCircleId(event.getUserId(), event.getCircleId());
    }

}
