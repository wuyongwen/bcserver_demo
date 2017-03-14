package com.cyberlink.cosmetic.modules.file.exception;

import java.util.List;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.exception.CosmeticException;

public class InvalidFileTypeException extends CosmeticException {
    private static final long serialVersionUID = -1897791184456231818L;

    public InvalidFileTypeException() {
        super();
    }    
    
    public InvalidFileTypeException(Object errorObject) {
        super(errorObject);      
    }
    
    public InvalidFileTypeException(List<?> errorList) {
        super(errorList);      
    }
    
    public Integer getErrorCode() {
        return ErrorDef.InvalidFileType.code();
    }
    
    public String getErrorMessage() {
        return ErrorDef.InvalidFileType.message();
    }
}
