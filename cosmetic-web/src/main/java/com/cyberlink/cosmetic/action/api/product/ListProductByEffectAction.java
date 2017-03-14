package com.cyberlink.cosmetic.action.api.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductEffectDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductProductEffectDao;
import com.cyberlink.cosmetic.modules.product.model.ProductEffect;
import com.cyberlink.cosmetic.modules.product.model.ProductProductEffect;
import com.cyberlink.cosmetic.modules.product.model.result.ProductWrapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/list-product-byeffect.action")
public class ListProductByEffectAction extends AbstractAction{
    @SpringBean("product.ProductProductEffectDao")
    private ProductProductEffectDao productProductEffectDao;
    
    @SpringBean("product.ProductEffectDao")
    private ProductEffectDao productEffectDao;
    
    private Long productEffectId;
    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
    
    @DefaultHandler
    public Resolution route() {
        final Map<String, Object> results = new HashMap<String, Object>();

        ProductEffect productEffect = productEffectDao.findById(productEffectId);
        PageResult<ProductProductEffect> productList = productProductEffectDao.findByProductEffect(productEffect, offset, limit);
        List<ProductWrapper>  wrapperList = new ArrayList<ProductWrapper>();
        for (ProductProductEffect p : productList.getResults()) {
            wrapperList.add(new ProductWrapper(p.getProduct()));
        }
        results.put("results", wrapperList);
        results.put("totalSize", productList.getTotalSize());
        return json(results);
    }
    
    public Long getProductEffectId() {
        return productEffectId;
    }
    public void setProductEffectId(Long productEffectId) {
        this.productEffectId = productEffectId;
    }
    public Long getOffset() {
        return offset;
    }
    public void setOffset(Long offset) {
        this.offset = offset;
    }
    public Long getLimit() {
        return limit;
    }
    public void setLimit(Long limit) {
        this.limit = limit;
    }
}
