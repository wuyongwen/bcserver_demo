package com.cyberlink.cosmetic.modules.product.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.model.ProductFeature;

public interface ProductFeatureDao extends GenericDao<ProductFeature, Long>{

    PageResult<ProductFeature> getProductFeatureByUser(Long userId, Date startDate, Date endDate, Long version, BlockLimit blockLimit);
    Long getProductFeatureCountByUser(Long userId, Date startDate, Date endDate, Long version);
    Map<String, List<ProductFeature>> getProductFeatureMapByUser(Long userId, Date startDate, Date endDate, Long version, BlockLimit blockLimit);
    PageResult<Pair<String, ProductFeature>> findByExtProductIds(Long userId, Set<String> extProductIds, BlockLimit blockLimit);
    Boolean batchCreate(List<ProductFeature> list);
    int bacthDeleteByExtProductIds(Set<String> extProductIds);
    int bacthDeleteByUserId(Long userId);
    int bacthDeleteIfMissing(Long userId, Set<String> extProductIds);
    
}
