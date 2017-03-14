package com.cyberlink.cosmetic.amqp.rabbitmq;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;

import com.cyberlink.core.event.DurableEvent;
import com.cyberlink.core.event.Event;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DurableEventMessageConsumer extends AbstractService implements
        MessageListener, InitializingBean {
    
    private static final String CAMELCASE_PATTERN = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";
    private static final String CN_EVENT_ROUTE_KEY = "bc.cn.event";
    private ObjectMapper objectMapper;
    private List<RabbitTemplate> slaveRabbitTemplates;
    private RabbitTemplate rabbitTemplate;
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @Override
    public void onMessage(Message message) {
        if(!Constants.getIsRabbitMqEnable())
            return;
        
        DurableEvent e = deserialize(message);
        if(slaveRabbitTemplates != null && e.isGlobal()) {
            final String routeKey = getRouteKey(e);
            for(RabbitTemplate rqTemp : slaveRabbitTemplates) {
                rqTemp.convertAndSend(routeKey, message);
            }
            rabbitTemplate.convertAndSend(CN_EVENT_ROUTE_KEY, message);
        }
        publishEvent(e);
    }

    private DurableEvent deserialize(Message message) {
        try {
            return objectMapper
                    .readValue(message.getBody(), DurableEvent.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    protected final Message serialize(Object object) {
        try {
            return MessageBuilder.withBody(
                    objectMapper.writeValueAsBytes(object)).build();
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
    
    public <T extends Event> String getRouteKey(T t) {
        final String n = t.getClass().getSimpleName();
        final List<String> r = new ArrayList<String>();
        for (final String w : n.split(CAMELCASE_PATTERN)) {
            r.add(w);
        }

        return StringUtils.lowerCase(StringUtils.join(r, "."));
    }
    
    @Override
    public void afterPropertiesSet()  {
        String[] slaveHosts = Constants.getRabbitMqSlaves();
        if(slaveHosts == null || slaveHosts.length <= 0)
            return;
        
        String userName = Constants.getRabbitMqUserName();
        if(userName == null)
            return;
        String password = Constants.getRabbitMqPassword();
        if(password == null)
            return;
        
        slaveRabbitTemplates = new ArrayList<RabbitTemplate>();
        for(String slaveHost : slaveHosts) {
            String [] ipPort = slaveHost.split(":");
            if(ipPort.length != 2)
                continue;
            
            String ip = ipPort[0];
            Integer port = Integer.valueOf(ipPort[1]);
            CachingConnectionFactory c = new CachingConnectionFactory(ip, port); 
            c.setUsername(userName);
            c.setPassword(password);
            RabbitTemplate slaveRabbitTemplate = new RabbitTemplate(c);
            slaveRabbitTemplate.setExchange("exchange.event.republish");
            slaveRabbitTemplates.add(slaveRabbitTemplate);
        }
    }
}
