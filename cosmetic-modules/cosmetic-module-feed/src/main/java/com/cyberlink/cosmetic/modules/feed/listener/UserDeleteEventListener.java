package com.cyberlink.cosmetic.modules.feed.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.feed.service.PoolService;
import com.cyberlink.cosmetic.modules.user.event.UserDeleteEvent;

public class UserDeleteEventListener extends
        AbstractEventListener<UserDeleteEvent> {
    private PoolService poolService;

    public void setPoolService(PoolService poolService) {
        this.poolService = poolService;
    }

    @Override
    public void onEvent(UserDeleteEvent event) {
        poolService.deleteByCreatorId(event.getUserId());
    }

}
