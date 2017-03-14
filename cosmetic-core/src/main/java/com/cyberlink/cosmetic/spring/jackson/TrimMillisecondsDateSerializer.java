package com.cyberlink.cosmetic.spring.jackson;


import java.util.Date;

import com.fasterxml.jackson.databind.ser.std.DateSerializer;

public class TrimMillisecondsDateSerializer extends DateSerializer {
    public TrimMillisecondsDateSerializer() {
        super(true, null);
    }

    @Override
    protected long _timestamp(Date value) {
        return super._timestamp(value) / 1000;
    }
}
