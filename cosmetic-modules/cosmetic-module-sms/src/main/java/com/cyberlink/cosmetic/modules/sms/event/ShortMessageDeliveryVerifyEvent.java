package com.cyberlink.cosmetic.modules.sms.event;

import com.cyberlink.core.event.Event;
import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;

public class ShortMessageDeliveryVerifyEvent extends Event {

    private static final long serialVersionUID = 4332233434304765021L;

    private final ShortMessageServiceProvider serviceProvider;
    private final Long timeOfSMSSent;
    private final Long timeOfFirstSent;
    private final int indexOfSuccess;
    private final String countryCode;

    public ShortMessageDeliveryVerifyEvent(
            ShortMessageServiceProvider serviceProvider, Long timeOfSent,
            Long timeOfFirstSent, int indexOfSuccess, String countryCode) {
        super(new Object());
        this.serviceProvider = serviceProvider;
        this.timeOfSMSSent = timeOfSent;
        this.timeOfFirstSent = timeOfFirstSent;
        this.indexOfSuccess = indexOfSuccess;
        this.countryCode = countryCode;
    }

    public ShortMessageServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public Long getTimeOfSMSSent() {
        return timeOfSMSSent;
    }

    public Long getTimeOfFirstSent() {
        return timeOfFirstSent;
    }

    public int getIndexOfSuccess() {
        return indexOfSuccess;
    }

    public String getCountryCode() {
        return countryCode;
    }

}
