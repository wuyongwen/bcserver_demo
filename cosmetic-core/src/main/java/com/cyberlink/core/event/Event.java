package com.cyberlink.core.event;

import org.springframework.context.ApplicationEvent;

public class Event extends ApplicationEvent {
    private static final long serialVersionUID = -3671851653883783046L;

    public Event(Object source) {
        super(source);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        Event event = (Event) o;
        if (source == null) {
            return event.source == null;
        } else {
            return source.equals(event.source);
        }
    }

    public int hashCode() {
        if (source == null) {
            return 0;
        } else {
            return source.hashCode();
        }
    }
}
