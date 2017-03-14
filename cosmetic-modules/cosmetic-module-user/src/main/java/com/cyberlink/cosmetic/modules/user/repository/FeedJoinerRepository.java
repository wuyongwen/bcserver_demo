package com.cyberlink.cosmetic.modules.user.repository;

import java.util.Set;

public interface FeedJoinerRepository {

    Long addUser(Long userId);

    Long removeUser(Long userId);
    
    Long removeAll();
    
    Set<String> getUserList();

    Boolean isFeedJoiner(Long userId);
    
}
