package com.cyberlink.cosmetic.spring.jackson;


import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;

public class TrimMillisecondsDateDeserializer extends DateDeserializer {

    private static final long serialVersionUID = -6045546733636827102L;

    @Override
    protected Date _parseDate(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        if (NumberUtils.isDigits(jp.getText())) {
            return new Date(Long.valueOf(jp.getText()) * 1000);
        } else {
            return super._parseDate(jp, ctxt);
        }
    }
}
