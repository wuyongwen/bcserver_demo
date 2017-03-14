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
import com.cyberlink.cosmetic.modules.search.model.SearchUser;
import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;
import com.cyberlink.cosmetic.modules.search.service.SearchUserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchUserServiceImpl extends AbstractService implements SearchUserService{

	private SolrClient solr;
	
	public SolrClient getSolr() {
		return solr;
	}

	public void setSolr(SolrClient solr) {
		this.solr = solr;
	}
	
	public PageResult<SearchUser> searchUser(String locale, Long curUserId, String keyword, int offset, int limit, List<String> type) throws Exception {
		List<SearchUser> users = new ArrayList<SearchUser>();
		if(limit>20)
			limit = 20;
		
		if(curUserId==null)
			curUserId = 0L;
		
		PageResult<SearchUser> pageResult = new PageResult<SearchUser>();

		String apiDomain = Constants.getSolrSearchAPIDomain();
        if (apiDomain == null || apiDomain.length() <= 0) {
    		pageResult.setResults(users);
    		pageResult.setTotalSize(0);
    		return pageResult;
        }		
        
		String[] keywordTerms = keyword.split("\\s+");
		StringBuffer userTypes = new StringBuffer();
		for(String t:type){
			if(t!=null){
				if(userTypes.length()>0){
					userTypes.append(",");
				}else{
					userTypes.append("&type=");
				}
				userTypes.append(t);
			}
		}
		
        try {
    		String apiUrl = String.format("%ssearch?tbm=user&q=%s&locale=%s&curUserId=%d&start=%d&rows=%d%s", apiDomain, URLEncoder.encode(keyword, "UTF-8"), locale, curUserId, offset, limit, userTypes.toString());

    		URL url = new URL(apiUrl);
    		ObjectMapper om = new ObjectMapper();
        	HashMap<String,Object> queryResponse = om.readValue(url, HashMap.class); 
        	HashMap<String,Object> response = (HashMap<String,Object>)queryResponse.get("response");
        	if (response != null) {
        		ArrayList<HashMap<String,Object>> docs = (ArrayList<HashMap<String,Object>>)response.get("docs");
        		for (HashMap<String,Object> doc : docs) {
        			SearchUser user = new SearchUser();
        			Number id = (Number)doc.get("id");
        			user.setId(id.longValue());
        			user.setResultJson((String)doc.get("rawContent"));
        			users.add(user);
        		}
        	}

    		pageResult.setResults(users);
    		pageResult.setTotalSize((Integer)response.get("numFound"));
    		return pageResult;
        }
        catch(Exception e) {
        }

		pageResult.setResults(users);
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
		conn.data("type", TypeKeyword.User.toString());
		conn.ignoreContentType(true).post();
	}
}
