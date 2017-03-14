package com.cyberlink.cosmetic.hibernate.id;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

import com.cyberlink.cosmetic.Constants;
import com.cyberlink.core.model.AbstractCoreEntity;

public class UserIdGenerator extends MillisecValueGenerator {

	private static final Long SHARD_OTHERS = 1001L;
	private static Integer SERVER_SHARD_ID = null;
	
	public UserIdGenerator() {
	    SERVER_SHARD_ID = Constants.getShardId().intValue() * 10000;
	}

	public synchronized Serializable generate(SessionImplementor session,
			Object object) throws HibernateException {

		try {
			@SuppressWarnings("rawtypes")
			Long shardId = ((AbstractCoreEntity) object).getShardId();

			if (shardId == null)
				shardId = SHARD_OTHERS;
			initialValues = new Integer[] { SERVER_SHARD_ID + shardId.intValue() };
		} catch (Exception e) {
			initialValues = new Integer[] { SERVER_SHARD_ID + SHARD_OTHERS.intValue() };
		}
		return super.generate(session, object);
	}
}
