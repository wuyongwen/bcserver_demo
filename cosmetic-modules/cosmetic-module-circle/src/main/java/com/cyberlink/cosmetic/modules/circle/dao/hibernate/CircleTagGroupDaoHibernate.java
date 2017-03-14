package com.cyberlink.cosmetic.modules.circle.dao.hibernate;

import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleTagGroup;

public class CircleTagGroupDaoHibernate extends AbstractDaoCosmetic<CircleTagGroup, Long> implements CircleTagGroupDao{
	private String regionOffindByCircleId = "com.cyberlink.cosmetic.modules.circle.model.CircleTagGroup.query.findByCircleId";
	
	@Override
	public CircleTagGroup create(String circleTagGroupName) {
    	CircleTagGroup oCricleTagGroupNew= new CircleTagGroup();
    	DetachedCriteria dc = createDetachedCriteria();
    	dc.add(Restrictions.eq("circleTagGroupName", circleTagGroupName));
    	List<CircleTagGroup> listCircleTagGroup = findByCriteria(dc);
    	if (listCircleTagGroup.size() > 0) {
    		oCricleTagGroupNew = listCircleTagGroup.get(0); 
    		oCricleTagGroupNew.setIsDeleted(false);
    	}
    	else {    		
    		CircleTagGroup oCricleTagGroup = new CircleTagGroup();
    		oCricleTagGroup.setCircleTagGroupName(circleTagGroupName);
    		oCricleTagGroupNew = create(oCricleTagGroup);
    	}        
        return oCricleTagGroupNew;
	}
	
    @Override
    public List<CircleTagGroup> findByIds(Long... ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }

        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("id", ids));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }
    
    @Override
    public List<CircleTagGroup> listAllTagGroups() {
    	return findAll();
    }
    
    @Override
    public List<CircleTagGroup> findByCircleId(Long id) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("circleId", id));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);    	
    }
    
    @Override
    public PageResult<CircleTagGroup> findByCircleId(Long id, Long offset, Long limit) {
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("circleId", id));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
    	return findByCriteria(dc, offset, limit, regionOffindByCircleId);
    }
}
