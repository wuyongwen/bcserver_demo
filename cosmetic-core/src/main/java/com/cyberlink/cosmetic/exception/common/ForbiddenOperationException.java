package com.cyberlink.cosmetic.exception.common;

import com.cyberlink.cosmetic.exception.ForbiddenException;

public class ForbiddenOperationException extends ForbiddenException {

    private static final long serialVersionUID = -3930524263828582443L;

    public String getErrorMessage() {
        return "Forbidden Operation";
    }

}
