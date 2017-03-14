package com.cyberlink.cosmetic.action.api.sms;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;
import com.cyberlink.cosmetic.modules.sms.service.PhoneNumberService;
import com.cyberlink.cosmetic.modules.sms.service.PhoneRegistrationService;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/sms/verify-phone.action")
public class VerifyPhoneAction extends AbstractAction {

    @SpringBean("sms.phoneNumberService")
    private PhoneNumberService phoneNumberService;

    @SpringBean("sms.phoneRegistrationService")
    private PhoneRegistrationService phoneRegistrationService;

    private String countryCode;

    private String phoneNumber;

    private String verificationCode;

    private String uuid;

    public String getUuid() {
        return uuid;
    }

    @Validate(required = true, on = "route")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Validate(required = true, on = "route")
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Validate(required = true, on = "route")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Validate(required = true, on = "route")
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    @DefaultHandler
    public Resolution route() {
        Map<String, String> registration = phoneRegistrationService.verifyPhoneNumber(uuid, countryCode, phoneNumber,
                verificationCode);

        publishShortMessageDeliveryVerifyEventIfNeed(registration);

        return json(buildResult(registration));
    }

    private void publishShortMessageDeliveryVerifyEventIfNeed(Map<String, String> registration) {
        if (isTestPhoneNumber(registration)) {
            return;
        }

        if (!StringUtils.equals(registration.get("verificationType"), "SMS")) {
            return;
        }

        ShortMessageService sms = ShortMessageServiceProvider.valueOf(registration.get("smsProvider")).get();
        sms.publishShortMessageDeliveryVerifyEvent(Long.valueOf(registration.get("timestamp")),
                Long.valueOf(registration.get("start")), Integer.valueOf(registration.get("index")), countryCode);
    }

    private Boolean isTestPhoneNumber(Map<String, String> registration) {
        return phoneRegistrationService.isTestPhoneNumber(registration.get("countryCode"),
                registration.get("phoneNumber"));
    }

    private Map<String, Object> buildResult(Map<String, String> registration) {
        String accountToken = registration.get("accountToken");
        boolean hasRegistered = hasRegistered(registration.get("countryCode"), registration.get("phoneNumber"));

        final Map<String, Object> r = new HashMap<String, Object>();
        r.put("accountToken", accountToken);
        //r.put("hasRegistered", hasRegistered);
        return r;
    }

    private Boolean hasRegistered(String countryCode, String phoneNumber) {
        // FIXME
        return Boolean.FALSE;
        // final String reference =
        // phoneNumberService.convertToE164Format(countryCode, phoneNumber);
        // return accountDao.exists(AccountSource.Phone, reference);
    }

}
