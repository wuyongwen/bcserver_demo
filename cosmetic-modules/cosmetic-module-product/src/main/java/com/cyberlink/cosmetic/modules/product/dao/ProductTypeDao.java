package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.ProductType;

public interface ProductTypeDao extends GenericDao<ProductType, Long>{
	PageResult<ProductType> findAllProductTypes(long offset, long limit);
	PageResult<ProductType> findProdTypesByBrandID_LangID(long BrandID, String BrandCode, 
			int LandID, long offset, long limit);
	
	List<ProductType> listAllProdTypeByLocale(String locale);
	List<ProductType> listProdTypeByTypeNameLocale(String typeName, String locale);
	PageResult<ProductType> listAllProdTypeByLocale(String locale, long offset, long limit);
}
