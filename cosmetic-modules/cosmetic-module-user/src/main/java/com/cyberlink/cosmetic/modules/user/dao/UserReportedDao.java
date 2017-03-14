package com.cyberlink.cosmetic.modules.user.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.UserReported;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedReason;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedStatus;

public interface UserReportedDao extends GenericDao<UserReported, Long> {
	UserReported findByTargetAndReporter(Long targetId, Long reporterId,
			List<UserReportedStatus> status);

	PageResult<Long> findGroupTargetId(UserReportedStatus status,
			UserReportedReason reason, BlockLimit limit, String region);
	
	PageResult<Long> findGroupTargetId(UserReportedStatus status,
			UserReportedReason reason, Long reporterId , BlockLimit limit, String region);

	int countGroupTargetId(UserReportedStatus status,
			UserReportedReason reason, String region);
	
	int countGroupTargetId(UserReportedStatus status,
			UserReportedReason reason, String region, Long reporterId);

	List<UserReported> listByTargetId(Long targetId, UserReportedStatus status,
			UserReportedReason reason);

	List<UserReported> listByTargetId(Long targetId,
			List<UserReportedStatus> status, UserReportedReason reason);

	List<UserReported> listByTargetIds(List<Long> targetIds,
			UserReportedStatus status, UserReportedReason reason);

	PageResult<Long> findGroupTargetIdWithReviewerId(UserReportedStatus status,
			UserReportedReason reason, BlockLimit limit, String region);
	
	PageResult<Long> findGroupTargetIdWithReviewerId(UserReportedStatus status,
			UserReportedReason reason, Long reporterId, BlockLimit limit, String region);

	int countGroupTargetIdWithReviewerId(UserReportedStatus status,
			UserReportedReason reason, String region);
	
	int countGroupTargetIdWithReviewerId(UserReportedStatus status,
			UserReportedReason reason, Long reporterId, String region);

	List<UserReported> listGroupByTargetIds(List<Long> targetIds,
			UserReportedStatus status, UserReportedReason reason);
}