package com.cyberlink.core.hibernate.event;

public enum ChangeType {
    PostInsert, PostUpdate, PostDelete;

    public boolean isPostInsert() {
        return PostInsert == this;
    }

    public boolean isPostUpdate() {
        return PostUpdate == this;
    }

    public boolean isPostDelete() {
        return PostDelete == this;
    }
}
