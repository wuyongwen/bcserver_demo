package com.cyberlink.cosmetic.modules.feed.model;

public enum PoolType {
    Official(Boolean.TRUE, 500, 0, Boolean.TRUE), 
    Following(Boolean.TRUE, 500, 1, Boolean.FALSE), 
    Related(Boolean.TRUE, 200, 2, Boolean.FALSE), 
    Interest(Boolean.TRUE, 200, 3, Boolean.FALSE), 
    Advertisement(Boolean.TRUE, 200, 4, Boolean.TRUE), 
    PublicCreation(Boolean.FALSE, 500, 14, Boolean.TRUE), 
    Unknown(Boolean.FALSE, 0, 15, Boolean.FALSE);

    private final boolean trim;
    private final Integer maxlength;
    private final Integer index;
    private final boolean global;

    private PoolType(boolean trim, Integer maxlength, Integer index,
            boolean global) {
        this.trim = trim;
        this.maxlength = maxlength;
        this.index = index;
        this.global = global;
    }

    public boolean isGlobal() {
        return global;
    }

    public boolean isTrim() {
        return trim;
    }

    public Integer getMaxlength() {
        return maxlength;
    }

    public boolean isOfficial() {
        return Official == this;
    }

    public boolean isFollowing() {
        return Following == this;
    }

    public boolean isRelated() {
        return Related == this;
    }

    public boolean isInterest() {
        return Interest == this;
    }

    public boolean isAdvertisement() {
        return Advertisement == this;
    }

    public Integer getIndex() {
        return index;
    }

    public static PoolType getByIndex(Integer index) {
        for (final PoolType pt : values()) {
            if (pt.getIndex().intValue() == index) {
                return pt;
            }
        }
        return Unknown;
    }
}
