package com.cyberlink.cosmetic.modules.circle.dao.hibernate;

import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoHibernate;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleTag;


public class CircleTagDaoHibernate extends AbstractDaoHibernate<CircleTag, Long> implements CircleTagDao{
	@Override
	public CircleTag create(String circleTagName) {
		CircleTag oCricleNewTag = new CircleTag();
    	DetachedCriteria dc = createDetachedCriteria();
    	dc.add(Restrictions.eq("circleTagName", circleTagName));
    	List<CircleTag> listCircleTag = findByCriteria(dc);
    	if (listCircleTag.size() > 0) {
    		oCricleNewTag = listCircleTag.get(0); 
    		oCricleNewTag.setIsDeleted(false);
    	}
    	else {    		
    		CircleTag oCricleTag = new CircleTag();
    		oCricleTag.setCircleTagName(circleTagName);
    		oCricleNewTag = create(oCricleTag);
    	}        
        return oCricleNewTag;
	}
	
    @Override
    public List<CircleTag> findByIds(Long... ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }

        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("id", ids));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }
    
    @Override
    public List<CircleTag> listAllTags() {
    	return findAll();
    }
    
    @Override
    public List<CircleTag> findByGroupId(Long id) {
    	 DetachedCriteria dc = createDetachedCriteria();
         dc.add(Restrictions.eq("circleTagGroupId", id));
         dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

         return findByCriteria(dc);    	
    }
    
}
