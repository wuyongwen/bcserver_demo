package com.cyberlink.cosmetic.modules.feed.service.impl;

import java.util.List;

import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.service.PoolPostRetriever;

public class DefaultPoolPostRetriever extends AbstractPoolPostRetriever
        implements PoolPostRetriever {

    @Override
    protected List<PoolPost> retrieveFromPool(PoolType poolType, String locale,
            String hashKey, Integer numRetrieved, Integer iteration,
            Integer numToRetrieve) {
        return poolRepository.pop(poolType, hashKey, numToRetrieve
                - numRetrieved);
    }

}
