package com.cyberlink.cosmetic.modules.sms.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.sms.event.ShortMessageDeliveryVerifyEvent;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageStatsDUpdater;

public class ShortMessageDeliveryVerifyEventListener extends AbstractEventListener<ShortMessageDeliveryVerifyEvent> {

    private ShortMessageStatsDUpdater shortMessageStatsDUpdater;

    public void setShortMessageStatsDUpdater(ShortMessageStatsDUpdater shortMessageStatsDUpdater) {
        this.shortMessageStatsDUpdater = shortMessageStatsDUpdater;
    }

    @Override
    public void onEvent(ShortMessageDeliveryVerifyEvent event) {
        shortMessageStatsDUpdater.recordDeliverySuccessVerified(event);
    }

}
