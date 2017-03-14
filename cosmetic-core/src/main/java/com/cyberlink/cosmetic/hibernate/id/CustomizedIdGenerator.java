package com.cyberlink.cosmetic.hibernate.id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.cyberlink.core.hibernate.id.IncrementValueGenerator;
import com.cyberlink.core.model.AbstractCoreEntity;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.core.model.AbstractEntity;
import com.cyberlink.cosmetic.utils.IdGenerator;

public class CustomizedIdGenerator extends AbstractService implements
IdentifierGenerator {
    
    @SuppressWarnings("rawtypes")
    public synchronized Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        Serializable customId = null;
        if(object instanceof AbstractCoreEntity)
            customId = ((AbstractCoreEntity) object).getId();
        else if(object instanceof AbstractEntity)
            customId = ((AbstractEntity) object).getId();
        else
            throw new IdentifierGenerationException( "not vaild entity class" );
        
        try {
            if ( customId == null )
                return IdGenerator.generate(Constants.getShardId());
            else
                return customId;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return IdGenerator.generate(null);
    }
}
