package com.cyberlink.cosmetic.modules.sms.exception;

import com.cyberlink.cosmetic.exception.BadRequestException;

public class InvalidUuidException extends BadRequestException {

    private static final long serialVersionUID = -7377939282100961787L;

    public String getErrorMessage() {
        return "The uuid is invalid.";
    }

}
