package com.cyberlink.cosmetic.action.api.sms;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.sms.exception.SendMessageFailedException;
import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;
import com.cyberlink.cosmetic.modules.sms.repository.PhoneRegistrationRepository;
import com.cyberlink.cosmetic.modules.sms.service.PhoneNumberService;
import com.cyberlink.cosmetic.modules.sms.service.PhoneRegistrationService;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/sms/register-phone.action")
public class RegisterPhoneAction extends AbstractAction {

    private static final String VERIFICATION_MESSAGE_TEMPLATE = "请于30分钟内，将%s输入完美直播以完成注册。";

    @SpringBean("sms.phoneNumberService")
    private PhoneNumberService phoneNumberService;

    @SpringBean("sms.phoneRegistrationService")
    private PhoneRegistrationService phoneRegistrationService;

    @SpringBean("sms.phoneRegistrationRepository")
    private PhoneRegistrationRepository phoneRegistrationRepository;

    private String uuid;

    private String regionCode;

    private String countryCode;

    private String phoneNumber;

    public String getUuid() {
        return uuid;
    }

    @Validate(required = true, on = "route")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Validate(required = false, maxlength = 2, on = "route")
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    @Validate(required = true, on = "route")
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Validate(required = true, on = "route")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private ShortMessageServiceProvider getShortMessageServiceProviderByRoundRobin(String countryCode, String uuid) {
        ShortMessageServiceProvider smsProvider = getCurrentShortMessageServiceProvider(uuid);

        if (smsProvider == null) {
            return ShortMessageServiceProvider.Nexmo;
        } else {
            return ShortMessageServiceProvider.next(smsProvider);
        }
    }

    private ShortMessageServiceProvider getCurrentShortMessageServiceProvider(String uuid) {
        final String t = phoneRegistrationRepository.getLastSmsProvider(uuid);
        if (StringUtils.isBlank(t)) {
            return null;
        }
        try {
            return ShortMessageServiceProvider.valueOf(t);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @DefaultHandler
    public Resolution route() {
        ShortMessageServiceProvider smsProvider = getShortMessageServiceProviderByRoundRobin(countryCode, uuid);
        Map<String, String> request = phoneRegistrationService.registerPhoneNumberForSMS(uuid, regionCode, countryCode,
                phoneNumber, smsProvider);

        if (isTestPhoneNumber(request)) {
            return success();
        }

        sendVerificationMessageBySMS(request);
        return success();
    }

    private Boolean isTestPhoneNumber(Map<String, String> request) {
        return phoneRegistrationService.isTestPhoneNumber(request.get("countryCode"), request.get("phoneNumber"));
    }

    private void sendVerificationMessageBySMS(Map<String, String> request) {
        String uuid = request.get("uuid");
        String countryCode = request.get("countryCode");
        String phoneNumber = request.get("phoneNumber");
        String verificationCode = getLastVerificationCode(request);
        String index = request.get("registerCount");
        ShortMessageServiceProvider smsProvider = getLastShortMessageServiceProvider(request);

        try {
            sendVerificationMessage(countryCode, phoneNumber, verificationCode, smsProvider, Integer.valueOf(index));
        } catch (SendMessageFailedException e) {
            logger.error(e.getMessage(), e);
            if (Constants.ignoreSmsError()) {
                return;
            }

            resendVerificationMessageByRoundRobin(uuid, countryCode, phoneNumber, verificationCode, smsProvider,
                    Integer.valueOf(index));
        }
    }

    private String getLastVerificationCode(Map<String, String> request) {
        return getLastSplit(request.get("verificationCodes"), ",");
    }

    private String getLastSplit(String str, String separatorChars) {
        String[] splits = StringUtils.split(str, separatorChars);
        return splits[splits.length - 1];
    }

    private ShortMessageServiceProvider getLastShortMessageServiceProvider(Map<String, String> request) {
        String value = getLastSplit(request.get("smsProviders"), ",");
        return ShortMessageServiceProvider.valueOf(value);
    }

    private void sendVerificationMessage(String countryCode, String phoneNumber, String verificationCode,
            ShortMessageServiceProvider smsProvider, int index) {
        String e164PhoneNumber = toE164PhoneNumber(countryCode, phoneNumber);
        String verificationMessage = generateVerificationMessage(verificationCode);
        ShortMessageService sms = smsProvider.get();

        boolean isSucceeded = sms.sendTextMessage(countryCode, e164PhoneNumber, verificationMessage);

        if (isSucceeded) {
            sms.publishShortMessageDeliveryEvent(index, countryCode);
        } else {
            throw new SendMessageFailedException();
        }
    }

    private String toE164PhoneNumber(String countryCode, String phoneNumber) {
        return phoneNumberService.convertToE164Format(countryCode, phoneNumber);
    }

    private String generateVerificationMessage(String verificationCode) {
        return String.format(VERIFICATION_MESSAGE_TEMPLATE, verificationCode);
    }

    private void resendVerificationMessageByRoundRobin(String uuid, String countryCode, String phoneNumber,
            String verificationCode, ShortMessageServiceProvider failedProvider, int index) {
        for (int i = 0; i < ShortMessageServiceProvider.number(); i++) {
            ShortMessageServiceProvider p = getShortMessageServiceProviderByRoundRobin(countryCode, uuid);

            if (p.equals(failedProvider)) {
                continue;
            }

            try {
                phoneRegistrationService.updatePhoneNumberRegistrationRequestForSMS(uuid, p);
                sendVerificationMessage(countryCode, phoneNumber, verificationCode, p, index);
                return;
            } catch (SendMessageFailedException e) {
                logger.error(e.getMessage());
            }
        }

        throw new SendMessageFailedException();
    }

}
