package com.cyberlink.cosmetic.modules.user.dao.hibernate;


import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.user.dao.FriendshipDao;
import com.cyberlink.cosmetic.modules.user.model.Friendship;

public class FriendshipDaoHibernate extends AbstractDaoCosmetic<Friendship, Long> implements FriendshipDao{

	@Override
	public Set<Long> findUserIdByAccountSource(Long userId,
			List<String> accountSource) {
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.in("accountSource", accountSource));
        dc.add(Restrictions.isNotNull("friendId"));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("friendId"));
        List<Long> list = findByCriteria(dc);
        return new HashSet<Long>(list);
	}

	@Override
	public void updateBySourceId(String sourceID, String accountSource,
			Long userId, String name) {
		name = name.replaceAll("'", "''");
		name = name.replaceAll("\\\\", "\\\\\\\\");

		String sql = "UPDATE `BC_FRIENDSHIP` SET `FRIEND_ID`="+ userId + ", " +
				"`DISPLAY_NAME`='" + name + "' " +
				"WHERE `ACCOUNT_SOURCE` ='" + accountSource + "' AND " + 
				"`SOURCE_ID`='" + sourceID + "'"; 
		
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
        return;		
	}

	@Override
	public void createByUserIdsAndSourceId(Collection<Long> userIds,
			String accountSource, String sourceId, String name, Long friendId) {
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("userId", userIds));
        dc.add(Restrictions.eq("accountSource", accountSource));
        dc.add(Restrictions.eq("sourceId", sourceId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("userId"));
        List<Long> list = findByCriteria(dc);
        userIds.removeAll(list);
        for (Long user : userIds) {
        	Friendship f = new Friendship();
        	f.setAccountSource(accountSource);
        	f.setDisplayName(name);
        	f.setShardId(user);
        	f.setSourceId(sourceId);
        	f.setFriendId(friendId);
        	f.setUserId(user);
        	create(f);
        }
        return;
		
	}

	@Override
	public String findName(Long userId, String accountSource) {
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("friendId", userId));
        dc.add(Restrictions.eq("accountSource", accountSource));
        dc.add(Restrictions.isNotNull("displayName"));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.addOrder(Order.desc("id"));
        dc.setProjection(Projections.property("displayName"));
        return uniqueResult(dc);
	}

	@Override
	public void createByUserIdAndSourceIds(Long userId, String accountSource,
			List<String> sourceIds, Map<String, String> nameMap,
			Map<String, Long> friendIdMap) {
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("accountSource", accountSource));
        dc.add(Restrictions.in("sourceId", sourceIds));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.property("sourceId"));
        List<Long> list = findByCriteria(dc);
        sourceIds.removeAll(list);
        for (String sourceId : sourceIds) {
        	Friendship f = new Friendship();
        	f.setAccountSource(accountSource);
        	if (nameMap.containsKey(sourceId))
        		f.setDisplayName(nameMap.get(sourceId));
        	f.setShardId(userId);
        	f.setSourceId(sourceId);
        	if (friendIdMap.containsKey(sourceId))
        		f.setFriendId(friendIdMap.get(sourceId));
        	f.setUserId(userId);
        	create(f);
        }
        return;		
	}

}
