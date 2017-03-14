package com.cyberlink.cosmetic.modules.feed.listener;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.integration.spring.SpringBean;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.event.Event;
import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.FeedNotifyRepository;
import com.cyberlink.cosmetic.modules.feed.repository.PoolRepository;
import com.cyberlink.cosmetic.modules.user.repository.FeedJoinerRepository;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;
import com.cyberlink.cosmetic.statsd.StatsDUpdater;

public abstract class AbstractFeedEventListener<T extends Event> extends
        AbstractEventListener<T> {
    private FollowRepository followRepository;
    private PoolRepository poolRepository;
    private StatsDUpdater statsDUpdater;
    private List<String> joinerList;
    private FeedNotifyRepository feedNotifyRepository;
    
    public void setStatsDUpdater(StatsDUpdater statsDUpdater) {
        this.statsDUpdater = statsDUpdater;
    }

    public void setFollowRepository(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void setPoolRepository(PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    public void setFeedNotifyRepository(FeedNotifyRepository feedNotifyRepository) {
        this.feedNotifyRepository = feedNotifyRepository;
    }
    
    protected final void addToCreated(final PoolPost post, final Long score) {
        poolRepository.add(PoolType.PublicCreation, post.getCreatorId()
                .toString(), post.getValueInPool(), score);
    }

    protected final void fanoutToAllFollower(final Long creatorId,
            final Long circleId, final PoolPost post, final Long score) {
        final long begin = System.currentTimeMillis();
        final String value = post.getValueInPool();
        if(joinerList == null) {
            joinerList = new ArrayList<String>();
            FeedJoinerRepository feedJoinerRepository = BeanLocator.getBean("user.feedJoinerRepository");
            joinerList.addAll(feedJoinerRepository.getUserList());
        }
        
        final List<Long> toAddNewFeedNotify = new ArrayList<Long>();
        followRepository.doWithAllFollowers(creatorId, circleId,
                new CursorCallback<TypedTuple<String>>() {
                    @Override
                    public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                        long i = 0;
                        while (cursor.hasNext()) {
                            i++;
                            String userId = cursor.next().getValue();
                            if(joinerList.contains(userId))
                                poolRepository.add(PoolType.Following, userId, value, score);
                            else
                                poolRepository.clean(PoolType.Following, userId);
                            toAddNewFeedNotify.add(Long.valueOf(userId));
                            if(toAddNewFeedNotify.size() >= 1000) {
                                feedNotifyRepository.batchAddNewFeedNotify(toAddNewFeedNotify);
                                toAddNewFeedNotify.clear();
                            }
                        }
                        if(toAddNewFeedNotify.size() > 0) {
                            feedNotifyRepository.batchAddNewFeedNotify(toAddNewFeedNotify);
                            toAddNewFeedNotify.clear();
                        }
                        logFanoutStats(i);
                    }

                    private void logFanoutStats(long i) {
                        final String aspect = getAspect(i);
                        statsDUpdater.increment(aspect);
                        statsDUpdater.recordExecutionTime(aspect,
                                (System.currentTimeMillis() - begin));
                    }

                    private String getAspect(long i) {
                        final StringBuffer sb = new StringBuffer();
                        sb.append("pool.following.fanout.group." + getGroup(i));
                        return sb.toString();
                    }

                    private String getGroup(long i) {
                        if (i < 100) {
                            return "0-100";
                        }
                        if (i < 200) {
                            return "100-200";
                        }
                        if (i < 300) {
                            return "200-300";
                        }
                        if (i < 500) {
                            return "300-500";
                        }
                        if (i < 750) {
                            return "500-750";
                        }
                        if (i < 1000) {
                            return "750-1k";
                        }
                        if (i < 1500) {
                            return "100-1.5k";
                        }
                        if (i < 2000) {
                            return "1.5k-2k";
                        }
                        if (i < 5000) {
                            return "2k-5k";
                        }
                        if (i < 10000) {
                            return "5k-10k";
                        }
                        if (i < 20000) {
                            return "10k-20k";
                        }
                        if (i < 100000) {
                            return "20k-100k";
                        }
                        return "100k-";
                    }
                });
    }
}
