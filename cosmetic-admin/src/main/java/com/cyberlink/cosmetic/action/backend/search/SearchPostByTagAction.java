package com.cyberlink.cosmetic.action.backend.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.search.model.PostView;
import com.cyberlink.cosmetic.modules.search.model.PostViewFile;
import com.cyberlink.cosmetic.modules.search.model.SearchPost;
import com.cyberlink.cosmetic.modules.search.service.SearchPostService;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/search/searchPostByTag.action")
public class SearchPostByTagAction extends AbstractAction{

	@SpringBean("search.SearchPostService")
	private SearchPostService searchPostService;

    @SpringBean("common.localeDao")
    private LocaleDao localeDao;

    @SpringBean("post.PostDao")
    private PostDao postDao;
    
	private String locale = "en_US";
	private String tag;
	private Integer offset = 0;
	private Integer limit = 20;
	private Integer pageNumber = 1;
	private Integer maxPageNumber;
	private Set<String> userLocales;
	PageResult<PostView> postViewResult = new PageResult<PostView>();
	Map<Long,Map<String,String>> extraPostDataMap = new HashMap<Long,Map<String,String>>();
	
	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
			return new StreamingResolution("text/html", "Need to login");
		}
		userLocales = localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE);
		if(tag != null){
			try {
				offset = (pageNumber - 1) * 20;
				PageResult<SearchPost> searchResult = searchPostService.searchPostByTag(locale, tag, offset, limit);
				List<SearchPost> searchPosts = searchResult.getResults();
				List<PostView> postViews = new ArrayList<PostView>();
				ObjectMapper om = new ObjectMapper();
				for (SearchPost searchPost : searchPosts) {
					String json = searchPost.getResultJson();
					PostView pv = om.readValue(json, PostView.class);
					postViews.add(pv);
				}
				int searchResultSize = searchResult.getTotalSize();
				if (searchResultSize != 0) {
					maxPageNumber = (searchResult.getTotalSize() / 20) + 1;
				}
				if (postViews.size() > 0) {
					for (PostView postView : postViews) {
						Map<String, String> postDataMap = new HashMap<String, String>();
						List<PostViewFile> files = postView.getAttachments().getFiles();
						postDataMap.put("postImageUrl", "");
						extraPostDataMap.put(postView.getPostId(), postDataMap);
						String postImageUrl = "";
						if (files != null) {
							for (PostViewFile file : files) {
								if (file.getFileType().equals(FileType.PostCover.toString())) {
									JSONParser parser = new JSONParser();
									JSONObject jsonObject = new JSONObject();
									try {
										String fileMetadata = file.getMetadata();
										jsonObject = (JSONObject) parser.parse(fileMetadata);
										if (jsonObject.containsKey("originalUrl")) {
											postImageUrl = jsonObject.get("originalUrl").toString();
											postDataMap.put("postImageUrl", postImageUrl);
										}
									} catch (ParseException e) {
									}
								}
							}
						}
					}
					Set<Long> postIdSet = extraPostDataMap.keySet();
					Long[] postIdArray = new Long[postIdSet.size()];
					// get post locale by id
					Map<Long, String> localeMap = postDao.findPostLocaleByPostIds(postIdSet.toArray(postIdArray));
					for (Long postId : postIdSet) {
						if (localeMap.containsKey(postId)) {
							extraPostDataMap.get(postId).put("locale", localeMap.get(postId));
						} else {
							extraPostDataMap.get(postId).put("locale", "");
						}
					}
				}
				postViewResult.setResults(postViews);
				postViewResult.setTotalSize(searchResult.getTotalSize());
				
			} catch (Exception e) {
				logger.error("SearchPostByTagAction route fail. message:" + e.getMessage());
				return new StreamingResolution("text/html", e.getMessage());
			}
		}
		return forward();
	}
	
	public Map<Long, Map<String, String>> getExtraPostDataMap() {
		return extraPostDataMap;
	}

	public PageResult<PostView> getPostViewResult() {
		return postViewResult;
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getMaxPageNumber() {
		return maxPageNumber;
	}

	public Set<String> getUserLocales() {
		return userLocales;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}
