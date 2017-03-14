package com.cyberlink.cosmetic.modules.user.repository;

import java.util.Map;

public interface UserInfoRepository {
	
	Map<String, Object> getActiveInfo(Long roomId, Long offset);
}