package com.cyberlink.cosmetic.modules.sms.exception;

import com.cyberlink.cosmetic.exception.BadRequestException;

public class InvalidVerificationCodeException extends BadRequestException {

    private static final long serialVersionUID = -3812623253537525070L;

    public String getErrorMessage() {
        return "The verification code is invalid.";
    }

}