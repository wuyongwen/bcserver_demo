package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.model.ProductType;

public class ProductTypeDaoHibernate extends AbstractDaoCosmetic<ProductType, Long> implements ProductTypeDao{

	public PageResult<ProductType> findAllProductTypes(long offset, long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		return findByCriteria(dc, offset, limit, "com.cyberlink.cosmetic.modules.product.dao.hibernate."
				+ "ProductTypeDaoHibernate.findAllProductTypes");
	}

	public PageResult<ProductType> findProdTypesByBrandID_LangID(long BrandID,
			String BrandCode, int LandID, long offset, long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		return findByCriteria(dc, offset, limit, null);
	}
	
	public List<ProductType> listAllProdTypeByLocale(String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.desc("sortPriority"));
		return findByCriteria(dc);
	}

	public PageResult<ProductType> listAllProdTypeByLocale(String locale, long offset, long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.desc("sortPriority"));
		return findByCriteria(dc, offset, limit, "com.cyberlink.cosmetic.modules.product.dao.hibernate."
				+ "ProductTypeDaoHibernate.");
	}

	public List<ProductType> listProdTypeByTypeNameLocale(String typeName,
			String locale) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "typeName" , typeName )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		
		return findByCriteria(dc);
	}
}
