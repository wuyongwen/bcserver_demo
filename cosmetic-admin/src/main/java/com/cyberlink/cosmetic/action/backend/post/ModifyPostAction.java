package com.cyberlink.cosmetic.action.backend.post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleTag;
import com.cyberlink.cosmetic.modules.circle.model.CircleTagGroup;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileTypeException;
import com.cyberlink.cosmetic.modules.file.exception.InvalidMetadataException;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostHoroscopeTag;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostTags;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.File;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.json.JsonObject;

@UrlBinding("/post/ModifyPost.action")
public class ModifyPostAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("file.fileService")
    private FileService fileService;
      
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("file.fileDao")
    private FileDao fileDao;
    
    @SpringBean("circle.circleDao")
    private CircleDao circleDao;
    
    @SpringBean("circle.circleTypeDao")
    private CircleTypeDao circleTypeDao;
    
    @SpringBean("circle.circleTagDao")
    private CircleTagDao circleTagDao;
    
    @SpringBean("circle.circleTagGroupDao")
    private CircleTagGroupDao circleTagGroupDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("circle.circleService")
    private CircleService circleService;

    @SpringBean("look.LookTypeDao")
    private LookTypeDao lookTypeDao;
    
	@SpringBean("web.objectMapper")
	private ObjectMapper objectMapper;
    
    // Post
    private static final String loginMessage = "You need to login";
    
    private Long postId = (long)0;
    private Long parentPostId = (long)0;
    private PageResult<MainPostSimpleWrapper> pageResult;
    private List<String> attachMetadatas = new ArrayList<String>();
    private MainPostSimpleWrapper relPost;
    private String userDisplayName;
    private String userAvater;
    private String postForm;
    private List<Circle> circles = new ArrayList<Circle>(0);
    private List<CircleTag> circleTags = new ArrayList<CircleTag>(0);
    private String postTypeMap = "{}";
    private List<LookType> lookTypes = new ArrayList<LookType>();
    private Long promoteScore;
    private String lookTag;
    private String horoscopeType;
    private String masterId = "";
    private String masterDisplayName = "";
	private String masterAvatarUrl = "";
    private String masterDescription = "";
    private String masterExtLink = "";

    // Update
	private PostType postType = null;
	private AppName appName = null;
	private String title = null;
    private String content = null;
    private List<String> attachments = null;
    private PostStatus postStatus = null;
    private List<Long> selCircles;
    private Set<String> selKeywords = null;
    private String postKwObj = "[]";
    private String extLookUrl;
    private Long lookTypeId;

	// Upload
    private String dataUrl = "";
    private String metadata = "";
    private FileType fileType = FileType.Photo;
    
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    
    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
    
	public PostType getPostType() {
		return postType;
	}
	
    public void setPostType(PostType postType) {
		this.postType = postType;

		String withAppName = this.postType.getWithAppName();
		if(withAppName != "")
			setAppName(AppName.valueOf(withAppName));
	}
    
	public void setAppName(AppName appName) {
		this.appName = appName;
	}
	
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }
    
    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }
    
    public void setSelCircles(List<Long> selCircles) {
        this.selCircles = selCircles;
    }
    
    public List<Long>  getSelCircles() {
        return this.selCircles;
    }
    
    public Set<String> getSelKeywords() {
        return selKeywords;
    }

    public void setSelKeywords(Set<String> selKeywords) {
        this.selKeywords = selKeywords;
    }
    
    public String getPostKwObj() {
        return postKwObj;
    }
    
    public List<Circle> getCircles() {
        return circles;
    }
    
    public List<CircleTag> getCircleTags() {
        return circleTags;
    }
    
	public String getPostTypeMap() {
		return postTypeMap;
	}
	
    public List<LookType> getLookTypes() {
		return lookTypes;
	}
    
    public Long getPromoteScore() {
        return promoteScore;
    }
    
    public void setPromoteScore(Long promoteScore) {
        this.promoteScore = promoteScore;
    }
    
	public String getLookTag() {
		return lookTag;
	}

	public void setLookTag(String lookTag) {
		if (lookTag == null)
			this.lookTag = "";
		else
			this.lookTag = lookTag;
	}

	public String getHoroscopeType() {
		return horoscopeType;
	}

	public void setHoroscopeType(String horoscopeType) {
		if (horoscopeType == null)
			this.horoscopeType = "";
		else
			this.horoscopeType = horoscopeType;
	}
	
	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		if(masterId == null)
			this.masterId = "";
		else
			this.masterId = masterId;
	}
	public String getMasterDisplayName() {
		return masterDisplayName;
	}

	public void setMasterDisplayName(String masterDisplayName) {
		if(masterDisplayName == null)
			this.masterDisplayName = "";
		else
			this.masterDisplayName = masterDisplayName;
	}

	public String getMasterAvatarUrl() {
		return masterAvatarUrl;
	}

	public void setMasterAvatarUrl(String masterAvatarUrl) {
		if(masterAvatarUrl == null)
			this.masterAvatarUrl = "";
		else
			this.masterAvatarUrl = masterAvatarUrl;
	}

	public String getMasterDescription() {
		return masterDescription;
	}

	public void setMasterDescription(String masterDescription) {
		if(masterDescription == null)
			this.masterDescription = "";
		else
			this.masterDescription = masterDescription;
	}

	public String getMasterExtLink() {
		return masterExtLink;
	}

	public void setMasterExtLink(String masterExtLink) {
		if(masterExtLink == null)
			this.masterExtLink = "";
		else
			this.masterExtLink = masterExtLink;
	}
	
    public String getExtLookUrl() {
        return extLookUrl;
    }

    public void setExtLookUrl(String extLookUrl) {
        if(extLookUrl == null)
            this.extLookUrl = "";
        else
            this.extLookUrl = extLookUrl;
    }
    
    public Long getLookTypeId() {
		return lookTypeId;
	}
	
	public void setLookTypeId(Long lookTypeId) {
		this.lookTypeId = lookTypeId;
	}
    
    public List<CircleTag> getCircleTagsFunction(List<Circle> cirs) {
        List<CircleTagGroup> cirTagGroups = new ArrayList<CircleTagGroup>(0);
        List<CircleTag> cirTags = new ArrayList<CircleTag>(0);
        for(Circle cir : cirs) {
            cirTagGroups.addAll(circleTagGroupDao.findByCircleId(cir.getId()));
        }
        
        for(CircleTagGroup cirTagGroup : cirTagGroups) {
            cirTags.addAll(circleTagDao.findByGroupId(cirTagGroup.getId()));
        }
        
        return cirTags;
    }
    
    private Resolution route() {       
        Boolean isLogin = false;
        HttpSession session = getContext().getRequest().getSession();
        Long userId=(long)0;
        User user = null;
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                userId = loginSession.getUserId();
                user = userDao.findById(userId);
                userDisplayName = user.getDisplayName();
                userAvater = user.getAvatarUrl();
            }
        }
        
        if(!isLogin || userId == null) {
            return new StreamingResolution("text/html", "Need to login");
        }
        
        Post p = postService.queryPostById(postId).getResult();
        if(p == null)
            return new StreamingResolution("text/html", "Invalid Post ID");
        Post mainPost = null;
        if(p.getParentId() == null) {
        	postForm = "mainpost";
            promoteScore = p.getPromoteScore();
            selCircles = new ArrayList<Long>();
            selCircles.add(p.getCircleId());
            
            User postCreator = p.getCreator();
            String region = "en_US";
            Set<String> postLocale = localeDao.getLocaleByType(postCreator.getRegion(), LocaleType.POST_LOCALE);
            if(postLocale != null && postLocale.size() > 0)
                region = postLocale.iterator().next();
            
            PageResult<Circle> cirPageResult = circleService.listUserCircle(postCreator.getId(), true, region, true, new BlockLimit(0, 100));
            for(Circle cir : cirPageResult.getResults()) {
                circles.add(cir);
            }

            Map<String, Object> postTypeMaps = new HashMap<String, Object>();
        	for(PostType postType : PostType.values()){
        		postTypeMaps.put(postType.name(), postType.getSerializedPostType());
        	}
            try {
            	postTypeMap = objectMapper.writeValueAsString(postTypeMaps);
    		} catch (JsonProcessingException e) {
    			logger.error(e.getMessage());
    		}
            circleTags = getCircleTagsFunction(circles);
            lookTypes = lookTypeDao.listByLocale(region);
            postType = p.getPostType();
            lookTypeId = p.getLookTypeId();
            mainPost = p;
        }
        else {
        	postForm = "subpost";
            parentPostId = p.getParentId();
            mainPost = postService.queryPostById(parentPostId).getResult();
        }

        if(mainPost != null) {
            PostTags postTags = mainPost.getPostTags();
            selKeywords = new HashSet<String>();
            if(postTags != null && !postTags.IsNullKeywords()) {
                for(String kw : postTags.getKeywords())
                    selKeywords.add(kw);
            }
            try {
                postKwObj = objectMapper.writeValueAsString(selKeywords);
            } catch (JsonProcessingException e) {
            }
        }
        
        if(mainPost == null)
            return new StreamingResolution("text/html", "Invalid Post ID");
        
        if(!mainPost.getCreatorId().equals(user.getId()) && !getCurrentUserAdmin() && !getAccessControl().getPostManagerAccess())
            return new StreamingResolution("text/html", "Not authorized to delete this post");
                
        List<Post> posts = new ArrayList<Post>();
        posts.add(p);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(posts, ThumbnailType.Detail);//.List);
		Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(posts);
        LookType lookType = null;
        if(p.getLookTypeId() != null)
            lookType = lookTypeDao.findById(p.getLookTypeId());
        
        relPost = new MainPostSimpleWrapper(p, null, postFileItems.get(p.getId()), postCircles.get(p.getId()), lookType);
        for(File file : relPost.getAttachments().getFiles()) {
            String metadata = "{\"fileId\":" + String.valueOf(file.getFileId()) + ",";
            metadata += "\"fileType\":\"" + file.getFileType() + "\",";
            metadata += file.getMetadata().substring(1);
            attachMetadatas.add(metadata);
        }
        
        if(relPost.getTags() == null)
        	return forward();
        	
        PostHoroscopeTag postHoroscopeTag = relPost.getTags().getHoroscopeTag();
		if (postHoroscopeTag == null)
			return forward();
		
        horoscopeType = postHoroscopeTag.horoscopeType;
        Object horoscopeMaster = postHoroscopeTag.horoscopeMaster;
		if (horoscopeMaster != null) {
			try {
				String horoscopeMasterJson = objectMapper.writeValueAsString(horoscopeMaster);
				try {
					JsonNode node = objectMapper.readValue(horoscopeMasterJson, JsonNode.class);
					if (node.get("userId") != null)
						masterId = node.get("userId").asText();
					if (node.get("displayName") != null)
						masterDisplayName = node.get("displayName").asText();
					if (node.get("avatarUrl") != null)
						masterAvatarUrl = node.get("avatarUrl").asText();
					if (node.get("description") != null)
						masterDescription = node.get("description").asText();
					if (node.get("externalLink") != null)
						masterExtLink = node.get("externalLink").asText();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

        return forward();
    }
    
    @DefaultHandler
    public Resolution wallpost() {
        return route();
    }
    
    public Resolution clpost() {
        return route();
    }
    
    public Resolution update() {
        Boolean isLogin = false;
        HttpSession session = getContext().getRequest().getSession();
        Long userId=(long)0;
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                userId = loginSession.getUserId();
                User user = userDao.findById(userId);
                userDisplayName = user.getDisplayName();
                userAvater = user.getAvatarUrl();
            }
        }

        if(!isLogin || userId == null)
        	return json(loginMessage);
        
        Long handlerUserId = userId;
        if (getCurrentUserAdmin() || getAccessControl().getPostManagerAccess()) {
            List<Long> ids = new ArrayList<Long>();
            ids.add(postId);
            List<Post> relPosts = postService.findPostByIds(ids);
            if(relPosts.size() <= 0)
                return new ErrorResolution(400, "Bad request");
            Post relPost = relPosts.get(0);
            if(relPost.getCreatorId() != null) {
                handlerUserId = relPost.getCreatorId();
                postForm = "mainpost";
            }
            else {
                List<Long> pIds = new ArrayList<Long>();
                pIds.add(relPost.getParentId());
                List<Post> relParentPosts = postService.findPostByIds(pIds);
                if(relParentPosts.size() <= 0)
                    return new ErrorResolution(400, "Bad request");
                Post parentPost = relParentPosts.get(0);
                handlerUserId = parentPost.getCreatorId();
                postForm = "subpost";
            }
        }
        
        String attacStr = null;
        if(attachments != null && attachments.size() > 0)
        {
            attacStr = "{\"files\":[";
            for(int idx = 0; idx < attachments.size(); idx++) {
                String attcStr = attachments.get(idx);
                if(attcStr.equals("null")) {
                    attacStr = "{[";
                    break;
                }
                JsonObject obj = new JsonObject(attcStr);
                String fileIdStr = obj.getString("fileId");
                if(fileIdStr != null && fileIdStr.length() > 0)
                    obj.remove("fileId");
                String fileType = obj.getString("fileType");
                if(fileType != null && fileType.length() > 0) {
                    obj.remove("fileType");
                }

                attacStr += "{";
                if(!fileIdStr.equals("null") && !fileIdStr.equals("0"))
                    attacStr += "\"fileId\":"+ fileIdStr + ",";
                attacStr += "\"fileType\":\""+ fileType +"\",\"metadata\":";
                attacStr += obj.toString() + "}";
                if(idx < attachments.size() - 1)
                    attacStr += ",";
            }
            attacStr += "]}";
        }
        
        if(content != null && content.equals("null")) {
            content = "";
        }

        String tags = "{";
        if(lookTag != null) {
            if(tags.length() > 1)
                tags += ",";
            tags += "\"lookTag\":\"" + lookTag + "\"";
        }
        if(selKeywords != null) {
            if(tags.length() > 1)
                tags += ",";
            tags += "\"keywords\":[";
            String kws = "";
            for(String kw : selKeywords) {
                if(kw.equalsIgnoreCase("undefined"))
                    continue;
                if(kws.length() > 0)
                    kws += ",";
                kws += "\"" + kw + "\"";
            }
            tags += kws + "]";
        }
        
		if (horoscopeType != null) {
			if (tags.length() > 1)
				tags += ",";
			tags += "\"horoscopeTag\":{";
				tags += "\"horoscopeType\":\"" + horoscopeType + "\"";
			if (postForm.equals("mainpost")) {
				tags += ",\"horoscopeMaster\":{";
				tags += "\"userId\":\"" + masterId + "\",";
				tags += "\"displayName\":\"" + masterDisplayName + "\",";
				tags += "\"avatarUrl\":\"" + masterAvatarUrl + "\",";
				tags += "\"description\":\"" + masterDescription + "\",";
				tags += "\"externalLink\":\"" + masterExtLink + "\"";
				tags += "}";
			}
			tags += "}";
		}
		if(content != null){
			if (tags.length() > 1)
				tags += ",";
			tags += "\"userDefTags\":[";
			Set<String> hashTagSet = postService.extractHashtagsFromText(content);
			if (hashTagSet != null && hashTagSet.size() > 0) {
				for (String hashTag : hashTagSet) {
					tags += "\"" + hashTag + "\",";
				}
				if (tags.endsWith(","))
					tags = tags.substring(0, tags.length() - 1);
			}
			tags += "]";
		}
		tags += "}";

		if(promoteScore == null)
		    promoteScore = -1L;
        if(postService.updatePost(handlerUserId, null, postId, appName, postType, title, content, selCircles, attacStr, tags, postStatus, promoteScore, extLookUrl, lookTypeId, null) == null)
            return new ErrorResolution(400, "Bad request");
        
        return new StreamingResolution("text/html", "OK");
    }
    
    public Resolution upload() {
        Boolean isLogin = false;
        HttpSession session = getContext().getRequest().getSession();
        Long userId=(long)0;
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                userId = loginSession.getUserId();
            }
        }
        
        if(!isLogin || userId == null || dataUrl.equals("") || metadata.equals("")) {
            return new StreamingResolution("text/html", "");
        }

        FileItem fileItem = null;
        try {
            fileItem = fileService.createImageFile(userId, dataUrl, metadata, fileType);
            
        } catch (InvalidMetadataException | InvalidFileTypeException | IOException e) {
            e.printStackTrace();
            return new StreamingResolution("text/html", "");
        }
        
        if(fileItem == null)
            return new StreamingResolution("text/html", "");
        else {
            String response = String.format("{\"fileId\" : \"%d\", \"fileType\" : \"%s\", \"metadata\" :%s}", fileItem.getFile().getId(), fileType, fileItem.getMetadata());
            return json(response);
        }
            
    }
    
    public Resolution listAttachablePost() {
        boolean isLogin = false;
        Long userId = null;
        HttpSession session = getContext().getRequest().getSession();
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                User curUser = loginSession.getUser();
                userId = curUser.getId();
            }
        }
        
        if(!isLogin || userId == null) {
            return new StreamingResolution("text/html", "Need to login");
        }
        
        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit(pageLimit.getStartIndex(), pageLimit.getPageSize());
        blockLimit.addOrderBy("createdTime", false);
        List<Long> userIds = new ArrayList<Long>();
        userIds.add(userId);
        final PageResult<Post> posts = postService.listPostByUsers(userIds, null, null, blockLimit).getResult();
        pageResult = new PageResult<MainPostSimpleWrapper>();
        pageResult.setTotalSize(posts.getTotalSize());
        
        Set<Long> lookTypeIds = new HashSet<Long>();
        for(Post c : posts.getResults()) {
            if(c.getLookTypeId() != null)
                lookTypeIds.add(c.getLookTypeId());
        }
        
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(posts.getResults(), ThumbnailType.Detail);//.List);
		Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(posts.getResults());
		Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        for (final Post p : posts.getResults()) {
            LookType lt = lookTypeMap.get(p.getLookTypeId());
            MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, postFileItems.get(p.getId()), postCircles.get(p.getId()), lt);
            pageResult.add(pw);
        }
        return forward();
    }
    
    public Resolution verifyHoroscopeMaster() {
    	if(masterId == null || masterId.length() <= 0)
    		return forward();
		Long mastersId = Long.valueOf(masterId);
		if(!userDao.exists(mastersId))
			return json("Invalid horoscope masterId");
		
		User horoscopeMaster = userDao.findById(mastersId);
		String horoscopeMasterInfo = "{\"displayName\":\"" + horoscopeMaster.getDisplayName() + "\",";
				horoscopeMasterInfo += "\"avatarUrl\":\"" + horoscopeMaster.getAvatarUrl() + "\"}";
	    return json(horoscopeMasterInfo);
    }

    public PageResult<MainPostSimpleWrapper> getPageResult() {
        return pageResult;
    }
    
    public String getUserDisplayName() {
        return userDisplayName;
    }
    
    public String getUserAvater() {
        return userAvater;
    }
    
    public String getPostForm() {
        return postForm;
    }
    
    public MainPostSimpleWrapper getRelPost() {
        return relPost; 
    }
    
    public Long getParentPostId() {
        return parentPostId;
    }
    
    public List<String> getAttachMetadatas() {
        return attachMetadatas;
    }
    
}
