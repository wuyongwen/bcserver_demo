package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoHibernate;
import com.cyberlink.cosmetic.modules.post.dao.PostProductDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostProduct;

public class PostProductDaoHibernate extends AbstractDaoHibernate<PostProduct, Long>
    implements PostProductDao {

    @Override
    public List<PostProduct> listByPost(Post post) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("post", post));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(d);
    }
    
    @Override
    public List<PostProduct> listByPost(Post post, Boolean isExternal) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.eq("post", post));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(isExternal)
        	d.add(Restrictions.isNull("productId"));
        return findByCriteria(d);
    }

    @Override
    public Long getPostProductCount(Post post) {
        String updateSqlCmd = "SELECT COUNT(*) FROM BC_POST_PRODUCT "
                            + "INNER JOIN BC_POST ON BC_POST_PRODUCT.POST_ID = BC_POST.ID "
                            + "WHERE (BC_POST.ID = :postId OR BC_POST.PARENT_ID = :postId) "
                            + "AND BC_POST_PRODUCT.IS_DELETED = 0 "
                            + "AND BC_POST.IS_DELETED = 0";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updateSqlCmd);
        sqlPostsQuery.setParameter("postId", post.getId());
        List<BigInteger> productTagCount = sqlPostsQuery.list();
        if(productTagCount == null || productTagCount.size() <= 0)
            return Long.valueOf(0);
        return productTagCount.get(0).longValue();
    }
}
