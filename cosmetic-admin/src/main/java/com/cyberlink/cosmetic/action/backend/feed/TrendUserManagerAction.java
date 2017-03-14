package com.cyberlink.cosmetic.action.backend.feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.post.repository.TrendingRepository;
import com.cyberlink.cosmetic.modules.post.service.TrendingService;

@UrlBinding("/feed/trendUserManager.action")
public class TrendUserManagerAction extends AbstractAction {
	
	@SpringBean("post.trendingService")
    private TrendingService trendingService;
	
	@SpringBean("post.trendingRepository")
	private TrendingRepository trendingRepository;
	
	private Long userId;
	private Long shardId;

	@DefaultHandler
    public Resolution route() {
		return forward();
	}
	
	public Resolution listUser() {
		Set<String> users = trendingService.getTrendUserList(shardId);
		List<String> userList = new ArrayList<String>();
		userList.addAll(users);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", userList);
		return json(result);
	}
	
	public Resolution listCategory() {
		List<String> categories = trendingService.getPostCategoryList(userId);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", categories);
		return json(result);
	}
	
	public Resolution updateCategory() {
		return null;
	}
	
	public Resolution getGroup() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("result", trendingService.getUserGroup(userId));
		return json(result);
	}
	
	public Resolution updateGroup() {
		return null;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setShardId(Long shardId) {
		this.shardId = shardId;
	}

	public List<Long> getShardList() {
		return trendingRepository.getShardList();
	}
	
}