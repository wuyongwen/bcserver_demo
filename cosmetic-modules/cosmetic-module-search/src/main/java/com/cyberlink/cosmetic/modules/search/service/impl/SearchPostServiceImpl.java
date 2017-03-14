package com.cyberlink.cosmetic.modules.search.service.impl;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.TermsParams;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.search.dao.PostKeywordDao;
import com.cyberlink.cosmetic.modules.search.model.SearchPost;
import com.cyberlink.cosmetic.modules.search.model.TypeKeyword;
import com.cyberlink.cosmetic.modules.search.service.SearchPostService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchPostServiceImpl extends AbstractService implements SearchPostService{

	private SolrClient solr;
	
	public SolrClient getSolr() {
		return solr;
	}

	public void setSolr(SolrClient solr) {
		this.solr = solr;
	}
	
	private Set<String> langSet = new HashSet<String>(Arrays.asList("fr","de","ja","ko","en"));
	
	private PostKeywordDao postKeywordDao;
	
	public PostKeywordDao getPostKeywordDao() {
		return postKeywordDao;
	}

	public void setPostKeywordDao(PostKeywordDao postKeywordDao) {
		this.postKeywordDao = postKeywordDao;
	}
	
	public PageResult<SearchPost> searchPost(String locale, String keyword, int offset, int limit) throws Exception {
		List<SearchPost> searchPosts = new ArrayList<SearchPost>();
		if(limit>20)
			limit = 20;
		
		PageResult<SearchPost> pageResult = new PageResult<SearchPost>();

		String apiDomain = Constants.getSolrSearchAPIDomain();
        if (apiDomain == null || apiDomain.length() <= 0) {
    		pageResult.setResults(searchPosts);
    		pageResult.setTotalSize(0);
    		return pageResult;
        }

		String[] keywordTerms = keyword.split("\\s+");
		StringBuffer circleTypes = new StringBuffer();
		for(String term:keywordTerms){
			Long circleTypeId = postKeywordDao.getCircleTypeId(locale, term);
			if(circleTypeId!=null){
				if(circleTypes.length()>0){
					circleTypes.append(",");
				}else{
					circleTypes.append("&circleType=");
				}
				circleTypes.append(circleTypeId);
			}
		}
        
        try {
    		String apiUrl = String.format("%ssearch?tbm=post&q=%s&locale=%s&start=%d&rows=%d%s", apiDomain, URLEncoder.encode(keyword, "UTF-8"), locale, offset, limit, circleTypes.toString());

    		URL url = new URL(apiUrl);
    		ObjectMapper om = new ObjectMapper();
        	HashMap<String,Object> queryResponse = om.readValue(url, HashMap.class); 
        	HashMap<String,Object> response = (HashMap<String,Object>)queryResponse.get("response");
        	if (response != null) {
        		ArrayList<HashMap<String,Object>> docs = (ArrayList<HashMap<String,Object>>)response.get("docs");
        		for (HashMap<String,Object> doc : docs) {
        			SearchPost searchPost = new SearchPost();
        			Number id = (Number)doc.get("id");
        			searchPost.setId(id.longValue());
        			searchPost.setResultJson((String)doc.get("rawContent"));
        			searchPosts.add(searchPost);
        		}
        	}

    		pageResult.setResults(searchPosts);
    		pageResult.setTotalSize((Integer)response.get("numFound"));
    		return pageResult;
        }
        catch(Exception e) {
        }

		pageResult.setResults(searchPosts);
		pageResult.setTotalSize(0);
		return pageResult;
	}

	private String getSearchFields(String lang, String keyword){
		StringBuffer fieldStr = new StringBuffer();
		fieldStr.append("title_en^1 content_en^1e-10 postTags_en^0.125 keywords_en^2 circleTypeName_en^1");
		if(!lang.equalsIgnoreCase("en")){
			if(lang.equalsIgnoreCase("cht")){
				fieldStr.append(" title_").append("zh-tw").append("^1");
				fieldStr.append(" content_").append("zh-tw").append("^1e-10");
				fieldStr.append(" postTags_").append("zh-tw").append("^0.125");
				if (keyword !=null && keyword.length() <= 1) {
					fieldStr.append(" sdKeywords_").append("zh-tw").append("^2");
				}
				else {
					fieldStr.append(" keywords_").append("zh-tw").append("^2");
				}
				fieldStr.append(" circleTypeName_").append("zh-tw").append("^1");
			}else if(lang.equalsIgnoreCase("chs")){
				fieldStr.append(" title_").append("zh-cn").append("^1");
				fieldStr.append(" content_").append("zh-cn").append("^1e-10");
				fieldStr.append(" postTags_").append("zh-cn").append("^0.125");
				if (keyword !=null && keyword.length() <= 1) {
					fieldStr.append(" sdKeywords_").append("zh-cn").append("^2");
				}
				else {
					fieldStr.append(" keywords_").append("zh-cn").append("^2");
				}
				fieldStr.append(" circleTypeName_").append("zh-cn").append("^1");
			}else{
				fieldStr.append(" title_").append(lang).append("^1");
				fieldStr.append(" content_").append(lang).append("^1e-10");
				fieldStr.append(" postTags_").append(lang).append("^0.125");
				fieldStr.append(" keywords_").append(lang).append("^2");
				fieldStr.append(" circleTypeName_").append(lang).append("^1");
			}
		}
		return fieldStr.toString();
	}
	
	private String getePostFix(String locale){
		Set<String> langs = new HashSet<String>(Arrays.asList("ja", "kr", "en", "de", "fr"));
		
		if(!StringUtils.hasText(locale))
			return "en";
		
		if(locale.equalsIgnoreCase("zh_tw") || locale.equalsIgnoreCase("zh-tw"))
			return "zh-tw";
		else if(locale.equalsIgnoreCase("zh_HK") || locale.equalsIgnoreCase("zh_CN") || locale.equalsIgnoreCase("zh-CN") || locale.equalsIgnoreCase("zh-HK"))
			return "zh-cn";
		else if(locale.equalsIgnoreCase("en_ROW") || locale.equalsIgnoreCase("en-ROW"))
			return "row";
		
		if(locale.length()<2)
			return "en";
		
		if(locale.length()==2){
			if(langs.contains(locale.toLowerCase()))
				return locale.toLowerCase();
			else
				return "en";
		}
		
		String onlyLang = locale.substring(0, 2);
		if(langs.contains(onlyLang.toLowerCase()))
			return onlyLang.toLowerCase();
		else
			return "en";
	}	
	
	public String getLang(String locale){
		if(!StringUtils.hasText(locale))
			return "en";
		
		if(locale.length()<2)
			return "en";
		
		locale = locale.replaceAll("-", "_");
		if(locale.equalsIgnoreCase("en_ROW"))
			return "row";
		
		String lang = locale.substring(0, 2);
		if(lang.equalsIgnoreCase("zh")){
			if(locale.equalsIgnoreCase("zh_cn"))
				return "chs";
			else
				return "cht";
		}
		
		if(langSet.contains(lang.toLowerCase()))
			return lang;
		else
			return "en";
	}

	@Override
	public List<String> getTopTags(String locale, int topN) throws Exception {
		String lang = getePostFix(locale);
		SolrQuery query = new SolrQuery();
	    query.setParam(CommonParams.QT, "/terms");
	    query.setParam(TermsParams.TERMS, true);
	    query.setParam(TermsParams.TERMS_LIMIT, String.valueOf(topN));
	    query.setParam(TermsParams.TERMS_FIELD, "postTags_"+lang);
	    query.setParam(TermsParams.TERMS_SORT, TermsParams.TERMS_SORT_COUNT);
	    TermsResponse termResp = solr.query(query).getTermsResponse();
	    List<Term> terms = termResp.getTerms("postTags_"+lang);
	    List<String> tags = new ArrayList<String>();
	    for(Term term:terms)
	    	tags.add(term.getTerm());
	    
		return tags;
	}

	@Override
	public PageResult<SearchPost> searchPostByTag(String locale, String tag, int offset, int limit) throws Exception {
		List<SearchPost> searchPosts = new ArrayList<SearchPost>();
		if(limit>20)
			limit = 20;
		
		String lang = getePostFix(locale);
		SolrQuery parameters = new SolrQuery();
		parameters.set("q", tag);
		parameters.set("qf", "postTags_en^1e-10 postTags_ja^1e-10 postTags_kr^1e-10 postTags_de^1e-10 postTags_fr^1e-10 postTags_zh-tw^1e-10 postTags_zh-cn^1e-10 postTags_row^1e-10");
		parameters.set("fl", "id,rawContent");
		//parameters.add("fq", "createTime:[NOW-180DAY TO NOW]");
		parameters.add("bf", "sum(product(termfreq(language,'"+lang+"'),8),scale(goodCount,0,4),scale(commentCount,0,3))");
		parameters.set("start", offset);
		parameters.set("rows", limit);
		
		PageResult<SearchPost> pageResult = new PageResult<SearchPost>();
		QueryResponse response = solr.query(parameters);
		SolrDocumentList list = response.getResults();
		Iterator<SolrDocument> it = list.iterator();
		while (it.hasNext()) {
			SolrDocument sd = it.next();
			SearchPost searchPost = new SearchPost();
			searchPost.setId((Long)sd.getFieldValue("id"));
			searchPost.setResultJson((String)sd.getFieldValue("rawContent"));
			searchPosts.add(searchPost);
		}
		
		pageResult.setResults(searchPosts);
		pageResult.setTotalSize((int)list.getNumFound());
		return pageResult;
	}

	@Override
	public List<String> autocompleteTag(String locale, int topN, String prefixTag) throws Exception {
		String lang = getePostFix(locale);
		SolrQuery query = new SolrQuery();
	    query.setParam(CommonParams.QT, "/terms");
	    query.setParam(TermsParams.TERMS, true);
	    query.setParam(TermsParams.TERMS_LIMIT, String.valueOf(topN));
	    query.setParam(TermsParams.TERMS_FIELD, "postTags_"+lang);
	    query.setParam(TermsParams.TERMS_SORT, TermsParams.TERMS_SORT_COUNT);
	    query.setParam(TermsParams.TERMS_PREFIX_STR, prefixTag);
	    TermsResponse termResp = solr.query(query).getTermsResponse();
	    List<Term> terms = termResp.getTerms("postTags_"+lang);
	    List<String> tags = new ArrayList<String>();
	    for(Term term:terms)
	    	tags.add(term.getTerm());
	    
		return tags;
	}

	@Async
	public void saveUserKeyword(Long curUserId, String keyword)
			throws Exception {
		if (curUserId == null)
			return;
		
		Connection conn = Jsoup.connect("http://" + Constants.getWebsiteWrite() + "/api/search/save-user-post-keyword.action");
		conn.data("curUserId", String.valueOf(curUserId));
		conn.data("keyword", keyword);
		conn.data("type", TypeKeyword.Post.toString());
		conn.ignoreContentType(true).post();
	}

	@Async
	public void savePostKeyword(String keyword, String locale) throws Exception {
		System.out.println("savePostKeyword");
		Connection conn2 = Jsoup.connect("http://"+ Constants.getWebsiteWrite()+ "/api/search/save-post-keyword.action");
		conn2.data("keyword", keyword);
		conn2.data("lang", getLang(locale));
		conn2.ignoreContentType(true).post();
	}
}
