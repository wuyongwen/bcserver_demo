package com.cyberlink.cosmetic.utils;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    public final static Integer SHARD_NUM = 2000;
    private final static Long OUR_EPOCH = 1397550080000l;
    private static AtomicLong counter = new AtomicLong(1l);

    public static Long generate(final Long memberId) {
        final Long currentTime = new Date().getTime();
        final Long shardId = memberId == null ? 1 : (memberId % SHARD_NUM);

        Long id = (currentTime - OUR_EPOCH) << (63 - 41);
        id |= shardId << (63 - 41 - 12);
        id |= counter.getAndIncrement() % 1024;

        return id;
    }

    public static void main(String[] args) {
        Set<Long> s = new HashSet<Long>();
        for (int i = 0; i < 20; i++) {
            s.add(IdGenerator.generate(23344655l));
        }
        Iterator<Long> iter = s.iterator();
        while (iter.hasNext()) {
            Long lo = iter.next();
            System.out.println(lo);
            System.out.println(Long.toBinaryString(lo));
        }
        System.out.println("set count = " + s.size());
    }

}
