package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductCollectionDao;
import com.cyberlink.cosmetic.modules.product.model.ProductCollection;
import com.cyberlink.cosmetic.modules.product.model.TargetType;

public class ProductCollectionDaoHibernate extends AbstractDaoCosmetic<ProductCollection, Long> implements ProductCollectionDao{
	private String regionOfFindByUserIdAndType = "com.cyberlink.cosmetic.modules.product.model.ProductCollection.query.findByUserIdAndType";
	@Override
	public PageResult<Long> findByUserIdAndType(Long userId, TargetType target,
			Long offset, Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("target", target));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("productId"));
		return findByCriteria(dc, offset, limit, regionOfFindByUserIdAndType);
	}

	@Override
	public ProductCollection findProductCollection(Long productId, Long userId,
			TargetType target) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("productId", productId));
        dc.add(Restrictions.eq("target", target));
        return uniqueResult(dc);
	}

}
