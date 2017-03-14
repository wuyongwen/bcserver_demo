package com.cyberlink.cosmetic.hibernate.id;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.utils.IdGenerator;

public class ShardIdGenerator extends AbstractService implements
        IdentifierGenerator {
    
    public synchronized Serializable generate(SessionImplementor session,
            Object object) throws HibernateException {
       
        try {
            return IdGenerator.generate(Constants.getShardId());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return IdGenerator.generate(null);
    }
}
