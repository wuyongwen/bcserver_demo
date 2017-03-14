package com.cyberlink.cosmetic.modules.sms.exception;

import com.cyberlink.cosmetic.exception.TooManyRequestsException;

public class InvalidRegisterCountException extends TooManyRequestsException {

    private static final long serialVersionUID = -1469802974712464027L;

    public String getErrorMessage() {
        return "The register count is invalid.";
    }

}
