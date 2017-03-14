package com.cyberlink.cosmetic.modules.feed.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleCloseEvent;
import com.cyberlink.cosmetic.modules.feed.service.PoolService;

public class CircleCloseEventListener extends
        AbstractEventListener<CircleCloseEvent> {
    private PoolService poolService;

    public void setPoolService(PoolService poolService) {
        this.poolService = poolService;
    }

    @Override
    public void onEvent(CircleCloseEvent event) {
        poolService.deleteByCircleId(event.getCreatorId(), event.getCircleId());
    }

}
