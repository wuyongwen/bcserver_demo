package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoHibernate;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;

public class SessionDaoHibernate extends AbstractDaoHibernate<Session, Long> implements SessionDao{
	private String regionOfFindByToken = "com.cyberlink.cosmetic.modules.user.model.Session.query.findByToken";
	
    @Override
    public Session findByToken(String token) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("token", token));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc, regionOfFindByToken);
    }

    @Override
    public List<Session> findByUserId(Long userId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
    }
    
    @Override
    public Session findUniqueByUserId(Long userId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
    }
    
    @Override
    public Session findByUserIdAndStatus(Long userId,SessionStatus status) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("status", status));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
    }

	@Override
	public void deleteByUser(Long userId) {
        String format = "UPDATE `BC_USER_SESSION` SET `IS_DELETED`= '1' WHERE `USER_ID` =  '%s'";
        String sql = String.format(format, userId.toString());
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
        return;		        
	}

}
