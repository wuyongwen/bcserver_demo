package com.cyberlink.cosmetic.modules.post.service.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.core.service.CacheService;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.post.dao.PostTopKeywordDao;
import com.cyberlink.cosmetic.modules.post.model.PostTags.MainPostDetailView;
import com.cyberlink.cosmetic.modules.post.model.PostTopKeyword;
import com.cyberlink.cosmetic.modules.post.result.FullPostWrapper;
import com.cyberlink.cosmetic.modules.post.service.RelatedPostService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RelatedPostServiceImpl extends AbstractService implements RelatedPostService {
    
    public ObjectMapper objectMapper;
    public CacheService cacheMgr;
    private PostTopKeywordDao postTopKeywordDao;
    private LocaleDao localeDao;
    private CircleTypeDao circleTypeDao;

	private String relatedPostCacheName = "com.cyberlink.cosmetic.modules.post.service.impl.getRelatedPostIds";
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setCacheMgr(CacheService cacheMgr) {
        this.cacheMgr = cacheMgr;
    }
    
	public void setPostTopKeywordDao(PostTopKeywordDao postTopKeywordDao) {
		this.postTopKeywordDao = postTopKeywordDao;
	}
    
	public void setLocaleDao(LocaleDao localeDao) {
		this.localeDao = localeDao;
	}
	
	public void setCircleTypeDao(CircleTypeDao circleTypeDao) {
		this.circleTypeDao = circleTypeDao;
	}
	
    @Override
    public Long getRelatedPostIds(Long postId, String locale, Integer offset, Integer limit, List<Long> relatedPostIds) {
        Long totalSize = 0L;
        if(relatedPostIds == null)
            return totalSize;
        String cacheKey = getCacheKey(postId, locale, offset, limit);
        if(cacheKey != null) {
            PageResult<Long> pgResult = cacheMgr.get(relatedPostCacheName, cacheKey);
            if(pgResult != null) {
                relatedPostIds.addAll(pgResult.getResults());
                totalSize = Long.valueOf(pgResult.getTotalSize());
                return totalSize;
            }
        }
        
        String apiDomain = Constants.getSolrRelatedPostAPIDomain();
        if(apiDomain == null || apiDomain.length() <= 0)
            return totalSize;
        String apiUrl = String.format("%srelated/query?postID=%d&locale=%s&start=%d&rows=%d", apiDomain, postId, locale, offset, limit);
        
        try {
            do {
                Connection conn = Jsoup.connect(apiUrl);
                Document postIdResDoc = conn.ignoreContentType(true).get();
                Response postIdRes = getRelatedPostResposes(postIdResDoc);
                if(postIdRes.getNumFound() <= 0 || postIdRes.getDocs() == null || postIdRes.getDocs().size() <= 0)
                    break;
                Object postIdsObjs = postIdRes.getDocs();
                if(!(postIdsObjs instanceof List<?>))
                    break;
                List<Map<String, String>> postIds = (List<Map<String, String>>)postIdsObjs;
                totalSize = postIdRes.getNumFound();
                for(Map<String, String> map : postIds) {
                    String id = map.get("postId");
                    relatedPostIds.add(Long.valueOf(id));
                }
                
                if(cacheKey != null) {
                    PageResult<Long> pgResult = new PageResult<Long>(); 
                    pgResult.setResults(relatedPostIds);
                    pgResult.setTotalSize(totalSize.intValue());
                    cacheMgr.put(relatedPostCacheName, cacheKey, pgResult);
                }
            } while(false);
        }
        catch(Exception e) {
        }
        return totalSize;
    }
    
    @Override
    public void insertRelatedPostIds(String locale, FullPostWrapper fpw) {
        if(fpw == null)
            return;
        
        String apiDomain = Constants.getSolrRelatedPostWriteAPIDomain();
        if(apiDomain == null || !apiDomain.startsWith("http"))
            return;
        
        try {
            String apiUrl = String.format("%srelated/insert?locale=%s", apiDomain, locale);      
            String data = objectMapper.writerWithView(MainPostDetailView.class).writeValueAsString(fpw);
            doPost(apiUrl, data, null, null, "UTF-8");
        } catch (IOException e) {
        }
    }
    
    @Override
    public void generatePostIdsByKeyword(Date genTime)	{
    	
    	Integer kwBucketId = getKeywordBucketId(genTime);
    	if(kwBucketId == null)
    		return;
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(genTime);
		cal.add(Calendar.DATE, 7);
		Date dayInNextWeek = cal.getTime(); //get a day of next week
		Integer nextBucketId = getKeywordBucketId(dayInNextWeek);
		if(nextBucketId == null)
    		return;
    	
		int kwLimit = 10;
    	int postLimit = 10;
    	Set<String> locales = localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE);
    	for(String locale : locales) {
    		
            //get top 10 most popular Keywords
    		int baseKwLimit = 20;
        	BlockLimit blockLimit = new BlockLimit(0, baseKwLimit);

        	List<String> localeList = new ArrayList<String>();
        	localeList.add(locale);
            PageResult<CircleType> circleTypes = circleTypeDao.listTypesByLocales(localeList, null, new BlockLimit(0, 100));
            List<String> exCircleName = new ArrayList<String>();
            for(CircleType circleType : circleTypes.getResults()) {
            	exCircleName.add(circleType.getCircleTypeName());
            }
            
            PageResult<PostTopKeyword> postTopKeyword = postTopKeywordDao.getPopularKeywords(locale, blockLimit, false, kwBucketId, exCircleName);
            if (postTopKeyword.getTotalSize() == 0)
				continue;
            List<String> baseKeywords = new ArrayList<String>();
        	Map<String, Long> frequencyMap = new HashMap<String, Long>();
        	for(PostTopKeyword ptKw : postTopKeyword.getResults()) {
        		baseKeywords.add(ptKw.getKeyword());
        		frequencyMap.put(ptKw.getKeyword(), ptKw.getFrequency());
        	}
        	
        	//get related post ids & url        	
        	Map<String, Map<String, String>> postIdUrls = new HashMap<String, Map<String, String>>();
        	int fromIndex = 0;
        	while(postIdUrls.size() < kwLimit && postIdUrls.size() < baseKeywords.size()){
        		if(fromIndex >= baseKeywords.size())
        			break;
        		int kwNum = kwLimit - postIdUrls.size(); //the number of keyword that need to be query
        		int toIndex = fromIndex + kwNum;
        		if(toIndex > baseKeywords.size())
        			toIndex = baseKeywords.size();
        		List<String> keywords = baseKeywords.subList(fromIndex, toIndex); //the keywords that need to be query
        		Map<String, Map<String, String>> postIdUrl = getPostsByTopKeyword(keywords, locale, 0, postLimit);
        		postIdUrls.putAll(postIdUrl);
        		fromIndex += kwNum;
        	};
        	
        	//create related post ids & url
            List<PostTopKeyword> topKeywordList = new ArrayList<PostTopKeyword>();
        	for(String keyword : postIdUrls.keySet()) {
        		String postIds = "";
        		try {
					postIds = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(postIdUrls.get(keyword));
				} catch (JsonProcessingException e) {
					logger.error(e.getMessage());
				}
        		PostTopKeyword topKeyword = new PostTopKeyword();
        		topKeyword.setLocale(locale);
        		topKeyword.setKeyword(keyword);
        		topKeyword.setFrequency(frequencyMap.get(keyword));
        		topKeyword.setPostIds(postIds);
        		topKeyword.setIsTop(true);
        		topKeyword.setBucketId(nextBucketId);
        		topKeywordList.add(topKeyword);
        	}
        	postTopKeywordDao.batchInsert(topKeywordList);
    	}

    }
    
    @Override
    public void deleteOldRecord(Date deleteTime) {
    	//delete old records
		Integer kwBucketId = getKeywordBucketId(deleteTime);
		if(kwBucketId == null)
    		return;
    	postTopKeywordDao.deleteOldRecord(kwBucketId);
    }
    
    private Map<String, Map<String, String>> getPostsByTopKeyword(List<String> keywords, String locale, Integer start, Integer rows) {
        String apiDomain = Constants.getSolrRelatedPostAPIDomain();
        if(apiDomain == null || apiDomain.length() <= 0)
        	return null;
        
        String keywordStr = "";
        for(String kw: keywords){
        	keywordStr += kw + "|";
        }
        if(keywordStr.endsWith("|"))
        	keywordStr = keywordStr.substring(0, keywordStr.length() - 1);
        
        try {
			keywordStr = URLEncoder.encode(keywordStr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}

        String apiUrl = String.format("%ssearch?tbm=batchpost&q=%s&locale=%s&start=%d&rows=%d", apiDomain, keywordStr, locale, start, rows);
        Map<String, Map<String, String>> postIdUrls = new HashMap<String, Map<String, String>>();
		try {
			Connection conn = Jsoup.connect(apiUrl);
			Document kwResDoc = conn.ignoreContentType(true).get();
			List<KeywordResponse> kwResponses = getPostKeywordResposes(kwResDoc);
			for(KeywordResponse kwRes: kwResponses) {
				String keyword = kwRes.getKeyword();
				if(!keywords.contains(keyword)) // to verify the response data
					continue;
				Object postIdsObjs = kwRes.getResponse().getDocs();
				JSONArray jArray = (JSONArray) new JSONTokener(postIdsObjs.toString()).nextValue();
				if(jArray.length() <= 0) // to verify the empty keyword
					continue;
				Map<String, String> postIdUrl = new HashMap<String, String>();
				for(int i=0; i<jArray.length(); i++) {
					JSONObject jObj = jArray.getJSONObject(i);
					String postId = jObj.get("postId").toString();
					JSONArray jArr = jObj.getJSONObject("attachments").getJSONArray("files");
					jObj = jArr.getJSONObject(0);
					String metadata = jObj.get("metadata").toString();
					jObj = new JSONObject(metadata);
					String originalUrl = jObj.get("originalUrl").toString();
					postIdUrl.put(postId, originalUrl);
				}
				postIdUrls.put(keyword, postIdUrl);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return postIdUrls;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    static public class Response {
        
        private Long numFound = 0L;
        private List<Map<String, Object>> docs = null;
        
        public Response() {
            
        }
        
        public Long getNumFound() {
            return numFound;
        }
        public void setNumFound(Long numFound) {
            this.numFound = numFound;
        }
        public List<Map<String, Object>> getDocs() {
            return docs;
        }
        public void setDocs(List<Map<String, Object>> docs) {
            this.docs = docs;
        }
    }
    
	@JsonIgnoreProperties(ignoreUnknown = true)
	static public class KeywordResponse {

		String keyword;
		Response response;

		public KeywordResponse() {

		}

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		public Response getResponse() {
			return response;
		}

		public void setResponse(Response response2) {
			this.response = response2;
		}
	}
    
    private String getCacheKey(Long postId, String locale, Integer offset, Integer limit) {
        String key = "";
        if(postId != null)
            key += postId.toString() + "#";
        if(locale != null)
            key += locale + "#";
        if(offset != null)
            key += offset.toString() + "#";
        if(limit != null)
            key += limit.toString() + "#";
        if(key.length() <= 0)
            return null;
        return key;
    }
    
    private Response getRelatedPostResposes(Document document) throws Exception {
        if(document == null)
            throw new Exception("Null response document");
        Element body = document.body();
        if(body == null)
            throw new Exception("Null response body");
        String returnJson = body.text();
        if(returnJson == null || returnJson.length() <= 0)
            throw new Exception("Empty result");
        Response result = objectMapper.readValue(returnJson, Response.class);
        if(result == null)
            return new Response();
        return result;
    }
    
	private List<KeywordResponse> getPostKeywordResposes(Document document) throws Exception {
		if (document == null)
			throw new Exception("Null response document");
		Element body = document.body();
		if (body == null)
			throw new Exception("Null response body");
		String returnJson = body.text();
		if (returnJson == null || returnJson.length() <= 0)
			throw new Exception("Empty result");

		JSONObject jObject = new JSONObject(returnJson);
		List<KeywordResponse> results = new ArrayList<KeywordResponse>();
		for (int i = 0; i < jObject.names().length(); i++) {
			String key = jObject.names().getString(i);
			Response response = objectMapper.readValue(jObject.get(key).toString(), Response.class);
			KeywordResponse result = new KeywordResponse();
			result.setKeyword(key);
			result.setResponse(response);
			results.add(result);
		}
		return results;
	}

    private void doPost(String sURL, String payload, String cookie, String referer, String charset) { 
        InputStream result = null;
        try { 
            URL url = new URL(sURL); 
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
            conn.setDoOutput(true); 
            conn.setDoInput(true); 
            conn.setRequestMethod("POST"); 
            conn.setUseCaches(false); 
            conn.setAllowUserInteraction(true);  
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            if (cookie != null) 
                conn.setRequestProperty("Cookie", cookie); 
            if (referer != null) 
                conn.setRequestProperty("Referer", referer); 
      
            conn.setRequestProperty("Content-Type", "application/json"); 
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if(payload != null && payload.length() > 0) {
                conn.getOutputStream().write(payload.getBytes("UTF-8"));
            }
            result = conn.getInputStream();
        } catch (java.io.IOException e) {   
            logger.error(e.getMessage());
        } finally { 
            try { 
                if (result != null)
                    result.close();
            } catch (java.io.IOException ex) { 
                logger.error(ex.getMessage());
            } 
        }  
    } 
    
    @Override
    public Integer getKeywordBucketId(Date currentDate) {
        if(currentDate == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int dateToSub = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if(dateToSub < 0)
            dateToSub = 6;
        cal.add(Calendar.DATE, -dateToSub);
        return cal.get(Calendar.WEEK_OF_YEAR) % 5;
    }
    
    @Override
    public PageResult<PostTopKeyword> listPostByTopKeyword(String locale, Integer kLimit, Date dateTime){
    	Integer kwBucketId = getKeywordBucketId(dateTime);
    	if(kwBucketId == null)
    		return null;
		BlockLimit blockLimit = new BlockLimit(0, kLimit);
		return postTopKeywordDao.getPopularKeywords(locale, blockLimit, true, kwBucketId, null);
    }
}
