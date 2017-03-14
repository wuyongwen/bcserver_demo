package com.cyberlink.cosmetic.modules.feed.repository.redis;

import java.util.List;
import java.util.Map;

import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.InterestPoolRepository;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.repository.InterestUserRepository;

import net.sourceforge.stripes.integration.spring.SpringBean;

public class InterestPoolRepositoryRedis extends PoolRepositoryRedis implements InterestPoolRepository{
	
	private final static long TARGET_USER_EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000; // 2 weeks
	
	@SpringBean("user.UserDao")
	private UserDao userDao;
	
	@SpringBean("user.interestUserRepository")
	private InterestUserRepository userRepository;
	
	public void setUserDao(UserDao dao) {
		this.userDao=dao;
	}

	public void setUserRepository(InterestUserRepository repository) {
		this.userRepository=repository;
	}

	@Override
	public void addPost(String userId, String postId, Long score) {     
        super.add(PoolType.Interest, userId, postId, score);
	}

	@Override
	public void updateAll(final Map<String, Map<String, Double>> localePostMap) {
		// expire old users
		removeExpiredUser();
		
		List<Long> userList = userRepository.getUserList();
		for (int i = 0; i < userList.size() ; i++) {
			Long userId = userList.get(i);
			String locale = getUserLocale(userId);
			
			Map<String, Double> postMap = null;
			if (locale != null)
				postMap = localePostMap.get(locale);
			if (postMap != null)
				addPost(String.valueOf(userId), postMap);
		}
	}

	@Override
	public void addPost(String userId, Map<String, Double> postMap) {
		if (postMap.size() > 0)
			super.add(PoolType.Interest, userId, postMap);
	}
	
	private void removeExpiredUser() {
		Long expiredTime = System.currentTimeMillis() - TARGET_USER_EXPIRE_TIME;
		userRepository.removeUserExpired(expiredTime);
	}

	private String getUserLocale(Long userId) {
		String locale = userRepository.getUserLocale(userId);
		if (locale == null) {
			// locale not in cache, retrieve user locale info from db
			List<User> user = userDao.findByIds(userId);
			if (user != null) {
				User u = user.get(0);
				locale = u.getRegion();
				if (locale!= null)
					userRepository.addUserLocale(userId, locale);
			}
		}
		return locale;
	}
	
	@Override
	public void updateUser(Long userId, Map<String, Map<String, Double>> localePostMap) {
		String locale = getUserLocale(userId);
		Map<String, Double> postMap = null;
		if (locale != null)
			postMap = localePostMap.get(locale);
		if (postMap != null)
			addPost(String.valueOf(userId), postMap);
	}
	
	@Override
	public void cleanUser(Long userId) {
		super.clean(PoolType.Interest, String.valueOf(userId));
	}
}
