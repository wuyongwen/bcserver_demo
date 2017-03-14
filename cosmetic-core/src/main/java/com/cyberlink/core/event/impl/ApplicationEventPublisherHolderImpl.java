package com.cyberlink.core.event.impl;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.cyberlink.core.event.ApplicationEventPublisherHolder;

public class ApplicationEventPublisherHolderImpl implements
        ApplicationEventPublisherHolder, ApplicationEventPublisherAware {
    private ApplicationEventPublisher publisher;

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return publisher;
    }

    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

}