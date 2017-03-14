package com.cyberlink.cosmetic.exception;

public class InternalServerErrorException extends CosmeticException {
    private static final long serialVersionUID = 4815167770288154515L;

    public Integer getErrorCode() {
        return 500;
    }
    
    public String getErrorMessage() {
        return "Internal Server Error";
    }    
    
}
