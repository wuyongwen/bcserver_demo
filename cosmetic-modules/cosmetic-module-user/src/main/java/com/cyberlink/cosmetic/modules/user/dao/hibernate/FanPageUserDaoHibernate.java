package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.user.dao.FanPageUserDao;
import com.cyberlink.cosmetic.modules.user.model.FanPageUser;

public class FanPageUserDaoHibernate extends AbstractDaoCosmetic<FanPageUser, Long> implements FanPageUserDao {

	@Override
	public FanPageUser findFanPageUserByFanPageName(String fanPageName) {
		if (fanPageName == null) {
    		return null;
    	}
		
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("fanPageName", fanPageName));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.addOrder(Order.desc("id"));
        return uniqueResult(dc);
	}
	
	@Override
	public List<FanPageUser> listAllFanPageUser(){
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("autoPost", Boolean.TRUE));
        return findByCriteria(dc);
	}
	
	@Override
	public FanPageUser findFanPageUserByUserId(Long userId){
		if (userId == null) {
    		return null;
    	}
		
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
	}
}