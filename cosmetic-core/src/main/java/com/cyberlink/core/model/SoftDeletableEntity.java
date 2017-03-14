package com.cyberlink.core.model;

public interface SoftDeletableEntity {
    Boolean getIsDeleted();

    void setIsDeleted(Boolean isDeleted);
}
