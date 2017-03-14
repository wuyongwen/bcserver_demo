package com.cyberlink.cosmetic.exception;

public class UnauthorizedException extends CosmeticException {
    private static final long serialVersionUID = -1078359548377084509L;
    
    public Integer getErrorCode() {
        return 401;
    }
    
    public String getErrorMessage() {
        return "Unauthorized";
    }
    
}
