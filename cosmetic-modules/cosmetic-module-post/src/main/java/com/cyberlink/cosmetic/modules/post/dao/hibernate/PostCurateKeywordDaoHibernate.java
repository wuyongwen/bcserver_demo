package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.PostCurateKeywordDao;
import com.cyberlink.cosmetic.modules.post.model.PostCurateKeyword;

public class PostCurateKeywordDaoHibernate extends AbstractDaoCosmetic<PostCurateKeyword, Long>
    implements PostCurateKeywordDao {

    private String regionOfListPostCurateKeyword = "com.cyberlink.cosmetic.modules.post.model.PostCurateKeyword.list";
    private String regionOfListAllPostCurateKeyword = "com.cyberlink.cosmetic.modules.post.model.PostCurateKeyword.listAll";
    
    @Override
    public PageResult<PostCurateKeyword> listByLocale(String locale, Boolean withDefaultType, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        if(locale != null)
            dc.add(Restrictions.eq("locale", locale));
        
        if(withDefaultType != null) {
            if(withDefaultType)
                dc.add(Restrictions.isNotNull("defaultType"));
            else
                dc.add(Restrictions.isNull("defaultType"));
        }
            
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return blockQuery(dc, blockLimit, regionOfListPostCurateKeyword);
    }

    @Override
    public PostCurateKeyword findByKeyword(String locale, String keyword) {
        if(locale == null || keyword == null)
            return null;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("locale", locale));
        dc.add(Restrictions.eq("keyword", keyword));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
    }
    
    @Override
    public List<PostCurateKeyword> listAllByLocale(String locale, Boolean withDefaultType) {
        DetachedCriteria dc = createDetachedCriteria();
        if(locale != null)
            dc.add(Restrictions.eq("locale", locale));
        
        if(withDefaultType != null) {
            if(withDefaultType)
                dc.add(Restrictions.isNotNull("defaultType"));
            else
                dc.add(Restrictions.isNull("defaultType"));
        }
            
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.addOrder(Order.desc("frequency"));
        return findByCriteria(dc, regionOfListAllPostCurateKeyword);
    }
    
    @Override
    public void updateFrequency(List<Long> keywordIds) {
        if(keywordIds == null || keywordIds.size() <= 0)
            return;
        
        String updateSqlCmd = "UPDATE BC_POST_CUR_KW SET FREQUENCY = FREQUENCY + 1 WHERE ID IN (:keywordIds)";
        SQLQuery sqlQuery = getSession().createSQLQuery(updateSqlCmd);
        sqlQuery.setParameterList("keywordIds", keywordIds);
        try {
            sqlQuery.executeUpdate();
        }
        catch(Exception e) {
        }
        
    }
    
    @Override
    public Boolean batchCreate(List<PostCurateKeyword> list) {
        if(list == null || list.size() <= 0)
            return true;
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        int i = 0;
        for (PostCurateKeyword toCreate : list) {
            try {
                session.save(toCreate);
                i++;
                if ( i % 50 == 0 ) {
                    session.flush();
                    session.clear();
                }       
                if (i % 200 == 0) {
                    Thread.sleep(500);                           
                }
            }
            catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }       
        tx.commit();
        session.close();
        return true;
    }
}
