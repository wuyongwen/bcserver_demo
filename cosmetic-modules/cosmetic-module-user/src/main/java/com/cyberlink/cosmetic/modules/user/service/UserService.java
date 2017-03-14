package com.cyberlink.cosmetic.modules.user.service;

import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;
import com.cyberlink.cosmetic.modules.user.model.UserReported.UserReportedReason;
import com.cyberlink.cosmetic.modules.user.result.UserApiResult;

public interface UserService {
	UserApiResult<Boolean> reportUser(Long targetId,Long reporterId,UserReportedReason reason);
	public PageResult<User> getUsersByBadgeType(List<String> locale, BadgeType badgeType, BlockLimit blockLimit);
	public List<Long> updateStarOfWeek(String locale, Map<Long, Long> userIdScore);
	public void updateUserBadge(String locale, Long userId, BadgeType badgeType);
	public String getToken(Long userId,UserType userType);
	void signOutToken(String token);
	void deleteSessionByUser(Long userId);
	void asyncRun(Runnable r);
}
