package com.cyberlink.cosmetic.modules.post.result;

import java.io.Serializable;
import java.math.BigInteger;

public class PostReportedWrapper implements Serializable {

    private static final long serialVersionUID = -3895667383823566023L;

    private String refType;
    private Long refId;
    private Long count;
    
    public String getRefType() {
        return refType;
    }
    
    public void setRefType(String refType) {
        this.refType = refType;
    }
    
    public Long getRefId() {
        return refId;
    }
    
    public void setRefId(BigInteger refId) {
        this.refId = refId.longValue();
    }
    
    public Long getCount() {
        return count;
    }
    
    public void setCount(BigInteger count) {
        this.count = count.longValue();
    }
}
