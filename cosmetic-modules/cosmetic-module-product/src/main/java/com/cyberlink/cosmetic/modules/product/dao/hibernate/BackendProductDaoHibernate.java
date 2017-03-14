package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.BackendProductDao;
import com.cyberlink.cosmetic.modules.product.model.BackendProduct;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;

public class BackendProductDaoHibernate extends AbstractDaoCosmetic<BackendProduct, Long>
        implements BackendProductDao {
    private String regionOfFindProdByParameters = "com.cyberlink.cosmetic.modules.product.dao.hibernate"
    		+ "BackendProductDaoHibernate.findProdByParameters";

    public PageResult<BackendProduct> findProdByParameters(String locale,
			Long brandId, Long typeId, String brandName, Long offset,
			Long limit, Boolean onShelf, List<StorePriceRange> storePriceRange) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("priceRange", storePriceRange)) ;
        dc.add(Restrictions.eq("locale", locale));
        dc.createAlias("brand", "b");
        if (brandId != null)
            dc.add(Restrictions.eq("b.id", brandId));
        if (brandName != null)
            dc.add(Restrictions.ilike("b.brandName", brandName,
                    MatchMode.ANYWHERE));
        if (typeId != null) {
        	dc.createAlias("relProductType", "type");
            dc.createAlias("type.productType", "ptype");
            dc.add(Restrictions.eq("ptype.id", typeId));
        }
        if (onShelf != null) {
            dc.add(Restrictions.eq("onShelf", onShelf));
        }
        return findByCriteria(dc, offset, limit, regionOfFindProdByParameters);
    }

	@Override
	public PageResult<BackendProduct> findProdByParams(String locale,
			Long brandId, Long typeId, String brandName, Long offset,
			Long limit, Boolean onShelf, Long priceRangeId) {
		final DetachedCriteria dc = createDetachedCriteria();
        if( priceRangeId != 0 ){
        	dc.add(Restrictions.eq("priceRange.id", priceRangeId)) ;
        }
        dc.add(Restrictions.eq("locale", locale));
        dc.createAlias("brand", "b");
        if (brandId != null)
            dc.add(Restrictions.eq("b.id", brandId));
        if (brandName != null)
            dc.add(Restrictions.ilike("b.brandName", brandName,
                    MatchMode.ANYWHERE));
        if (typeId != null) {
        	dc.createAlias("relProductType", "type");
            dc.createAlias("type.productType", "ptype");
            dc.add(Restrictions.eq("ptype.id", typeId));
        }
        if (onShelf != null) {
            dc.add(Restrictions.eq("onShelf", onShelf));
        }
        //add order condition
        if( typeId != null && brandId == null ){
        	dc.addOrder(Order.desc("b.priority"));
        	dc.addOrder(Order.asc("price"));
        }
        else if( typeId == null && brandId != null ){
        	dc.createAlias("relProductType", "type") ;
            dc.createAlias("type.productType", "ptype");
        	dc.addOrder(Order.desc("ptype.sortPriority"));
        	dc.addOrder(Order.asc("price"));
        	dc.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        }
        else if( typeId != null && brandId != null ){
        	dc.addOrder(Order.asc("price"));
        }
        else{
        	dc.addOrder(Order.desc("lastModified"));
        }
        
        return findByCriteria(dc, offset, limit, regionOfFindProdByParameters);
	}
    
}
