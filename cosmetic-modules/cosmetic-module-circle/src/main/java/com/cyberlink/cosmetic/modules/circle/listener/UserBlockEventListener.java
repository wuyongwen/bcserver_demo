package com.cyberlink.cosmetic.modules.circle.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.user.event.UserBlockEvent;

public class UserBlockEventListener extends
        AbstractEventListener<UserBlockEvent> {
    private CircleService circleService;

    public void setCircleService(CircleService circleService) {
        this.circleService = circleService;
    }

    @Override
    public void onEvent(final UserBlockEvent e) {
        circleService.deleteByUserId(e.getUserId());
    }

}
