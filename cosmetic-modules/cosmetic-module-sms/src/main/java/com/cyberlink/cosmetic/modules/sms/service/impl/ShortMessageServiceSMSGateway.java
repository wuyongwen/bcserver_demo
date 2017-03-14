package com.cyberlink.cosmetic.modules.sms.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringUtils;

import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageService;
import com.cyberlink.cosmetic.statsd.StatsDUpdater;

public class ShortMessageServiceSMSGateway extends AbstractShortMessageService implements ShortMessageService {

    private static final String REQUEST_PAYLOAD_TEMPLATE = generateRequestPayloadTemplate();

    private static String generateRequestPayloadTemplate() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0'?>");
        sb.append("<MESSAGES>");
        sb.append("<AUTHENTICATION>");
        sb.append("<PRODUCTTOKEN>%s</PRODUCTTOKEN>");
        sb.append("</AUTHENTICATION>");
        sb.append("<MSG>");
        sb.append("<FROM>%s</FROM>");
        sb.append("<TO>%s</TO>");
        sb.append("<DCS>8</DCS>");
        sb.append("<BODY>%s</BODY>");
        sb.append("</MSG>");
        sb.append("</MESSAGES>");
        return sb.toString();
    }

    private String serviceUrl;

    private String productToken;

    private String from;

    // private StatsDUpdater statsDUpdater;

    /*public void setStatsDUpdater(StatsDUpdater statsDUpdater) {
        this.statsDUpdater = statsDUpdater;
    }*/

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setProductToken(String productToken) {
        this.productToken = productToken;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public ShortMessageServiceProvider getServiceProvider() {
        return ShortMessageServiceProvider.SMSGateway;
    }

    @Override
    public Boolean sendTextMessage(String countryCode, String to, String text) {
        try {
            String requestPayload = buildRequestPayload(productToken, from, to, text);
            String response = request(serviceUrl, requestPayload);

            final boolean r = isSucceeded(response);
            if (r) {
                //statsDUpdater.increment("sms.smsgateway.send.ok");
            	logger.info("sms.smsgateway.send.ok");
            } else {
                //statsDUpdater.increment("sms.smsgateway.send.fail");
            	logger.info("sms.smsgateway.send.fail");
            }
            return r;
        } catch (Exception e) {
            //statsDUpdater.increment("sms.smsgateway.send.fatal");
        	logger.info("sms.smsgateway.send.fatal");
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    private String buildRequestPayload(String productToken, String from, String to, String body) {
        return String.format(REQUEST_PAYLOAD_TEMPLATE, productToken, from, to, body);
    }

    private String request(String url, String requestPayload) throws IOException {
        URLConnection conn = new URL(url).openConnection();
        conn.setDoOutput(true);

        OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
        osw.write(requestPayload);
        osw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;
        String response = "";

        while ((line = br.readLine()) != null) {
            response += line;
        }

        osw.close();
        br.close();

        return response;
    }

    private Boolean isSucceeded(String response) {
        return !StringUtils.startsWith(response, "Error:");
    }

}
