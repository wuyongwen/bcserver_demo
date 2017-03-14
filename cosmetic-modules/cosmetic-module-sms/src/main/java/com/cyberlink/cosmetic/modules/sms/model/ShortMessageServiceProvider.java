package com.cyberlink.cosmetic.modules.sms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageService;

public enum ShortMessageServiceProvider {

    Nexmo("sms.shortMessageServiceNexmo"), Clickatell("sms.shortMessageServiceClickatell"), Twilio(
            "sms.shortMessageServiceTwilio"), SMSGateway("sms.shortMessageServiceSMSGateway"), TeleSign(
                    "sms.shortMessageServiceTeleSign") ,Alidayu("sms.shortMessageServiceAlidayu");

    private static final List<ShortMessageServiceProvider> VALID_LIST = new ArrayList<ShortMessageServiceProvider>();

    static {
        VALID_LIST.add(Nexmo);
    }

    private ShortMessageServiceProvider(String beanName) {
        this.beanName = beanName;
    }

    private String beanName;

    public static Integer number() {
        return VALID_LIST.size();
    }

    public static ShortMessageServiceProvider random() {
        int index = new Random().nextInt(number());
        return VALID_LIST.get(index);
    }

    public static ShortMessageServiceProvider next(ShortMessageServiceProvider smsProvider) {
        int index = (smsProvider.ordinal() + 1) % number();
        return VALID_LIST.get(index);
    }

    public static ShortMessageServiceProvider[] roundRobin(ShortMessageServiceProvider smsProvider) {
        ShortMessageServiceProvider[] r = new ShortMessageServiceProvider[number()];

        for (int i = 0; i < number(); i++) {
            int index = (smsProvider.ordinal() + i) % number();
            r[i] = VALID_LIST.get(index);
        }

        return r;
    }

    public Boolean isNexmo() {
        return Nexmo == this;
    }

    public Boolean isSMSGateway() {
        return SMSGateway == this;
    }

    public Boolean isClickatell() {
        return Clickatell == this;
    }

    public Boolean isTwilio() {
        return Twilio == this;
    }

    public Boolean isTeleSign() {
        return TeleSign == this;
    }

    public ShortMessageService get() {
        return BeanLocator.getBean(beanName);
    }

}
