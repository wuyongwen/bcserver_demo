package com.cyberlink.cosmetic.modules.product.service;

import com.cyberlink.cosmetic.modules.product.model.BrandIndex;

public interface BrandIndexService {
	public BrandIndex createOrUpdate( String indexName, String locale );
}
