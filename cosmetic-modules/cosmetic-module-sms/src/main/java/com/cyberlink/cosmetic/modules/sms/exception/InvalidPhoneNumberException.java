package com.cyberlink.cosmetic.modules.sms.exception;

import com.cyberlink.cosmetic.exception.BadRequestException;

public class InvalidPhoneNumberException extends BadRequestException {

    private static final long serialVersionUID = 1201297858464928705L;

    public String getErrorMessage() {
        return "The phone number is invalid.";
    }

}