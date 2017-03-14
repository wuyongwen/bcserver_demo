package com.cyberlink.cosmetic.modules.sms.event;

import com.cyberlink.core.event.Event;
import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;

public class ShortMessageDeliveryEvent extends Event {

    private static final long serialVersionUID = -6567726915567306759L;
    private final ShortMessageServiceProvider serviceProvider;
    private final int index;
    private final String countryCode;

    public ShortMessageDeliveryEvent(
            ShortMessageServiceProvider serviceProvider, int index,
            String countryCode) {
        super(new Object());
        this.serviceProvider = serviceProvider;
        this.index = index;
        this.countryCode = countryCode;
    }

    public int getIndex() {
        return index;
    }

    public ShortMessageServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public String getCountryCode() {
        return countryCode;
    }

}
