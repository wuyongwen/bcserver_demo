package com.cyberlink.cosmetic.modules.user.repository;

import java.util.List;

public interface InterestUserRepository {
	void addUser(Long userId, Long timestamp);
	void removeUserExpired(Long expiredTime);
	List<Long> getUserList();
	Boolean IsUserExisted(Long userId);
	void addUserLocale(Long userId, String locale);
	String getUserLocale(Long userId);
}
