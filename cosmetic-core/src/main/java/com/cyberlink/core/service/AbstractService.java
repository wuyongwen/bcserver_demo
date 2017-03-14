package com.cyberlink.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.event.DurableEvent;
import com.cyberlink.core.event.Event;
import com.cyberlink.cosmetic.amqp.MessageProducer;

public abstract class AbstractService implements ApplicationEventPublisherAware {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private ApplicationEventPublisher publisher;
    private MessageProducer messageProducer;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    protected final void publishEvent(Event e) {
        publisher.publishEvent(e);
    }

    protected final void publishDurableEvent(DurableEvent e) {
        if (messageProducer == null) {
            //messageProducer = BeanLocator.getBean("core.amqp.messageProducer");
            return;
        }
        
        try {
            messageProducer.convertAndSend(e);
        }
        catch(Exception ex) {
            logger.error(ex.getMessage());
        }
    }

}
