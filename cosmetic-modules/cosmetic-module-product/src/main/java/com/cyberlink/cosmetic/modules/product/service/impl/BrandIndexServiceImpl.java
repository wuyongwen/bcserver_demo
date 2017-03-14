package com.cyberlink.cosmetic.modules.product.service.impl;

import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.modules.product.dao.BrandIndexDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;
import com.cyberlink.cosmetic.modules.product.service.BrandIndexService;
import com.cyberlink.cosmetic.modules.product.service.BrandService;

public class BrandIndexServiceImpl implements BrandIndexService{
	
	@SpringBean("product.BrandIndexDao")
	private BrandIndexDao brandIndexDao;

	public BrandIndex createOrUpdate(String indexName, String locale) {
		if( brandIndexDao.findIndexByNameLocale(indexName, locale) == null ){
			return create(indexName, locale);
		}
		else {
			return brandIndexDao.findIndexByNameLocale(indexName, locale) ;
		}
	}
	
	public BrandIndex create(String indexName, String locale) {
		BrandIndex newBrandIndex = new BrandIndex();
		newBrandIndex.setIndex(indexName);
		newBrandIndex.setLocale(locale);
		return brandIndexDao.create(newBrandIndex) ;
	}
	
	public BrandIndexDao getBrandIndexDao() {
		return brandIndexDao;
	}

	public void setBrandIndexDao(BrandIndexDao brandIndexDao) {
		this.brandIndexDao = brandIndexDao;
	}

	

}
