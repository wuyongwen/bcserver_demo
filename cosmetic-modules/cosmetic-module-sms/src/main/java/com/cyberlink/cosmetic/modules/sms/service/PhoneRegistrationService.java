package com.cyberlink.cosmetic.modules.sms.service;

import java.util.Map;

import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;

public interface PhoneRegistrationService {

    Boolean isTestPhoneNumber(String countryCode, String phoneNumber);

    Map<String, String> registerPhoneNumber(String uuid, String regionCode, String countryCode, String phoneNumber);

    Map<String, String> registerPhoneNumberForSMS(String uuid, String regionCode, String countryCode,
            String phoneNumber, ShortMessageServiceProvider smsProvider);

    Map<String, String> verifyPhoneNumber(String uuid, String countryCode, String phoneNumber, String verificationCode);

    Map<String, String> updatePhoneNumberRegistrationRequestForSMS(String uuid,
            ShortMessageServiceProvider smsProvider);

}
