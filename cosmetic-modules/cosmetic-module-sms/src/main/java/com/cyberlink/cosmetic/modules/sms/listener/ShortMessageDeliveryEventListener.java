package com.cyberlink.cosmetic.modules.sms.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.sms.event.ShortMessageDeliveryEvent;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageStatsDUpdater;

public class ShortMessageDeliveryEventListener extends AbstractEventListener<ShortMessageDeliveryEvent> {

    private ShortMessageStatsDUpdater shortMessageStatsDUpdater;

    public void setShortMessageStatsDUpdater(ShortMessageStatsDUpdater shortMessageStatsDUpdater) {
        this.shortMessageStatsDUpdater = shortMessageStatsDUpdater;
    }

    @Override
    public void onEvent(ShortMessageDeliveryEvent event) {
        shortMessageStatsDUpdater.recordDeliverySucceeded(event);
    }

}
