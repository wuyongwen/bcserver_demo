package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.model.AccountMailStatus;
import com.cyberlink.cosmetic.modules.user.model.AccountSourceType;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.User;

public class AccountDaoHibernate extends AbstractDaoCosmetic<Account, Long> implements AccountDao{
    private String regionOfFindBySourceAndReference = "com.cyberlink.cosmetic.modules.user.model.BCAccount.query.findBySourceAndReference";

    @Override
    public Account findBySourceAndReference(AccountSourceType source,
            String ... reference) {
    	if (reference == null || reference.length == 0) {
    		return null;
    	}

    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("accountSource", source));
        dc.add(Restrictions.in("account", reference));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.addOrder(Order.desc("id"));
        return uniqueResult(dc);
    }

	@Override
	public List<Account> findByUserId(Long userId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(dc);
	}

	@Override
	public PageResult<User> findUserByEmail(String email, Long offset, Long limit) {
    	DetachedCriteria dc = createDetachedCriteria();
    	dc.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        dc.add(Restrictions.or(
                Restrictions.eq("account", email),
                Restrictions.eq("email", email)));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("user"));
        return findByCriteria(dc, offset, limit, null);
	}

	@Override
	public Map<String, Long> findUserIdBySourceAndReference(AccountSourceType source,
			List<String> reference) {
		Map<String, Long> resultMap = new HashMap<String, Long>();
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("accountSource", source));
        dc.add(Restrictions.in("account", reference));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("account"))
                .add(Projections.property("userId")));
        List<Object> objs = findByCriteria(dc);
        for (Object obj : objs) {
            Object[] row = (Object[]) obj;
            String refId = (String) row[0];
            Long userId = (Long) row[1];
            resultMap.put(refId, userId);
        }
        return resultMap;
	}
	
	@Override
	public List<Account> findByEmail(String email) {
		if (email == null || email.isEmpty())
			return null;
		
		String sqlCmd = "SELECT * FROM BC_USER_ACCOUNT "
				+ "WHERE EMAIL = :email "
				+ "AND USER_ID IS NOT NULL "
				+ "AND IS_DELETED = 0 "
				+ "UNION "
				+ "SELECT * FROM BC_USER_ACCOUNT "
				+ "WHERE ACCOUNT_SOURCE = 'Email' "
				+ "AND ACCOUNT_REFERENCE = :email "
				+ "AND USER_ID IS NOT NULL "
				+ "AND IS_DELETED = 0 ";
		
		SQLQuery sqlQuery = getSession().createSQLQuery(sqlCmd);
		sqlQuery.addEntity(Account.class);
		sqlQuery.setParameter("email", email);
		return sqlQuery.list();
	}
	
	@Override
	public Integer updateStatus(String email, AccountMailStatus status) {
		if (email == null || email.isEmpty())
			return 0;
		String sql  = "UPDATE BC_USER_ACCOUNT "
				+ "SET IS_VERIFIED = 1 ";
		if (status != null)
			sql	+= ", MAIL_STATUS = :status ";
		sql	+= "WHERE EMAIL = :email ";
		
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		if (status != null)
			sqlQuery.setParameter("status", status.toString());
		sqlQuery.setParameter("email", email);
		return sqlQuery.executeUpdate();
	}
	
	@Override
	public Integer updateStatusByEmail(Collection<String> email, AccountMailStatus status) {
		if (email == null || email.isEmpty() || status == null)
			return 0;
		String sql = "UPDATE BC_USER_ACCOUNT SET IS_VERIFIED = 1, MAIL_STATUS = :status WHERE EMAIL IN ( :email ) "
				+ "OR ( ACCOUNT_SOURCE = 'Email' AND ACCOUNT_REFERENCE IN ( :email ))";
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		sqlQuery.setParameter("status", status.toString());
		sqlQuery.setParameterList("email", email);
		return sqlQuery.executeUpdate();
	}

	@Override
	public Account findByIdWithNotDelete(Long id) {
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("id", id));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
	}

}
