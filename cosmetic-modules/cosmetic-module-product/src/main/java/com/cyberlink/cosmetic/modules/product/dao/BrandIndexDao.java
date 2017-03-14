package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;

public interface BrandIndexDao extends GenericDao<BrandIndex, Long>{
	List<BrandIndex> listAllIndexByLocale(String locale);
	PageResult<BrandIndex> listIndexByLocale(String locale, Long offset, Long limit);
	BrandIndex findIndexByNameLocale(String brandIndex, String locale);
}
