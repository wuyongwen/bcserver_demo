package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.BrandIndexDao;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;

public class BrandIndexDaoHibernate extends AbstractDaoCosmetic<BrandIndex, Long> implements BrandIndexDao{

	public List<BrandIndex> listAllIndexByLocale(String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.asc("index"));
		return findByCriteria(dc);
	}

	@Override
	public BrandIndex findIndexByNameLocale(String brandIndex, String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.add(Restrictions.eq( "index" , brandIndex )) ;
		return uniqueResult(dc);
	}

	@Override
	public PageResult<BrandIndex> listIndexByLocale(String locale, Long offset,
			Long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.asc("index"));
		return findByCriteria(dc, offset, limit, "com.cyberlink.cosmetic.modules."
				+ "product.dao.hibernate.BrandIndexDaoHibernate.listIndexByLocale");
	}

}
