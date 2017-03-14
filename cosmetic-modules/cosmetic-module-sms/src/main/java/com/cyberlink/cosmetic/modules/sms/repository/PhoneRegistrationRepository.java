package com.cyberlink.cosmetic.modules.sms.repository;

import java.util.Map;

import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;

public interface PhoneRegistrationRepository {

    Boolean existsRegistrationRequest(String uuid);

    Map<String, String> getRegistrationRequest(String uuid);

    Map<String, String> createRegistrationRequest(String uuid, String regionCode, String countryCode,
            String phoneNumber, String verificationCode);

    Map<String, String> createRegistrationRequestForSMS(String uuid, String regionCode, String countryCode,
            String phoneNumber, String verificationCode, ShortMessageServiceProvider smsProvider);

    Map<String, String> refreshRegistrationRequest(String uuid, String regionCode, String countryCode,
            String phoneNumber, String verificationCode);

    Map<String, String> refreshRegistrationRequestForSMS(String uuid, String regionCode, String countryCode,
            String phoneNumber, String verificationCode, ShortMessageServiceProvider smsProvider);

    Map<String, String> replaceLastShortMessageServiceProviderForSMS(String uuid,
            ShortMessageServiceProvider smsProvider);

    Long increaseVerifyCount(String uuid);

    Long increaseRegisterCount(String uuid);

    void deleteRegistrationRequest(String uuid);

    Map<String, String> getRegistration(String uuid);

    Map<String, String> createRegistration(String uuid, String accountToken, String regionCode, String countryCode,
            String phoneNumber);

    Map<String, String> createRegistrationForSMS(String uuid, String accountToken, String regionCode,
            String countryCode, String phoneNumber, ShortMessageServiceProvider smsProvider, Long timeOfSMSSent,
            Long timeOfFirstSent, int indexOfSuccess);

    void deleteRegistration(String uuid);

    Map<String, String> findAll();

    String getLastSmsProvider(String uuid);

}
