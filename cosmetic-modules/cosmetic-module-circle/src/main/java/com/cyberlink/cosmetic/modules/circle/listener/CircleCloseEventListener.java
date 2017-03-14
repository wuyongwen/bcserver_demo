package com.cyberlink.cosmetic.modules.circle.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleCloseEvent;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;

public class CircleCloseEventListener extends
        AbstractEventListener<CircleCloseEvent> {
    private CircleService circleService;

    public void setCircleService(CircleService circleService) {
        this.circleService = circleService;
    }

    @Override
    public void onEvent(CircleCloseEvent event) {
        circleService.deleteByCircleId(event.getCreatorId(), event.getCircleId());
    }
}
