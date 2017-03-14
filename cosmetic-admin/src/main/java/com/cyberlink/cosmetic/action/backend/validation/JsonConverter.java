package com.cyberlink.cosmetic.action.backend.validation;

import java.util.Collection;

import net.sourceforge.stripes.validation.StringTypeConverter;
import net.sourceforge.stripes.validation.ValidationError;

import org.apache.commons.lang.StringUtils;

import com.cyberlink.core.BeanLocator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter extends StringTypeConverter {

    @Override
    public String convert(String input, Class<? extends String> targetType,
            Collection<ValidationError> errors) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        try {
            final ObjectMapper m = BeanLocator.getBean("web.objectMapper");
            m.readValue(input, JsonNode.class);
        } catch (Exception e) {
            return null;
        }
        return input;
    }
}