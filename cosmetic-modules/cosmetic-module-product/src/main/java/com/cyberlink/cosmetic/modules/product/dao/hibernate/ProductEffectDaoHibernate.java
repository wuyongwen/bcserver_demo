package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductEffectDao;
import com.cyberlink.cosmetic.modules.product.model.ProductEffect;

public class ProductEffectDaoHibernate extends AbstractDaoCosmetic<ProductEffect, Long> implements ProductEffectDao{

    public PageResult<ProductEffect> listAllByGroupId(Long groupId, long offset, long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("productGroupId", groupId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc, offset, limit, null);
    }
}
