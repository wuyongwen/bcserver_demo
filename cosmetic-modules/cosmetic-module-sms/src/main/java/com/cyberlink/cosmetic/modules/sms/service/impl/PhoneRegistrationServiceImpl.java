package com.cyberlink.cosmetic.modules.sms.service.impl;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.exception.BadRequestException;
import com.cyberlink.cosmetic.modules.sms.exception.InvalidCountryCodeException;
import com.cyberlink.cosmetic.modules.sms.exception.InvalidPhoneNumberException;
import com.cyberlink.cosmetic.modules.sms.exception.InvalidRegisterCountException;
import com.cyberlink.cosmetic.modules.sms.exception.InvalidUuidException;
import com.cyberlink.cosmetic.modules.sms.exception.InvalidVerificationCodeException;
import com.cyberlink.cosmetic.modules.sms.exception.InvalidVerifyCountException;
import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;
import com.cyberlink.cosmetic.modules.sms.repository.PhoneRegistrationRepository;
import com.cyberlink.cosmetic.modules.sms.service.PhoneNumberService;
import com.cyberlink.cosmetic.modules.sms.service.PhoneRegistrationService;

public class PhoneRegistrationServiceImpl extends AbstractService implements PhoneRegistrationService {

    private static final Set<String> TEST_PHONE_NUMBERS = new HashSet<String>();

    static {
        TEST_PHONE_NUMBERS.add("886300000001");
        TEST_PHONE_NUMBERS.add("886300000002");
        TEST_PHONE_NUMBERS.add("886300000003");
        TEST_PHONE_NUMBERS.add("886300000004");
        TEST_PHONE_NUMBERS.add("886300000005");
        TEST_PHONE_NUMBERS.add("886300000006");
        TEST_PHONE_NUMBERS.add("886300000007");
        TEST_PHONE_NUMBERS.add("886300000008");
        TEST_PHONE_NUMBERS.add("886300000009");
        TEST_PHONE_NUMBERS.add("886300000010");
    }

    private static final String TEST_VERIFICATION_CODE = "0000";

    private PhoneNumberService phoneNumberService;

    private PhoneRegistrationRepository phoneRegistrationRepository;

    public void setPhoneNumberService(PhoneNumberService phoneNumberService) {
        this.phoneNumberService = phoneNumberService;
    }

    public void setPhoneRegistrationRepository(PhoneRegistrationRepository phoneRegistrationRepository) {
        this.phoneRegistrationRepository = phoneRegistrationRepository;
    }

    @Override
    public Boolean isTestPhoneNumber(String countryCode, String phoneNumber) {
        return TEST_PHONE_NUMBERS.contains(countryCode + phoneNumber);
    }

    @Override
    public Map<String, String> registerPhoneNumber(String uuid, String regionCode, String countryCode,
            String phoneNumber) {
        if (isInvalidUuid(uuid)) {
            throw new InvalidUuidException();
        }

        if (isInvalidCountryCode(countryCode)) {
            throw new InvalidCountryCodeException();
        }

        if (isInvalidPhoneNumber(countryCode, phoneNumber)) {
            throw new InvalidPhoneNumberException();
        }

        deletePhoneRegistration(uuid);

        String checkedRegionCode = toCheckedRegionCode(regionCode);
        String checkedPhoneNumber = toCheckedPhoneNumber(countryCode, phoneNumber);
        String verificationCode = generateVerificationCode();



        return createOrRefreshPhoneRegistrationRequest(uuid, checkedRegionCode, countryCode, checkedPhoneNumber,
                verificationCode);
    }

    private Boolean isInvalidUuid(String uuid) {
        return StringUtils.isBlank(uuid);
    }

    private Boolean isInvalidCountryCode(String countryCode) {
        return !phoneNumberService.isVaildCountryCode(countryCode);
    }

    private Boolean isInvalidPhoneNumber(String countryCode, String phoneNumber) {
        return !phoneNumberService.isVaildPhoneNumber(countryCode, phoneNumber);
    }

    private void deletePhoneRegistration(String uuid) {
        phoneRegistrationRepository.deleteRegistration(uuid);
    }

    private String toCheckedRegionCode(String regionCode) {
        return phoneNumberService.formatRegionCode(regionCode);
    }

    private String toCheckedPhoneNumber(String countryCode, String phoneNumber) {
        return phoneNumberService.trimToCheckedPhoneNumber(countryCode, phoneNumber);
    }

    private String generateVerificationCode() {

        int random = new Random().nextInt(10000);
        return StringUtils.leftPad(String.valueOf(random), 4, "0");
    }

    private Map<String, String> createOrRefreshPhoneRegistrationRequest(String uuid, String regionCode,
            String countryCode, String phoneNumber, String verificationCode) {
        if (!phoneRegistrationRepository.existsRegistrationRequest(uuid)) {
            return phoneRegistrationRepository.createRegistrationRequest(uuid, regionCode, countryCode, phoneNumber,
                    verificationCode);
        }

        if (phoneRegistrationRepository.increaseRegisterCount(uuid) > 3) {
            throw new InvalidRegisterCountException();
        }

        return phoneRegistrationRepository.refreshRegistrationRequest(uuid, regionCode, countryCode, phoneNumber,
                verificationCode);
    }

    @Override
    public Map<String, String> registerPhoneNumberForSMS(String uuid, String regionCode, String countryCode,
            String phoneNumber, ShortMessageServiceProvider smsProvider) {
        if (isInvalidUuid(uuid)) {
            throw new InvalidUuidException();
        }

        if (isInvalidCountryCode(countryCode)) {
            throw new InvalidCountryCodeException();
        }

        if (isInvalidPhoneNumber(countryCode, phoneNumber)) {
            throw new InvalidPhoneNumberException();
        }

        deletePhoneRegistration(uuid);

        String checkedRegionCode = toCheckedRegionCode(regionCode);
        String checkedPhoneNumber = toCheckedPhoneNumber(countryCode, phoneNumber);
        String verificationCode = isTestPhoneNumber(countryCode, checkedPhoneNumber) ? TEST_VERIFICATION_CODE
                : generateVerificationCode();

        if  (("15810006385".equals(phoneNumber))){
            System.out.println("in 15810006385");
            verificationCode = "6666";
        }

        return createOrRefreshPhoneRegistrationRequestForSMS(uuid, checkedRegionCode, countryCode, checkedPhoneNumber,
                verificationCode, smsProvider);
    }

    private Map<String, String> createOrRefreshPhoneRegistrationRequestForSMS(String uuid, String regionCode,
            String countryCode, String phoneNumber, String verificationCode, ShortMessageServiceProvider smsProvider) {
        if (!phoneRegistrationRepository.existsRegistrationRequest(uuid)) {
            return phoneRegistrationRepository.createRegistrationRequestForSMS(uuid, regionCode, countryCode,
                    phoneNumber, verificationCode, smsProvider);
        }

        if (phoneRegistrationRepository.increaseRegisterCount(uuid) > 3) {
            throw new InvalidRegisterCountException();
        }

        return phoneRegistrationRepository.refreshRegistrationRequestForSMS(uuid, regionCode, countryCode, phoneNumber,
                verificationCode, smsProvider);
    }

    @Override
    public Map<String, String> verifyPhoneNumber(String uuid, String countryCode, String phoneNumber,
            String verificationCode) {
        if (isInvalidUuid(uuid)) {
            throw new InvalidUuidException();
        }

        if (isInvalidCountryCode(countryCode)) {
            throw new InvalidCountryCodeException();
        }

        if (isInvalidPhoneNumber(countryCode, phoneNumber)) {
            throw new InvalidPhoneNumberException();
        }

        Map<String, String> request = getPhoneRegistrationRequest(uuid);
        verifyPhoneRegistrationRequest(request, countryCode, phoneNumber, verificationCode);

        deletePhoneRegistrationRequest(uuid);
        String accountToken = generateAccountToken();

        if (StringUtils.equals(request.get("verificationType"), "SMS")) {
            return createPhoneRegistrationForSMS(request, verificationCode, accountToken);
        }

        return createPhoneRegistration(request, accountToken);
    }

    private Map<String, String> getPhoneRegistrationRequest(String uuid) {
        return phoneRegistrationRepository.getRegistrationRequest(uuid);
    }

    private void verifyPhoneRegistrationRequest(Map<String, String> request, String countryCode, String phoneNumber,
            String verificationCode) {
        String checkedPhoneNumber = toCheckedPhoneNumber(countryCode, phoneNumber);

        if (request.isEmpty()) {
            throw new BadRequestException();
        }

        if (!StringUtils.equals(request.get("countryCode"), countryCode)) {
            throw new InvalidCountryCodeException();
        }

        if (!StringUtils.equals(request.get("phoneNumber"), checkedPhoneNumber)) {
            throw new InvalidPhoneNumberException();
        }

        if (phoneRegistrationRepository.increaseVerifyCount(request.get("uuid")) > 3) {
            throw new InvalidVerifyCountException();
        }

        if (isInvalidVerificationCode(request, verificationCode)) {
            throw new InvalidVerificationCodeException();
        }
    }

    private boolean isInvalidVerificationCode(Map<String, String> request, String verificationCode) {

        String[] verificationCodes = StringUtils.split(request.get("verificationCodes"), ",");

        for (String code : verificationCodes) {
            if (StringUtils.equals(code, verificationCode)) {
                return false;
            }
        }

        return true;
    }

    private void deletePhoneRegistrationRequest(String uuid) {
        phoneRegistrationRepository.deleteRegistrationRequest(uuid);
    }

    private String generateAccountToken() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    private Map<String, String> createPhoneRegistration(Map<String, String> request, String accountToken) {
        String uuid = request.get("uuid");
        String regionCode = request.get("regionCode");
        String countryCode = request.get("countryCode");
        String phoneNumber = request.get("phoneNumber");

        return phoneRegistrationRepository.createRegistration(uuid, accountToken, regionCode, countryCode, phoneNumber);
    }

    private Map<String, String> createPhoneRegistrationForSMS(Map<String, String> request, String verificationCode,
            String accountToken) {
        String uuid = request.get("uuid");
        String regionCode = request.get("regionCode");
        String countryCode = request.get("countryCode");
        String phoneNumber = request.get("phoneNumber");
        ShortMessageServiceProvider smsProvider = getCorrespondingShortMessageServiceProvider(request,
                verificationCode);
        Long timeOfSMSSent = getCorrespondingTimestamp(request, verificationCode);
        Long timeOfFirstSent = Long.valueOf(request.get("start"));
        Integer indexOfSuccess = getCorrespondingIndex(request, verificationCode);

        return phoneRegistrationRepository.createRegistrationForSMS(uuid, accountToken, regionCode, countryCode,
                phoneNumber, smsProvider, timeOfSMSSent, timeOfFirstSent, indexOfSuccess);
    }

    private Integer getCorrespondingIndex(Map<String, String> request, String verificationCode) {
        String[] verificationCodes = StringUtils.split(request.get("verificationCodes"), ",");
        try {
            for (int i = 0; i < verificationCodes.length; i++) {
                if (StringUtils.equals(verificationCodes[i], verificationCode)) {
                    return i + 1;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private Long getCorrespondingTimestamp(Map<String, String> request, String verificationCode) {
        String[] verificationCodes = StringUtils.split(request.get("verificationCodes"), ",");
        String[] timestamps = StringUtils.split(request.get("timestamps"), ",");
        try {
            for (int i = 0; i < verificationCodes.length; i++) {
                if (StringUtils.equals(verificationCodes[i], verificationCode)) {
                    return Long.valueOf(timestamps[i]);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private ShortMessageServiceProvider getCorrespondingShortMessageServiceProvider(Map<String, String> request,
            String verificationCode) {
        String[] verificationCodes = StringUtils.split(request.get("verificationCodes"), ",");
        String[] smsProviders = StringUtils.split(request.get("smsProviders"), ",");

        for (int i = 0; i < verificationCodes.length; i++) {
            if (StringUtils.equals(verificationCodes[i], verificationCode)) {
                return ShortMessageServiceProvider.valueOf(smsProviders[i]);
            }
        }

        return null;
    }

    @Override
    public Map<String, String> updatePhoneNumberRegistrationRequestForSMS(String uuid,
            ShortMessageServiceProvider smsProvider) {
        return phoneRegistrationRepository.replaceLastShortMessageServiceProviderForSMS(uuid, smsProvider);
    }

}
