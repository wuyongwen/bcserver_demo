package com.cyberlink.cosmetic.modules.product.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.ProductEffect;

public interface ProductEffectDao extends GenericDao<ProductEffect, Long>{

    PageResult<ProductEffect> listAllByGroupId(Long groupId, long offset, long limit);
}
