package com.cyberlink.cosmetic.modules.sms.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.sms.service.PhoneNumberService;

public class PhoneNumberServiceImpl extends AbstractService implements
        PhoneNumberService {

    private Properties trunkPrefixProperties = new Properties();

    private Properties lengthProperties = new Properties();

    public void setTrunkPrefixProperties(Properties trunkPrefixProperties) {
        this.trunkPrefixProperties = trunkPrefixProperties;
    }

    public void setLengthProperties(Properties lengthProperties) {
        this.lengthProperties = lengthProperties;
    }

    @Override
    public boolean isVaildCountryCode(String countryCode) {
        return trunkPrefixProperties.containsKey(countryCode);
    }

    @Override
    public boolean isVaildPhoneNumber(String countryCode, String phoneNumber) {
        if (!isVaildCountryCode(countryCode)) {
            return false;
        }

        if (StringUtils.isBlank(phoneNumber)) {
            return false;
        }

        if (!StringUtils.isNumeric(phoneNumber)) {
            return false;
        }

        return isValidPhoneNumberLength(countryCode, phoneNumber);
    }

    private boolean isValidPhoneNumberLength(String countryCode,
            String phoneNumber) {
        Integer length = trimToCheckedPhoneNumber(countryCode, phoneNumber)
                .length();
        List<Integer> validLengthList = getValidPhoneNumberLengthList(countryCode);

        if (validLengthList.isEmpty()) {
            return length > 0;
        }

        for (Integer validLength : validLengthList) {
            if (length.equals(validLength)) {
                return true;
            }
        }

        return false;
    }

    private List<Integer> getValidPhoneNumberLengthList(String countryCode) {
        if (!lengthProperties.containsKey(countryCode)) {
            return Collections.emptyList();
        }

        List<Integer> result = new ArrayList<Integer>();

        for (String value : lengthProperties.getProperty(countryCode)
                .split(",")) {
            result.add(Integer.parseInt(value));
        }

        return result;
    }

    @Override
    public String trimToCheckedPhoneNumber(String countryCode,
            String phoneNumber) {
        String r = trimTrunkPrefix(countryCode, phoneNumber);

        if (StringUtils.length(r) < StringUtils.length(phoneNumber)) {
            return r;
        }

        return trimTaiwanCountryCodeIfNeed(countryCode, phoneNumber);
    }

    private String trimTrunkPrefix(String countryCode, String phoneNumber) {
        String trunkPrefix = trunkPrefixProperties.getProperty(countryCode);

        if (StringUtils.startsWith(phoneNumber, trunkPrefix)) {
            return StringUtils.substring(phoneNumber, trunkPrefix.length());
        }

        return phoneNumber;
    }

    private String trimTaiwanCountryCodeIfNeed(String countryCode,
            String phoneNumber) {
        if (!StringUtils.equals(countryCode, "886")) {
            return phoneNumber;
        }

        if (StringUtils.length(phoneNumber) == 9) {
            return phoneNumber;
        }

        return phoneNumber.replaceFirst("^886", "");
    }

    @Override
    public String prependExitCode(String countryCode) {
        return StringUtils.startsWith(countryCode, "+") ? countryCode : String
                .format("+%s", countryCode);
    }

    @Override
    public String convertToE164Format(String countryCode, String phoneNumber) {
        return prependExitCode(countryCode)
                + trimToCheckedPhoneNumber(countryCode, phoneNumber);
    }

    @Override
    public String formatRegionCode(String regionCode) {
        String code = StringUtils.trimToEmpty(regionCode);
        return StringUtils.upperCase(code);
    }

}
