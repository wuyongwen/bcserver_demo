package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.product.dao.ProductSearchKeywordDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogType;
import com.cyberlink.cosmetic.modules.product.model.ProductSearchKeyword;
import com.cyberlink.cosmetic.modules.product.model.ProductType;

public class ProductSearchKeywordDaoHibernate extends AbstractDaoCosmetic<ProductSearchKeyword, Long>
implements ProductSearchKeywordDao{

	@Override
	public List<ProductSearchKeyword> findByKeywordLocale(
			ProductChangeLogType refType, Long refId, String keyword) {
		
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "refId" , refId )) ;
		dc.add(Restrictions.eq( "refType" , refType )) ;
		dc.add(Restrictions.eq( "keyword" , keyword )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		return findByCriteria(dc);
	}
}
