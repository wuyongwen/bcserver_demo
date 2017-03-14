package com.cyberlink.cosmetic.modules.user.repository.redis;

import java.util.Iterator;
import java.util.Set;

import com.cyberlink.cosmetic.modules.user.repository.FeedJoinerRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class FeedJoinerRepositoryRedis extends AbstractRedisRepository implements FeedJoinerRepository {

	@Override
	public Long addUser(Long userId) {
		return opsForSet().add(KeyUtils.feedJoiner(), userId.toString());
	}
	
	@Override
    public Long removeUser(Long userId) {
        return opsForSet().remove(KeyUtils.feedJoiner(), userId.toString());
    }
	
	@Override
    public Long removeAll() {
	    Set<String> joiners =  getUserList();
	    Iterator<String> joinerIt = joiners.iterator();
	    while(joinerIt.hasNext()) {
	        removeUser(Long.valueOf(joinerIt.next()));
	    }
        return 1L;
    }
	
	@Override
	public Set<String> getUserList() {
		return opsForSet().members(KeyUtils.feedJoiner());
	}
	
	@Override
	public Boolean isFeedJoiner(Long userId) {
	    return opsForSet().isMember(KeyUtils.feedJoiner(), userId.toString());
	}
	
}
