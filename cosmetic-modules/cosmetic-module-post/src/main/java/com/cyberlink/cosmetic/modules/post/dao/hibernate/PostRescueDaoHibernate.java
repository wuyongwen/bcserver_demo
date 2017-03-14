package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostRescueDao;
import com.cyberlink.cosmetic.modules.post.model.PostRescue;
import com.cyberlink.cosmetic.modules.post.model.PostRescue.RescueType;

public class PostRescueDaoHibernate extends AbstractDaoCosmetic<PostRescue, Long>
    implements PostRescueDao {

    @Override
    public PageResult<PostRescue> getPostRescueBetween(Long handlerId, String postLocale, Date begin, Date end, Boolean isHandled, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        if(handlerId != null) {
            dc.add(Restrictions.eq("reviewerId", handlerId));
        }
        
        if(postLocale != null && postLocale.length() > 0) {
            dc.createAlias("post", "post");
            dc.add(Restrictions.eq("post.locale", postLocale));
        }
        if(begin != null) {
            dc.add(Restrictions.ge("createdTime", begin));
            
        }
        if(end != null) {
            dc.add(Restrictions.le("createdTime", end));
            
        }
        
        if(isHandled != null)
            dc.add(Restrictions.eq("isHandled", isHandled));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return blockQuery(dc, blockLimit);
    }

    @Override
    public PostRescue getLastRecord(String postLocale, Boolean isHandled) {
        DetachedCriteria dc = createDetachedCriteria();
        if(postLocale != null && postLocale.length() > 0) {
            dc.createAlias("post", "post");
            dc.add(Restrictions.eq("post.locale", postLocale));
        }
        
        if(isHandled != null)
            dc.add(Restrictions.eq("isHandled", isHandled));
        
        BlockLimit blockLimit = new BlockLimit(0, 1);
        blockLimit.addOrderBy("createdTime", false);
        PageResult<PostRescue> results = blockQuery(dc, blockLimit);
        if(results.getResults().size() <= 0)
            return null;
        else
            return results.getResults().get(0);
    }

    @Override
    public Boolean batchCreate(List<PostRescue> list) {
        if(list == null || list.size() <= 0)
            return true;
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        int i = 0;
        for (PostRescue toCreate : list) {
            session.save(toCreate);
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
    public List<Long> findExPostIds(List<Long> postIds, RescueType rescueType, Boolean isHandled) {
        DetachedCriteria dc = createDetachedCriteria();
        if(postIds == null || postIds.size() <= 0)
             return null;
        dc.add(Restrictions.in("postId", postIds));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(rescueType != null)
            dc.add(Restrictions.eq("rescueType", rescueType));
        if(isHandled != null)
            dc.add(Restrictions.eq("isHandled", isHandled));
        
        dc.setProjection(Projections.property("postId"));
        return findByCriteria(dc);
    }
    
    @Override
    public List<PostRescue> findByIds(List<Long> ids) {
        DetachedCriteria dc = createDetachedCriteria();
        if(ids == null || ids.size() <= 0)
             return null;
        dc.add(Restrictions.in("id", ids));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
    }
    
}
