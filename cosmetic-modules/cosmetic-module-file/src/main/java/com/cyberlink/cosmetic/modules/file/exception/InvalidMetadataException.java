package com.cyberlink.cosmetic.modules.file.exception;

import java.util.List;

import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.exception.CosmeticException;

public class InvalidMetadataException extends CosmeticException {
    private static final long serialVersionUID = -7125910176658620374L;

    public InvalidMetadataException() {
        super();
    }    
    
    public InvalidMetadataException(Object errorObject) {
        super(errorObject);      
    }
    
    public InvalidMetadataException(List<?> errorList) {
        super(errorList);      
    }
    
    public Integer getErrorCode() {
        return ErrorDef.InvalidMetadata.code();
    }
    
    public String getErrorMessage() {
        return ErrorDef.InvalidMetadata.message();
    }
}
