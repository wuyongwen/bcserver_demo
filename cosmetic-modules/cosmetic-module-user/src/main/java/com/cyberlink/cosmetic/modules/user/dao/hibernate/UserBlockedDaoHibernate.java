package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.dao.UserBlockedDao;
import com.cyberlink.cosmetic.modules.user.model.UserBlocked;

public class UserBlockedDaoHibernate extends
		AbstractDaoCosmetic<UserBlocked, Long> implements UserBlockedDao {

	@Override
	public UserBlocked findByTargetAndCreater(Long targetId, Long createrId) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("targetId", targetId));
		dc.add(Restrictions.eq("userId", createrId));
		return uniqueResult(dc);
	}
	
	@Override
	public PageResult<Long> findByUserOrderByName(Long userId, BlockLimit blockLimit, Boolean withSize) {
		if (userId == null || blockLimit == null)
			return null;
		
		PageResult<Long> result = new PageResult<Long>();
		String sqlCmd = "LEFT(TARGET_NAME, 1) REGEXP '[a-zA-Z]' as isAlpha "
				+ "FROM BC_USER_BLOCKED "
				+ "USE INDEX (targetNameSort) "
				+ "WHERE USER_ID = :userId "
				+ "AND IS_DELETED = 0 ";
		String selectCmd = "SELECT DISTINCT TARGET_ID, " + sqlCmd
				+ "ORDER BY ISNULL(TARGET_NAME), isAlpha desc, TARGET_NAME "
				+ "LIMIT :offset, :limit";
		SQLQuery sqlSelectQuery = getSession().createSQLQuery(selectCmd);
		sqlSelectQuery.setParameter("userId", userId);
        sqlSelectQuery.setParameter("offset", blockLimit.getOffset());
        sqlSelectQuery.setParameter("limit", blockLimit.getSize());
        List<Object> list = sqlSelectQuery.list();
        List<Long> userIdlist = new ArrayList<Long>();
        for (Object obj : list) {
        	Object[] row = (Object[]) obj;
        	userIdlist.add(Long.valueOf(row[0].toString()));
        }
        result.setResults(userIdlist);
        
        if (withSize) {
			String countCmd = "SELECT COUNT(DISTINCT TARGET_ID), " + sqlCmd;
			SQLQuery sqlCountQuery = getSession().createSQLQuery(countCmd);
			sqlCountQuery.setParameter("userId", userId);
			Object[] row = (Object[]) sqlCountQuery.uniqueResult();
			Integer size = ((Number) row[0]).intValue();
			result.setTotalSize(size);
		}
		return result;
	}
}
