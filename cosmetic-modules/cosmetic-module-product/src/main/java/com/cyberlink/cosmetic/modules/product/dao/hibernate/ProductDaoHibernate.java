package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;

public class ProductDaoHibernate extends AbstractDaoCosmetic<Product, Long>
        implements ProductDao {
    private String regionOfFindByText = "com.cyberlink.cosmetic.modules.product.model.Product.query.findByText";
    private String regionOfFindProdByParameters = "com.cyberlink.cosmetic.modules.product.model.Product."
    		+ "query.findProdByParameters";
    

    @Override
    public PageResult<Product> findByText(String text, String locale,
            Long offset, Long limit) {
        DetachedCriteria dc = createDetachedCriteria("p");
        // dc.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        dc.createAlias("relProductType", "type", JoinType.LEFT_OUTER_JOIN);
        dc.createAlias("type.productType", "ptype", JoinType.LEFT_OUTER_JOIN);
        dc.add(Restrictions.or(
                Restrictions.like("p.productName", text, MatchMode.ANYWHERE),
                Restrictions.like("p.productTitle", text, MatchMode.ANYWHERE),
                Restrictions.like("ptype.typeName", text, MatchMode.ANYWHERE)));
        dc.add(Restrictions.eq("locale", locale));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc, offset, limit, regionOfFindByText);
    }

    public List<Product> findByProdIDLocale(String locale, Long... prodID) {
        if (prodID == null || prodID.length == 0) {
            return new ArrayList<Product>();
        }

        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("id", prodID));
        dc.add(Restrictions.eq("locale", locale));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
    }

    public PageResult<Product> findProdByParameters(String locale,
			Long brandId, Long typeId, String brandName, Long offset,
			Long limit, Boolean onShelf, List<StorePriceRange> storePriceRange) {
        final DetachedCriteria dc = createDetachedCriteria();
        if( storePriceRange != null ){
        	dc.add(Restrictions.in("priceRange", storePriceRange)) ;
        }
        dc.add(Restrictions.eq("locale", locale));
        dc.createAlias("brand", "b");
        //dc.createAlias("relProductType", "type");
        //dc.createAlias("type.productType", "ptype");
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
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        //add order condition
        if( typeId != null && brandId == null ){
        	dc.addOrder(Order.desc("b.priority"));
        	dc.addOrder(Order.asc("price"));
        }
        else if( typeId == null && brandId != null ){
        	final DetachedCriteria countDetachedCriteria =
        			(DetachedCriteria) SerializationUtils.clone(dc);
        	countDetachedCriteria.setProjection(Projections.countDistinct("id"));
        	Long uniqueIdCount = uniqueResult(countDetachedCriteria);
        	dc.createAlias("relProductType", "type") ;
            dc.createAlias("type.productType", "ptype");
        	dc.addOrder(Order.desc("ptype.sortPriority"));
        	dc.addOrder(Order.asc("price"));
        	dc.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        	PageResult<Product> outputProdList = 
        			findByCriteria(dc, offset, limit, regionOfFindProdByParameters);
        	outputProdList.setTotalSize(uniqueIdCount.intValue());
        	return outputProdList;
        }
        else if( typeId != null && brandId != null ){
        	dc.addOrder(Order.asc("price"));
        }
        else if( typeId == null && brandId == null && storePriceRange != null ){
        	final DetachedCriteria countDetachedCriteria =
        			(DetachedCriteria) SerializationUtils.clone(dc);
        	countDetachedCriteria.setProjection(Projections.countDistinct("id"));
        	Long uniqueIdCount = uniqueResult(countDetachedCriteria);
        	dc.createAlias("relProductType", "type") ;
            dc.createAlias("type.productType", "ptype");
            dc.addOrder(Order.desc("b.priority"));
        	dc.addOrder(Order.desc("ptype.sortPriority"));
        	dc.addOrder(Order.asc("price"));
        	dc.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        	PageResult<Product> outputProdList = 
        			findByCriteria(dc, offset, limit, regionOfFindProdByParameters);
        	outputProdList.setTotalSize(uniqueIdCount.intValue());
        	return outputProdList;
        }
        else{
        	dc.addOrder(Order.desc("lastModified"));
        }
        
        return findByCriteria(dc, offset, limit, regionOfFindProdByParameters);
    }

    public PageResult<Product> findByBarcodeLocale(String locale, Long offset,
            Long limit, Long... Barcode) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("locale", locale));
        dc.add(Restrictions.in("barCode", Barcode));
        return findByCriteria(dc, offset, limit,
                "com.cyberlink.cosmetic.modules.product.dao.hibernate."
                        + "ProductDaoHibernate.findByBarcodeLocale");
    }

    public Product findByBrandIdExtProdID_StoreID(Long BrandId, String ExtProdID, Long StoreID, Long typeId[]) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("brand.id", BrandId));
        dc.add(Restrictions.eq("extProdID", ExtProdID));
        dc.add(Restrictions.eq("store.id", StoreID));
        if(typeId != null){
        	dc.createAlias("relProductType", "type");
            dc.createAlias("type.productType", "ptype");
            dc.add(Restrictions.in("ptype.id", typeId));
        }
        return uniqueResult(dc);
    }

    public Long countUndeleted() {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.rowCount());
        return uniqueResult(dc);
    }

    public List<Product> findUndeleted(Integer pageIndex, Integer pageSize) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.addOrder(Order.desc("createdTime"));
        return findByCriteria(dc, new PageLimit(pageIndex, pageSize)); 
    }

	public List<Product> listProdByLocale(String locale) {
		final DetachedCriteria dc = createDetachedCriteria();
        //dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("locale", locale));
        return findByCriteria(dc);
	}

	public PageResult<Product> findProdByParams(String locale, Long brandId,
			Long typeId, String brandName, Long offset, Long limit,
			Boolean onShelf, Long priceRangeId) {
		final DetachedCriteria dc = createDetachedCriteria();
        if( priceRangeId != 0 ){
        	dc.add(Restrictions.eq("priceRange.id", priceRangeId)) ;
        }
        dc.add(Restrictions.eq("locale", locale));
        dc.createAlias("brand", "b");
        //dc.createAlias("relProductType", "type");
        //dc.createAlias("type.productType", "ptype");
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
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        //add order condition
        if( typeId != null && brandId == null ){
        	dc.addOrder(Order.desc("b.priority"));
        	dc.addOrder(Order.asc("price"));
        }
        else if( typeId == null && brandId != null ){
        	final DetachedCriteria countDetachedCriteria =
        			(DetachedCriteria) SerializationUtils.clone(dc);
        	countDetachedCriteria.setProjection(Projections.countDistinct("id"));
        	Long uniqueIdCount = uniqueResult(countDetachedCriteria);
        	dc.createAlias("relProductType", "type") ;
            dc.createAlias("type.productType", "ptype");
        	dc.addOrder(Order.desc("ptype.sortPriority"));
        	dc.addOrder(Order.asc("price"));
        	dc.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        	PageResult<Product> outputProdList = 
        			findByCriteria(dc, offset, limit, regionOfFindProdByParameters);
        	outputProdList.setTotalSize(uniqueIdCount.intValue());
        	return outputProdList;
        }
        else if( typeId != null && brandId != null ){
        	dc.addOrder(Order.asc("price"));
        }
        else if( typeId == null && brandId == null && priceRangeId != 0 ){
        	final DetachedCriteria countDetachedCriteria =
        			(DetachedCriteria) SerializationUtils.clone(dc);
        	countDetachedCriteria.setProjection(Projections.countDistinct("id"));
        	Long uniqueIdCount = uniqueResult(countDetachedCriteria);
        	dc.createAlias("relProductType", "type") ;
            dc.createAlias("type.productType", "ptype");
            dc.addOrder(Order.desc("b.priority"));
        	dc.addOrder(Order.desc("ptype.sortPriority"));
        	dc.addOrder(Order.asc("price"));
        	dc.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        	PageResult<Product> outputProdList = 
        			findByCriteria(dc, offset, limit, regionOfFindProdByParameters);
        	outputProdList.setTotalSize(uniqueIdCount.intValue());
        	return outputProdList;
        }
        else{
        	dc.addOrder(Order.desc("lastModified"));
        }
        
        return findByCriteria(dc, offset, limit, regionOfFindProdByParameters);
	}

	@Override
	public List<Product> listProdByLocalePriceRangeId(String locale,
			Long priceRangeId) {
		final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("locale", locale));
        dc.add(Restrictions.eq("priceRange.id", priceRangeId)) ;
        return findByCriteria(dc);
	}

	@Override
	public PageResult<Product> findAllProduct(PageLimit pageLimit) {
		return pageQuery(pageLimit);
	}

	@Override
	public List<Long> findProdByCommentTime(Date startTime, Date endTime) {
		final DetachedCriteria dc = createDetachedCriteria();		
		dc.createAlias("prodCommentList", "c");
		if( startTime != null && endTime != null ){
			dc.add(Restrictions.between("c.createdTime", startTime, endTime));
		}
		dc.setProjection(Projections.distinct(Projections.property("id")));
		return findByCriteria(dc);
	}

	@Override
	public List<Product> findByProductId(Long... prodID) {
		if (prodID == null || prodID.length == 0) {
			return new ArrayList<Product>();
		}
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.in("id", prodID));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return findByCriteria(dc);
	}
    
}
