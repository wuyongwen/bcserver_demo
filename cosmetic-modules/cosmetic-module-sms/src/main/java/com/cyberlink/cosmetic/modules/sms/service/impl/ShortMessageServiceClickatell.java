package com.cyberlink.cosmetic.modules.sms.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.core.web.utl.URLContentReader;
import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageService;

public class ShortMessageServiceClickatell extends AbstractShortMessageService
        implements ShortMessageService {

    private String serviceUrl;

    private String apiId;

    private String username;

    private String password;

    URLContentReader urlContentReader;

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrlContentReader(URLContentReader urlContentReader) {
        this.urlContentReader = urlContentReader;
    }

    @Override
    public ShortMessageServiceProvider getServiceProvider() {
        return ShortMessageServiceProvider.Clickatell;
    }

    @Override
    public Boolean sendTextMessage(String countryCode, String to, String text) {
        String response = urlContentReader.post(serviceUrl,
                generateRequest(to, text));
        return succeed(response);
    }

    private Map<String, String> generateRequest(String to, String text) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("api_id", apiId);
        result.put("user", username);
        result.put("password", password);
        result.put("to", to);
        result.put("text", text);
        return result;
    }

    private Boolean succeed(String response) {
        logger.info("SMS RESULT - " + response);
        return !StringUtils.isEmpty(response)
                && !StringUtils.startsWithIgnoreCase(response, "ERR");
    }

}
