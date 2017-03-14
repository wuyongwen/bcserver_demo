package com.cyberlink.cosmetic.modules.user.repository.redis;

import com.cyberlink.cosmetic.modules.user.repository.UserSessionRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class UserSessionRepositoryRedis extends AbstractRedisRepository implements UserSessionRepository {

	@Override
	public void addSession(String token, Long userId) {
		if (token == null || userId == null)
			return;
		
		opsForValue().set(KeyUtils.userSession(token), userId.toString());
	}

	@Override
	public void removeSession(String token) {
		delete(KeyUtils.userSession(token));
		
	}

	@Override
	public Long authenticate(String token) {
		if (token == null || token.isEmpty())
			return null;
		try {
			String value = opsForValue().get(KeyUtils.userSession(token));
			if (value == null || value.isEmpty())
				return null;
			return Long.parseLong(value);
		} catch (Exception e) {
			return null;
		}
	}
	
}