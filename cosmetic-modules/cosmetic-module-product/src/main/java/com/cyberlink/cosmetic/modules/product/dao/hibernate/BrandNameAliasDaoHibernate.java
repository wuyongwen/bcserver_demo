package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.product.dao.BrandNameAliasDao;
import com.cyberlink.cosmetic.modules.product.model.BrandNameAlias;

public class BrandNameAliasDaoHibernate extends AbstractDaoCosmetic<BrandNameAlias, Long> implements BrandNameAliasDao{

	@Override
	public List<BrandNameAlias> listAliasByBrandIdAliasName(Long refBrandId,
			String aliasName) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq( "refBrand.id" , refBrandId )) ;
		dc.add(Restrictions.eq( "aliasName" , aliasName )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		return findByCriteria(dc);
	}

}
