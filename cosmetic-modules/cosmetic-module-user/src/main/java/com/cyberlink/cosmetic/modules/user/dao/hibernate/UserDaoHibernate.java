package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.GenderType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserStatus;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.modules.user.model.User.LookSource;
import com.cyberlink.cosmetic.modules.user.model.User.LookStatus;

public class UserDaoHibernate extends AbstractDaoCosmetic<User, Long> implements UserDao{
	private String regionOfFindByUserType = "com.cyberlink.cosmetic.modules.user.model.User.query.findByUserType";
	private String regionOfFindIdByUserType = "com.cyberlink.cosmetic.modules.user.model.User.query.findIdByUserType";
	private String regionOfFindIdByUserTypeWithoutPaging = "com.cyberlink.cosmetic.modules.user.model.User.query.findIdByUserType.withoutPaging";
	@Override
    public List<User> findByIds(Long... userIds) {
        if (userIds == null || userIds.length == 0) {
            return Collections.emptyList();
        }

        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("id", userIds));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }
	
	@Override
    public List<User> findByIdsWithOrder(Long... userIds) {
        if (userIds == null || userIds.length == 0) {
            return Collections.emptyList();
        }

        String sqlCmd = "SELECT * FROM BC_USER "
        		+ "WHERE ID IN (:userIds) "
        		+ "AND IS_DELETED = 0 "
        		+ "ORDER BY FIELD (ID, :userIds) ";	
		
		SQLQuery sqlQuery = getSession().createSQLQuery(sqlCmd);
		sqlQuery.addEntity(User.class);
		sqlQuery.setParameterList("userIds", userIds);
		return sqlQuery.list();
    }

	@Override
	public PageResult<User> findByUserType(List<UserType> userType, List<String> locale,
			Long offset, Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("userType", userType.toArray(new UserType[userType.size()])));
        if (locale != null)
        	dc.add(Restrictions.in("region", locale.toArray(new String[locale.size()])));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.or(Restrictions.eq("userStatus", UserStatus.Published), Restrictions.isNull("userStatus")));
		return findByCriteria(dc, offset, limit, regionOfFindByUserType);
	}
	
	@Override
	public PageResult<User> findByUserTypeAndLookSource(List<UserType> userType, List<LookSource> lookSource, 
			List<String> locale, Long offset, Long limit) {
		if (lookSource == null || lookSource.isEmpty())
			return new PageResult<User>();
		
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("lookStatus", LookStatus.PUBLISHED));
        dc.add(Restrictions.in("lookSource", lookSource));
        if (userType != null && !userType.isEmpty())
        	dc.add(Restrictions.in("userType", userType.toArray(new UserType[userType.size()])));
        if (locale != null)
        	dc.add(Restrictions.in("region", locale.toArray(new String[locale.size()])));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.or(Restrictions.eq("userStatus", UserStatus.Published), Restrictions.isNull("userStatus")));
		return findByCriteria(dc, offset, limit, regionOfFindByUserType);
	}

	@Override
	public PageResult<Long> findIdByUserType(UserType userType, String locale,
			Long offset, Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userType", userType));
        if (locale != null)
        	dc.add(Restrictions.eq("region", locale));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("id"));
		return findByCriteria(dc, offset, limit, regionOfFindIdByUserType);
	}

	@Override
    public Map<Long, User> findUserMap(Set<Long> userIds) {
	    Map<Long, User> result = new HashMap<Long, User>();
        if(userIds == null || userIds.size() <= 0)
            return result;
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.in("id", userIds));
        List<User> users = findByCriteria(dc);
        for(User user : users)
            result.put(user.getId(), user);
        return result;
    }
	
	@Override
	public PageResult<User> findUserByParameters(Long id, GenderType gender,
			UserType userType, String locale, Date startTime, Date endTime,
			Long offset, Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        if (id != null) {
        	dc.add(Restrictions.eq("id", id));
        } else {
        	if (userType != null)
        		dc.add(Restrictions.eq("userType", userType));
        	if (locale != null)
        		dc.add(Restrictions.eq("region", locale));
        	if (gender != null)
        		dc.add(Restrictions.eq("gender", gender));
        	if (startTime != null)
        		dc.add(Restrictions.ge("createdTime", startTime));
        	if (endTime != null)
        		dc.add(Restrictions.le("createdTime", endTime));

        }
        
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return findByCriteria(dc, offset, limit, null);
	}

    @Override
    public List<Long> findIdByUserType(UserType userType, List<String> locale) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userType", userType));
        if (locale != null)
            dc.add(Restrictions.in("region", locale.toArray(new String[locale.size()])));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.or(Restrictions.eq("userStatus", UserStatus.Published), Restrictions.isNull("userStatus")));
        dc.setProjection(Projections.property("id"));
        return findByCriteria(dc, regionOfFindIdByUserTypeWithoutPaging);
    }
    
    @Override
	public List<Long> findIdByUserTypeWithoutStatus(List<UserType> userType, List<String> locale) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("userType", userType.toArray(new UserType[userType.size()])));
        if (locale != null)
        	dc.add(Restrictions.in("region", locale.toArray(new String[locale.size()])));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("id"));
		return findByCriteria(dc, regionOfFindIdByUserTypeWithoutPaging);
	}

	@Override
	public PageResult<Long> findAllUserId(Long offset, Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("id"));
        return findByCriteria(dc, offset, limit, null);
	}

	@Override
	public PageResult<User> findLastModifiedUser(Date startTime, Date endTime,
			Long offset, Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        if (startTime != null)
        	dc.add(Restrictions.ge("lastModified", startTime));
        if (endTime != null)
        	dc.add(Restrictions.le("lastModified", endTime));
        
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return findByCriteria(dc, offset, limit, null);
	}

	@Override
	public PageResult<User> findUserWithoutAvatarUrl(Long limit) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.isNull("avatarLink"));
        dc.add(Restrictions.isNotNull("avatarId"));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        //dc.setProjection(Projections.property("id"));
        return findByCriteria(dc, Long.valueOf(0), limit, null);
	}
	
	@Override
	public void doWithAllUser(String locale, BlockLimit blockLimit, ScrollableResultsCallback callback) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("region", locale));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("id"));
        for(String ord : blockLimit.getOrderBy().keySet()) {
            Boolean asc = blockLimit.getOrderBy().get(ord);
            if(asc)
                dc.addOrder(Order.asc(ord));
            else
                dc.addOrder(Order.desc(ord));
        }
        final Criteria c  = dc.getExecutableCriteria(getSession());
        c.setFirstResult(blockLimit.getOffset());
        c.setMaxResults(blockLimit.getSize());
        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageResult<User> findUserByParameters(Long id, GenderType gender, UserType userType, String locale,
			Date startTime, Date endTime, Long offset, Long limit, Long access) {
        if (id == null && access != null){
        	String initSql = "";
        	SQLQuery sqlPostsQuery;
    		String conditionSQL = "";
    		String limitSQL = "LIMIT :offset ,:limit";
        	if (userType != null)
        		conditionSQL += " BU.USER_TYPE = :userType AND ";
        	if (locale != null)
        		conditionSQL += " BU.REGION = :locale AND ";
        	if (gender != null)
        		conditionSQL += " BU.GENDER = :gender AND ";
        	if (startTime != null)
        		conditionSQL += " BU.CREATED_TIME >= :startTime AND ";
        	if (endTime != null)
        		conditionSQL += " BU.LAST_MODIFIED =< :endTime AND ";
        	if(access == 0){
        		initSql = "FROM BC_USER BU JOIN BC_ATTR BUA ON BU.ID = BUA.REF_ID WHERE " + conditionSQL
            		+ " BU.IS_DELETED = 0 AND BUA.IS_DELETED = 0 AND BUA.ATTR_NAME = 'Access'  AND BUA.ATTR_VALUE = 'Admin' ";
        	}else {
        		initSql = "FROM BC_USER BU JOIN BC_ATTR BUA ON BU.ID = BUA.REF_ID WHERE " + conditionSQL 
            		+ "BU.IS_DELETED = 0 AND BUA.IS_DELETED = 0 AND BUA.ATTR_NAME = 'AccessMap' "
            		+ "AND ATTR_VALUE IS NOT NULL AND ((CONV(HEX(UNHEX(BUA.ATTR_VALUE)), 16, 10) & :access ) > 0) ";
        	}
        	String finalSql = "SELECT BU.* " + initSql + limitSQL;
        	sqlPostsQuery = getSession().createSQLQuery(finalSql);
        	finalSql = "SELECT COUNT(*) " + initSql ;
            SQLQuery sqlSizeQuery = getSession().createSQLQuery(finalSql);
        	
        	if (userType != null){
        		sqlPostsQuery.setParameter("userType", userType.toString());
        		sqlSizeQuery.setParameter("userType", userType.toString());
        	}
        	if (locale != null){
        		sqlPostsQuery.setParameter("locale", locale);
        		sqlSizeQuery.setParameter("locale", locale);
        	}
        	if (gender != null){
        		sqlPostsQuery.setParameter("gender", gender.toString());
        		sqlSizeQuery.setParameter("gender", gender.toString());
        	}
        	if (startTime != null){
        		sqlPostsQuery.setParameter("startTime", startTime);
        		sqlSizeQuery.setParameter("startTime", startTime);
        	}
        	if (endTime != null){
        		sqlPostsQuery.setParameter("endTime", endTime);
        		sqlSizeQuery.setParameter("endTime", endTime);
        	}
        	if (access != null && access != 0){
        		sqlPostsQuery.setParameter("access", access);
        		sqlSizeQuery.setParameter("access", access);
        	}
        	sqlPostsQuery.setParameter("offset", offset);
        	sqlPostsQuery.setParameter("limit", limit);
            sqlPostsQuery.addEntity("user", User.class);
            List<User> users = sqlPostsQuery.list();
            PageResult<User> result = new PageResult<User>();
            
            Integer size = ((Number)sqlSizeQuery.uniqueResult()).intValue();
            
            result.setTotalSize(size);
            result.setResults(users);
            return result;
        }
        else {
        	return findUserByParameters(id,gender,userType,locale,startTime,endTime,offset,limit);
        }
	}
	
	@Override
	public Long verifyUniqueId(String uniqueId) {
		if (uniqueId == null || uniqueId.isEmpty())
			return null;
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("uniqueId", uniqueId));
		dc.setProjection(Projections.property("id"));
		Long existId = uniqueResult(dc);
		return existId;
	}
}
