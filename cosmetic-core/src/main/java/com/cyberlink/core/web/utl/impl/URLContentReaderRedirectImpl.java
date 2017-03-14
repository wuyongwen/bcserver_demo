package com.cyberlink.core.web.utl.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.service.CacheService;
import com.cyberlink.core.web.utl.URLContentReader;

public class URLContentReaderRedirectImpl extends AbstractService implements
        URLContentReader {
    private CacheService<String, String> cacheService;
    private boolean cacheEnabled = false;

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public void setCacheService(CacheService<String, String> cacheService) {
        this.cacheService = cacheService;
    }

    public String get(final String url) {
        logger.debug(url);
        String result = getContentFromCache(url);
        if (result == null) {
            result = getFromUrl(url);
            putInCache(url, result);
        }
        return result;
    }

    public String post(final String url, final Map<String, String> params) {
        logger.debug(url);
        final String cacheKey = getCacheKey(url, params);
        String result = getContentFromCache(cacheKey);
        if (result == null) {
            result = postFromUrl(url, params);
            if (StringUtils.isNotBlank(result)) {
                putInCache(cacheKey, result);
            }
        }
        return result;
    }

    private String getCacheKey(String url, Map<String, String> params) {
        final StringBuffer sb = new StringBuffer();
        sb.append(url);
        for (final Map.Entry<String, String> e : params.entrySet()) {
            sb.append("::");
            sb.append(e.getKey());
            sb.append("=");
            sb.append(e.getValue());
        }
        return sb.toString();
    }

    private String getContentFromCache(String url) {
        if (!cacheEnabled) {
            return null;
        }
        if (cacheService == null) {
            return null;
        }
        return cacheService.get(url);
    }

    private void putInCache(String key, String value) {
        if (!cacheEnabled) {
            return;
        }
        if (cacheService == null) {
            return;
        }
        cacheService.put(key, value);
    }

    private String postFromUrl(final String url,
            final Map<String, String> params) {
        final HttpPost post = new HttpPost(url);
        post.setEntity(getEntity(params));
        return retrieve(post);
    }

    private HttpEntity getEntity(Map<String, String> params) {
        final List<NameValuePair> values = new ArrayList<NameValuePair>();
        for (final Map.Entry<String, String> e : params.entrySet()) {
            values.add(new BasicNameValuePair(e.getKey(), e.getValue()));
        }
        try {
            return new UrlEncodedFormEntity(values, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
        }
        return null;
    }

    private String retrieve(HttpRequestBase request) {
        final StringBuffer sb = new StringBuffer();
        final HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
        	((DefaultHttpClient)client).setRedirectStrategy(new LaxRedirectStrategy());
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
