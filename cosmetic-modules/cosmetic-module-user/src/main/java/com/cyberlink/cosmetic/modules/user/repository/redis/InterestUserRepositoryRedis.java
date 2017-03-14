package com.cyberlink.cosmetic.modules.user.repository.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cyberlink.cosmetic.modules.user.repository.InterestUserRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class InterestUserRepositoryRedis extends AbstractRedisRepository implements InterestUserRepository {

	@Override
	public void addUser(Long userId, Long timestamp) {
		opsForZSet().add(KeyUtils.userInterestPool(), String.valueOf(userId), timestamp.doubleValue());
	}
	
	@Override
	public List<Long> getUserList() {
		Set<String> userSet = opsForZSet().range(KeyUtils.userInterestPool(), 0, -1);
		final List<Long> list = new ArrayList<Long>();
        for (final String s : userSet) {
        	list.add(Long.parseLong(s));
        }
        return list;
	}
	
	@Override
	public void removeUserExpired(Long expiredTime) {
		opsForZSet().removeRangeByScore(KeyUtils.userInterestPool(), 0, expiredTime);
	}
	
	@Override
	public void addUserLocale(Long userId, String locale) {
		opsForHash().put(KeyUtils.userInterestPoolLocale(),  String.valueOf(userId), locale);
	}
	
	@Override
	public String getUserLocale(Long userId){
		return opsForHash().get(KeyUtils.userInterestPoolLocale(), String.valueOf(userId));
	}

	@Override
	public Boolean IsUserExisted(Long userId) {
		List<Long> userList = getUserList();
		return userList.contains(userId);
	}
}
