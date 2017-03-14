package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.model.Store;

public class StoreDaoHibernate extends AbstractDaoCosmetic<Store, Long> implements StoreDao{
	
	public Store findStoreByLocale(String locale){
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("locale", locale));
		return uniqueResult(dc);
	}

}
