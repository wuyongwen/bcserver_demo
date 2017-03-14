package com.cyberlink.cosmetic.modules.user.repository.redis;

import java.util.HashMap;
import java.util.Map;

import com.cyberlink.cosmetic.modules.user.repository.UserInfoRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class UserInfoRepositoryRedis extends AbstractRedisRepository implements UserInfoRepository {

	@Override
	public Map<String, Object> getActiveInfo(Long roomId, Long offset) {
		if (roomId == null)
			return null;
		if (offset == null)
			offset = 0l;
		
		final Map<String, Object> results = new HashMap<String, Object>();
		Long size = opsForList().size(KeyUtils.activeInfo(roomId));
		results.put("results", opsForList().range(KeyUtils.activeInfo(roomId), offset, size));
		return results;
	}
	
}