package com.cyberlink.core.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(value = { "source", "timestamp", "global", "toMaster" }, ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class DurableEvent extends Event {

    private static final long serialVersionUID = 1778594622497245168L;

    public Boolean isGlobal() {
        return true;
    }
    
    public Boolean toMaster() {
        return false;
    }
    
    public DurableEvent() {
        super(new Object());
    }

    public DurableEvent(Object source) {
        super(source);
    }

}
