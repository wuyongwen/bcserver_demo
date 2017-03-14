package com.cyberlink.cosmetic.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CosmeticException extends RuntimeException {
    private static final long serialVersionUID = -6235841180577612471L;
    
    private Map<String, Object> errorResults = null;   
    
    protected CosmeticException() {
        super();
    }
    
    protected CosmeticException(Object errorObject) {
        super();
        buildResultMap(errorObject);
    }
    
    protected CosmeticException(List<?> errorList) {
        super();
        buildResultMap(errorList);
    }
    
    protected CosmeticException(Map<String, Object> errorMap) {
        super();
        this.errorResults = errorMap;
    }
    
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public abstract Integer getErrorCode();
    
    public abstract String getErrorMessage();
    
    public Map<String, Object> getErrorResults() {
        return this.errorResults;
    }
    
    private void buildResultMap(Object errorObject) {
        List<Object> list = new ArrayList<Object>();
        list.add(errorObject);
        buildResultMap(list);
    }
    
    private void buildResultMap(List<?> errorList) {
        if (this.errorResults == null) {
            this.errorResults = new HashMap<String, Object>();
        }
        this.errorResults.put("invalidParams", errorList);
    }   
    
}
