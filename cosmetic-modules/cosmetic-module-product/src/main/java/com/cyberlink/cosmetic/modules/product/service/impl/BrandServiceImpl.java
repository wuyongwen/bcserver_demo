package com.cyberlink.cosmetic.modules.product.service.impl;

import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;
import com.cyberlink.cosmetic.modules.product.service.BrandService;

public class BrandServiceImpl implements BrandService{

	@SpringBean("product.BrandDao")
	private BrandDao brandDao ;

	@Override
	public Brand createOrUpdate(String brandName, BrandIndex brandIndex, String locale) {
		if( brandDao.findBrandByNameLocale(brandName, locale).size() == 0 ){
			return create(brandName, brandIndex, locale);
		}
		else{
			//always return 1st Brand item with same name, locale
			return brandDao.findBrandByNameLocale(brandName, locale).get(0) ;
		}
	}
	
	public Brand create(String brandName, BrandIndex brandIndex, String locale){
		final Brand newBrand = new Brand();
		newBrand.setBrandName(brandName);
		newBrand.setBrandIndex(brandIndex);
		newBrand.setLocale(locale);
		return brandDao.create(newBrand) ;
	}

	public BrandDao getBrandDao() {
		return brandDao;
	}

	public void setBrandDao(BrandDao brandDao) {
		this.brandDao = brandDao;
	}
}
