package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.product.model.BrandNameAlias;

public interface BrandNameAliasDao extends GenericDao<BrandNameAlias, Long>{

	public List<BrandNameAlias> listAliasByBrandIdAliasName( Long refBrandId, String aliasName );
}
