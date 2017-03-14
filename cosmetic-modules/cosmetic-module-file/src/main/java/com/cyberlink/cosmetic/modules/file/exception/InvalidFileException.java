package com.cyberlink.cosmetic.modules.file.exception;

import java.util.List;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.exception.CosmeticException;

public class InvalidFileException extends CosmeticException {
    private static final long serialVersionUID = 7452140071207699701L;

    public InvalidFileException() {
        super();
    }    
    
    public InvalidFileException(Object errorObject) {
        super(errorObject);      
    }
    
    public InvalidFileException(List<?> errorList) {
        super(errorList);      
    }
    
    public Integer getErrorCode() {
        return ErrorDef.InvalidFile.code();
    }
    
    public String getErrorMessage() {
        return ErrorDef.InvalidFile.message();
    }
}
