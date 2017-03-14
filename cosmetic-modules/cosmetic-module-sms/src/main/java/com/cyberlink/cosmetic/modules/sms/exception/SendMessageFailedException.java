package com.cyberlink.cosmetic.modules.sms.exception;

import com.cyberlink.cosmetic.exception.BadRequestException;

public class SendMessageFailedException extends BadRequestException {

    private static final long serialVersionUID = 3789715323151384923L;

    public String getErrorMessage() {
        return "Send message failed.";
    }

}
