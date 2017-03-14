package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductFeatureDao;
import com.cyberlink.cosmetic.modules.product.model.ProductFeature;

public class ProductFeatureDaoHibernate extends AbstractDaoCosmetic<ProductFeature, Long>
        implements ProductFeatureDao {

    @Override
    public PageResult<ProductFeature> getProductFeatureByUser(Long userId, Date startDate, Date endDate, Long version, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        
        if(startDate != null)
            dc.add(Restrictions.or(Restrictions.isNull("startDate"), Restrictions.le("startDate", startDate)));
        if(endDate != null)
            dc.add(Restrictions.or(Restrictions.isNull("endDate"), Restrictions.ge("endDate", endDate)));
        if(version != null)
            dc.add(Restrictions.le("version", version));
        
        return blockQuery(dc, blockLimit);
    }
    
    @Override
    public Long getProductFeatureCountByUser(Long userId, Date startDate, Date endDate, Long version) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        
        if(startDate != null)
            dc.add(Restrictions.or(Restrictions.isNull("startDate"), Restrictions.le("startDate", startDate)));
        if(endDate != null)
            dc.add(Restrictions.or(Restrictions.isNull("endDate"), Restrictions.ge("endDate", endDate)));
        if(version != null)
            dc.add(Restrictions.le("version", version));
        
        dc.setProjection(Projections.rowCount());
        return uniqueResult(dc);
    }
    
    @Override
    public Map<String, List<ProductFeature>> getProductFeatureMapByUser(Long userId, Date startDate, Date endDate, Long version, BlockLimit blockLimit) {
        Map<String, List<ProductFeature>> mapResult = new LinkedHashMap<String, List<ProductFeature>>();
        PageResult<ProductFeature> pgResult = getProductFeatureByUser(userId, startDate, endDate, version, blockLimit);
        for(ProductFeature pf : pgResult.getResults()) {
            if(!mapResult.containsKey(pf.getProductType())) {
                List<ProductFeature> pfList = new ArrayList<ProductFeature>();
                mapResult.put(pf.getProductType(), pfList);
            }
            mapResult.get(pf.getProductType()).add(pf);
        }
        return mapResult;
    }
    
    @Override
    public PageResult<Pair<String, ProductFeature>> findByExtProductIds(Long userId, Set<String> extProductIds, BlockLimit blockLimit) {
        PageResult<Pair<String, ProductFeature>> result = new PageResult<Pair<String, ProductFeature>>();
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.in("extProductId", extProductIds));
        PageResult<ProductFeature> pgResult = blockQuery(dc, blockLimit);
        result.setTotalSize(pgResult.getTotalSize());
        for(ProductFeature pf : pgResult.getResults()) {
            result.add(Pair.of((String)pf.getExtProductId(), (ProductFeature)pf));
        }
        return result;
    }
    
    @Override
    public Boolean batchCreate(List<ProductFeature> list) {
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        int i = 0;
        for (ProductFeature pf : list) {
            session.save(pf);
            i++;
            if ( i % 50 == 0 ) {
                session.flush();
                session.clear();
            }       
            if (i % 200 == 0) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    return false;
                }                           
            }
        }       
        tx.commit();
        session.close();
        return true;
    }
    
    @Override
    public int bacthDeleteByExtProductIds(Set<String> extProductIds) {
        if(extProductIds == null || extProductIds.size() <= 0)
            return 0;
        String updatePostSqlCmd = "UPDATE BC_PRODUCT_FEATURE SET IS_DELETED=1 WHERE EXT_PRODUCT_ID IN (:extProductIds)";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameterList("extProductIds", extProductIds);
        return sqlPostsQuery.executeUpdate();
    }
    
    @Override
    public int bacthDeleteByUserId(Long userId) {
        if(userId == null)
            return 0;
        String updatePostSqlCmd = "UPDATE BC_PRODUCT_FEATURE SET IS_DELETED=1 WHERE USER_ID = :userId";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameter("userId", userId);
        return sqlPostsQuery.executeUpdate();
    }
    
    @Override
    public int bacthDeleteIfMissing(Long userId, Set<String> extProductIds) {
        if(extProductIds == null || extProductIds.size() <= 0)
            return 0;
        String updatePostSqlCmd = "UPDATE BC_PRODUCT_FEATURE SET IS_DELETED=1 WHERE USER_ID = :userId AND EXT_PRODUCT_ID NOT IN (:extProductIds)";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameterList("extProductIds", extProductIds);
        sqlPostsQuery.setParameter("userId", userId);
        return sqlPostsQuery.executeUpdate();
    }
}
