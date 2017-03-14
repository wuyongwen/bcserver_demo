package com.cyberlink.cosmetic.modules.product.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.ProductEffect;
import com.cyberlink.cosmetic.modules.product.model.ProductProductEffect;

public interface ProductProductEffectDao extends GenericDao<ProductProductEffect, Long> {
    
    PageResult<ProductProductEffect> findByProductEffect(ProductEffect productEffect, Long offset, Long limit);

}