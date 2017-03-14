package com.cyberlink.cosmetic.modules.notify.dao.hibernate;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.notify.dao.NotifyDao;
import com.cyberlink.cosmetic.modules.notify.model.Notify;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;

public class NotifyDaoHibernate extends AbstractDaoCosmetic<Notify, Long> implements NotifyDao{
	private String regionOfFindIsGrouped = "com.cyberlink.cosmetic.modules.notify.model.Notify.query.findIsGrouped";
	private String regionOfFindNewNotifyByType = "com.cyberlink.cosmetic.modules.notify.model.Notify.query.findNewNotifyByType";
	
	@Override
	public PageResult<Notify> findNotifyByType(Long userId,
			List<String> notifyType, Long offset, Long limit) {
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("receiverId", userId));
        if (notifyType != null && notifyType.size() > 0) {
        	dc.add(Restrictions.in("notifyType", notifyType));
        }
        dc.addOrder(Order.desc("createdTime"));
        return findByCriteria(dc, offset, limit, null);
	}
	
	@Override
	public Boolean checkUnReadNotifyWithType(Long userId, List<String> notifyType) {
		if (userId == null)
			return false;
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("receiverId", userId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("isRead", Boolean.FALSE));
        if (notifyType != null && notifyType.size() > 0) {
        	dc.add(Restrictions.in("notifyType", notifyType));
        }
        dc.setProjection(Projections.property("id"));
        List<Long> result = findByCriteria(dc, new PageLimit(0, 1));
        
        if (result == null || result.isEmpty())
        	return false;
        else
        	return true;
	}

	@Override
	public Notify findNotifyGroup(Long receiverId, Long groupId, String notifyType, String groupType) {
		DetachedCriteria dc = createDetachedCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -24);
		dc.add(Restrictions.eq("receiverId", receiverId));
        dc.add(Restrictions.eq(groupType, groupId));
        dc.add(Restrictions.eq("notifyType", notifyType));
        dc.add(Restrictions.ne("groupNum", Long.valueOf(0)));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.ge("createdTime", cal.getTime()));
        return uniqueResult(dc);
	}
	
	@Override
	public Notify findNotifyGroupByFirstCreated(Long receiverId, Long groupId, String notifyType, String groupType) {
		DetachedCriteria dc = createDetachedCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -24);
		dc.add(Restrictions.eq("receiverId", receiverId));
        dc.add(Restrictions.eq(groupType, groupId));
        dc.add(Restrictions.eq("notifyType", notifyType));
        dc.add(Restrictions.ne("groupNum", Long.valueOf(0)));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.ge("firstCreated", cal.getTime()));
        return uniqueResult(dc);
	}

	@Override
	public Notify findNewNotifyByType(Long receiverId, List<String> notifyType) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("receiverId", receiverId));
        dc.add(Restrictions.in("notifyType", notifyType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.addOrder(Order.desc("id"));
        return uniqueResult(dc, regionOfFindNewNotifyByType);
	}

	@Override
	public Map<Long, Notify> findNotifyGroups(Set<Long> receiverId,
			Long groupId, String notifyType, String groupType) {

		Map<Long, Notify> resultMap = new HashMap<Long, Notify>();
		if (receiverId == null || receiverId.isEmpty())
			return resultMap;

		List<Long> receiverIds = new ArrayList<Long>(receiverId);
		int offset = 0;
		int limit = 100;
		do {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -24);

			DetachedCriteria dc = createDetachedCriteria();
			dc.add(Restrictions.in(
					"receiverId",
					receiverIds.subList(offset,
							Math.min(offset + limit, receiverIds.size()))));
			if (groupId != null)
				dc.add(Restrictions.eq(groupType, groupId));
			dc.add(Restrictions.eq("notifyType", notifyType));
			dc.add(Restrictions.ne("groupNum", Long.valueOf(0)));
			dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
			dc.add(Restrictions.ge("createdTime", cal.getTime()));
			List<Notify> list = findByCriteria(dc);
			for (Notify notify : list) {
				resultMap.put(notify.getReceiverId(), notify);
			}

			offset += limit;
			if (offset >= receiverIds.size())
				break;
		} while (true);
		return resultMap;
	}
	
	@Override
	public Map<Long, Notify> findNotifyGroupsByFirstCreated(
			Set<Long> receiverId, Long groupId, String notifyType,
			String groupType) {

		Map<Long, Notify> resultMap = new HashMap<Long, Notify>();
		if (receiverId == null || receiverId.isEmpty())
			return resultMap;

		List<Long> receiverIds = new ArrayList<Long>(receiverId);
		int offset = 0;
		int limit = 100;
		do {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -24);
			
			DetachedCriteria dc = createDetachedCriteria();
			dc.add(Restrictions.in(
					"receiverId",
					receiverIds.subList(offset,
							Math.min(offset + limit, receiverIds.size()))));
			if (groupId != null)
				dc.add(Restrictions.eq(groupType, groupId));
			dc.add(Restrictions.eq("notifyType", notifyType));
	        dc.add(Restrictions.ne("groupNum", Long.valueOf(0)));
	        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
	        dc.add(Restrictions.ge("firstCreated", cal.getTime()));
	        List<Notify> list = findByCriteria(dc);
	        for (Notify notify : list) {
				resultMap.put(notify.getReceiverId(), notify);
			}
			
			offset += limit;
			if (offset >= receiverIds.size())
				break;
		} while (true);
		return resultMap;
	}

	@Override
	public int setIsReaded(Long receiverId, Long time, String type) {
    	Timestamp stamp = new Timestamp(time);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql;
		if ("You".equalsIgnoreCase(type)) {
			sql = "UPDATE `BC_NOTIFY` SET `IS_READ`=1 WHERE `IS_READ`=0 AND `RECEIVER_ID`=" + receiverId.toString() + " AND `CREATED_TIME` <= '" +
    			dateFormat.format(stamp.getTime())+"' AND (";
			List<String> typeList = NotifyType.getYouType();
			for (int i = 0; i < typeList.size(); i++) {
				sql += ("`NOTIFY_TYPE` = '" + typeList.get(i));
				if (i < typeList.size() -1)
					sql += "' OR ";
				else
					sql += "') LIMIT 100";
			}
		} else {
			sql = "UPDATE `BC_NOTIFY` SET `IS_READ`=1 WHERE `IS_READ`=0 AND `RECEIVER_ID`=" + receiverId.toString() + " AND `CREATED_TIME` <= '" +
	    			dateFormat.format(stamp.getTime())+"' AND (";
			List<String> typeList = NotifyType.getFriendType();
			for (int i = 0; i < typeList.size(); i++) {
				sql += ("`NOTIFY_TYPE` = '" + typeList.get(i));
				if (i < typeList.size() -1)
					sql += "' OR ";
				else
					sql += "') LIMIT 100";
			}
		}
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        return sqlPostsQuery.executeUpdate();
	}

	@Override
	public Boolean findIsGrouped(Long receiverId, Long refId, Long senderId, String notifyType) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("receiverId", receiverId));
        dc.add(Restrictions.eq("senderId", senderId));
        dc.add(Restrictions.eq("refId", refId));
        //dc.add(Restrictions.eq("notifyType", NotifyType.CommentPost.toString()));
        dc.add(Restrictions.eq("notifyType", notifyType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc) != null;
	}

	@SuppressWarnings("unchecked")
    @Override
	public List<Notify> findUnSendNotify(Long limit, String notifyType) {
	    String sql = "SELECT * FROM BC_NOTIFY USE INDEX (findUnSendNotify, PRIMARY) "
	               + "WHERE SEND_TARGET IS NULL ";
	    if (NotifyType.CommentPost.toString().equalsIgnoreCase(notifyType)) {
	        sql += "AND NOTIFY_TYPE = '" + notifyType.toString() + "' ";
        } else{ 
            String notifyTypes = "";
            List<String> notifyTypeList = NotifyType.getAllWithoutCommentType();
            for(String nt : notifyTypeList) {
                if(notifyTypes.length() > 0)
                    notifyTypes += ", ";
                notifyTypes += "'" + nt + "'";
            }
            sql += "AND NOTIFY_TYPE IN (" + notifyTypes + ") ";
        } 
	       
	    sql += "AND GROUP_NUM <> 0 "
             + "AND IS_DELETED = 0 ";
	    
	    Boolean needSetCreateTime = false;
	    if (!NotifyType.CommentPost.toString().equalsIgnoreCase(notifyType)) {
	        needSetCreateTime = true;
            sql += "AND CREATED_TIME <= :createdTime "
                 + "ORDER BY ID DESC ";
	    }
	    
	    sql += "LIMIT :limit";
	    
	    SQLQuery sqlQuery = getSession().createSQLQuery(sql);
	    if(needSetCreateTime) {
	        Calendar cal = Calendar.getInstance();
            cal.add(Constants.getNotifyOffsetUnit(), Constants.getNotifyOffset());
	        sqlQuery.setParameter("createdTime", cal.getTime());
	    }
	    
	    sqlQuery.setParameter("limit", limit);
	    sqlQuery.addEntity(Notify.class);
        return sqlQuery.list();
	}
	
	@SuppressWarnings("unchecked")
    @Override
	public List<Notify> findUnSendNotifyByUser(Long limit,
			String notifyType, Set<Long> userIds) {
	    if(userIds == null || userIds.size() <= 0)
	        return new ArrayList<Notify>();
	    
	    String sql = "SELECT * FROM BC_NOTIFY USE INDEX (findUnSendNotifyByUser) "
                + "WHERE SEND_TARGET IS NULL ";
	    
	    if (NotifyType.CommentPost.toString().equalsIgnoreCase(notifyType)) {
	        sql += "AND NOTIFY_TYPE = '" + notifyType.toString() + "' ";
        } else if ("You".equalsIgnoreCase(notifyType)) {
            String notifyTypes = "";
            List<String> notifyTypeList = NotifyType.getAllWithoutCommentType();
            for(String nt : notifyTypeList) {
                if(notifyTypes.length() > 0)
                    notifyTypes += ", ";
                notifyTypes += "'" + nt + "'";
            }
            sql += "AND NOTIFY_TYPE IN (" + notifyTypes + ") ";
        }
	    
	    sql += "AND GROUP_NUM <> 0 "
             + "AND IS_DELETED = 0 "
             + "AND RECEIVER_ID IN (";
	    String receiverIdsStr = "";
	    for(Long rId : userIds) {
	        receiverIdsStr += rId.toString() + ", ";
	    }
	    if(receiverIdsStr.length() > 0)
	        receiverIdsStr = receiverIdsStr.substring(0, receiverIdsStr.length() - 2);
	    sql += receiverIdsStr + ") ORDER BY ID LIMIT :limit";
	    
	    SQLQuery sqlQuery = getSession().createSQLQuery(sql);
	    sqlQuery.setParameter("limit", limit);
        sqlQuery.addEntity(Notify.class);
        return sqlQuery.list();
	}	
	
	@Override
	public void updateUserSendTarget(Long userId, String target,
			List<String> notifyTypes) {
		String sql;
		sql = "UPDATE `BC_NOTIFY` SET `SEND_TARGET`='" + target +"' WHERE `SEND_TARGET` IS NULL AND `RECEIVER_ID`=" + userId.toString() + " AND (";
		
		for (int i = 0; i < notifyTypes.size(); i++) {
				sql += ("`NOTIFY_TYPE` = '" + notifyTypes.get(i));
				if (i < notifyTypes.size() -1)
					sql += "' OR ";
				else
					sql += "')";
		}
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
        return;		
	}

	@Override
	public Long findPrevGroupId(Notify notify) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("receiverId", notify.getReceiverId()));
        dc.add(Restrictions.eq("senderId", notify.getSenderId()));
        dc.add(Restrictions.eq("notifyType", notify.getNotifyType()));
        dc.add(Restrictions.ne("groupNum", Long.valueOf(0)));
        dc.add(Restrictions.lt("id", notify.getId()));
        dc.addOrder(Order.desc("id"));
        dc.setProjection(Projections.property("id"));
        return uniqueResult(dc);
	}	

	@Override
	public PageResult<Long> findRefIdByNotify(Notify notify, Long prevId, Long offset,
			Long limit) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("receiverId", notify.getReceiverId()));
        dc.add(Restrictions.eq("notifyType", notify.getNotifyType()));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("senderId", notify.getSenderId()));
        dc.add(Restrictions.le("id", notify.getId()));
        if (prevId != null)
        	dc.add(Restrictions.gt("id", prevId));
        dc.addOrder(Order.desc("id"));
        dc.setProjection(Projections.property("refId"));
        return findByCriteria(dc, offset, limit, null);
	}

	@Override
	public void realDelete() {
		String sql  = "DELETE FROM `BC_NOTIFY` WHERE `IS_DELETED`= '1'";	
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
        return;		
	}
	
    @SuppressWarnings("unchecked")
    @Override
	public Integer deleteOldNotify() {
		Integer count = 0;
		String toDeleteTypes = "";
		for(NotifyType nType : NotifyType.values()) {
		    if(nType.equals(NotifyType.FreeSample) || nType.equals(NotifyType.Message))
		        continue;
		    if(toDeleteTypes.length() > 0)
		        toDeleteTypes += ", ";
		    toDeleteTypes += "'" + nType.toString() + "'";
		}
        String queryListSql  = "SELECT ID as nid FROM `BC_NOTIFY` USE INDEX (deleteOldNotify) "
                    + "WHERE `CREATED_TIME` < DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY) AND  `NOTIFY_TYPE` IN ("
                    + toDeleteTypes
                    + ") ORDER BY `CREATED_TIME` LIMIT 1000 ";	
        SQLQuery queryListSqlPostsQuery = getSession().createSQLQuery(queryListSql);
        queryListSqlPostsQuery.addScalar("nid", new LongType());
        List<Long> notifyIds = queryListSqlPostsQuery.list();

        if(notifyIds == null || notifyIds.size() <= 0)
            return 0;
        
        String deleteSql  = "DELETE FROM BC_NOTIFY WHERE ID IN :notifyIds ";  
        SQLQuery deleteSqlQuery = getSession().createSQLQuery(deleteSql);
        deleteSqlQuery.setParameterList("notifyIds", notifyIds);
        count = deleteSqlQuery.executeUpdate();              
        
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		return count;
	}
	
	@Override
	public void deleteFreeSampleNotify(Long refId) {
		if (refId == null)
			return;
		String sql  = "DELETE FROM `BC_NOTIFY` "
				+ "WHERE `NOTIFY_TYPE` = 'FreeSample' "
				+ "AND `REF_ID`= :refId";
		SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
		sqlPostsQuery.setParameter("refId", refId);
        sqlPostsQuery.executeUpdate();	   
	}

	@Override
	public void updateSenderAvatar(Long userId, Long avatarId) {
		Integer count = 0;
		do {
		    String format  = "UPDATE `BC_NOTIFY` SET `SENDER_AVATAR` = '%s'  WHERE `SENDER_ID`= '%s' AND `GROUP_NUM` != '0' AND `SENDER_AVATAR` IS NULL ORDER BY `ID` LIMIT 1000";	
		    String sql = String.format(format, avatarId.toString(), userId.toString());			
		    SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
		    count = sqlPostsQuery.executeUpdate();	        	
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
		} while(count > 0);    	
        return;				
	}

	@Override
	public void updateByDeleteComment(Long postId, Long receiverId, Long senderId, String text) {
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.ne("groupNum", Long.valueOf(0)));
    	dc.add(Restrictions.eq("notifyType", NotifyType.CommentPost.toString()));
    	dc.add(Restrictions.eq("receiverId", receiverId));
    	dc.add(Restrictions.eq("senderId", senderId));
    	dc.add(Restrictions.eq("refId", postId));
    	dc.addOrder(Order.desc("id"));
    	List<Notify> notifyList = findByCriteria(dc);
    	Long id = null;
    	
    	for (Notify notify : notifyList) {
    		List<String> commentList = notify.getComments();
    		if (commentList.size() > 0 && commentList.get(0)!= null && commentList.get(0).equals(text)) {
    			id = notify.getId();
    			break;
    		}
    	}
    	if (id == null) {
    		if (notifyList.size() > 0) {
    			id = notifyList.get(0).getId();
    		} else {
    			return;
    		}
    	}
    	String format  = "DELETE FROM `BC_NOTIFY` WHERE `ID`= '%s'";
		String sql = String.format(format, id.toString());
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
        return;		
	}

	@Override
	public void updateByDeletePost(Long postId) {
    	String format  = "DELETE FROM `BC_NOTIFY` WHERE `NOTIFY_TYPE` = 'CommentPost' AND `REF_ID` = '%s'";
		String sql = String.format(format, postId.toString());
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
        return;				
	}

	@Override
	public void deleteById(Long notifyId) {
    	String format  = "DELETE FROM `BC_NOTIFY` WHERE `ID`= '%s'";
		String sql = String.format(format, notifyId.toString());
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
        return;		
	}

	@Override
	public void batchInsert(List<Notify> list) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			int i = 0;
			for (Notify n : list) {
				session.save(n);
				i++;
				if (i % 50 == 0) {
					session.flush();
					session.clear();
				}
				if (i % 200 == 0) {
					try {
						Thread.sleep(500);
					} catch (Exception e) {
					}
				}
			}
			tx.commit();
		} catch (RuntimeException e) {
			try {
				tx.rollback();
			} catch (RuntimeException rbe) {
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	@Override
	public Boolean batchDelete(List<Long> list) {
		try{
			String sql = "DELETE FROM `BC_NOTIFY` WHERE `ID` IN (";
			SQLQuery sqlPostsQuery = null;
			for (int i = 0; i < list.size(); i++) {
				sql += list.get(i).toString();
				if ( (i+1) % 200 == 0 && i < list.size() -1) {
					sql += ")";
					sqlPostsQuery = getSession().createSQLQuery(sql);
			        sqlPostsQuery.executeUpdate();			
			        sql = "DELETE FROM `BC_NOTIFY` WHERE `ID` IN (";
					try {
						Thread.sleep(500);
					} catch (Exception e) {
					}    		
				} else if (i < list.size() -1) {
					sql += ", ";
				} else {
					sql += ")";
				}
			}
			sqlPostsQuery = getSession().createSQLQuery(sql);
			sqlPostsQuery.executeUpdate();
		}catch(RuntimeException e){
			logger.info("NotifyDaoHibernate batchDelete occur " + list + " RuntimeException " + e);
			return Boolean.FALSE;
		}catch(Exception e){
			logger.info("NotifyDaoHibernate batchDelete  occur " + list + " Exception ", e);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
}
