package com.cyberlink.cosmetic.modules.circle.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.user.event.UserDeleteEvent;

public class UserDeleteEventListener extends
        AbstractEventListener<UserDeleteEvent> {
    private CircleService circleService;

    public void setCircleService(CircleService circleService) {
        this.circleService = circleService;
    }

    @Override
    public void onEvent(final UserDeleteEvent e) {
        circleService.deleteByUserId(e.getUserId());
    }

}
