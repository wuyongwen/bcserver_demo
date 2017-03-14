package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.product.dao.RelProductTypeDao;
import com.cyberlink.cosmetic.modules.product.model.RelProductType;

public class RelProductTypeDaoHibernate extends AbstractDaoCosmetic<RelProductType, Long> implements RelProductTypeDao{

	@Override
	public RelProductType findByProdIDTypeID(long PID, long typeID) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("productId", PID));		
		dc.add(Restrictions.eq("productType.id", typeID));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return uniqueResult(dc);
		
	}

	@Override
	public List<RelProductType> findByProdID(long PID) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("productId", PID));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return findByCriteria(dc);
	}

}
