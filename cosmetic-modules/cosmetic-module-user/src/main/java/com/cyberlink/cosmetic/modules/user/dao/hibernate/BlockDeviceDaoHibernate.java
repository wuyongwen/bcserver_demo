package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.user.dao.BlockDeviceDao;
import com.cyberlink.cosmetic.modules.user.model.BlockDevice;

public class BlockDeviceDaoHibernate extends AbstractDaoCosmetic<BlockDevice, Long> implements BlockDeviceDao{

	@Override
	public Boolean isBlockedUuid(String uuid) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("uuid", uuid));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc) != null;
	}

	@Override
	public Set<String> checkIsBlocked(Set<String> uuids) {
        if (uuids == null || uuids.size() == 0) {
        	return new HashSet<String>();
        }
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("uuid", uuids));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("uuid"));
        List<String> list = findByCriteria(dc);
        return new HashSet<String>(list);
	}

	@Override
	public List<BlockDevice> findByUuid(String uuid) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("uuid", uuid));
        return findByCriteria(dc);
	}
	
}
