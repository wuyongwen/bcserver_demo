package com.cyberlink.cosmetic.modules.feed.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.feed.event.SmartFeedContentGenerateEvent;
import com.cyberlink.cosmetic.modules.feed.model.FeedPost;
import com.cyberlink.cosmetic.modules.feed.repository.FeedRepository;
import com.cyberlink.cosmetic.modules.feed.service.FeedService;
import com.cyberlink.cosmetic.modules.user.service.LocaleService;
import com.cyberlink.cosmetic.statsd.StatsD;
import com.cyberlink.cosmetic.statsd.StatsDUpdater;

public class FeedServiceImpl extends AbstractService implements FeedService {
    private LocaleService localeService;

    private FeedService feedService;

    private FeedRepository feedRepository;

    private StatsDUpdater updater;

    public void setLocaleService(LocaleService localeService) {
        this.localeService = localeService;
    }

    public void setUpdater(StatsDUpdater updater) {
        this.updater = updater;
    }

    public void setFeedService(FeedService feedService) {
        this.feedService = feedService;
    }

    public void setFeedRepository(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    @Override
    public List<FeedPost> retrieve(Long userId, List<String> inputLocales,
            String init, int offset, int limit) {
        return feedService.retrieve(userId, getLocale(inputLocales), init,
                offset, limit);
    }

    @Override
    @StatsD
    public List<FeedPost> retrieve(Long userId, String locale, String init,
            int offset, int limit) {
        
        List<FeedPost> results = null;
        Integer numToRetrieve = limit;
        Boolean feedAnonymous = true;
        if(userId != null) {
            feedAnonymous = false;
            updater.increment("feed.feedservice.retrieve.user");
            results = feedRepository.range(userId.toString(), offset, limit);
            if (results == null || results.size() <= 0) {
                numToRetrieve = 100;
                if(offset <= 0)
                    feedAnonymous = true;
            }
        }
        if (feedAnonymous) {
            updater.increment("feed.feedservice.retrieve.anonymous");
            results = feedRepository.range(locale, offset, limit);
        }
        
        if (needToGenerate(init, userId, Long.valueOf(offset))) {
            publishDurableEvent(new SmartFeedContentGenerateEvent(locale,
                    userId.toString(), userId.toString(), numToRetrieve));
        }

        return results;
    }

    @Override
    public List<FeedPost> retrieveByNext(Long userId, List<String> locales, String init,
            Long next, int limit) {
        return feedService.retrieveByNext(userId, getLocale(locales), init,
                next, limit);
    }

    @Override
    @StatsD
    public List<FeedPost> retrieveByNext(Long userId, String locale, String init,
            Long next, int limit) {
                
                List<FeedPost> results = null;
                Integer numToRetrieve = limit;
                Boolean feedAnonymous = true;
                Double score = Double.valueOf(next);
                int offset = 1;
                if(next.equals(0L)) {
                    score = Double.MAX_VALUE;
                    offset = 0;
                }
                
                if(userId != null) {
                    feedAnonymous = false;
                    updater.increment("feed.feedservice.retrieve.user");
                    results = feedRepository.rangeByScore(userId.toString(), score, offset, limit);
                    if (results == null || results.size() <= 0) {
                        numToRetrieve = 100;
                        if(next <= 0)
                            feedAnonymous = true;
                    }
                }
                if (feedAnonymous) {
                    updater.increment("feed.feedservice.retrieve.anonymous");
                    results = feedRepository.rangeByScore(locale, score, offset, limit);
                }
                
                if (needToGenerate(init, userId, next)) {
                    publishDurableEvent(new SmartFeedContentGenerateEvent(locale,
                            userId.toString(), userId.toString(), numToRetrieve));
                }

                return results;
    }
    
    private boolean needToGenerate(String init, Long userId, Long offset) {
        if (StringUtils.equals("1", init)) {
            return Boolean.FALSE;
        }
        if (userId == null) {
            return Boolean.FALSE;
        }
        return offset == 0L;
    }

    public String getLocale(List<String> inputLocales) {
        return localeService.getLocale(inputLocales);
    }

}
