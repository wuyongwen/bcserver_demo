package com.cyberlink.cosmetic.modules.sms.repository.redis;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;
import com.cyberlink.cosmetic.modules.sms.repository.PhoneRegistrationRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class PhoneRegistrationRepositoryRedis extends AbstractRedisRepository implements PhoneRegistrationRepository {

    private static final Long PHONE_REGISTRATION_REQUEST_EXPIRE_TIME_IN_MINUTES = 30L;

    private static final Long PHONE_REGISTRATION_EXPIRE_TIME_IN_HOURS = 24L;

    @Override
    public Boolean existsRegistrationRequest(String uuid) {
        String key = KeyUtils.phoneRegistrationRequest(uuid);
        return hasKey(key);
    }

    @Override
    public Map<String, String> getRegistrationRequest(String uuid) {
        String key = KeyUtils.phoneRegistrationRequest(uuid);
        return opsForHash().entries(key);
    }

    @Override
    public Map<String, String> createRegistrationRequest(String uuid, String regionCode, String countryCode,
            String phoneNumber, String verificationCode) {
        String key = KeyUtils.phoneRegistrationRequest(uuid);

        Map<String, String> value = new HashMap<String, String>();
        value.put("uuid", uuid);
        value.put("regionCode", regionCode);
        value.put("countryCode", countryCode);
        value.put("phoneNumber", phoneNumber);
        value.put("verificationCodes", verificationCode);
        value.put("verifyCount", "0");
        value.put("registerCount", "1");

        opsForHash().putAll(key, value);
        expire(key, PHONE_REGISTRATION_REQUEST_EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
        return value;
    }

    @Override
    public Map<String, String> createRegistrationRequestForSMS(String uuid, String regionCode, String countryCode,
            String phoneNumber, String verificationCode, ShortMessageServiceProvider smsProvider) {
        String key = KeyUtils.phoneRegistrationRequest(uuid);

        Map<String, String> value = new HashMap<String, String>();
        value.put("uuid", uuid);
        value.put("regionCode", regionCode);
        value.put("countryCode", countryCode);
        value.put("phoneNumber", phoneNumber);
        value.put("verificationCodes", verificationCode);
        value.put("verifyCount", "0");
        value.put("registerCount", "1");
        value.put("verificationType", "SMS");
        value.put("smsProviders", smsProvider.name());
        value.put("timestamps", getCurrentTS());
        value.put("start", getCurrentTS());

        opsForHash().putAll(key, value);
        expire(key, PHONE_REGISTRATION_REQUEST_EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
        return value;
    }

    @Override
    public Map<String, String> refreshRegistrationRequest(String uuid, String regionCode, String countryCode,
            String phoneNumber, String verificationCode) {
        String key = KeyUtils.phoneRegistrationRequest(uuid);

        Map<String, String> value = getRegistrationRequest(uuid);
        value.put("regionCode", regionCode);
        value.put("countryCode", countryCode);
        value.put("phoneNumber", phoneNumber);
        value.put("verificationCodes", joinWithComma(value.get("verificationCodes"), verificationCode));

        opsForHash().putAll(key, value);
        expire(key, PHONE_REGISTRATION_REQUEST_EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
        return value;
    }

    private String joinWithComma(String... str) {
        return StringUtils.join(str, ",");
    }

    @Override
    public Map<String, String> refreshRegistrationRequestForSMS(String uuid, String regionCode, String countryCode,
            String phoneNumber, String verificationCode, ShortMessageServiceProvider smsProvider) {
        String key = KeyUtils.phoneRegistrationRequest(uuid);

        Map<String, String> value = getRegistrationRequest(uuid);
        value.put("regionCode", regionCode);
        value.put("countryCode", countryCode);
        value.put("phoneNumber", phoneNumber);
        value.put("verificationCodes", joinWithComma(value.get("verificationCodes"), verificationCode));
        value.put("smsProviders", joinWithComma(value.get("smsProviders"), smsProvider.name()));
        value.put("timestamps", joinWithComma(value.get("timestamps"), getCurrentTS()));

        opsForHash().putAll(key, value);
        expire(key, PHONE_REGISTRATION_REQUEST_EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
        return value;
    }

    private String getCurrentTS() {
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public Map<String, String> replaceLastShortMessageServiceProviderForSMS(String uuid,
            ShortMessageServiceProvider smsProvider) {
        String key = KeyUtils.phoneRegistrationRequest(uuid);
        Map<String, String> value = getRegistrationRequest(uuid);

        if (!StringUtils.equals(value.get("verificationType"), "SMS")) {
            return value;
        }

        String[] smsProviders = StringUtils.split(value.get("smsProviders"), ",");
        smsProviders[smsProviders.length - 1] = smsProvider.name();

        value.put("smsProviders", joinWithComma(smsProviders));

        opsForHash().putAll(key, value);
        return value;
    }

    @Override
    public Long increaseVerifyCount(String uuid) {
        String key = KeyUtils.phoneRegistrationRequest(uuid);
        return opsForHash().increment(key, "verifyCount", 1L);
    }

    @Override
    public Long increaseRegisterCount(String uuid) {
        String key = KeyUtils.phoneRegistrationRequest(uuid);
        return opsForHash().increment(key, "registerCount", 1L);
    }

    @Override
    public void deleteRegistrationRequest(String uuid) {
        String key = KeyUtils.phoneRegistrationRequest(uuid);
        delete(key);
    }

    @Override
    public Map<String, String> getRegistration(String uuid) {
        String key = KeyUtils.phoneRegistration(uuid);
        return opsForHash().entries(key);
    }

    @Override
    public Map<String, String> createRegistration(String uuid, String accountToken, String regionCode,
            String countryCode, String phoneNumber) {
        String key = KeyUtils.phoneRegistration(uuid);

        Map<String, String> value = new HashMap<String, String>();
        value.put("uuid", uuid);
        value.put("accountToken", accountToken);
        value.put("regionCode", regionCode);
        value.put("countryCode", countryCode);
        value.put("phoneNumber", phoneNumber);

        opsForHash().putAll(key, value);
        expire(key, PHONE_REGISTRATION_EXPIRE_TIME_IN_HOURS, TimeUnit.HOURS);
        return value;
    }

    @Override
    public Map<String, String> createRegistrationForSMS(String uuid, String accountToken, String regionCode,
            String countryCode, String phoneNumber, ShortMessageServiceProvider smsProvider, Long timeOfSMSSent,
            Long timeOfFirstSent, int indexOfSuccess) {
        String key = KeyUtils.phoneRegistration(uuid);

        Map<String, String> value = new HashMap<String, String>();
        value.put("uuid", uuid);
        value.put("accountToken", accountToken);
        value.put("regionCode", regionCode);
        value.put("countryCode", countryCode);
        value.put("phoneNumber", phoneNumber);
        value.put("verificationType", "SMS");
        value.put("smsProvider", smsProvider.name());
        if (timeOfSMSSent != null) {
            value.put("timestamp", String.valueOf(timeOfSMSSent));
        }
        value.put("start", String.valueOf(timeOfFirstSent));
        value.put("index", String.valueOf(indexOfSuccess));

        opsForHash().putAll(key, value);
        expire(key, PHONE_REGISTRATION_EXPIRE_TIME_IN_HOURS, TimeUnit.HOURS);
        return value;
    }

    @Override
    public void deleteRegistration(String uuid) {
        String key = KeyUtils.phoneRegistration(uuid);
        delete(key);
    }

    @Override
    public Map<String, String> findAll() {
        final String pattern = KeyUtils.phoneRegistrationRequest("*");
        final Map<String, String> results = new HashMap<String, String>();

        for (final String key : keys(pattern)) {
            final Map<String, String> r = opsForHash().entries(key);
            results.put(r.get("phoneNumber"), r.get("verificationCodes"));
        }

        return results;
    }

    @Override
    public String getLastSmsProvider(String uuid) {
        final Map<String, String> m = getRegistrationRequest(uuid);
        if (m == null) {
            return null;
        }
        final String t = m.get("smsProviders");
        if (StringUtils.isBlank(t)) {
            return null;
        }
        final String[] buff = StringUtils.split(t, ",");
        if (buff == null || buff.length == 0) {
            return null;
        }
        return buff[buff.length - 1];
    }

}
