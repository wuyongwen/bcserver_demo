package com.cyberlink.cosmetic.modules.user.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.UserBadge;
import com.cyberlink.cosmetic.modules.user.model.UserBadge.BadgeType;

public interface UserBadgeDao extends GenericDao<UserBadge, Long> {
	public PageResult<UserBadge> listUsersByBadgeType(List<String> locale, BadgeType badgeType, BlockLimit blockLimit);
	public UserBadge findUserBadgeByUserId(String locale, Long userId, Boolean isStar);
	public List<UserBadge> findStarOfWeekByLocale(String locale);
	public Long findStarOfWeekByUserId(Long userId);
	void batchInsert(List<UserBadge> list);
}
