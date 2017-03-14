package com.cyberlink.cosmetic.modules.sms.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.core.web.utl.URLContentReader;
import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageService;

public class ShortMessageServiceTwilio extends AbstractShortMessageService
        implements ShortMessageService {

    private String serviceUrl;

    private String from;

    private URLContentReader urlContentReader;

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setUrlContentReader(URLContentReader urlContentReader) {
        this.urlContentReader = urlContentReader;
    }

    @Override
    public ShortMessageServiceProvider getServiceProvider() {
        return ShortMessageServiceProvider.Twilio;
    }

    @Override
    public Boolean sendTextMessage(String countryCode, String to, String text) {
        String response = urlContentReader.post(serviceUrl,
                generateRequest(to, text));
        return succeed(response);
    }

    private Map<String, String> generateRequest(String to, String text) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("From", from);
        result.put("To", to);
        result.put("Body", text);
        return result;
    }

    private Boolean succeed(String response) {
        Boolean result = !StringUtils.isEmpty(response)
                && !StringUtils.containsIgnoreCase(response, "RestException");
        if (!result) {
            logger.debug(response);
        }
        return result;
    }

}
