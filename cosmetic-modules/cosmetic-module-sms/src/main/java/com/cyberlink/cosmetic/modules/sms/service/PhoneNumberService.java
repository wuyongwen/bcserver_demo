package com.cyberlink.cosmetic.modules.sms.service;

public interface PhoneNumberService {

    boolean isVaildCountryCode(String countryCode);

    boolean isVaildPhoneNumber(String countryCode, String phoneNumber);

    String trimToCheckedPhoneNumber(String countryCode, String phoneNumber);

    String prependExitCode(String countryCode);

    String convertToE164Format(String countryCode, String phoneNumber);

    String formatRegionCode(String regionCode);

}
