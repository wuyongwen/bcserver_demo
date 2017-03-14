package com.cyberlink.cosmetic.modules.product.service.impl;

import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.model.ProductType;
import com.cyberlink.cosmetic.modules.product.service.ProductTypeService;

public class ProductTypeServiceImpl implements ProductTypeService{

	@SpringBean("product.ProductTypeDao")
	private ProductTypeDao productTypeDao ;

	public ProductType createOrUpdate(String typeName, String locale) {
		if( productTypeDao.listProdTypeByTypeNameLocale( typeName, locale ).size() == 0 ){
			return create(typeName, locale);
		}
		else{
			//always return 1st item if there's existing multiple same name item
			return productTypeDao.listProdTypeByTypeNameLocale( typeName, locale ).get(0);
		}
	}
	

	public ProductType create(String typeName, String locale) {
		ProductType newProductType = new ProductType ();
		newProductType.setTypeName(typeName);
		newProductType.setLocale(locale);
		return productTypeDao.create(newProductType);
	}


	public ProductTypeDao getProductTypeDao() {
		return productTypeDao;
	}


	public void setProductTypeDao(ProductTypeDao productTypeDao) {
		this.productTypeDao = productTypeDao;
	}

	
}
