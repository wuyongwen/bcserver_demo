package com.cyberlink.cosmetic.modules.feed.event;

import com.cyberlink.core.event.DurableEvent;

public class SmartFeedContentGenerateEvent extends DurableEvent {

    private static final long serialVersionUID = -4256560209425819242L;
    private String locale;
    private String sourceHashKey;
    private String targetHashKey;
    private Integer numToRetrieve;

    public SmartFeedContentGenerateEvent() {
    }

    public SmartFeedContentGenerateEvent(String locale, String sourceHashKey,
            String targetHashKey, Integer numToRetrieve) {
        this.locale = locale;
        this.sourceHashKey = sourceHashKey;
        this.targetHashKey = targetHashKey;
        this.numToRetrieve = numToRetrieve;
    }

    public String getLocale() {
        return locale;
    }

    public String getSourceHashKey() {
        return sourceHashKey;
    }

    public String getTargetHashKey() {
        return targetHashKey;
    }

    public Integer getNumToRetrieve() {
        return numToRetrieve;
    }
    
    @Override
    public Boolean toMaster() {
        return true;
    }
}
