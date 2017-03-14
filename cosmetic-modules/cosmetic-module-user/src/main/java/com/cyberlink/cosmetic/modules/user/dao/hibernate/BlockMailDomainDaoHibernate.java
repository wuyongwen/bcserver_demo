package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.user.dao.BlockMailDomainDao;
import com.cyberlink.cosmetic.modules.user.model.BlockMailDomain;

public class BlockMailDomainDaoHibernate extends AbstractDaoCosmetic<BlockMailDomain, Long> implements BlockMailDomainDao {
	
	@Override
	public Boolean isBlockedDomain(String domain) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("domain", domain));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc) != null;
	}
}