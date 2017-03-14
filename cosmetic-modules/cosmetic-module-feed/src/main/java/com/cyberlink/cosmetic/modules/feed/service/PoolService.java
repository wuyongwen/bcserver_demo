package com.cyberlink.cosmetic.modules.feed.service;

public interface PoolService {
    void deleteByCircleId(Long creatorId, Long circleId);

    void deleteByCreatorId(Long creatorId);
}
