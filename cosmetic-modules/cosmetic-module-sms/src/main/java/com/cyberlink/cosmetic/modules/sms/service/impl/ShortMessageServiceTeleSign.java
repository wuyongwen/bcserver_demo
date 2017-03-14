package com.cyberlink.cosmetic.modules.sms.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;

import com.cyberlink.cosmetic.modules.sms.model.ShortMessageServiceProvider;
import com.cyberlink.cosmetic.modules.sms.model.telesign.AuthMethod;
import com.cyberlink.cosmetic.modules.sms.model.telesign.VerifyResponse;
import com.cyberlink.cosmetic.modules.sms.service.ShortMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShortMessageServiceTeleSign extends AbstractShortMessageService
        implements ShortMessageService {

    private static final String httpsProtocol = "TLSv1.2";

    private static final AuthMethod auth = AuthMethod.SHA1;

    private static final int connectTimeout = 30000;

    private static final int readTimeout = 30000;

    private String customerId;

    private String apiSecretkey;

    private String apiBaseURL;

    private String v1VerifySMS;

    private ObjectMapper objectMapper;

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setApiSecretkey(String apiSecretkey) {
        this.apiSecretkey = apiSecretkey;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setApiBaseURL(String apiBaseURL) {
        this.apiBaseURL = apiBaseURL;
    }

    public void setV1VerifySMS(String v1VerifySMS) {
        this.v1VerifySMS = v1VerifySMS;
    }

    @Override
    public ShortMessageServiceProvider getServiceProvider() {
        return ShortMessageServiceProvider.TeleSign;
    }

    @Override
    public Boolean sendTextMessage(String countryCode, String to, String text) {
        try {
            VerifyResponse response = doSendTextMessage(to, text);
            return isSucceeded(response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return Boolean.FALSE;
    }

    private VerifyResponse doSendTextMessage(final String to, final String text)
            throws Exception {
        String body = buildRequestBody(to, text);
        String date = getDateString();
        String strToSign = getStringToSign(body, date);
        String contentLen = Integer.toString(body.length());
        String auth_header = "TSA " + customerId + ":"
                + encode(strToSign, apiSecretkey);

        URL url = new URL(apiBaseURL + v1VerifySMS);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        SSLContext sslContext = SSLContext.getInstance(httpsProtocol);
        sslContext.init(null, null, new SecureRandom());
        ((HttpsURLConnection) conn).setSSLSocketFactory(sslContext
                .getSocketFactory());

        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setRequestProperty("Authorization", auth_header);
        conn.setRequestProperty("Content-Length", contentLen);
        conn.setRequestProperty("Date", date);
        conn.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(body);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (responseCode == 200) ? conn.getInputStream()
                        : conn.getErrorStream()));
        String line;
        String response = "";

        while ((line = br.readLine()) != null) {
            response += line;
        }

        return objectMapper.readValue(response, VerifyResponse.class);
    }

    private String getStringToSign(String body, String date) {
        String method = "POST\n";
        String contentType = "application/x-www-form-urlencoded\n";

        StringBuilder sb = new StringBuilder();
        sb.append(method);
        sb.append(contentType);
        sb.append(date + "\n");
        sb.append(body + "\n");
        sb.append(v1VerifySMS);

        return sb.toString();
    }

    private String getDateString() {
        SimpleDateFormat rfc2616 = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss ZZZZ", Locale.US);
        return rfc2616.format(new Date());
    }

    private String encode(String data, String key)
            throws java.security.SignatureException {
        String result;
        byte[] decoded_key = Base64.decodeBase64(key);

        try {
            SecretKeySpec signingKey = new SecretKeySpec(decoded_key,
                    auth.value());
            Mac mac = Mac.getInstance(auth.value());
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = new String(Base64.encodeBase64(rawHmac));
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : "
                    + e.getMessage());
        }
        return result;
    }

    private String buildRequestBody(final String to, final String text)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("phone_number=" + URLEncoder.encode(to, "UTF-8"));
        sb.append("&template=" + URLEncoder.encode(text, "UTF-8"));
        return sb.toString();
    }

    private Boolean isSucceeded(VerifyResponse response) {
        if (response == null) {
            return Boolean.FALSE;
        }
        final int errors = response.errors.length;
        if (errors > 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
