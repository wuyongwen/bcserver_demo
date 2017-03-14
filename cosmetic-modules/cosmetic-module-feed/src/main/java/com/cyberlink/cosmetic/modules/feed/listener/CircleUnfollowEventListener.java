package com.cyberlink.cosmetic.modules.feed.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.circle.CircleUnfollowEvent;
import com.cyberlink.cosmetic.modules.feed.service.PoolService;

public class CircleUnfollowEventListener<T> extends
        AbstractEventListener<CircleUnfollowEvent> {
    private PoolService poolService;

    public void setPoolService(PoolService poolService) {
        this.poolService = poolService;
    }

    @Override
    public void onEvent(final CircleUnfollowEvent event) {
        poolService.deleteByCircleId(event.getCircleCreatorId(),
                event.getCircleId());
    }

}
