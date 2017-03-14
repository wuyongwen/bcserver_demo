package com.cyberlink.cosmetic.redis;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface Repository {

    boolean exists(String key);

    void expireAt(String key, Date date);

    void expire(String key, Long num, TimeUnit unit);
}
