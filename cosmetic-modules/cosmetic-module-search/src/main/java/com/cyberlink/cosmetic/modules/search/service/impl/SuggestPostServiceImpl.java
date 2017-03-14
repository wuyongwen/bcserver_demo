package com.cyberlink.cosmetic.modules.search.service.impl;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.springframework.util.StringUtils;

import com.cyberlink.cosmetic.modules.search.helper.SuggestComparator;
import com.cyberlink.cosmetic.modules.search.model.Suggest;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.search.service.SuggestPostService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SuggestPostServiceImpl implements SuggestPostService{
	//private SolrClient solr = new HttpSolrClient("http://54.64.170.46:8983/solr/post");

	private SolrClient solr;
	
	public SolrClient getSolr() {
		return solr;
	}

	public void setSolr(SolrClient solr) {
		this.solr = solr;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> getSuggestion(String keyword, String locale) throws Exception {
		List<Suggest> result = new ArrayList<Suggest>();
		String suggestionDict = getSuggestionDictionary(locale);
		
		List<String> terms = new ArrayList<String>();
		
		String apiDomain = Constants.getSolrSearchAPIDomain();
        if (apiDomain == null || apiDomain.length() <= 0) {
    		return terms;
        }
        
        try {
    		do {
        		String apiUrl = String.format("%ssearch?tbm=suggestpost&q=%s&locale=%s&count=%d", apiDomain, URLEncoder.encode(keyword, "UTF-8"), locale, 10);

        		URL url = new URL(apiUrl);
            	ObjectMapper om = new ObjectMapper();
            	HashMap<String,Object> queryResponse = om.readValue(url, HashMap.class); 
            	HashMap<String,Object> suggest = (HashMap<String,Object>)queryResponse.get("suggest");
            	if (suggest == null)
            		break;
            	HashMap<String,Object> suggester = (HashMap<String,Object>)suggest.get(suggestionDict);
            	if (suggester == null)
            		break;
            	HashMap<String,Object> suggestKeyword = (HashMap<String,Object>)suggester.get(keyword);
            	if (suggestKeyword == null)
            		break;
            	
        		ArrayList<HashMap<String,Object>> suggestions = (ArrayList<HashMap<String,Object>>)suggestKeyword.get("suggestions");
        		for (HashMap<String,Object> suggestion : suggestions) {
        			Suggest s = new Suggest();
        			s.setTerm((String)suggestion.get("term"));
        			Integer weight = (Integer)suggestion.get("weight");
        			s.setWeight(new Long(weight.longValue()));
        			result.add(s);        			
        		}
        		
        		Collections.sort(result, new SuggestComparator());
        		for(Suggest s:result){
        			terms.add(s.getTerm());
        		}
        		return terms;
        	} while(false);
        }
        catch(Exception e) {
        }

		return terms;
                
		/*
		SolrQuery query = new SolrQuery();
		query.setRequestHandler("/suggest");
		query.setParam("suggest", "true");
		query.setParam("suggest.dictionary", suggestionDict);
		query.setParam("suggest.q", keyword);
		query.setParam("suggest.count", "10");
		QueryResponse response = solr.query(query);

		NamedList obj = (NamedList)((Map)response.getResponse().get("suggest")).get(suggestionDict);
		SimpleOrderedMap obj2 = (SimpleOrderedMap) obj.get(keyword);
		List<SimpleOrderedMap> obj3 = (List<SimpleOrderedMap>) obj2.get("suggestions");
		for(SimpleOrderedMap o:obj3){
			System.out.println((String)o.get("term"));
			//if((Long)o.get("weight")==0)
			//	continue;
			Suggest s = new Suggest();
			s.setTerm((String)o.get("term"));
			s.setWeight((Long)o.get("weight"));
			result.add(s);
		}
		
		Collections.sort(result, new SuggestComparator());
		List<String> terms = new ArrayList<String>();
		for(Suggest s:result){
			terms.add(s.getTerm());
		}
		return terms;
		*/
	}
	
	private String getSuggestionDictionary(String locale){
		if(!StringUtils.hasText(locale))
			return "suggester_en";
		
		if(locale.length()<2)
			return "suggester_en";
		
		if(locale.substring(0, 2).equalsIgnoreCase("zh"))
			return "suggester_zh";
		
		return "suggester_en";
	}
}
