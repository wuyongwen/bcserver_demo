package com.cyberlink.cosmetic.modules.facebook.service;

import com.restfb.FacebookClient;
import com.restfb.types.User;

public interface FacebookService {
  
    User findUserByUid(String uid);

    User findUserByClientAndUid(FacebookClient client, String uid);
}
