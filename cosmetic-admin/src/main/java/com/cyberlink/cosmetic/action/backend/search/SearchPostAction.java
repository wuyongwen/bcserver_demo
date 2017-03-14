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
import com.cyberlink.cosmetic.action.backend.post.AbstractPostAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.model.Attachment;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.search.model.PostView;
import com.cyberlink.cosmetic.modules.search.model.PostViewFile;
import com.cyberlink.cosmetic.modules.search.model.SearchPost;
import com.cyberlink.cosmetic.modules.search.service.SearchPostService;
import com.fasterxml.jackson.databind.ObjectMapper;

@UrlBinding("/search/searchPost.action")
public class SearchPostAction extends AbstractPostAction {

	@SpringBean("search.SearchPostService")
	private SearchPostService searchPostService;
	
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("post.PostDao")
    private PostDao postDao;
    
    @SpringBean("post.PostViewDao")
    protected PostViewDao postViewDao;

	private String locale = "en_US";
	private String keyword;
	private Long postId;
	private Integer offset = 0;
	private Integer limit = 20;
	private Long curUserId;
	private Integer maxPageNumber;
	private Integer pageNumber = 1;
	private Set<String> userLocales;
	private PageResult<PostView> postViewResult = new PageResult<PostView>();
	private Post post;
	Map<Long,Map<String,String>> extraPostDataMap = new HashMap<Long,Map<String,String>>();
	private String postImageUrl;
	private int searchMod;//1:search by post id 2:search by keyword

	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
			return new StreamingResolution("text/html", "Need to login");
		}
		userLocales = localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE);
		if (postId != null) {
			// initial value
			searchMod = 1;
			locale = "en_US";
			keyword = null;
			if (postDao.exists(postId)) {
				post = postDao.findById(postId);
				List<Attachment> attachmentList = post.getAttachments();
				for (Attachment attachment : attachmentList) {
					File attachmentFile = attachment.getAttachmentFile();
					if (attachmentFile != null && attachmentFile.getFileType().equals(FileType.Photo)) {
						List<FileItem> fileItemList = attachmentFile.getFileItems();
						if(fileItemList != null)
							for (FileItem fileItem : fileItemList) {
								if (postImageUrl == null)
									postImageUrl = fileItem.getOriginalUrl();
							}
					}
				}
			}
		} else if (keyword != null) {
			searchMod = 2;
			try {
				offset = (pageNumber - 1) * 20;
				PageResult<SearchPost> searchResult = searchPostService.searchPost(locale, keyword, offset, limit);
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
								if (file.getFileType().equals(FileType.Photo.toString())) {
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
				logger.error("SearchPostAction route fail. message:" + e.getMessage());
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

	public Set<String> getUserLocales() {
		return userLocales;
	}
	
	public Integer getMaxPageNumber() {
		return maxPageNumber;
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public int getSearchMod() {
		return searchMod;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}
	
	public Post getPost() {
		return post;
	}
	
	public String getPostImageUrl() {
		return postImageUrl;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
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

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}