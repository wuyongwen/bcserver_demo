package com.cyberlink.cosmetic.action.api.product;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.ProductEffectDao;
import com.cyberlink.cosmetic.modules.product.model.ProductEffect;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/list-effect-bygroup.action")
public class ListEffectByGroupAction extends AbstractAction{
    
    @SpringBean("product.ProductEffectDao")
    private ProductEffectDao productEffectDao;
    
    private Long groupId;
    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
    
    @DefaultHandler
    public Resolution route() {
        PageResult<ProductEffect> productEffectList = productEffectDao.listAllByGroupId(groupId, offset , limit);
        return json(productEffectList);
    }
    
    public Long getGroupId() {
        return groupId;
    }
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
