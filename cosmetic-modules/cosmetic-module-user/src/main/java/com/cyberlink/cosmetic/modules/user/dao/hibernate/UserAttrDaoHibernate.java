package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.Date;

import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;

public class UserAttrDaoHibernate extends AbstractDaoCosmetic<UserAttr, Long> implements UserAttrDao {
	
	@Override
    public UserAttr findByUserId(Long userId) {
	    DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
    }
	
	@Override
	public int increaseNonNullValue(Long userId, String columnName) {
		if (userId == null || columnName == null || columnName.isEmpty())
			return 0;
		
		String sql  = "UPDATE BC_USER_ATTR "
				+ "SET %s = %s + 1 "
				+ "WHERE USER_ID = :userId "
				+ "AND %s IS NOT NULL ";
		String sqlCmd = String.format(sql, columnName, columnName, columnName);
		SQLQuery sqlPostsQuery = getSession().createSQLQuery(sqlCmd);
		sqlPostsQuery.setParameter("userId", userId);
		return sqlPostsQuery.executeUpdate();
	}
	
	@Override
	public int decreaseNonNullValue(Long userId, String columnName) {
		if (userId == null || columnName == null || columnName.isEmpty())
			return 0;
		
		String sql  = "UPDATE BC_USER_ATTR "
				+ "SET %s = %s - 1 "
				+ "WHERE USER_ID = :userId "
				+ "AND %s > 0 ";
		String sqlCmd = String.format(sql, columnName, columnName, columnName);
		SQLQuery sqlPostsQuery = getSession().createSQLQuery(sqlCmd);
		sqlPostsQuery.setParameter("userId", userId);
		return sqlPostsQuery.executeUpdate();
	}
	
	@Override
	public int updateNullValue(Long userId, String columnName, long value) {
		if (userId == null || columnName == null || columnName.isEmpty())
			return 0;
		String sql = "UPDATE BC_USER_ATTR "
				+ "SET %s = :value "
				+ "WHERE USER_ID = :userId "
				+ "AND %s IS NULL ";
		String sqlCmd = String.format(sql, columnName, columnName);
		SQLQuery sqlPostsQuery = getSession().createSQLQuery(sqlCmd);
		sqlPostsQuery.setParameter("userId", userId);
		sqlPostsQuery.setParameter("value", value);
		return sqlPostsQuery.executeUpdate();
	}
	
	@Override
    public int deleteByUserId(Long userId) {
	    String updatePostSqlCmd = "DELETE FROM BC_USER_ATTR WHERE USER_ID = :userId";
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(updatePostSqlCmd);
        sqlPostsQuery.setParameter("userId", userId);
        return sqlPostsQuery.executeUpdate();
	}
	
	@Override
    public int increaseNonNullValueBy(Long userId, String columnName, Long diff) {
	    if (userId == null || columnName == null || columnName.isEmpty())
            return 0;
        
        String sql  = "UPDATE BC_USER_ATTR "
                + "SET %s = %s + %d "
                + "WHERE USER_ID = :userId "
                + "AND %s IS NOT NULL ";
        String sqlCmd = String.format(sql, columnName, columnName, diff, columnName);
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sqlCmd);
        sqlPostsQuery.setParameter("userId", userId);
        return sqlPostsQuery.executeUpdate();
	}
	
	@Override
    public int decreaseNonNullValueBy(Long userId, String columnName, Long diff) {
	    if (userId == null || columnName == null || columnName.isEmpty() || diff == null)
            return 0;
        
        String sql  = "UPDATE BC_USER_ATTR "
                + "SET %s = %s - %d "
                + "WHERE USER_ID = :userId "
                + "AND %s > 0 ";
        String sqlCmd = String.format(sql, columnName, columnName, diff, columnName);
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sqlCmd);
        sqlPostsQuery.setParameter("userId", userId);
        return sqlPostsQuery.executeUpdate();
	}
	
	@Override
    public int setNonNullValue(Long userId, String columnName, Long value) {
	    if (userId == null || columnName == null || columnName.isEmpty())
            return 0;
        String sql = "UPDATE BC_USER_ATTR "
                + "SET %s = :value "
                + "WHERE USER_ID = :userId ";
        String sqlCmd = String.format(sql, columnName, columnName);
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sqlCmd);
        sqlPostsQuery.setParameter("userId", userId);
        sqlPostsQuery.setParameter("value", value);
        return sqlPostsQuery.executeUpdate();
	}
	
    @Override
    public void getFollowerCountPerUser(Date startDate, Date endTime, ScrollableResultsCallback callback) {
        if(callback == null)
            return;
        
        String querySql = "SELECT BC_USER_ATTR.USER_ID as userId, BC_USER.REGION as region, BC_USER.CREATED_TIME as createdTime, BC_USER_ATTR.FOLLOWER_COUNT as followerCount FROM BC_USER_ATTR ";
        querySql += "INNER JOIN BC_USER ON BC_USER.ID = BC_USER_ATTR.USER_ID ";
        querySql += "WHERE BC_USER_ATTR.FOLLOWER_COUNT IS NOT NULL ";
        if(startDate != null)
            querySql += "AND BC_USER.CREATED_TIME >= :startDate ";
        if(endTime != null)
            querySql += "AND BC_USER.CREATED_TIME < :endTime ";
        
        SQLQuery sqlQuery = getSession().createSQLQuery(querySql);
        if(startDate != null)
            sqlQuery.setParameter("startDate", startDate);
        if(endTime != null)
            sqlQuery.setParameter("endTime", endTime);
        sqlQuery.addScalar("userId", new LongType());
        sqlQuery.addScalar("region", new StringType());
        sqlQuery.addScalar("createdTime", new TimestampType());
        sqlQuery.addScalar("followerCount", new IntegerType());
        final ScrollableResults sr = sqlQuery.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
}
