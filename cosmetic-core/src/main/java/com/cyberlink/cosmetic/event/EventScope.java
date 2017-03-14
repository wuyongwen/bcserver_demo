package com.cyberlink.cosmetic.event;

public enum EventScope {
    WEB, ADMIN, NOTIFY;

    public boolean isWeb() {
        return WEB == this;
    }

    public boolean isAdmin() {
        return ADMIN == this;
    }

    public boolean isNotify() {
        return NOTIFY == this;
    }

}
