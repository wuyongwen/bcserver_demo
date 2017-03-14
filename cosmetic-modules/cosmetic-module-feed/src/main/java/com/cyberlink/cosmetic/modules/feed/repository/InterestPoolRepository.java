package com.cyberlink.cosmetic.modules.feed.repository;

import java.util.Map;

public interface InterestPoolRepository {
	void addPost(String userId, String postId, Long score);
	void addPost(String userId, Map<String, Double> postMap);
	void updateAll(Map<String, Map<String, Double>> locatePostMap);
	void updateUser(Long userId, Map<String, Map<String, Double>> localePostMap);
	void cleanUser(Long userId);
}
