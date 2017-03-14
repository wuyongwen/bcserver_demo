package com.cyberlink.cosmetic.hibernate.id;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.cosmetic.utils.IdGenerator;

public class GeneralIdGenerator extends AbstractService implements
        IdentifierGenerator {
    
    public synchronized Serializable generate(SessionImplementor session,
            Object object) throws HibernateException {

        @SuppressWarnings("rawtypes")
        Long userId = ((AbstractEntity) object).getShardId();
        
        if ( userId == null ) {
            RequestAttributes attrs = RequestContextHolder.currentRequestAttributes();
            userId = (Long) attrs.getAttribute(Constants.PARAM_CURRENT_USER_ID, 0);
        }
        
        if ( userId == null ) {
            throw new IdentifierGenerationException( "no shardId" );
        }
        
        try {
            return IdGenerator.generate(userId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return IdGenerator.generate(null);
    }

}
