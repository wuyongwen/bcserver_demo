package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogType;
import com.cyberlink.cosmetic.modules.product.model.ProductSearchKeyword;

public interface ProductSearchKeywordDao extends GenericDao<ProductSearchKeyword, Long>{
	List<ProductSearchKeyword> findByKeywordLocale( ProductChangeLogType refType, Long refId, String keyword );
}
