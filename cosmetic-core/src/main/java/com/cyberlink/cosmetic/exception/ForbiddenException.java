package com.cyberlink.cosmetic.exception;

import java.util.List;

public class ForbiddenException extends CosmeticException {

    private static final long serialVersionUID = -747997473430991932L;

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(Object errorObject) {
        super(errorObject);
    }

    public ForbiddenException(List<?> errorList) {
        super(errorList);
    }

    public Integer getErrorCode() {
        return 403;
    }

    public String getErrorMessage() {
        return "Forbidden";
    }

}