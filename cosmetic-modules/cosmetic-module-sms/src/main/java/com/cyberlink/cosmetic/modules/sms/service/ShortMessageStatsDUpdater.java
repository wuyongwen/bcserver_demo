package com.cyberlink.cosmetic.modules.sms.service;

import com.cyberlink.cosmetic.modules.sms.event.ShortMessageDeliveryEvent;
import com.cyberlink.cosmetic.modules.sms.event.ShortMessageDeliveryVerifyEvent;

public interface ShortMessageStatsDUpdater {

    void recordDeliverySucceeded(ShortMessageDeliveryEvent event);

    void recordDeliverySuccessVerified(ShortMessageDeliveryVerifyEvent event);

}
