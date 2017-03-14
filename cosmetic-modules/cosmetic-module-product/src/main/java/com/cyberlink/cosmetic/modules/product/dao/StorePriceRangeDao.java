package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;

public interface StorePriceRangeDao extends GenericDao<StorePriceRange, Long>{
	List<StorePriceRange> listAllPriceRangeByLocale(String locale);
	PageResult<StorePriceRange> listPriceRangeByLocale(String locale, Long offset, Long limit);
	List<StorePriceRange> listPriceRangeByIdLocale(Long Id, String locale);
	List<StorePriceRange> listAllPriceRangeByLocalePrice(String locale, Float priceMax, Float priceMin);
	List<StorePriceRange> findPriceRangeByLocalePriceMax(String locale, Float priceMax);
	List<StorePriceRange> findPriceRangeByLocalePriceMin(String locale, Float priceMin);
	StorePriceRange findPriceRangeByLocalePrice(String locale, Float price);

}
