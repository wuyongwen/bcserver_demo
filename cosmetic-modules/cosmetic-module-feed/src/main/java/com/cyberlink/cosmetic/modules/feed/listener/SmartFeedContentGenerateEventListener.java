package com.cyberlink.cosmetic.modules.feed.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.feed.event.SmartFeedContentGenerateEvent;
import com.cyberlink.cosmetic.modules.feed.service.SmartFeedContentGenerator;

public class SmartFeedContentGenerateEventListener extends
        AbstractEventListener<SmartFeedContentGenerateEvent> {
    private SmartFeedContentGenerator generator;

    public void setGenerator(SmartFeedContentGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void onEvent(SmartFeedContentGenerateEvent event) {
        generator.generate(event.getLocale(), event.getSourceHashKey(),
                event.getTargetHashKey(), event.getNumToRetrieve());
    }

}
