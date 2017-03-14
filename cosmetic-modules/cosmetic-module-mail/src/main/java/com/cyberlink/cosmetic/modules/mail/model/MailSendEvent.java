package com.cyberlink.cosmetic.modules.mail.model;

import com.cyberlink.core.event.Event;

public class MailSendEvent extends Event {
    private static final long serialVersionUID = 5723430695315812998L;

    private final String mailType;

    public MailSendEvent(final String mailType) {
        super(mailType);
        this.mailType = mailType;
    }

    public String getMailType() {
        return mailType;
    }

    @Override
    public String toString() {
        return "Mail Type: " + mailType;
    }
}
