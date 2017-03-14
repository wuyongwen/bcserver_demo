package com.cyberlink.cosmetic.modules.circle.dao.hibernate;

import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.dao.CircleUserDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleUser;

public class CircleUserDaoHibernate extends AbstractDaoCosmetic<CircleUser, Long> implements CircleUserDao{
	private String regionOffindByUserId = "com.cyberlink.cosmetic.modules.circle.model.CircleUser.query.findByUserId";
	private String regionOffindByCircleId = "com.cyberlink.cosmetic.modules.circle.model.CircleUser.query.findByCircleId";
	
    @Override
    public List<CircleUser> findByIds(Long... ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }

        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("id", ids));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }
    
    @Override
    public List<CircleUser> findByUserId(Long id){
    	DetachedCriteria dc = createDetachedCriteria();
    	dc.add(Restrictions.eq("userId", id));
    	dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
    	return findByCriteria(dc);
    }
    
    @Override
    public List<CircleUser> findByCircleId(Long id){
    	DetachedCriteria dc = createDetachedCriteria();
    	dc.add(Restrictions.eq("circleId", id));
    	dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
    	return findByCriteria(dc);
    }
    
    @Override
    public PageResult<CircleUser> findByCircleId(Long id, Long offset, Long limit) {
    	DetachedCriteria dc = createDetachedCriteria();
    	dc.add(Restrictions.eq("circleId", id));
    	dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
    	return findByCriteria(dc, offset, limit, regionOffindByCircleId);
    }
    
    @Override
    public PageResult<CircleUser> findByUserId(Long id, Long offset, Long limit){
    	DetachedCriteria dc = createDetachedCriteria();
    	dc.add(Restrictions.eq("userId", id));
    	dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
    	return findByCriteria(dc, offset, limit, regionOffindByUserId);
    }    
}
