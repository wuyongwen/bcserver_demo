package com.cyberlink.cosmetic.exception;

public class TooManyRequestsException extends CosmeticException {

    private static final long serialVersionUID = -6382914949794616107L;

    public Integer getErrorCode() {
        return 429;
    }

    public String getErrorMessage() {
        return "Too Many Requests";
    }

}