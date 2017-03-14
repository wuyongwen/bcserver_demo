package com.cyberlink.cosmetic.modules.user.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public interface SubscribeDao extends GenericDao<Subscribe, Long>{
	List<Subscribe> findBySubscribeeId(Long subscribeeId, PageLimit limit);
	List<Subscribe> findBySubscriberId(Long subscriberId, PageLimit limit);
    Subscribe findBySubscriberAndSubscribee(Long subscriberId, Long subscribeeId, SubscribeType subscribeType);
    List<Subscribe> findBySubscriberAndSubscribees(Long subscriberId, SubscribeType subscribeType, Long... subscribeeIds);
    List<Subscribe> findBySubscribeeAndSubscribers(Long subscribeeId, SubscribeType subscribeType, Long... subscriberIds);
    PageResult<Long> findBySubscribee(Long subscribeeId, SubscribeType subscribeType, BlockLimit blockLimit);
    PageResult<Long> findBySubscriber(Long subscriberId, SubscribeType subscribeType, BlockLimit blockLimit);
    PageResult<Long> findBySubscriber(Long subscriberId, SubscribeType subscribeType, BlockLimit blockLimit, Boolean withSize);
    Set<Long> findIdBySubscriberAndSubscribees(Long subscriberId, SubscribeType subscribeType, Long... subscribeeIds);
    List<Long> findBySubscriber(Long subscriberId, SubscribeType subscribeType);
    List<Long> findBySubscribee(Long subscribeeId, SubscribeType subscribeType);
    PageResult<Long> findBySubscribeeOrderByName(Long subscribeeId, SubscribeType subscribeType, BlockLimit blockLimit, Boolean withSize);
    PageResult<Long> findBySubscriberOrderByName(Long subscriberId, SubscribeType subscribeType, BlockLimit blockLimit, Boolean withSize);

    Map<Long, Long> getFollowerCountByUserIds(List<Long> userIds, SubscribeType subscribeType);
    Map<Long, Long> getFollowingCountByUserIds(List<Long> userIds, SubscribeType subscribeType);
    Integer bacthDeleteSubscribe(Long subscribeeId, SubscribeType subscribeType);
    Integer bacthDeleteBySubscriber(Long subscriberId, SubscribeType subscribeType);
    void batchDelete(List<Long> ids);
    
    // For backend use only
    PageResult<User> findFollowingWithoutUserType(Long userId, UserType userType, Long offset, Long limit);
    List<Subscribe> findByUser(Long userId);
    PageResult<Subscribe> findAllSubscribe(Date startTime, Date endTime, Long offset, Long limit);
    
    void doWithAllValidSubscribe(ScrollableResultsCallback callback);
    
    void doWithAllValidSubscribeBetween(Date begin, Date end, ScrollableResultsCallback callback);
    
    void doWithAllValidSubscribe(SubscribeType subscribeType, ScrollableResultsCallback callback);
    
    void doWithAllValidSubscribeBetween(Date begin, Date end, SubscribeType subscribeType, ScrollableResultsCallback callback);

}
