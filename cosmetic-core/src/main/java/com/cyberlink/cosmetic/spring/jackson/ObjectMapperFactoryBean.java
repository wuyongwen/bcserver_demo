package com.cyberlink.cosmetic.spring.jackson;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.cyberlink.core.service.AbstractService;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ObjectMapperFactoryBean extends AbstractService implements
        FactoryBean<ObjectMapper>, InitializingBean {

    private ObjectMapper objectMapper = new ObjectMapper();
    private boolean jsonPrettyPrint = false;

    public void setJsonPrettyPrint(boolean jsonPrettyPrint) {
        this.jsonPrettyPrint = jsonPrettyPrint;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT,
                jsonPrettyPrint);
        /*
        final SimpleModule module = new SimpleModule();
        module.addSerializer(new TrimMillisecondsDateSerializer());
        module.addDeserializer(Date.class,
                new TrimMillisecondsDateDeserializer());
        objectMapper.registerModule(module);
        */
    }

    @Override
    public ObjectMapper getObject() throws Exception {
        return objectMapper;
    }

    @Override
    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
