package com.cyberlink.cosmetic.exception;

public class UnKnownException extends InternalServerErrorException {
    private static final long serialVersionUID = 8276011475317906557L;
    
    private String errorMessage = "Unknown Error";
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public String getErrorMessage() {
        return errorMessage;
    }    
    
}