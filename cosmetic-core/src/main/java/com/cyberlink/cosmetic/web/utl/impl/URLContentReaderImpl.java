package com.cyberlink.cosmetic.web.utl.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import com.cyberlink.core.service.AbstractService;
import org.apache.http.entity.ByteArrayEntity;

public class URLContentReaderImpl extends AbstractService {

    public String get(final String url) {
        logger.debug(url);
        return getFromUrl(url);
    }

    public String post(final String url, final String data) {
        logger.debug(url);
        return postFromUrl(url, data);
    }

    private String postFromUrl(final String url,
            final String data) {
        final HttpPost post = new HttpPost(url);
        post.setEntity(new ByteArrayEntity(data.getBytes()));
        return retrieve(post);
    }

    private String retrieve(HttpRequestBase request) {
        final StringBuffer sb = new StringBuffer();
        final HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (Exception e) {
            logger.error("", e);
        }
        if (response != null) {
            final HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream is = null;
                try {
                    is = entity.getContent();
                    final BufferedReader in = new BufferedReader(
                            new InputStreamReader(is, "UTF-8"));
                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException ex) {
                    logger.error("", ex);
                } catch (RuntimeException e) {
                    request.abort();
                    logger.error("", e);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            logger.error("", e);
                        }
                    }
                }
            }
        }
        client.getConnectionManager().shutdown();
        return sb.toString();
    }

    private String getFromUrl(final String url) {
        return retrieve(new HttpGet(url));
    }

}
