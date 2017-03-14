package com.cyberlink.cosmetic.modules.post.result;

import com.cyberlink.cosmetic.error.ErrorDef;

public class PostApiResult <T> {
    private ErrorDef errorCode;
    private T result;
    private Boolean success = true;
    
    public void setErrorDef(ErrorDef err) {
        errorCode = err;
        success = false;
    }
    
    public ErrorDef getErrorDef() {
        return errorCode;
    }
    
    public void setResult(T rel) {
        result = rel;
    }
    
    public T getResult() {
        return result;
    }
    
    public Boolean success() {
        return success;
    }
}
