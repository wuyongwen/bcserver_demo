package com.cyberlink.cosmetic.utils;

import java.util.List;

public interface CosmeticBloomFilter<T> {

    Boolean add(final T element);

    List<Boolean> addAll(final List<T> elements);

    Boolean mightContain(final T element);

    List<Boolean> mightContains(final List<T> elements);

    Boolean remove(final T element);

    List<Boolean> removeAll(final List<T> elements);
    
}