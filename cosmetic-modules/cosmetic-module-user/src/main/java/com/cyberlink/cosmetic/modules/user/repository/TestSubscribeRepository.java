package com.cyberlink.cosmetic.modules.user.repository;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.TestSubscribe;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public interface TestSubscribeRepository extends GenericDao<TestSubscribe, String>{
    TestSubscribe findBySubscriberAndSubscribee(Long subscriberId, Long subscribeeId);
    List<TestSubscribe> findBySubscriberAndSubscribees(Long subscriberId, Long... subscribeeIds);
    PageResult<TestSubscribe> findBySubscribee(Long subscribeeId, Long offset, Long limit);
    PageResult<TestSubscribe> findBySubscriber(Long subscriberId, Long offset, Long limit);
    TestSubscribe update(TestSubscribe sub);

}
