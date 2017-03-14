package com.cyberlink.cosmetic.exception;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public class BadRequestException extends CosmeticException {
    private static final long serialVersionUID = -2889786816153584971L;

    private String errorMessage;

    public BadRequestException() {
        super();
    }

    protected BadRequestException(Object errorObject) {
        super(errorObject);
    }

    protected BadRequestException(List<?> errorList) {
        super(errorList);
    }

    public Integer getErrorCode() {
        return 400;
    }

    public String getErrorMessage() {
        return (StringUtils.isBlank(errorMessage) ? "Bad Request"
                : errorMessage);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
