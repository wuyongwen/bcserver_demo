package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.Store;

public interface StoreDao extends GenericDao<Store, Long>{
	public Store findStoreByLocale(String locale);

}
