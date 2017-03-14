package com.cyberlink.core.model;

public interface VersionedEntity {
    Integer getObjVersion();

    void setObjVersion(Integer objVersion);
}
