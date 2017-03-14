package com.cyberlink.cosmetic.amqp;

import com.cyberlink.core.event.DurableEvent;

public interface MessageProducer {

    <E extends DurableEvent> void convertAndSend(E e);
    
}
