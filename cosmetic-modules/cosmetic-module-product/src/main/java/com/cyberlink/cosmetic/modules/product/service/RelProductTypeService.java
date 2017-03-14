package com.cyberlink.cosmetic.modules.product.service;

import com.cyberlink.cosmetic.modules.product.model.RelProductType;

public interface RelProductTypeService {
	public RelProductType createOrUpdate(Long ProdID, long typeID);

}
