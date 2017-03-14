package com.cyberlink.cosmetic.modules.product.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.ProductCollection;
import com.cyberlink.cosmetic.modules.product.model.TargetType;

public interface ProductCollectionDao extends GenericDao<ProductCollection, Long>{
    PageResult<Long> findByUserIdAndType(Long userId, TargetType target, Long offset, Long limit);
    ProductCollection   findProductCollection(Long productId, Long userId, TargetType target);
}
