package com.cyberlink.cosmetic.modules.user.repository.mongodb;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

import com.cyberlink.core.dao.hibernate.AbstractDaoMongo;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.TestSubscribe;
import com.cyberlink.cosmetic.modules.user.repository.TestSubscribeRepository;

public class TestSubscribeRepositoryMongo  extends AbstractDaoMongo<TestSubscribe, String> implements TestSubscribeRepository {

	@Override
	public TestSubscribe findBySubscriberAndSubscribee(Long subscriberId,
			Long subscribeeId) {
        Criteria c = Criteria.where("subscriberId").is(subscriberId).and("subscribeeId")
                .is(subscribeeId);        
        return uniqueResult(c);
	}

	@Override
	public List<TestSubscribe> findBySubscriberAndSubscribees(
			Long subscriberId, Long... subscribeeIds) {
        Criteria c = Criteria.where("subscriberId").is(subscriberId).and("subscribeeId")
                .in((Object[])subscribeeIds);
        return findByCriteria(c);
	}

	@Override
	public PageResult<TestSubscribe> findBySubscribee(Long subscribeeId, Long offset,
			Long limit) {
        Criteria c = Criteria.where("subscribeeId").is(subscribeeId);
        return findByCriteria(c, offset, limit, null);
	}

	@Override
	public PageResult<TestSubscribe> findBySubscriber(Long subscriberId, Long offset,
			Long limit) {
		Criteria c = Criteria.where("subscriberId").is(subscriberId);
		return findByCriteria(c, offset, limit, null);
	}
}
