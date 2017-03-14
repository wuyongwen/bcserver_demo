package com.cyberlink.cosmetic.utils.BloomFilterRedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyberlink.cosmetic.utils.CosmeticBloomFilter;

import orestes.bloomfilter.CountingBloomFilter;
import orestes.bloomfilter.FilterBuilder;

public class CosmeticBloomFilterRedis<T> implements CosmeticBloomFilter<T> {
    private Boolean enable = false;
    private CountingBloomFilter<T> cbFilter;
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    public CosmeticBloomFilterRedis(String redisHost, int port, String filterName, int expectedElements, double falsePositiveProbabilit) {
        try {
            cbFilter = new FilterBuilder(expectedElements, falsePositiveProbabilit)
            .redisHost(redisHost)
            .redisPort(port)
            .name(filterName)
            .overwriteIfExists(false)
            .redisBacked(true)
            .buildCountingBloomFilter();
            if(cbFilter != null)
                enable = true;
        }
        catch(Exception e) {
            enable = false;
        }
    }
    
    @Override
    public Boolean add(final T element) {
        if(!enable)
            return null;
        
        if(element == null)
            return false;
        
        Boolean result = retry("add", false, new RetryTask<Boolean>() {

            @Override
            public Boolean doIt() {
                return cbFilter.add(element);
            }
            
        });
        
        return result;
    }
    
    @Override
    public List<Boolean> addAll(final List<T> elements) {
        if(!enable)
            return null;
        
        if(elements == null || elements.size() <= 0)
            return new ArrayList<Boolean>();
        
        List<Boolean> results = retry("addAll", new ArrayList<Boolean>(Collections.nCopies(elements.size(), false)), new RetryTask<List<Boolean>>() {

            @Override
            public List<Boolean> doIt() {
                return cbFilter.addAll(elements);
            }
            
        });

        return results;
    }

    @Override
    public Boolean mightContain(final T element) {
        if(!enable)
            return null;
        
        Boolean result = true;
        if(element == null)
            return result;
        
        try {
            result = cbFilter.contains(element);
        }
        catch(Exception e) {
            logger.error("", e);
        }
        return result;
    }

    @Override
    public List<Boolean> mightContains(final List<T> elements) {
        if(!enable)
            return null;
        
        if(elements == null || elements.size() <= 0)
            return new ArrayList<Boolean>();
        
        List<Boolean> results;
        try {
            results = cbFilter.contains(elements);
        }
        catch(Exception e) {
            results = new ArrayList<Boolean>(Collections.nCopies(elements.size(), true));
            logger.error("", e);
        }
        return results;
    }

    @Override
    public Boolean remove(final T element) {
        if(!enable)
            return null;
        
        Boolean result = false;
        if(element == null)
            return result;
        
        try {
            result = cbFilter.remove(element);
        }
        catch(Exception e) {
            logger.error("", e);
        }
        
        return result;
    }

    @Override
    public List<Boolean> removeAll(final List<T> elements) {
        if(!enable)
            return null;
        
        if(elements == null || elements.size() <= 0)
            return new ArrayList<Boolean>();
        
        List<Boolean> results;
        try {
            results = cbFilter.removeAll(elements);
        }
        catch(Exception e) {
            results = new ArrayList<Boolean>(Collections.nCopies(elements.size(), false));
            logger.error("", e);
        }
        return results;
    }
    
    private interface RetryTask<R> {
        R doIt();
    }
    
    private <R> R retry(String errName, R defaultValue, RetryTask<R> c) {
        R result = defaultValue;
        if(c == null)
            return result;
        
        int chances = 3;
        while(chances > 0) {
            try {
                result = c.doIt();
                break;
            }
            catch(Exception e) {
                logger.error(String.format("%s Retry failed %d", errName, chances), e);
            }
            chances--;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                break;
            }
        }
        return result;
        
    }
}