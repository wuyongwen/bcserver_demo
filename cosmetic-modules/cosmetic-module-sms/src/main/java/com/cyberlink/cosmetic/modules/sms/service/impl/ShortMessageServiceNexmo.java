package com.cyberlink.cosmetic.modules.sms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.core.web.utl.URLContentReader;
import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageService;
import com.cyberlink.cosmetic.statsd.StatsDUpdater;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShortMessageServiceNexmo extends AbstractShortMessageService implements ShortMessageService {

    private Properties senderIdPoolProperties;

    private URLContentReader urlContentReader;

    private ObjectMapper objectMapper;

    private String serviceUrl;

    private String apiKey;

    private String apiSecret;
    // private StatsDUpdater statsDUpdater;

    /*public void setStatsDUpdater(StatsDUpdater statsDUpdater) {
        this.statsDUpdater = statsDUpdater;
    }*/

    public void setSenderIdPoolProperties(Properties senderIdPoolProperties) {
        this.senderIdPoolProperties = senderIdPoolProperties;
    }

    public void setUrlContentReader(URLContentReader urlContentReader) {
        this.urlContentReader = urlContentReader;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    @Override
    public ShortMessageServiceProvider getServiceProvider() {
        return ShortMessageServiceProvider.Nexmo;
    }

    @Override
    public Boolean sendTextMessage(String countryCode, String to, String text) {
        for (String senderId : getSenderIdPool(countryCode)) {
            try {
                final List<Map<String, String>> messages = doSendTextMessage(senderId, to, text);

                if (isNeedToResendMessages(messages)) {
                    continue;
                }
                final boolean r = isAllMessagesAccepted(messages);
                if (r) {
                    // statsDUpdater.increment("sms.nexmo.send.ok");
                	logger.info("sms.nexmo.send.ok");
                } else {
                    // statsDUpdater.increment("sms.nexmo.send.fail");
                	logger.info("sms.nexmo.send.fail");
                }
                return r;
            } catch (Exception e) {
                // statsDUpdater.increment("sms.nexmo.send.fatal");
                logger.info("sms.nexmo.send.fatal");
                logger.error(e.getMessage(), e);
                return false;
            }
        }

        return false;
    }

    private String[] getSenderIdPool(String countryCode) {
        String value = senderIdPoolProperties.getProperty(countryCode);

        if (StringUtils.isBlank(value)) {
            value = senderIdPoolProperties.getProperty("default");
        }

        return value.split(",");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> doSendTextMessage(String from, String to, String text) throws Exception {
        final Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", apiKey);
        params.put("api_secret", apiSecret);
        params.put("from", from);
        params.put("to", to);
        params.put("text", text);
        params.put("type", "unicode");

        final String content = urlContentReader.post(serviceUrl, params);
        logDeliveryReceipt(content);

        return (List<Map<String, String>>) objectMapper.readValue(content, Map.class).get("messages");
    }

    private void logDeliveryReceipt(String deliveryReceipt) {
        logger.info("SMS RESULT - " + deliveryReceipt);
    }

    private boolean isNeedToResendMessages(List<Map<String, String>> messages) {
        for (Map<String, String> m : messages) {
            if (StringUtils.equals(m.get("status"), "15")) {
                return true;
            }
        }

        return false;
    }

    private boolean isAllMessagesAccepted(List<Map<String, String>> messages) {
        for (Map<String, String> m : messages) {
            if (!StringUtils.equals(m.get("status"), "0")) {
                return false;
            }
        }

        return true;
    }

}
