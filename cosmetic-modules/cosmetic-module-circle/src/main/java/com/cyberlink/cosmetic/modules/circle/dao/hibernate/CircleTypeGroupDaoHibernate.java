package com.cyberlink.cosmetic.modules.circle.dao.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeGroupDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleTypeGroup;

public class CircleTypeGroupDaoHibernate extends AbstractDaoCosmetic<CircleTypeGroup, Long> implements CircleTypeGroupDao{
    
    private String regionOffindByDefaultType = "com.cyberlink.cosmetic.modules.circle.dao.findByDefaultType";
    
    @Override
    public CircleTypeGroup findByTypeGroupName(String typeGroupName) {
        if (typeGroupName == null || typeGroupName.length() <= 0) {
            return null;
        }

        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("groupName", typeGroupName));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return uniqueResult(dc);
    }
    
    @Override
    public Long findByDefaultTypeName(String defaultType) {
        if (defaultType == null || defaultType.length() <= 0) {
            return null;
        }

        String groupName = defaultType.replace("_", " ");
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.ilike("groupName", groupName));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("id"));
        
        return uniqueResult(dc, regionOffindByDefaultType);
    }
}
