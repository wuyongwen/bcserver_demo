package com.cyberlink.cosmetic.amqp.rabbitmq;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;

import com.cyberlink.core.event.DurableEvent;
import com.cyberlink.core.event.Event;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.amqp.MessageProducer;
import com.cyberlink.cosmetic.statsd.StatsDUpdater;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RabbitmqMessageProducer extends AbstractService implements
        MessageProducer, InitializingBean {
    private static final String CAMELCASE_PATTERN = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";

    private StatsDUpdater statsDUpdater;

    private RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;
    private RabbitTemplate masterRabbitTemplate;

    public void setStatsDUpdater(StatsDUpdater statsDUpdater) {
        this.statsDUpdater = statsDUpdater;
    }

    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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

    @Override
    public <E extends DurableEvent> void convertAndSend(E e) {
        if(!Constants.getIsRabbitMqEnable())
            return;
        
        final String routeKey = getRouteKey(e);
        statsDUpdater.increment("message.produce." + routeKey);
        if(e.toMaster() && masterRabbitTemplate != null) {
            masterRabbitTemplate.convertAndSend(routeKey, serialize(e));
            return;
        }
        
        rabbitTemplate.convertAndSend(routeKey, serialize(e));
    }

    @Override
    public void afterPropertiesSet()  {
        String masterHost = Constants.getRabbitMqMaster();
        if(masterHost == null || masterHost.length() <= 0)
            return;
        
        String userName = Constants.getRabbitMqUserName();
        if(userName == null)
            return;
        String password = Constants.getRabbitMqPassword();
        if(password == null)
            return;
        
        
        String [] ipPort = masterHost.split(":");
        if(ipPort.length != 2)
            return;
        
        String ip = ipPort[0];
        Integer port = Integer.valueOf(ipPort[1]);
        CachingConnectionFactory c = new CachingConnectionFactory(ip, port); 
        c.setUsername(userName);
        c.setPassword(password);
        masterRabbitTemplate = new RabbitTemplate(c);
        masterRabbitTemplate.setExchange("exchange.event.republish");
    }
    
    public <T extends Event> String getRouteKey(T t) {
        final String n = t.getClass().getSimpleName();
        final List<String> r = new ArrayList<String>();
        for (final String w : n.split(CAMELCASE_PATTERN)) {
            r.add(w);
        }

        return StringUtils.lowerCase(StringUtils.join(r, "."));
    }

}
