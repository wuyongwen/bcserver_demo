package com.cyberlink.core.web.stripes.converter;

import java.util.Locale;

import net.sourceforge.stripes.integration.spring.SpringHelper;
import net.sourceforge.stripes.validation.DefaultTypeConverterFactory;
import net.sourceforge.stripes.validation.TypeConverter;

public class SpringEnabledTypeConverterFactory extends DefaultTypeConverterFactory {


	@SuppressWarnings("rawtypes")
	public TypeConverter getInstance(Class<? extends TypeConverter> aClass, Locale locale) throws Exception {
        return inject(super.getInstance(aClass, locale));
    }

    @SuppressWarnings("rawtypes")
	public TypeConverter getTypeConverter(Class aClass, Locale locale) throws Exception {
        return inject(super.getTypeConverter(aClass, locale));    
    }

    protected TypeConverter<?> inject(TypeConverter<?> converter) {
        if (converter != null) {
            SpringHelper.injectBeans(converter, getConfiguration().getServletContext());
        }
        return converter;
    }
}