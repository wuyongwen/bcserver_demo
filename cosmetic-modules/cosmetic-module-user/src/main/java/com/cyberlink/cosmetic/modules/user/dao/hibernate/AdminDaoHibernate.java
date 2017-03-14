package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.user.dao.AdminDao;
import com.cyberlink.cosmetic.modules.user.model.Admin;
import com.cyberlink.cosmetic.modules.user.model.Admin.UserEvent;

public class AdminDaoHibernate extends AbstractDaoCosmetic<Admin, Long> implements AdminDao{

	@Override
	public String findAttributebyRefInfo(UserEvent event, String refInfo) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("event", event));
		dc.add(Restrictions.eq("refInfo", refInfo));
		dc.setProjection(Projections.property("attribute"));
		List<String> results = findByCriteria(dc);
		if (results == null || results.size() <= 0)
			return null;
		return results.get(0);
	}
	
	@Override
	public List<String> findAttributebyRefInfos(UserEvent event, List<String> refInfos) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("event", event));
		dc.add(Restrictions.in("refInfo", refInfos));
		dc.setProjection(Projections.property("attribute"));
		return findByCriteria(dc);
	}

	@Override
	public Admin findbyRefInfo(UserEvent event, String refInfo) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.add(Restrictions.eq("event", event));
		dc.add(Restrictions.eq("refInfo", refInfo));
		List<Admin> results = findByCriteria(dc);
		if (results == null || results.size() <= 0)
			return null;
		return results.get(0);
	}
	
}