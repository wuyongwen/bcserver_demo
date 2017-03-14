package com.cyberlink.cosmetic.modules.sms.exception;

import com.cyberlink.cosmetic.exception.BadRequestException;

public class InvalidCountryCodeException extends BadRequestException {

    private static final long serialVersionUID = 9142996407623297751L;

    public String getErrorMessage() {
        return "The country code is invalid.";
    }

}
