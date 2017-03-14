package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.Brand;

public interface BrandDao extends GenericDao<Brand, Long>{
	PageResult<Brand> listAllBrands(long offset, long limit);
	PageResult<Brand> listBrandByLocale(Long brandIndexId, String locale, long offset, long limit);
	List<Brand> findBrandByNameLocale(String brandName, String locale);
	List<Brand> listAllBrandByLocale(String locale);
	List<Brand> listAllBrandByLocalePriority(String locale);
}
