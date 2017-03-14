package com.cyberlink.cosmetic.action.api.post;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractMsrAction;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.MainPostWrapper;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAttachments;
import com.cyberlink.cosmetic.modules.post.model.PostFile;
import com.cyberlink.cosmetic.modules.post.model.PostHoroscopeTag;
import com.cyberlink.cosmetic.modules.post.model.PostLook;
import com.cyberlink.cosmetic.modules.post.model.PostProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostTags;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.model.SubPostWrapper;
import com.cyberlink.cosmetic.modules.post.result.FullPostWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostDetailWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Attachments;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.File;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Look;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.result.SubPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.SubPostSimpleWrapper.Tags;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/post/PostManage.action")
public class PostManageAction extends AbstractMsrAction {

    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("circle.circleService")
    protected CircleService circleService;
    
    @SpringBean("file.fileService")
    private FileService fileService;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    private String postGroup;
    private Long postId;
    private String defaultPostSource = "native_posting";
    private AppName appName = AppName.BACKEND_V1;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PostInput {
        private Long postId = null;
        private Long creatorId = null;
        private PostStatus postStatus = null;
        private String extLookUrl = null;
        private Date createdTime = null;
        private AppName appName = null;
        private PostType postType = PostType.NORMAL;
        private String tags = null;
        
        public PostInput() {
            
        }
        public Long getPostId() {
            return postId;
        }
        public void setPostId(Long postId) {
            this.postId = postId;
        }
        public Long getCreatorId() {
            return creatorId;
        }
        public void setCreatorId(Long creatorId) {
            this.creatorId = creatorId;
        }
        public PostStatus getPostStatus() {
            return postStatus;
        }
        public void setPostStatus(PostStatus postStatus) {
            this.postStatus = postStatus;
        }
        public String getExtLookUrl() {
            return extLookUrl;
        }
        public void setExtLookUrl(String extLookUrl) {
            this.extLookUrl = extLookUrl;
        }
        public Date getCreatedTime() {
            return createdTime;
        }
        public void setCreatedTime(Date createdTime) {
            this.createdTime = createdTime;
        }
        public AppName getAppName() {
            return appName;
        }
        public void setAppName(AppName appName) {
            this.appName = appName;
        }
        public PostType getPostType() {
            return postType;
        }
        public void setPostType(PostType postType) {
            this.postType = postType;
        }
		public String getTags() {
			return tags;
		}
		public void setTags(String tags) {
			this.tags = tags;
		}
    }
    
	public Resolution update() {
	    MsrApiResult apiResult = new MsrApiResult();
        if(postGroup == null) {
            apiResult.setError("Invalid post group format.");
            return apiResult.getResult();
        }    
        
        try {
            List<PostInput> tmp = objectMapper.readValue(postGroup, new TypeReference<List<PostInput>>() {});
            List<Long> postIds = new ArrayList<Long>();
            Map<Long, PostInput> postMap = new HashMap<Long, PostInput>();
            for(PostInput input : tmp) {
                postMap.put(input.getPostId(), input);
                if(input.getCreatorId() == null)
                    postIds.add(input.getPostId());
            }
            List<Post> posts = postService.findPostByIds(postIds);
            for(Post p : posts)
                postMap.get(p.getId()).setCreatorId(p.getCreatorId());
            for(PostInput postInput : postMap.values())
                updatePost(postInput);
        }
        catch(Exception ex) {
            apiResult.setError(ex.getMessage());
            return apiResult.getResult();
        }
        return apiResult.getResult();
	}
	
	public Resolution toProduction() {
	    MsrApiResult apiResult = new MsrApiResult();
	    if(postId == null) {
            apiResult.setError("Invalid postId.");
            return apiResult.getResult();
        }    
	    
	    Long prodPostId = null;
	    try {
            List<Post> posts = movePost("http://" + Constants.getMsrPreviewUrl(), postId);
            if(posts.size() > 0) {
                for(Post p : posts) {
                    if(p.getParentId() == null) {
                        prodPostId = p.getId();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            apiResult.setError(e.getMessage());
            return apiResult.getResult();
        }
	    apiResult.Add("postId", prodPostId);
	    return apiResult.getResult();
	}
	
	public Resolution availableLocale() {
        MsrApiResult apiResult = new MsrApiResult();
        apiResult.Add("locales", localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
        return apiResult.getResult();
    }
	
	public Resolution getRelLocale() {
        MsrApiResult apiResult = new MsrApiResult();
        if(postId == null) {
            apiResult.setError("Invalid postId");
            return apiResult.getResult();
        }
        
        List<Long> postIds = new ArrayList<Long>();
        postIds.add(postId);
        List<Post> posts = postService.findPostByIds(postIds);
        if(posts == null || posts.size() <= 0) {
            apiResult.setError("Invalid postId");
            return apiResult.getResult();
        }
        
        String postLocale = posts.get(0).getLocale();
        if(postLocale == null || postLocale.length() <= 0) {
            apiResult.setError("Invalid post locale");
            return apiResult.getResult();
        }
        
        apiResult.Add("locales", localeDao.getAvailableInputLocaleByType(postLocale, LocaleType.POST_LOCALE));
        return apiResult.getResult();
    }
	
	private FullPostWrapper getPostResponse(String previewUrl, Long postId) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("postId", postId.toString());
        InputStream postUrlStream = doPost(previewUrl + "/api/post/query-complete-post.action", params, null, null, "UTF-8");
        String postResponse = IOUtils.toString(postUrlStream, "UTF-8");
        return objectMapper.readValue(postResponse, FullPostWrapper.class);
	}
	
	@SuppressWarnings("unchecked")
    private String getUserResponse(String previewUrl, Long userId) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId.toString());
        InputStream userUrlStream = doPost(previewUrl + "/api/user/info.action", params, null, null, "UTF-8");
        String userResponse = IOUtils.toString(userUrlStream, "UTF-8");
        Map<String, Object> tmpResult = objectMapper.readValue(userResponse, new TypeReference<Map<String, Object>>() {});
        Object result = tmpResult.get("result");
        if(result == null || !(result instanceof Map)) {
            return null;
        }
        Map<String, Object> info = (Map<String, Object>) result;
        return (String) info.get("email"); 
    }
	
	private List<Post> movePost(String previewUrl, Long postId) throws Exception {
        FullPostWrapper fullPost = getPostResponse(previewUrl, postId);
        MainPostDetailWrapper mainPost = fullPost.getMainPost();
        Long creatorId = mainPost.getCreator().getUserId();
        String creatorEmail = getUserResponse(previewUrl, creatorId);
        if(creatorEmail == null)
            throw new Exception("Invalid post's creator");
        User prodCreator = getUserByEmail(creatorEmail); 
        String postLocale = "en_US";
        Set<String> locales = localeDao.getLocaleByType(prodCreator.getRegion(), LocaleType.POST_LOCALE);
        if(locales != null && locales.size() > 0) {
            postLocale = locales.iterator().next();
        }
        MainPostWrapper mainPostInput = responsePostToInput(prodCreator.getId(), postLocale, mainPost);
        List<SubPostSimpleWrapper> subPosts = fullPost.getSubPosts();
        List<SubPostWrapper> subPostsInput = responseSubPostToInput(prodCreator.getId(), subPosts);
        String mainPostParams = objectMapper.writeValueAsString(mainPostInput);
        List<String> subPostsParams = new ArrayList<String>();
        for(SubPostWrapper spw : subPostsInput) {
            String subPostParams = objectMapper.writeValueAsString(spw);
            subPostsParams.add(subPostParams);
        }
        
        PostApiResult<List<Post>> result = postService.createPosts(prodCreator.getId(), postLocale, null, mainPostParams, defaultPostSource, appName, null, subPostsParams, null);
        if(!result.success())
            throw new Exception(result.getErrorDef().message());
        return result.getResult();
	}
	
	private PostFile moveFiles(Long userId, FileType fileType, String metadata) throws Exception {
	    JsonNode metadataNode = objectMapper.readValue(metadata, JsonNode.class);
	    String originalUrl = null, fileName = null, filePath = null, contentType = null, md5 = null;
	    Integer width = null, height = null;
	    Long fileSize = null;
	    JsonNode attrNode = metadataNode.get("fileSize");
        if(attrNode != null)
            fileSize = attrNode.asLong();
        attrNode = metadataNode.get("width");
        if(attrNode != null)
            width = attrNode.asInt();
        attrNode = metadataNode.get("height");
        if(attrNode != null)
            height = attrNode.asInt();
        attrNode = metadataNode.get("md5");
        if(attrNode != null)
            md5 = attrNode.asText();        
        
        attrNode = metadataNode.get("originalUrl");
        if(attrNode != null)
            originalUrl = attrNode.asText();
        if(originalUrl == null || originalUrl.length() <= 0)
            throw new Exception("Invalid photo url");
        
        String cdnDomain = Constants.getCdnDomain();
        String bcCdnDomain = Constants.getBcCdnDomain();
        if(bcCdnDomain != null && bcCdnDomain.length() > 0 && originalUrl.contains(bcCdnDomain))
            filePath = originalUrl.substring(("http://" + bcCdnDomain + "/").length());
        else if(cdnDomain != null && cdnDomain.length() > 0 && originalUrl.contains(cdnDomain))
            filePath = originalUrl.substring(("http://" + cdnDomain + "/").length());
        else
            return null;
        fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        contentType = "image/" + fileName.substring(fileName.lastIndexOf(".") + 1);
	    FileItem fi = fileService.createBcFile(userId, fileType, metadata, fileName, filePath, fileSize, contentType, md5, width, height);
	    if(fi == null)
	        return null;
	    PostFile result = new PostFile();
	    result.fileId = fi.getFile().getId();
	    result.fileType = fi.getFile().getFileType().toString();
	    result.setMetadata(fi.getMetadata());
	    return result;
	}
	
	private PostLook moveLooks(Long lookId) { // Not yet implemented
	    return null;
	}
	
	private PostProductTag moveProductTag(PostProductTag productTags) { // Not yet implemented
	    return null;
	}
	
	private PostHoroscopeTag moveHoroscopeTag(PostHoroscopeTag horoscopeTag) throws Exception {
		if (horoscopeTag == null)
			return null;

		if(horoscopeTag.horoscopeMaster != null){
			Map<String, String> masterInfo = (LinkedHashMap<String, String>) horoscopeTag.horoscopeMaster;
			String userId = masterInfo.get("userId");
			if (!userId.isEmpty()) {
				Long creatorId = Long.parseLong(userId);
				String previewUrl = "http://" + Constants.getMsrPreviewUrl();
				String creatorEmail = getUserResponse(previewUrl, creatorId);
				if (creatorEmail == null)
					throw new Exception("Invalid post's creator");
				User prodCreator = getUserByEmail(creatorEmail);
				masterInfo.put("userId", prodCreator.getId().toString());
				masterInfo.put("displayName", prodCreator.getDisplayName());
				masterInfo.put("avatarUrl", prodCreator.getAvatarUrl());
			}
			horoscopeTag.horoscopeMaster = masterInfo;
		}
	    return horoscopeTag;
	}
	
	private List<SubPostWrapper> responseSubPostToInput(Long descUserId, List<SubPostSimpleWrapper> subPosts) throws Exception {
	    List<SubPostWrapper> subPostsInput = new ArrayList<SubPostWrapper>();
	    for(SubPostSimpleWrapper spsw : subPosts) {
	        SubPostWrapper spw = new SubPostWrapper();
	        spw.content = spsw.getContent();
	        
	        com.cyberlink.cosmetic.modules.post.result.SubPostSimpleWrapper.Attachments attachments = spsw.getAttachments();
	        List<com.cyberlink.cosmetic.modules.post.result.SubPostSimpleWrapper.File> files = attachments.getFiles();
	        if(files.size() > 0) {
	            spw.attachments = new PostAttachments();
	            spw.attachments.files = new ArrayList<PostFile>();
	        }
	        com.cyberlink.cosmetic.modules.post.result.SubPostSimpleWrapper.File toCopy = null;
	        for(com.cyberlink.cosmetic.modules.post.result.SubPostSimpleWrapper.File f : files) {
	            if("Photo".equalsIgnoreCase(f.getFileType())) {
                    toCopy = f;
                    break;
	            }
	        }
	        
	        if(toCopy == null && files.size() > 0)
	            toCopy = files.get(0);
	        if(toCopy != null) {
    	        PostFile pF = moveFiles(descUserId, FileType.valueOf(toCopy.getFileType()), toCopy.getMetadata());
                if(pF != null)
                    spw.attachments.files.add(pF);
	        }
            
	        Tags srcTags = spsw.getTags();
	        if(srcTags != null) {
	        	spw.tags = new PostTags();
    	        List<PostProductTag> productTags = srcTags.getProductTags();
    	        if((productTags != null && productTags.size() > 0)) {
    	            for(PostProductTag ppt : productTags) {
    	                PostProductTag movedPpt = moveProductTag(ppt);
    	                if(movedPpt != null)
    	                    spw.tags.getProductTags().add(movedPpt);
    	            }
    	        }
	        	spw.tags.lookTag = srcTags.getLookTag();
	        	spw.tags.horoscopeTag = srcTags.getHoroscopeTag();
	        }
	        spw.extLookUrl = spsw.getExtLookUrl();
	        subPostsInput.add(spw);
	    }
	    return subPostsInput;
	}
	
	private MainPostWrapper responsePostToInput(Long descUserId, String locale, MainPostDetailWrapper mainPost) throws Exception {
	    PageResult<Circle> descCircles = circleService.listUserCircle(descUserId, false, locale, true, new BlockLimit(0, 100));
	    MainPostWrapper mainPostInput = new MainPostWrapper();
	    mainPostInput.circleIds = new ArrayList<Long>();
	    List<MainPostDetailWrapper.DPWCircle> circles = mainPost.getCircles();
	    for(MainPostDetailWrapper.DPWCircle dpwc : circles) {
	        String defaultType = dpwc.getDefaultType();
	        String circleName = dpwc.getCircleName();
	        if(defaultType != null) {
	            for(Circle prodCircle : descCircles.getResults()) {
	                if(prodCircle.getDefaultType() == null ||  !prodCircle.getDefaultType().equals(defaultType))
	                    continue;
	                mainPostInput.circleIds.add(prodCircle.getId());
	                break;
	            }
	        }
	        else if(circleName != null) {
	            for(Circle prodCircle : descCircles.getResults()) {
                    if(prodCircle.getCircleName() == null ||  !prodCircle.getCircleName().equals(circleName))
                        continue;
                    mainPostInput.circleIds.add(prodCircle.getId());
                    break;
                }
	        }
	        if(mainPostInput.circleIds.size() > 0)
	            break;
	    }
	        
	    if(mainPostInput.circleIds.size() <= 0)
	        throw new Exception("Invalid post's circle");
	    
	    mainPostInput.title = mainPost.getTitle();
	    mainPostInput.content = mainPost.getContent();
	    mainPostInput.postType = mainPost.getPostType();
	    mainPostInput.postStatus = mainPost.getStatus();
	    mainPostInput.extLookUrl = mainPost.getExtLookUrl();
	    
	    Attachments attachments = mainPost.getAttachments();
	    List<File> files = attachments.getFiles();
        if(files.size() > 0) {
            mainPostInput.attachments = new PostAttachments();
            mainPostInput.attachments.files = new ArrayList<PostFile>();
        }
        File toCopy = null;
        for(File f : files) {
            if("Photo".equalsIgnoreCase(f.getFileType())) {
                toCopy = f;
                break;
            }
        }
        
        if(toCopy == null && files.size() > 0)
            toCopy = files.get(0);
        if(toCopy != null) {
            PostFile pF = moveFiles(descUserId, FileType.valueOf(toCopy.getFileType()), toCopy.getMetadata());
            if(pF != null)
                mainPostInput.attachments.files.add(pF);
        }
        
        PostTags srcTags = mainPost.getTags();
        if(srcTags != null) {
        	mainPostInput.tags = new PostTags();
            List<PostProductTag> productTags = srcTags.getProductTags();
            if((productTags != null && productTags.size() > 0)) {
                for(PostProductTag ppt : productTags) {
                    PostProductTag movedPpt = moveProductTag(ppt);
                    if(movedPpt != null)
                        mainPostInput.tags.getProductTags().add(movedPpt);
                }
            }
            mainPostInput.tags.setUserDefTags(srcTags.getUserDefTags());
            mainPostInput.tags.setKeywords(srcTags.getKeywords());
			mainPostInput.tags.lookTag = srcTags.getLookTag();
			mainPostInput.tags.horoscopeTag = moveHoroscopeTag(srcTags.getHoroscopeTag());
        }
	    return mainPostInput;
	}
	
	private void updatePost(PostInput postInput) {
	    postService.updatePost(postInput.getCreatorId(), null, postInput.getPostId(), postInput.getAppName(), postInput.getPostType(), null, null, null, null, postInput.getTags(), postInput.getPostStatus(), null, postInput.getExtLookUrl(), null, postInput.getCreatedTime());
	}

	public String getPostGroup() {
        return postGroup;
    }

    public void setPostGroup(String postGroup) {
        this.postGroup = postGroup;
    }
    
    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
