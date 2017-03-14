package com.cyberlink.cosmetic.modules.sms.exception;

import com.cyberlink.cosmetic.exception.TooManyRequestsException;

public class InvalidVerifyCountException extends TooManyRequestsException {

    private static final long serialVersionUID = -3277692297960821524L;

    public String getErrorMessage() {
        return "The verify count is invalid.";
    }

}
