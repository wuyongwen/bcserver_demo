package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.dao.UserReportedDao;
import com.cyberlink.cosmetic.modules.user.model.UserReported;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedReason;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedStatus;

public class UserReportedDaoHibernate extends
		AbstractDaoCosmetic<UserReported, Long> implements UserReportedDao {

	@Override
	public UserReported findByTargetAndReporter(Long targetId, Long reporterId,
			List<UserReportedStatus> status) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("targetId", targetId));
		dc.add(Restrictions.eq("reporterId", reporterId));
		if (status != null && !status.isEmpty())
			dc.add(Restrictions.in("status", status));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return uniqueResult(dc);
	}

	@Override
	public PageResult<Long> findGroupTargetId(UserReportedStatus status,
			UserReportedReason reason, BlockLimit limit, String region) {
		return findGroupTargetId(status, reason, null , limit, region);
	}
	
	@Override
	public PageResult<Long> findGroupTargetId(UserReportedStatus status,
			UserReportedReason reason, Long reporterId , BlockLimit limit, String region) {
		DetachedCriteria dc = createDetachedCriteria();
		if (status != null)
			dc.add(Restrictions.eq("status", status));
		if (reason != null)
			dc.add(Restrictions.eq("reason", reason));
		if (reporterId != null)
			dc.add(Restrictions.eq("reporterId", reporterId));
		if (region != null) {
			dc.createAlias("target", "target");
			dc.add(Restrictions.eq("target.region", region));
		}
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.projectionList().add(
				Projections.groupProperty("targetId")));

		return blockQuery(dc, limit);
	}

	@Override
	public int countGroupTargetId(UserReportedStatus status,
			UserReportedReason reason, String region) {
		return countGroupTargetId(status, reason,  region, null);
	}
	
	@Override
	public int countGroupTargetId(UserReportedStatus status,
			UserReportedReason reason, String region, Long reporterId) {
		String sqlCmd = "SELECT COUNT( * ) " + "FROM ( "
				+ "SELECT COUNT( * ) AS count " + "FROM BC_USER_REPORTED ";
		if (region != null)
			sqlCmd += "INNER JOIN BC_USER ON BC_USER_REPORTED.TARGET_ID = BC_USER.ID "
					+ "AND BC_USER.REGION = :region "
					+ "AND BC_USER.IS_DELETED = 0 ";
		sqlCmd += "WHERE BC_USER_REPORTED.IS_DELETED = 0 ";
		if (status != null)
			sqlCmd += "AND BC_USER_REPORTED.STATUS = :status ";
		if (reason != null)
			sqlCmd += "AND BC_USER_REPORTED.REASON = :reason ";
		if (reporterId != null)
			sqlCmd += "AND BC_USER_REPORTED.REPORTER_ID = :reporterId ";
		sqlCmd += "GROUP BY TARGET_ID " + ")t";
		SQLQuery sqlQuery = getSession().createSQLQuery(sqlCmd);
		if (region != null)
			sqlQuery.setParameter("region", region);
		if (status != null)
			sqlQuery.setParameter("status", status.toString());
		if (reason != null)
			sqlQuery.setParameter("reason", reason.toString());
		if (reporterId != null)
			sqlQuery.setParameter("reporterId", reporterId);

		Integer count = ((Number) sqlQuery.uniqueResult()).intValue();

		return count;
	}

	@Override
	public List<UserReported> listByTargetId(Long targetId,
			UserReportedStatus status, UserReportedReason reason) {
		if (targetId == null)
			return null;

		DetachedCriteria dc = createDetachedCriteria();
		if (status != null)
			dc.add(Restrictions.eq("status", status));
		if (reason != null)
			dc.add(Restrictions.eq("reason", reason));
		dc.add(Restrictions.eq("targetId", targetId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

		return findByCriteria(dc);
	}

	@Override
	public List<UserReported> listByTargetId(Long targetId,
			List<UserReportedStatus> status, UserReportedReason reason) {
		if (targetId == null)
			return null;

		DetachedCriteria dc = createDetachedCriteria();
		if (status != null && !status.isEmpty())
			dc.add(Restrictions.in("status", status));
		if (reason != null)
			dc.add(Restrictions.eq("reason", reason));
		dc.add(Restrictions.eq("targetId", targetId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

		return findByCriteria(dc);
	}

	@Override
	public List<UserReported> listByTargetIds(List<Long> targetIds,
			UserReportedStatus status, UserReportedReason reason) {
		if (targetIds == null || targetIds.isEmpty())
			return null;

		DetachedCriteria dc = createDetachedCriteria();
		if (status != null)
			dc.add(Restrictions.eq("status", status));
		if (reason != null)
			dc.add(Restrictions.eq("reason", reason));
		dc.add(Restrictions.in("targetId", targetIds));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

		return findByCriteria(dc);
	}

	@Override
	public PageResult<Long> findGroupTargetIdWithReviewerId(
			UserReportedStatus status, UserReportedReason reason,
			BlockLimit limit, String region) {
		return findGroupTargetIdWithReviewerId(status, reason, null, limit,  region);
	}
	
	@Override
	public PageResult<Long> findGroupTargetIdWithReviewerId(
			UserReportedStatus status, UserReportedReason reason,
			Long reporterId,BlockLimit limit, String region) {
		DetachedCriteria dc = createDetachedCriteria();
		if (status != null)
			dc.add(Restrictions.eq("status", status));
		if (reason != null)
			dc.add(Restrictions.eq("reason", reason));
		if (reporterId != null)
			dc.add(Restrictions.eq("reporterId", reporterId));
		if (region != null) {
			dc.createAlias("target", "target");
			dc.add(Restrictions.eq("target.region", region));
		}
		dc.add(Restrictions.isNotNull("reviewerId"));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.projectionList().add(
				Projections.groupProperty("targetId")));

		return blockQuery(dc, limit);
	}

	@Override
	public int countGroupTargetIdWithReviewerId(UserReportedStatus status,
			UserReportedReason reason, String region) {
		return countGroupTargetIdWithReviewerId(status, reason, null, region);
	}
	
	@Override
	public int countGroupTargetIdWithReviewerId(UserReportedStatus status,
			UserReportedReason reason, Long reporterId , String region) {
		String sqlCmd = "SELECT COUNT( * ) " + "FROM ( "
				+ "SELECT COUNT( * ) AS count " + "FROM BC_USER_REPORTED ";
		if (region != null)
			sqlCmd += "INNER JOIN BC_USER ON BC_USER_REPORTED.TARGET_ID = BC_USER.ID "
					+ "AND BC_USER.REGION = :region "
					+ "AND BC_USER.IS_DELETED = 0 ";
		sqlCmd += "WHERE BC_USER_REPORTED.IS_DELETED = 0 "
				+ "AND BC_USER_REPORTED.REVIEWER_ID IS NOT NULL ";
		if (status != null)
			sqlCmd += "AND BC_USER_REPORTED.STATUS = :status ";
		if (reason != null)
			sqlCmd += "AND BC_USER_REPORTED.REASON = :reason ";
		if (reporterId != null)
			sqlCmd += "AND BC_USER_REPORTED.REPORTER_ID = :reporterId ";
		sqlCmd += "GROUP BY TARGET_ID " + ")t";
		SQLQuery sqlQuery = getSession().createSQLQuery(sqlCmd);
		if (region != null)
			sqlQuery.setParameter("region", region);
		if (status != null)
			sqlQuery.setParameter("status", status.toString());
		if (reason != null)
			sqlQuery.setParameter("reason", reason.toString());
		if (reporterId != null)
			sqlQuery.setParameter("reporterId", reporterId);

		Integer count = ((Number) sqlQuery.uniqueResult()).intValue();

		return count;
	}

	@Override
	public List<UserReported> listGroupByTargetIds(List<Long> targetIds,
			UserReportedStatus status, UserReportedReason reason) {
		if (targetIds == null || targetIds.isEmpty())
			return null;

		String sqlCmd = "SELECT * " + "FROM BC_USER_REPORTED ";
		sqlCmd += "WHERE IS_DELETED = 0 " + "AND TARGET_ID IN (:targetIds) ";
		if (status != null)
			sqlCmd += "AND STATUS = :status ";
		if (reason != null)
			sqlCmd += "AND REASON = :reason ";
		sqlCmd += "GROUP BY TARGET_ID, REVIEWER_ID ";
		SQLQuery sqlQuery = getSession().createSQLQuery(sqlCmd);
		sqlQuery.addEntity(UserReported.class);
		sqlQuery.setParameterList("targetIds", targetIds);
		if (status != null)
			sqlQuery.setParameter("status", status.toString());
		if (reason != null)
			sqlQuery.setParameter("reason", reason.toString());

		return sqlQuery.list();
	}
}