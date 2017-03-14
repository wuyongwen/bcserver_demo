package com.cyberlink.cosmetic.modules.feed.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleDeleteEvent;
import com.cyberlink.cosmetic.modules.feed.service.PoolService;

public class CircleDeleteEventListener extends
        AbstractEventListener<CircleDeleteEvent> {
    private PoolService poolService;

    public void setPoolService(PoolService poolService) {
        this.poolService = poolService;
    }

    @Override
    public void onEvent(final CircleDeleteEvent event) {
        poolService.deleteByCircleId(event.getUserId(), event.getCircleId());
    }

}
