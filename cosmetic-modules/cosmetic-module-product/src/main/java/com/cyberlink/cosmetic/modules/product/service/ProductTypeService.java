package com.cyberlink.cosmetic.modules.product.service;

import com.cyberlink.cosmetic.modules.product.model.ProductType;

public interface ProductTypeService {
	public ProductType createOrUpdate(String typeName, String locale); 
}
