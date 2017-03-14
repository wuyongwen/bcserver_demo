package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendUserDao;
import com.cyberlink.cosmetic.modules.post.model.PsTrendUser;

public class PsTrendUserDaoHibernate extends AbstractDaoCosmetic<PsTrendUser, Long>
    implements PsTrendUserDao {

    private String regionOfFindPsTrendUser = "com.cyberlink.cosmetic.modules.post.model.PsTrendUser.findGroupByUser";
    
    @Override
    public Long findGroupByUuid(String uuid) {
        if(uuid == null)
            return null;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("uuid", uuid));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("groups"));
        return uniqueResult(dc, regionOfFindPsTrendUser);
    }

    @Override
    public List<PsTrendUser> findGroupByUuids(List<String> uuids) {
        if(uuids == null || uuids.size() <= 0)
            return new ArrayList<PsTrendUser>();
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("uuid", uuids));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
    }
}
