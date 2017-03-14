package com.cyberlink.cosmetic.exception;

public class ServiceUnavailableException extends CosmeticException {
    private static final long serialVersionUID = 6563345131549055204L;

    public Integer getErrorCode() {
        return 503;
    }

    public String getErrorMessage() {
        return "Service Unavailable";
    }

}