package com.cyberlink.cosmetic.action.api.validation;

import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import com.cyberlink.cosmetic.modules.file.model.FileType;

public class FileTypeConverter implements TypeConverter<FileType> {

    @Override
    public void setLocale(Locale locale) {
        // Do nothing 
    }

    @Override
    public FileType convert(String input, Class<? extends FileType> targetType,
            Collection<ValidationError> errors) {
        try {
            return FileType.valueOf(input); 
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }
}
