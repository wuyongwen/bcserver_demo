package com.cyberlink.cosmetic.modules.user.dao;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.user.model.Friendship;

public interface FriendshipDao extends GenericDao<Friendship, Long>{
	Set<Long> findUserIdByAccountSource(Long userId, List<String> accountSource); 
	
	void updateBySourceId(String sourceID, String accountSource, Long userId, String name);
	void createByUserIdsAndSourceId(Collection<Long> userId, String accountSource, String sourceId, String name, Long friendId);
	void createByUserIdAndSourceIds(Long userId, String accountSource, List<String> sourceIds, Map<String, String> nameMap, Map<String, Long> friendIdMap);
	String findName(Long userId, String accountSource); 

}
