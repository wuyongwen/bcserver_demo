package com.cyberlink.cosmetic.modules.post.model;

import java.io.Serializable;

public class TrendPoolInfo implements Serializable {

    private static final long serialVersionUID = 7911499233493556377L;

    private Long poolSize = 0L;
    private Long poolCursor = 0L;
    private Long idx = 1L;
    private TrendPoolType perType;
    private String locale;
    private String circleType;
    
    public TrendPoolInfo(TrendPoolType perType, String locale, String circleType) {
        this.perType = perType;
        this.locale = locale == null ? "null" : locale.toLowerCase();
        this.circleType = circleType == null ? "null" : circleType.toLowerCase();
    }
    
    public TrendPoolInfo(TrendPoolType perType, String locale, String circleType, final String value) {
        this(perType, locale, circleType);
        if(value == null)
            return;
        String [] toks = value.split("\\.");
        if(toks.length < 3)
            return;
        
        this.poolSize = Long.valueOf(toks[0]);
        this.poolCursor = Long.valueOf(toks[1]);
        this.idx = Long.valueOf(toks[2]);
    }

    public Long nextIdx() {
        return idx + 1;
    }
    
    public String toString() {
        return poolSize.toString() + "." + poolCursor.toString() + "." + idx;
    }
    
    public void stepCursor(Long step) {
        Long newCursor = poolCursor + step;
        if(newCursor > poolSize)
            newCursor = 0L;
        poolCursor = newCursor;
    }
    
    public Long getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Long poolSize) {
        this.poolSize = poolSize;
    }

    public Long getPoolCursor() {
        return poolCursor;
    }

    public void setPoolCursor(Long poolCursor) {
        this.poolCursor = poolCursor;
    }

    public Long getIdx() {
        return idx;
    }

    public void setIdx(Long idx) {
        this.idx = idx;
    }
    
    public TrendPoolType getPerType() {
        return perType;
    }

    public void setPerType(TrendPoolType perType) {
        this.perType = perType;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCircleType() {
        return circleType;
    }

    public void setCircleType(String circleType) {
        this.circleType = circleType;
    }

}
