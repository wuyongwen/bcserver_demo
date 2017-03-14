package com.cyberlink.cosmetic.modules.product.service;

import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;

public interface BrandService {
	public Brand createOrUpdate(String brandName, BrandIndex brandIndex, String locale);

}
