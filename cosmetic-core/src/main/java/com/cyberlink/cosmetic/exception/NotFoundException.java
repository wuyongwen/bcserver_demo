package com.cyberlink.cosmetic.exception;

import java.util.List;

public class NotFoundException extends CosmeticException {
    private static final long serialVersionUID = -1064891404942481981L;
        
    public NotFoundException() {
        super();
    }    
    
    public NotFoundException(Object errorObject) {
        super(errorObject);      
    }
    
    public NotFoundException(List<?> errorList) {
        super(errorList);      
    }
    
    public Integer getErrorCode() {
        return 404;
    }
    
    public String getErrorMessage() {
        return "Not Found";
    }
       
}
