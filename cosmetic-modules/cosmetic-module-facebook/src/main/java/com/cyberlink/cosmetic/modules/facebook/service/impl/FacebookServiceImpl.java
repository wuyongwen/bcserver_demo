package com.cyberlink.cosmetic.modules.facebook.service.impl;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.facebook.service.FacebookService;
import com.restfb.FacebookClient;
import com.restfb.types.User;

public class FacebookServiceImpl extends AbstractService implements
        FacebookService {
    private FacebookClient client;

    public void setClient(FacebookClient client) {
        this.client = client;
    }

    @Override
    public User findUserByUid(String uid) {
        return client.fetchObject(uid, User.class);
    }

    @Override
    public User findUserByClientAndUid(FacebookClient client, String uid) {
        return client.fetchObject(uid, User.class);
    }
}
