package com.cyberlink.cosmetic.modules.circle.repository;

import com.cyberlink.cosmetic.redis.CursorCallback;

public interface CircleRepository {
    void addCircle(Long userId, Long circleId);

    void deleteCircle(Long userId, Long circleId);

    void updateAll();

    void doWithCircleIds(Long circleCreatorId,
            CursorCallback<String> cursorCallback);
    
    void deleteByUserId(Long userId);
}
