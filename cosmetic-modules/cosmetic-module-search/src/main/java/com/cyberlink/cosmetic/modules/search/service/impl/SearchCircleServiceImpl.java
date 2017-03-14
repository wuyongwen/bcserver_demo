package com.cyberlink.cosmetic.modules.search.service.impl;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Async;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.search.model.SearchCircle;
import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;
import com.cyberlink.cosmetic.modules.search.service.SearchCircleService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchCircleServiceImpl extends AbstractService implements SearchCircleService{

	private SolrClient solr;
	
	public SolrClient getSolr() {
		return solr;
	}

	public void setSolr(SolrClient solr) {
		this.solr = solr;
	}

	public PageResult<SearchCircle> searchCircle(String locale, Long curUserId, String keyword, int offset, int limit) throws Exception {
		List<SearchCircle> circles = new ArrayList<SearchCircle>();
		if(limit>20)
			limit = 20;
		
		if(curUserId==null)
			curUserId = 0L;

		PageResult<SearchCircle> pageResult = new PageResult<SearchCircle>();

        String apiDomain = Constants.getSolrSearchAPIDomain();
        if (apiDomain == null || apiDomain.length() <= 0) {
    		pageResult.setResults(circles);
    		pageResult.setTotalSize(0);
    		return pageResult;
        }
        
        try {
    		String apiUrl = String.format("%ssearch?tbm=circle&q=%s&locale=%s&curUserId=%d&start=%d&rows=%d", apiDomain, URLEncoder.encode(keyword, "UTF-8"), locale, curUserId, offset, limit);

    		URL url = new URL(apiUrl);
    		ObjectMapper om = new ObjectMapper();
        	HashMap<String,Object> queryResponse = om.readValue(url, HashMap.class); 
        	HashMap<String,Object> response = (HashMap<String,Object>)queryResponse.get("response");
        	if (response != null) {
        		ArrayList<HashMap<String,Object>> docs = (ArrayList<HashMap<String,Object>>)response.get("docs");
        		for (HashMap<String,Object> doc : docs) {
        			SearchCircle circle = new SearchCircle();
        			Number id = (Number)doc.get("id");
        			circle.setId(id.longValue());
        			circle.setResultJson((String)doc.get("rawContent"));
        			circles.add(circle);
        		}
        	}

    		pageResult.setResults(circles);
    		pageResult.setTotalSize((Integer)response.get("numFound"));
    		return pageResult;
        }
        catch(Exception e) {
        }

		pageResult.setResults(circles);
		pageResult.setTotalSize(0);
		return pageResult;
	}

	@Async
	public void saveUserKeyword(Long curUserId, String keyword) throws Exception {
		if (curUserId == null)
			return;

		Connection conn = Jsoup.connect("http://" + Constants.getWebsiteWrite() + "/api/search/save-user-post-keyword.action");
		conn.data("curUserId", String.valueOf(curUserId));
		conn.data("keyword", keyword);
		conn.data("type", TypeKeyword.Circle.toString());
		conn.ignoreContentType(true).post();
	}
}
