package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import net.sourceforge.stripes.integration.spring.SpringBean;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductProductEffectDao;
import com.cyberlink.cosmetic.modules.product.model.ProductEffect;
import com.cyberlink.cosmetic.modules.product.model.ProductProductEffect;


public class ProductProductEffectDaoHibernate extends AbstractDaoCosmetic<ProductProductEffect, Long> implements ProductProductEffectDao{
	
	@SpringBean("product.ProductDao")
	protected ProductDao productDao;

    @Override
    public PageResult<ProductProductEffect> findByProductEffect(ProductEffect productEffect,
            Long offset, Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("productEffect", productEffect));
        return findByCriteria(dc, offset, limit, null);
    }
	
}
