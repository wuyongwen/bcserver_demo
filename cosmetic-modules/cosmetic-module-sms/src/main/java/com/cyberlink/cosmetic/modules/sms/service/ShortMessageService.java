package com.cyberlink.cosmetic.modules.sms.service;

import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;

public interface ShortMessageService {

    ShortMessageServiceProvider getServiceProvider();

    Boolean sendTextMessage(String countryCode, String to, String text);

    void publishShortMessageDeliveryEvent(int index, String countryCode);

    void publishShortMessageDeliveryVerifyEvent(Long timeOfSent,
            Long timeOfFirstSent, int indexOfSuccess, String countryCode);

}
