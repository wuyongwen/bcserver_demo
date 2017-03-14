package com.cyberlink.cosmetic.core.repository.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.core.model.AbstractESEntity;
import com.cyberlink.cosmetic.core.repository.EsRepository;
import com.cyberlink.cosmetic.web.utl.impl.URLContentReaderImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public abstract class EsRepositoryHttpClient<T extends AbstractESEntity> implements EsRepository<T>, InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected ObjectMapper objectMapper;
    private String apiDomain;
    private URLContentReaderImpl client;
    private Boolean isSupportDomain = false;
    
    public void setClient(URLContentReaderImpl client) {
        this.client = client;
    }
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setApiDomain(String apiDomain) {
        this.apiDomain = apiDomain + "/";
    }
    
    @Override
    public EsResult<Boolean> create(T t) {
        EsResult<Boolean> result = new EsResult<Boolean>();
        try {
            String data = objectMapper.writerWithView(Views.Public.class).writeValueAsString(t);
            result.response = doPost(t.getId(), data);
            result.result = true;
        } catch (JsonProcessingException e) {
            result.error = e.getOriginalMessage();
        }
        return result;
    }

    @Override
    public EsResult<Boolean> updateField(String id, String script) {
        EsResult<Boolean> result = new EsResult<Boolean>();
        try {
            String updateApi = id + "/_update";
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("script", script);
            String data = objectMapper.writeValueAsString(dataMap);
            result.response = doPost(updateApi, data);
            result.result = true;
        } catch (JsonProcessingException e) {
            result.error = e.getOriginalMessage();
        }
        return result;
    }
    
    @Override
    public EsResult<Boolean> isExists(String id) {
        EsResult<Boolean> result = new EsResult<Boolean>();
        result.response = doGet("_search/exists?q=id:" + id);
        try {
            JsonNode resNode = objectMapper.readValue(result.response, JsonNode.class);
            result.result = resNode.get("exists").asBoolean();
        } catch (IOException e) {
            result.error = e.getMessage();
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public EsResult<T> findById(String id) {
        EsResult<T> result = new EsResult<T>();
        String rs = doGet(id);
        try {
            JsonNode resNode = objectMapper.readValue(rs, JsonNode.class);
            JsonNode found = resNode.get("found");
            if(!found.asBoolean())
                return result;
                
            JsonNode source = resNode.get("_source");
            result.result = (T) objectMapper.treeToValue(source, getEntityClass());
        } catch (IOException e) {
            result.error = e.getMessage();
        }
        return result;
    }

    @Override
    public EsResult<Boolean> batchCreateOrUpdate(List<T> ts) {
        EsResult<Boolean> result = new EsResult<Boolean>();
        result.result = true;
        if(ts == null || ts.size() <= 0)
            return result;
        String batchApi = "_bulk";
        String data = "";
        ObjectWriter notPrettyPrinter = objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class);
        try {
            for(T t : ts) {
                data += String.format("{\"update\":{\"_id\":\"%s\"}}\n", t.getId());
                data += String.format("{\"doc\":%s,\"doc_as_upsert\":true}\n", notPrettyPrinter.writeValueAsString(t));
            }
            result.response = doPost(batchApi, data);
        } 
        catch (JsonProcessingException e) {
            result.result = false;
            result.error = e.getOriginalMessage();
        }
        
        return result;
    }
    
    protected String doPost(String url, String rawData) {
        String postApi = apiDomain + index() + padType() + url;
        return internalDoPost(postApi, rawData);
    }

    protected String doGet(String url) {
        String getApi = apiDomain + index() + padType() + url;
        return internalDoGet(getApi);
    }
    
    private String internalDoPost(String fullUrl, String rawData) {
        if(!isSupportDomain)
            return null;
        return client.post(fullUrl, rawData);
    }
    
    private String internalDoGet(String fullUrl) {
        if(!isSupportDomain)
            return null;
        return client.get(fullUrl);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if(apiDomain != null && apiDomain.startsWith("http"))
            isSupportDomain = true;
    }

    private String padType() {
        return "/" + type() + "/";
    }
    
    abstract protected String index();
    
    abstract protected String type();
    
    abstract protected Class<?> getEntityClass();
}
