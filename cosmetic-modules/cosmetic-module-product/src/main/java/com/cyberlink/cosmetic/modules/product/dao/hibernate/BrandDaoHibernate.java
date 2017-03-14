package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;

import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;

public class BrandDaoHibernate extends AbstractDaoCosmetic<Brand, Long> implements BrandDao{
	public PageResult<Brand> listAllBrands(long offset, long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		return findByCriteria(dc, offset, limit, "com.cyberlink.cosmetic.modules."
				+ "product.dao.hibernate.BrandDaoHibernate.listAllBrands");
	}

	
	public PageResult<Brand> listBrandByLocale(Long brandIndexId, String locale, long offset,
			long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		
		if( brandIndexId != null ){
			dc.add(Restrictions.eq("brandIndex.id", brandIndexId));
		}
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.desc("priority"));
		//dc.addOrder(Order.asc("brandName"));
		return findByCriteria(dc, offset, limit, "com.cyberlink.cosmetic.modules."
				+ "product.dao.hibernate.BrandDaoHibernate.listBrandByLocale");
	}

	public List<Brand> listAllBrandByLocale(String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		return findByCriteria(dc);
	}
	
	public List<Brand> findBrandByNameLocale(String brandName, String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "brandName" , brandName )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		return findByCriteria(dc);
	}


	public List<Brand> listAllBrandByLocalePriority(String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.desc("priority"));
		return findByCriteria(dc);
	}
	
}
