package com.cyberlink.core.event;

import org.springframework.context.ApplicationEventPublisher;

public interface ApplicationEventPublisherHolder {
    ApplicationEventPublisher getApplicationEventPublisher();
}
