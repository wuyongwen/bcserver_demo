package com.cyberlink.cosmetic.modules.feed.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ScrollableResults;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.common.model.Locale;
import com.cyberlink.cosmetic.modules.feed.model.FeedPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.FeedRepository;
import com.cyberlink.cosmetic.modules.feed.repository.PoolRepository;
import com.cyberlink.cosmetic.modules.feed.service.FeedInitializer;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.redis.CursorCallback;

public class FeedInitializerImpl extends AbstractService implements
        FeedInitializer {
    private LocaleDao localeDao;

    private PoolRepository poolRepository;

    private FeedRepository feedRepository;

    private PostDao postDao;
    
    public void setPoolRepository(PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    public void setFeedRepository(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public void setLocaleDao(LocaleDao localeDao) {
        this.localeDao = localeDao;
    }

    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }
    
    @Override
    public void fillAnonymousFeeds() {
        for (final String locale : getAllValidLocales()) {
            fillAnonymousFeed(locale);
        }
    }

    private void fillAnonymousFeed(final String locale) {
        logger.error("begin fillAnonymousFeed");
        poolRepository.doWithAllPostAscendingly(PoolType.Official, locale,
                new CursorCallback<TypedTuple<String>>() {
                    @Override
                    public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                        while (cursor.hasNext()) {
                            try {
                                final PoolPost pp = new PoolPost(cursor.next()
                                        .getValue());
                                final FeedPost fp = new FeedPost(PoolType.Official,
                                        pp.getPostId(), Boolean.FALSE);
                                feedRepository.add(locale, Arrays.asList(fp));
                            }
                            catch(Exception e) {
                                
                            }
                        }
                        logger.error("end fillAnonymousFeed");
                    }
                });
    }

    private Set<String> getAllValidLocales() {
        final Set<String> r = new HashSet<String>();
        for (final Locale l : localeDao.findAll()) {
            if (l.getIsDeleted()) {
                continue;
            }
            if (StringUtils.isBlank(l.getPostLocale())) {
                continue;
            }
            r.add(StringUtils.lowerCase(l.getPostLocale()));
        }
        return r;
    }
    
    private static final Integer BATCH_SIZE = 100;
    
    @Override
    public void fillOfficialFeed() {
        logger.error("begin fillOfficialFeed");
        try {
            Set<String> locales = localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE);
            if(locales == null || !locales.iterator().hasNext())
                return;
            Iterator<String> localesIt = locales.iterator();
            List<UserType> officialUserType = new ArrayList<UserType>();
            officialUserType.add(UserType.CL);
            while(localesIt.hasNext()) {
                String locale = localesIt.next();
                updateOfficialUserPost(officialUserType, locale);
            }
            logger.error("end fillOfficialFeed");
            return;
        } catch (Throwable e) {
            logger.error("error fillOfficialFeed : " + e.getMessage());
            return;
        }
    }
    
    private void addPost(String region, Long userId, Long postId, Long circleId, Long createdTime) {
        if (region == null || userId == null || postId == null || createdTime == null) {
            return;
        }
        
        PoolPost pp = new PoolPost(postId, userId, circleId);
        poolRepository.add(PoolType.Official, region.toLowerCase(), pp.getValueInPool(), createdTime);
    }

    private void updateOfficialUserPost(List<UserType> userTypes, final String region) {
        BlockLimit blockLimit = new BlockLimit(0, PoolType.Official.getMaxlength());
        blockLimit.addOrderBy("createdTime", false);
        postDao.doWithAllPost(userTypes, region, blockLimit, new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                int i = 0;
                long b = System.currentTimeMillis();
                while (sr.next()) {
                    if ((++i) % BATCH_SIZE == 0) {
                        logger.error("begin - end (" + i + "): "
                                + (System.currentTimeMillis() - b));
                        b = System.currentTimeMillis();
                        postDao.clear();
                    }
                    final Object[] o = sr.get();
                    final Long postId = (Long) o[0];
                    final Long userId = (Long) o[1];
                    final Long circleId = (Long) o[2];
                    final Date postCreatedTime = (Date) o[3];
                    addPost(region, userId, postId, circleId, postCreatedTime.getTime());
                }
            }
        });
    }
}
