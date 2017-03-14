package com.cyberlink.cosmetic.modules.feed.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.user.UserSignInEvent;
import com.cyberlink.cosmetic.modules.feed.repository.FeedRepository;
import com.cyberlink.cosmetic.modules.feed.service.SmartFeedContentGenerator;
import com.cyberlink.cosmetic.modules.user.service.LocaleService;

public class UserSignInEventListener extends
        AbstractEventListener<UserSignInEvent> {

    private LocaleService localeService;
    private FeedRepository feedRepository;
    private SmartFeedContentGenerator generator;
    private Integer numToRetrieve = 50;

    public void setLocaleService(LocaleService localeService) {
        this.localeService = localeService;
    }

    public void setNumToRetrieve(Integer numToRetrieve) {
        this.numToRetrieve = numToRetrieve;
    }

    public void setFeedRepository(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public void setGenerator(SmartFeedContentGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void onEvent(UserSignInEvent event) {
        final String key = event.getUserId().toString();
        if (feedRepository.existsHashKey(event.getUserId().toString())) {
            return;
        }

        generator.generate(getLocale(event.getUserRegion()), key, key,
                numToRetrieve);
    }

    private String getLocale(String userRegion) {
        if (userRegion == null) {
            return null;
        }

        return localeService.getLocale(userRegion);
    }

}
