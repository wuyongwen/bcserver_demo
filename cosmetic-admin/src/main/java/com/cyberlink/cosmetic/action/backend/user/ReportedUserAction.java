package com.cyberlink.cosmetic.action.backend.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.mail.service.MailImpersonationInvestigationService;
import com.cyberlink.cosmetic.modules.mail.service.MailImpersonationSuspensionService;
import com.cyberlink.cosmetic.modules.mail.service.MailSuspensionService;
import com.cyberlink.cosmetic.modules.post.service.DeleteUserService;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.BlockDeviceDao;
import com.cyberlink.cosmetic.modules.user.dao.DeviceDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.dao.UserReportedDao;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.BlockDevice;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserReported;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedReason;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedStatus;
import com.cyberlink.cosmetic.modules.user.result.UserApiResult;
import com.cyberlink.cosmetic.modules.user.service.UserService;

@UrlBinding("/user/reportedUser.action")
public class ReportedUserAction extends AbstractAction {
	@SpringBean("user.UserReportedDao")
	private UserReportedDao userReportedDao;

	@SpringBean("user.UserDao")
	private UserDao userDao;

	@SpringBean("user.AccountDao")
	private AccountDao accountDao;

	@SpringBean("user.DeviceDao")
	private DeviceDao deviceDao;

	@SpringBean("user.BlockDeviceDao")
	private BlockDeviceDao blockDao;

	@SpringBean("delete.DeleteUserService")
	private DeleteUserService deleteUserService;
	
	@SpringBean("user.userService")
	private UserService userService;

	@SpringBean("core.jdbcTemplate")
	private TransactionTemplate transactionTemplate;

	@SpringBean("mail.mailImpersonationInvestigationService")
	private MailImpersonationInvestigationService mailImpersonationInvestigationService;

	@SpringBean("mail.mailImpersonationSuspensionService")
	private MailImpersonationSuspensionService mailImpersonationSuspensionService;

	@SpringBean("mail.mailSuspensionService")
	private MailSuspensionService mailSuspensionService;

	private PageResult<ReportedUser> pageResult = new PageResult<ReportedUser>();
	private Long targetId;
	private UserReportedReason selReason = UserReportedReason.GRAPHIC;
	private UserReportedStatus selStatus = UserReportedStatus.REPORTED;
	private String selRegion = null;
	private Boolean isBlockDevice = Boolean.FALSE;
	private int pageSize = 20;
	private Long reporterId;

	public class ReportedUser {
		private User targetUser;
		private List<UserReported> userReporteds = new ArrayList<UserReported>();

		public ReportedUser(User targetUser) {
			this.targetUser = targetUser;
		}

		public void addReported(UserReported reported) {
			if (reported != null)
				userReporteds.add(reported);
		}

		public User getUser() {
			return targetUser;
		}

		public String getReporter() {
			String str = "";
			try {
				int idx = 0;
				for (UserReported userReported : userReporteds) {
					if (!UserReportedStatus.REPORTED.equals(userReported
							.getStatus()))
						return "";
					User reporter = userReported.getReporter();
					str += String.format("%s(%d)", reporter.getDisplayName(),
							reporter.getId());
					idx++;
					if (idx < userReporteds.size())
						str += ", ";
				}
				return str;
			} catch (Exception e) {
				return "";
			}
		}

		public String getReviewer() {
			String str = "";
			try {
				int idx = 0;
				for (UserReported userReported : userReporteds) {
					if (UserReportedStatus.REPORTED.equals(userReported
							.getStatus()))
						return "";
					User reviewer = userReported.getReviewer();
					str += String.format("%s(%d)", reviewer.getDisplayName(),
							reviewer.getId());
					idx++;
					if (idx < userReporteds.size())
						str += ", ";
				}
				return str;
			} catch (Exception e) {
				return "";
			}
		}
	}

	@DefaultHandler
	public Resolution list() {
		if (!getCurrentUserAdmin() && !getAccessControl().getUserManagerAccess()
				&& !getAccessControl().getReportManagerAccess()) {
			return new StreamingResolution("text/html", "Need to login");
		}

		PageLimit page = getPageLimit("row");

		int totalSize = 0;
		List<UserReported> reporteds = null;
		if (UserReportedStatus.REPORTED.equals(selStatus)) {
			totalSize = userReportedDao.countGroupTargetId(selStatus,
					selReason, selRegion, reporterId);
			if (totalSize == 0)
				return forward();
			PageResult<Long> targetResult = userReportedDao.findGroupTargetId(
					selStatus, selReason, reporterId, new BlockLimit(
							(page.getPageIndex() - 1) * pageSize, pageSize),
					selRegion);
			List<Long> targetIds = targetResult.getResults();
			if (targetIds == null || targetIds.isEmpty())
				return forward();
			reporteds = userReportedDao.listByTargetIds(targetIds, selStatus,
					selReason);
		} else {
			totalSize = userReportedDao.countGroupTargetIdWithReviewerId(
					selStatus, selReason, reporterId, selRegion);
			if (totalSize == 0)
				return forward();
			PageResult<Long> targetResult = userReportedDao
					.findGroupTargetIdWithReviewerId(selStatus, selReason, reporterId,
							new BlockLimit(
									(page.getPageIndex() - 1) * pageSize,
									pageSize), selRegion);
			List<Long> targetIds = targetResult.getResults();
			if (targetIds == null || targetIds.isEmpty())
				return forward();
			reporteds = userReportedDao.listByTargetIds(targetIds, selStatus,
					selReason);
			reporteds = userReportedDao.listGroupByTargetIds(targetIds,
					selStatus, selReason);
		}
		if (reporteds == null || reporteds.isEmpty())
			return forward();

		Map<Long, ReportedUser> resultMap = new HashMap<Long, ReportedUser>();

		for (UserReported reported : reporteds) {
			Long key = reported.getTargetId();

			if (resultMap.containsKey(key)) {
				ReportedUser reportedUser = resultMap.get(key);
				reportedUser.addReported(reported);
			} else {
				ReportedUser reportedUser = new ReportedUser(
						reported.getTarget());
				reportedUser.addReported(reported);
				resultMap.put(key, reportedUser);
			}
		}

		pageResult.setResults(new ArrayList<ReportedUser>(resultMap.values()));
		pageResult.setTotalSize(totalSize);
		return forward();
	}

	public Resolution reviewed() {
		if (!getCurrentUserAdmin() && !getAccessControl().getUserManagerAccess()
				&& !getAccessControl().getReportManagerAccess()) {
			return new StreamingResolution("text/html", "Need to login");
		}

		if (targetId == null || selReason == null || selStatus == null)
			return new ErrorResolution(400, "Bad request");

		Boolean response = transactionTemplate
				.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(TransactionStatus status) {
						try {
							List<UserReported> userReporteds = userReportedDao
									.listByTargetId(targetId, selStatus, null);
							for (UserReported reported : userReporteds) {
								if (selReason.equals(reported.getReason()))
									reported.setReviewerId(getCurrentUserId());
								reported.setStatus(UserReportedStatus.REVIEWED);
							}
							return true;
						} catch (Exception e) {
							return false;
						}
					}
				});

		if (response)
			return new StreamingResolution("text/html", "");
		return new ErrorResolution(400, "Bad request");
	}

	public Resolution reviewing() {
		if (!getCurrentUserAdmin() && !getAccessControl().getUserManagerAccess()
				&& !getAccessControl().getReportManagerAccess()) {
			return new StreamingResolution("text/html", "Need to login");
		}

		if (targetId == null || !userDao.exists(targetId)
				|| !UserReportedReason.PRETENDING.equals(selReason)
				|| !UserReportedStatus.REPORTED.equals(selStatus))
			return new ErrorResolution(400, "Bad request");

		Boolean response = transactionTemplate
				.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(TransactionStatus status) {
						try {
							List<UserReported> userReporteds = userReportedDao
									.listByTargetId(targetId, selStatus, null);
							for (UserReported reported : userReporteds) {
								if (selReason.equals(reported.getReason()))
									reported.setReviewerId(getCurrentUserId());
								reported.setStatus(UserReportedStatus.REVIEWING);
							}
							return true;
						} catch (Exception e) {
							return false;
						}
					}
				});

		if (response) {
			User targetUser = userDao.findById(targetId);
			String email = targetUser.getAllEmailAccountList().get(0)
					.getEmail();
			String region = targetUser.getRegion();
			if (region == null || region.isEmpty())
				region = "en_US";
			if (email != null && !email.isEmpty())
				mailImpersonationInvestigationService.send(email, region);
			return new StreamingResolution("text/html", "");
		}
		return new ErrorResolution(400, "Bad request");
	}

	public Resolution banned() {
		if (!getCurrentUserAdmin() && !getAccessControl().getUserManagerAccess()
				&& !getAccessControl().getReportManagerAccess()) {
			return new StreamingResolution("text/html", "Need to login");
		}

		if (targetId == null || !userDao.exists(targetId) || selReason == null)
			return new ErrorResolution(400, "Bad request");

		Boolean response = transactionTemplate
				.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(TransactionStatus status) {
						try {
							List<UserReported> userReporteds = userReportedDao
									.listByTargetId(
											targetId,
											new ArrayList<UserReportedStatus>(
													Arrays.asList(
															UserReportedStatus.REPORTED,
															UserReportedStatus.REVIEWING)),
											null);
							for (UserReported reported : userReporteds) {
								if (selReason.equals(reported.getReason()))
									reported.setReviewerId(getCurrentUserId());
								reported.setStatus(UserReportedStatus.BANNED);
							}
							return true;
						} catch (Exception e) {
							return false;
						}
					}
				});

		if (response) {
			User targetUser = userDao.findById(targetId);
			String email = targetUser.getAllEmailAccountList().get(0)
					.getEmail();
			String region = targetUser.getRegion();
			if (region == null || region.isEmpty())
				region = "en_US";
			if (email != null && !email.isEmpty()) {
				if (UserReportedReason.PRETENDING.equals(selReason))
					mailImpersonationSuspensionService.send(email, region);
				else
					mailSuspensionService.send(email, region);
			}
			deleteUser(targetId, isBlockDevice);
			return new StreamingResolution("text/html", "");
		}
		return new ErrorResolution(400, "Bad request");
	}

	public Resolution verified() {
		if (!getCurrentUserAdmin() && !getAccessControl().getUserManagerAccess()
				&& !getAccessControl().getReportManagerAccess()) {
			return new StreamingResolution("text/html", "Need to login");
		}

		// TODO

		return null;
	}

	private void deleteUser(Long userId, Boolean isBlockDevice) {
		// block device
		if (isBlockDevice) {
			List<String> uuidList = deviceDao.findDistinctByUserId(userId);
			for (String uuid : uuidList) {
				List<BlockDevice> b = blockDao.findByUuid(uuid);
				if (b.size() > 0) {
					b.get(0).setIsDeleted(Boolean.FALSE);
					blockDao.update(b.get(0));
				} else {
					BlockDevice d = new BlockDevice();
					d.setShardId(userId);
					d.setUuid(uuid);
					blockDao.create(d);
				}
			}
		}
		deleteUserService.pushUser(userId);
		deleteUserService.startAutoPostThread();

		User user = userDao.findById(userId);
		user.setIsDeleted(Boolean.TRUE);
		user = userDao.update(user);

		// delete account
		for (Account account : accountDao.findByUserId(user.getId())) {
			accountDao.delete(account);
		}
		// delete session
		userService.deleteSessionByUser(user.getId());

		userDao.refresh(user);
	}
	
	public Resolution reportUser() {
		if (!getCurrentUserAdmin() && !getAccessControl().getUserManagerAccess() 
				&& !getAccessControl().getReportManagerAccess()) {
			return new ErrorResolution(403, "Need to login");
		}
		UserApiResult<Boolean> result = new UserApiResult<Boolean>();
		result = userService.reportUser(targetId, reporterId, selReason);
		if (result.success())
			return new StreamingResolution("text/html", "OK");
		else
			return new ErrorResolution(result.getErrorDef().code(), result.getErrorDef().message());
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public UserReportedReason getSelReason() {
		return selReason;
	}

	public void setSelReason(UserReportedReason selReason) {
		this.selReason = selReason;
	}

	public UserReportedStatus getSelStatus() {
		return selStatus;
	}

	public void setSelStatus(UserReportedStatus selStatus) {
		this.selStatus = selStatus;
	}

	public String getSelRegion() {
		return selRegion;
	}

	public void setSelRegion(String selRegion) {
		this.selRegion = selRegion;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public PageResult<ReportedUser> getPageResult() {
		return pageResult;
	}

	public void setPageResult(PageResult<ReportedUser> pageResult) {
		this.pageResult = pageResult;
	}

	public Boolean getIsBlockDevice() {
		return isBlockDevice;
	}

	public void setIsBlockDevice(Boolean isBlockDevice) {
		this.isBlockDevice = isBlockDevice;
	}

	public Long getReporterId() {
		return reporterId;
	}

	public void setReporterId(Long reporterId) {
		this.reporterId = reporterId;
	}
}