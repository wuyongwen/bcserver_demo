package com.cyberlink.cosmetic.modules.sms.service.impl;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.sms.event.ShortMessageDeliveryEvent;
import com.cyberlink.cosmetic.modules.sms.event.ShortMessageDeliveryVerifyEvent;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageService;

public abstract class AbstractShortMessageService extends AbstractService
        implements ShortMessageService {

    @Override
    public void publishShortMessageDeliveryEvent(int index, String countryCode) {
        publishEvent(new ShortMessageDeliveryEvent(getServiceProvider(), index,
                countryCode));
    }

    @Override
    public void publishShortMessageDeliveryVerifyEvent(Long timeOfSent,
            Long timeOfFirstSent, int indexOfSuccess, String countryCode) {
        publishEvent(new ShortMessageDeliveryVerifyEvent(getServiceProvider(),
                timeOfSent, timeOfFirstSent, indexOfSuccess, countryCode));
    }

}
