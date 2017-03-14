package com.cyberlink.cosmetic.modules.user.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserBadgeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.dao.UserReportedDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.SessionStatus;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserBadge;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;
import com.cyberlink.cosmetic.modules.user.model.UserReported;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedReason;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedStatus;
import com.cyberlink.cosmetic.modules.user.repository.UserSessionRepository;
import com.cyberlink.cosmetic.modules.user.result.UserApiResult;
import com.cyberlink.cosmetic.modules.user.service.UserService;
import com.cyberlink.cosmetic.utils.CosmeticWorkQueue;

public class UserServiceImpl extends AbstractService implements UserService {

	private UserReportedDao userReportedDao;
	private UserDao userDao;
	private UserBadgeDao userBadgeDao;
	private SessionDao sessionDao;
	private UserSessionRepository userSessionRepository;
	
	CosmeticWorkQueue workQueue = new CosmeticWorkQueue(4, "UserService");

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setUserReportedDao(UserReportedDao userReportedDao) {
		this.userReportedDao = userReportedDao;
	}
	
	public void setUserBadgeDao(UserBadgeDao userBadgeDao) {
		this.userBadgeDao = userBadgeDao;
	}

	public void setSessionDao(SessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}
	
	public void setUserSessionRepository(UserSessionRepository userSessionRepository) {
		this.userSessionRepository = userSessionRepository;
	}

	public UserApiResult<Boolean> reportUser(Long targetId, Long reporterId, UserReportedReason reason) {
		UserApiResult<Boolean> result = new UserApiResult<Boolean>();

		if (!userDao.exists(targetId) || reporterId.equals(targetId)) {
			result.setErrorDef(ErrorDef.InvalidUserTargetId);
			return result;
		}

		User targetUser = userDao.findById(targetId);
		if (targetUser.getUserType() == null) {
			result.setErrorDef(ErrorDef.UnknownUserError);
			return result;
		} else if (UserType.Blogger.equals(targetUser.getUserType()))
			return result;
		else if (!UserType.Normal.equals(targetUser.getUserType())) {
			result.setErrorDef(ErrorDef.ReportCLAccount);
			return result;
		}

		if (reason == null) {
			result.setErrorDef(ErrorDef.InvalidUserReportReason);
			return result;
		}

		UserReported reported = userReportedDao.findByTargetAndReporter(targetId, reporterId,
				new ArrayList<UserReportedStatus>(
						Arrays.asList(UserReportedStatus.REPORTED, UserReportedStatus.REVIEWING)));

		if (reported == null) {
			List<UserReported> userReporteds = userReportedDao.listByTargetId(targetId, UserReportedStatus.REVIEWING,
					UserReportedReason.PRETENDING);
			reported = new UserReported();
			reported.setReporterId(reporterId);
			reported.setTargetId(targetId);
			reported.setReason(reason);
			if (userReporteds == null || userReporteds.isEmpty())
				reported.setStatus(UserReportedStatus.REPORTED);
			else {
				if (UserReportedReason.PRETENDING.equals(reason))
					reported.setReviewerId(userReporteds.get(0).getReviewerId());
				reported.setStatus(UserReportedStatus.REVIEWING);
			}
			userReportedDao.create(reported);
		}
		return result;
	}
	
	private Boolean updateUserAttr(Long userId, BadgeType badgeType, Boolean isStar){
		User user = userDao.findById(userId);
		if(user == null)
			return false;
		
		if(badgeType.getIsStar()) {
			String isStarString = isStar ? "1" : "0";
			user.setStringInAttr("sow", isStarString);
		}
		else
			user.setStringInAttr("bdl", badgeType.getBadgeTypeSign());
		userDao.update(user);
		return true;
	}
	
	public PageResult<User> getUsersByBadgeType(List<String> locale, BadgeType badgeType, BlockLimit blockLimit) {
		final PageResult<User> userResult = new PageResult<User>();
		List<User> users = new ArrayList<User>();
		PageResult<UserBadge> userBadges = userBadgeDao.listUsersByBadgeType(locale, badgeType, blockLimit);
		for(UserBadge userBadge : userBadges.getResults()) {
			users.add(userBadge.getUser());
		}
		userResult.setResults(users);
		userResult.setTotalSize(userBadges.getTotalSize());
		return userResult;
	}
	
	public List<Long> updateStarOfWeek(String locale, List<Long> userIds) { // update star of week
		BadgeType badgeType = BadgeType.StarOfWeek;

		List<UserBadge> userBadges = userBadgeDao.findStarOfWeekByLocale(locale);
		for(UserBadge userBadge : userBadges) {
			Long userId = userBadge.getUserId();
			if(userIds.contains(userId)) // already star of week
				userIds.remove(userId);
			else { // delete
				Boolean isUpdated = updateUserAttr(userId, badgeType, Boolean.FALSE);
				if(!isUpdated)
					continue;
				userBadge.setIsDeleted(Boolean.TRUE);
				userBadgeDao.update(userBadge);
			}
		}
		
		List<Long> newStars = new ArrayList<Long>();
		List<UserBadge> userBadgeList = new ArrayList<UserBadge>();
		for(Long userId : userIds) { // insert
			Boolean isUpdated = updateUserAttr(userId, badgeType, Boolean.TRUE);
			if(!isUpdated)
				continue;
			UserBadge userBadge = new UserBadge();
			userBadge.setLocale(locale);
			userBadge.setUserId(userId);
			userBadge.setBadgeType(badgeType);
			userBadgeList.add(userBadge);
			newStars.add(userId);
		}
		userBadgeDao.batchInsert(userBadgeList);
		return newStars;
	}
	
	public List<Long> updateStarOfWeek(String locale, Map<Long, Long> userIdScore) { // update star of week
		BadgeType badgeType = BadgeType.StarOfWeek;

		List<UserBadge> userBadges = userBadgeDao.findStarOfWeekByLocale(locale);
		for(UserBadge userBadge : userBadges) {
			Long userId = userBadge.getUserId();
			if(userIdScore.containsKey(userId)) { // already star of week
				userBadge.setScore(userIdScore.get(userId));
				userBadgeDao.update(userBadge);
				userIdScore.remove(userId);
			}
			else { // delete
				Long count = userBadgeDao.findStarOfWeekByUserId(userId);
				if(count <= 1){ // to handle when the user is star of week in multiple locale
					Boolean isUpdated = updateUserAttr(userId, badgeType, Boolean.FALSE);
					if(!isUpdated)
						continue;
				}
				userBadge.setIsDeleted(Boolean.TRUE);
				userBadgeDao.update(userBadge);
			}
		}
		
		List<Long> newStars = new ArrayList<Long>();
		List<UserBadge> userBadgeList = new ArrayList<UserBadge>();
		for(Long userId : userIdScore.keySet()) { // insert
			Boolean isUpdated = updateUserAttr(userId, badgeType, Boolean.TRUE);
			if(!isUpdated)
				continue;
			UserBadge userBadge = new UserBadge();
			userBadge.setLocale(locale);
			userBadge.setUserId(userId);
			userBadge.setBadgeType(badgeType);
			userBadge.setScore(userIdScore.get(userId));
			userBadgeList.add(userBadge);
			newStars.add(userId);
		}
		userBadgeDao.batchInsert(userBadgeList);
		return newStars;
	}
	
	public void updateUserBadge(String locale, Long userId, BadgeType badgeType) { // update badge
		if(badgeType.getIsStar())
			return;

		UserBadge userBadge = userBadgeDao.findUserBadgeByUserId(locale, userId, Boolean.FALSE);
		if(userBadge == null) { // insert a new record
			Boolean isUpdated = updateUserAttr(userId, badgeType, null);
			if(!isUpdated)
				return;
			userBadge = new UserBadge();
			userBadge.setLocale(locale);
			userBadge.setUserId(userId);
			userBadge.setBadgeType(badgeType);
			userBadgeDao.create(userBadge);
			return;
		}
		
		if(badgeType.getPriority() <= userBadge.getBadgeType().getPriority()) // update the record
			return;
		Boolean isUpdated = updateUserAttr(userId, badgeType, null);
		if(!isUpdated)
			return;
		userBadge.setBadgeType(badgeType);
		userBadgeDao.update(userBadge);
	}

	@Override
	public String getToken(Long userId, UserType userType) {
		String token = null;
		if (UserType.getBCConsoleUserType().contains(userType)) {
			Session session = sessionDao.findByUserIdAndStatus(userId, SessionStatus.Resident);
			if (session == null) {
				// first create need to delete all old sessions
				sessionDao.deleteByUser(userId);
				token = UUID.randomUUID().toString();
				Session newSession = new Session();
				newSession.setShardId(userId);
				newSession.setUserId(userId);
				newSession.setToken(token);
				newSession.setStatus(SessionStatus.Resident);
				sessionDao.create(newSession);
			} else {
				token = session.getToken();
			}
		}
    	else
    	{
    		Session session = sessionDao.findUniqueByUserId(userId);
			if (session == null) {
				token = UUID.randomUUID().toString();
				Session newSession = new Session();
				newSession.setShardId(userId);
				newSession.setUserId(userId);
				newSession.setToken(token);
				newSession.setStatus(SessionStatus.SignIn);
				sessionDao.create(newSession);
			} else {
				session.setStatus(SessionStatus.SignIn);
				sessionDao.update(session);
				token = session.getToken();
			}
    	}
    	userSessionRepository.addSession(token, userId);
        return token;
	}
	
	@Override
	public void signOutToken(String token) {
		Session session = sessionDao.findByToken(token);
        if(!session.getStatus().equals(SessionStatus.Resident)) {
	        session.setStatus(SessionStatus.SignOut);
	        sessionDao.update(session);
        }
        userSessionRepository.removeSession(token);
	}
	
	@Override
	public void deleteSessionByUser(Long userId) {
		List<Session> sessionList = sessionDao.findByUserId(userId);
		for (Session session : sessionList) {
			userSessionRepository.removeSession(session.getToken());
		}
		sessionDao.deleteByUser(userId);
	}
	
	@Override
    public void asyncRun(Runnable r) {
        workQueue.execute(r);
    }
}
