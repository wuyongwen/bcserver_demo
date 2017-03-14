package com.cyberlink.cosmetic.redis;

import org.springframework.data.redis.core.Cursor;

public interface CursorCallback<T> {
    void doWithCursor(Cursor<T> cursor);
}
