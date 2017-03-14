package com.cyberlink.cosmetic.action.api.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.post.model.PostTopKeyword;
import com.cyberlink.cosmetic.modules.post.service.RelatedPostService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/api/v4.9/post/list-post-by-top-keyword.action")
public class ListPostByTopKeywordAction extends AbstractPostAction {

	@SpringBean("web.objectMapper")
	private ObjectMapper objectMapper;

	@SpringBean("post.relatedPostService")
	private RelatedPostService relatedPostService;

	private String locale = "en_US";
	private Integer kLimit = 10;
	private Integer pLimit = 10;
	private Date dateTime = Calendar.getInstance().getTime();
	
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setkLimit(Integer kLimit) {
		this.kLimit = kLimit;
	}

	public void setpLimit(Integer pLimit) {
		this.pLimit = pLimit;
	}
	
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	@DefaultHandler
	public Resolution route() {
		if (locale == null)
			return new ErrorResolution(ErrorDef.InvalidLocale);
		
		PageResult<PostTopKeyword> postTopKeywords = null;
		int loop = 1;
		while(true) {
			postTopKeywords = relatedPostService.listPostByTopKeyword(locale, kLimit, dateTime);
			if (postTopKeywords == null)
				return new ErrorResolution(ErrorDef.UnknownPostError);
			
			if(postTopKeywords.getTotalSize() > 0 || loop >= 2)
				break;
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateTime);
			cal.add(Calendar.DATE, -7);
			dateTime = cal.getTime();
			loop++;
		}
			
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PostTopKeyword keyword : postTopKeywords.getResults()) {
			Map<String, Object> result = new LinkedHashMap<String, Object>();
			List<Map<String, String>> posts = new ArrayList<Map<String, String>>();
			try {
				Map<String, String> postIds = new HashMap<String, String>();
				postIds = objectMapper.readValue(keyword.getPostIds(), new TypeReference<Map<String, String>>() {});
				int i = 0;
				for (String postId : postIds.keySet()) {
					if (i == pLimit)
						break;
					Map<String, String> post = new HashMap<String, String>();
					post.put("postId", postId);
					post.put("originalUrl", postIds.get(postId));
					posts.add(post);
					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			result.put("tag", keyword.getKeyword());
			result.put("posts", posts);
			resultList.add(result);
		}
		final Map<String, Object> results = new HashMap<String, Object>();
		results.put("results", resultList);
        results.put("totalSize", postTopKeywords.getTotalSize());
		
		return json(results);
	}

	public Resolution update() { //update top 10 keywords manually
		long markTime;
        long lapTime;
        markTime = System.currentTimeMillis();
		relatedPostService.generatePostIdsByKeyword(dateTime);
		lapTime = System.currentTimeMillis();
		return json("done: " + (lapTime - markTime) + "(ms)");
	}
	
	public Resolution delete() { //delete old record manually
		long markTime;
        long lapTime;
        markTime = System.currentTimeMillis();
		relatedPostService.deleteOldRecord(dateTime);
		lapTime = System.currentTimeMillis();
		return json("done: " + (lapTime - markTime) + "(ms)");
	}
	
	public Resolution getBucketId() {
		return json(relatedPostService.getKeywordBucketId(dateTime));
	}

}
