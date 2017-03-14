package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;

public class StorePriceRangeDaoHibernate extends AbstractDaoCosmetic<StorePriceRange, Long> implements StorePriceRangeDao{

	public List<StorePriceRange> listAllPriceRangeByLocale(String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.asc("priceMin"));
		return findByCriteria(dc);
	}

	public List<StorePriceRange> listAllPriceRangeByLocalePrice(String locale,
			Float priceMax, Float priceMin) {
		if(priceMax == null && priceMin == null){
			return null;
		}
		else if( priceMax == null ){
			return findPriceRangeByLocalePriceMin(locale, priceMin);
		}
		else if( priceMin == null ){
			return findPriceRangeByLocalePriceMax(locale, priceMax);
		}
		else{
			DetachedCriteria dc = createDetachedCriteria();
			dc.add(Restrictions.eq( "locale" , locale )) ;
			dc.add(Restrictions.ge( "priceMin" , priceMin )) ;
			dc.add(Restrictions.le( "priceMax" , priceMax )) ;
			dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
			dc.addOrder(Order.asc("priceMin"));
			return findByCriteria(dc);
		}
	}

	@Override
	public List<StorePriceRange> findPriceRangeByLocalePriceMax(String locale,
			Float priceMax) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.le( "priceMax" , priceMax )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.asc("priceMin"));
		return findByCriteria(dc);
	}

	@Override
	public List<StorePriceRange> findPriceRangeByLocalePriceMin(String locale,
			Float priceMin) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.ge( "priceMin" , priceMin )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.asc("priceMin"));
		return findByCriteria(dc);
	}

	@Override
	public StorePriceRange findPriceRangeByLocalePrice(String locale,
			Float price) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.le( "priceMin" , price )) ;
		dc.add(Restrictions.gt( "priceMax" , price )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		return uniqueResult(dc);
	}

	@Override
	public List<StorePriceRange> listPriceRangeByIdLocale(Long Id, String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		if(Id != null){
			dc.add(Restrictions.eq( "id" , Id )) ;
		}
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.asc("priceMin"));
		return findByCriteria(dc);
	}

	@Override
	public PageResult<StorePriceRange> listPriceRangeByLocale(String locale,
			Long offset, Long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.asc("priceMin"));
		return findByCriteria(dc, offset, limit, "com.cyberlink.cosmetic.modules."
				+ "product.dao.hibernate.StorePriceRangeDaoHibernate.listPriceRangeByLocale");
	}

}
