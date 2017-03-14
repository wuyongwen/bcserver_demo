package com.cyberlink.cosmetic.hibernate.id;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.cyberlink.core.service.AbstractService;

public class MillisecValueGenerator extends AbstractService implements
		IdentifierGenerator {

	@SuppressWarnings("rawtypes")
	public final static Integer SHARD_NUM = 1000000;
	private final static Long OUR_EPOCH = 1456990000000l;
	protected Integer[] initialValues = new Integer[] { 1, 2, 3, 4, 5, 6 };

	public synchronized Serializable generate(SessionImplementor session,
			Object object) throws HibernateException {
		Long n = getMillisecValue();
		return n + getRandomInitialValue();
	}
	
	private Integer getRandomInitialValue() {
        final int random = RandomUtils.nextInt(initialValues.length);
        return initialValues[random];
    }

	private Long getMillisecValue() {
		final Long currentTime = new Date().getTime();
		return (currentTime - OUR_EPOCH) * SHARD_NUM;
	}
}
