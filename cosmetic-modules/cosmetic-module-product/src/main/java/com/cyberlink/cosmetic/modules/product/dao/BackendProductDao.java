package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.BackendProduct;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;

public interface BackendProductDao extends GenericDao<BackendProduct, Long>{
	
	PageResult<BackendProduct> findProdByParameters(String locale, Long brandId, Long typeId, String brandName,
			Long offset, Long limit, Boolean onShelf, List<StorePriceRange> storePriceRange);
	PageResult<BackendProduct> findProdByParams(String locale, Long brandId, Long typeId, String brandName,
			Long offset, Long limit, Boolean onShelf, Long priceRangeId);
}
