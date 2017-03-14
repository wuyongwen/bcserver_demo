package com.cyberlink.cosmetic.action.backend.post;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleTag;
import com.cyberlink.cosmetic.modules.circle.model.CircleTagGroup;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.exception.InvalidFileTypeException;
import com.cyberlink.cosmetic.modules.file.exception.InvalidMetadataException;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.result.MainPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.post.service.impl.DocPostConverter;
import com.cyberlink.cosmetic.modules.post.service.impl.DocPostConverter.ImageHandler;
import com.cyberlink.cosmetic.modules.post.service.impl.DocPostConverter.MainPostContent;
import com.cyberlink.cosmetic.modules.post.service.impl.DocPostConverter.PostContent;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.json.JsonObject;

@UrlBinding("/post/CreatePost.action")
public class CreatePostAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("file.fileService")
    private FileService fileService;
    
    @SpringBean("circle.circleDao")
    private CircleDao circleDao;
    
    @SpringBean("circle.circleTypeDao")
    private CircleTypeDao circleTypeDao;
    
    @SpringBean("circle.circleTagDao")
    private CircleTagDao circleTagDao;
    
    @SpringBean("circle.circleTagGroupDao")
    private CircleTagGroupDao circleTagGroupDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("file.fileDao")
    private FileDao fileDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("circle.circleService")
    private CircleService circleService;
    
    @SpringBean("web.objectMapper")
	private ObjectMapper objectMapper;
    
	@SpringBean("look.LookTypeDao")
	private LookTypeDao lookTypeDao;
    
    // Post
    private static final String loginMessage = "You need to login";
    private static final String creatorMessage = "You are not the owner of this MainPost";
    
    private String postForm="mainpost";
	private Long mainPostId = (long)0;
	private PostType postType = PostType.NORMAL;
	private AppName appName = AppName.BACKEND_V1;
	private String title;
    private String locale = "en_US";
    private List<Long> selCircles;
    private Set<String> selKeywords;
    private String content;
    private List<String> emoji;
    private String lookTag;
    private String horoscopeType;
    private String masterId = "";
    private String masterDisplayName = "";
	private String masterAvatarUrl = "";
    private String masterDescription = "";
    private String masterExtLink = "";
	private List<String> attachments;
    private String postId="0";
    private List<Circle> circles = new ArrayList<Circle>(0);
    private List<CircleTag> circleTags = new ArrayList<CircleTag>(0);
    private String postTypeMap = "{}";
    private List<LookType> lookTypes = new ArrayList<LookType>();
	private PageResult<MainPostSimpleWrapper> pageResult;
    private String userDisplayName;
    private String userAvater;
    private String extUrl;
    private Long promoteScore;
    private String extLookUrl;
    private Long lookTypeId;
	private FileBean docxPost;
    
    private String defaultPostSource = "native_posting";

	// Duplicate post
    private Long srcPostCreatorId;
    private Boolean likeIt = true;
    
    public void setSrcPostCreatorId(Long srcPostCreatorId) {
        this.srcPostCreatorId = srcPostCreatorId;
    }
    
    public void setLikeIt(Boolean likeIt) {
        this.likeIt = likeIt;
    }
    
    @Validate(required = true, on = "route")
    public void setPostForm(String postForm) {
        this.postForm = postForm;
    }
    
    public void setMainPostId(Long mainPostId) {
        this.mainPostId = mainPostId;
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
	
    public void setPostId(String postId) {
        this.postId = postId;
    }
    
    public void setContent(String content) {
		if (content == null)
			this.content = "";
		else
			this.content = content;
    }
    
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }
    
    public void setSelCircles(List<Long> selCircles) {
        this.selCircles = selCircles;
    }
    
    public Set<String> getSelKeywords() {
        return selKeywords;
    }

    public void setSelKeywords(Set<String> selKeywords) {
        this.selKeywords = selKeywords;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setEmoji(List<String> emoji) {
        this.emoji = emoji;
    }
    
    public String getLookTag() {
        return lookTag;
    }

    public void setLookTag(String lookTag) {
        this.lookTag = lookTag;
    }
    
	public String getHoroscopeType() {
		return horoscopeType;
	}
	
	public void setHoroscopeType(String horoscopeType) {
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
	
	public void setDocxPost(FileBean docxPost) {
        this.docxPost = docxPost;
    }
    public String getPostForm() {
       return postForm;
    }
    
    public Long getMainPostId(){
        return mainPostId;
    }
    
    public void setExtUrl(String extUrl) {
        this.extUrl = extUrl;
    }
    
    public Long getPromoteScore() {
        return promoteScore;
    }
    
    public void setPromoteScore(Long promoteScore) {
        this.promoteScore = promoteScore;
    }
    
    public String getExtLookUrl() {
        return extLookUrl;
    }

    public void setExtLookUrl(String extLookUrl) {
        this.extLookUrl = extLookUrl;
    }
    
	public Long getLookTypeId() {
		return lookTypeId;
	}
	
	public void setLookTypeId(Long lookTypeId) {
		this.lookTypeId = lookTypeId;
	}

    @DefaultHandler
    public Resolution route() {       
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
            return new StreamingResolution("text/html", loginMessage);
        }
        
        String region = "en_US";
        Set<String> postLocale = localeDao.getLocaleByType(user.getRegion(), LocaleType.POST_LOCALE);
        if(postLocale != null && postLocale.size() > 0)
            region = postLocale.iterator().next();
        
        PageResult<Circle> pageResult = circleService.listUserCircle(userId, true, region, true, new BlockLimit(0, 100));
        for(Circle cir : pageResult.getResults()) {
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
        return forward();
    }
    
    public Resolution createSubPost() {
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
        
        if(!isLogin || userId == null)
        	return json(loginMessage);
        
        Long lPostId = Long.valueOf(postId);
        Long mainPostCreatorId = postService.queryPostById(lPostId).getResult().getCreator().getId();
        if(!userId.equals(mainPostCreatorId)) {
        	String response = creatorMessage;
        	return json(response);
        }
        
        String attachsJson = "";
        if(attachments != null && attachments.size() > 0)
        {
            attachsJson += "{\"files\":[";
            for(int idx = 0; idx < attachments.size(); idx++) {
                String attcStr = attachments.get(idx);
                JsonObject obj = new JsonObject(attcStr);
                Long fileId = Long.valueOf(obj.getString("fileId"));
                if(fileId != null) {
                    obj.remove("fileId");
                }
                String fileType = obj.getString("fileType");
                if(fileType != null && fileType.length() > 0) {
                    obj.remove("fileType");
                }

                attachsJson += "{";
                if(!fileId.equals((long)0))
                    attachsJson += "\"fileId\":"+ fileId.toString() + ",";
                attachsJson += "\"fileType\":\""+ fileType +"\",\"metadata\":";
                attachsJson += obj.toString() + "}";
                if(idx < attachments.size() - 1)
                    attachsJson += ",";
            }
            attachsJson += "]}";
        }

        String tags = "{";
        if(emoji != null && emoji.size() > 0)
        {
            tags += "\"emojiTags\":[";
            for(int idx = 0; idx < emoji.size(); idx++) {
                tags += "\"" + emoji.get(idx) + "\"";
                if(idx < emoji.size() - 1)
                    tags += ",";
            }
            tags += "]";
        }
        if(lookTag != null && lookTag.length() > 0) {
            if(tags.length() > 1)
                tags += ",";
            tags += "\"lookTag\":\"" + lookTag + "\"";
        }
		if (horoscopeType != null && horoscopeType.length() > 0) {
			if (tags.length() > 1)
				tags += ",";
			tags += "\"horoscopeTag\":{";
				tags += "\"horoscopeType\":\"" + horoscopeType + "\"";
			tags += "}";
		}
        tags += "}";
        
        MainPostBaseWrapper pw = new MainPostBaseWrapper(postService.createSubPost(mainPostCreatorId, lPostId, content, attachsJson, tags, extLookUrl, PostStatus.Published, PostType.NORMAL).getResult());
        String response = String.format("{\"subPostId\" : \"%d\"}", pw.getPostId());
        return json(response);
    }
    
    public Resolution create() {
        Boolean isLogin = false;
        HttpSession session = getContext().getRequest().getSession();
        Long userId=(long)0;

        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                userId = loginSession.getUserId();
                locale = loginSession.getUser().getRegion();
            }
        }
        
        if(!isLogin || userId == null)
        	return json(loginMessage);
        
        String attachsJson = "";
        if(attachments != null && attachments.size() > 0)
        {
            attachsJson += "{\"files\":[";
            for(int idx = 0; idx < attachments.size(); idx++) {
                String attcStr = attachments.get(idx);
                JsonObject obj = new JsonObject(attcStr);
                Long fileId = Long.valueOf(obj.getString("fileId"));
                if(fileId != null) {
                    obj.remove("fileId");
                }
                String fileType = obj.getString("fileType");
                if(fileType != null && fileType.length() > 0) {
                    obj.remove("fileType");
                }

                attachsJson += "{";
                if(!fileId.equals((long)0))
                    attachsJson += "\"fileId\":"+ fileId.toString() + ",";
                attachsJson += "\"fileType\":\""+ fileType +"\",\"metadata\":";
                attachsJson += obj.toString() + "}";
                if(idx < attachments.size() - 1)
                    attachsJson += ",";
            }
            attachsJson += "]}";
        }
        
        String tags = "{";
        if(emoji != null && emoji.size() > 0)
        {
            if(tags.length() > 1)
                tags += ",";
            tags += "\"emojiTags\":[";
            for(int idx = 0; idx < emoji.size(); idx++) {
                tags += "\"" + emoji.get(idx) + "\"";
                if(idx < emoji.size() - 1)
                    tags += ",";
            }
            tags += "]";
        }
        if(lookTag != null && lookTag.length() > 0) {
            if(tags.length() > 1)
                tags += ",";
            tags += "\"lookTag\":\"" + lookTag + "\"";
        }
        if(selKeywords != null && selKeywords.size() > 0) {
            if(tags.length() > 1)
                tags += ",";
            tags += "\"keywords\":[";
            String kws = "";
            for(String kw : selKeywords) {
                if(kws.length() > 0)
                    kws += ",";
                kws += "\"" + kw + "\"";
            }
            tags += kws + "]";
        }
		if (horoscopeType != null && horoscopeType.length() > 0) {
			if (tags.length() > 1)
				tags += ",";
			tags += "\"horoscopeTag\":{";
				tags += "\"horoscopeType\":\"" + horoscopeType + "\"";
				tags += ",\"horoscopeMaster\":{";
					tags += "\"userId\":\"" + masterId + "\",";
					tags += "\"displayName\":\"" + masterDisplayName + "\",";
					tags += "\"avatarUrl\":\"" + masterAvatarUrl + "\",";
					tags += "\"description\":\"" + masterDescription + "\",";
					tags += "\"externalLink\":\"" + masterExtLink + "\"";
				tags += "}";
			tags += "}";
		}
		Set<String> hashTagSet = postService.extractHashtagsFromText(content);
		if (hashTagSet != null && hashTagSet.size() > 0) {
			if (tags.length() > 1)
				tags += ",";
			tags += "\"userDefTags\":[";
			for (String hashTag : hashTagSet) {
				tags += "\"" + hashTag + "\",";
			}
			if (tags.endsWith(","))
				tags = tags.substring(0, tags.length() - 1);
			tags += "]";
		}
        tags += "}";
        Set<String> postLocale = localeDao.getLocaleByType(locale, LocaleType.POST_LOCALE);
        MainPostBaseWrapper pw = new MainPostBaseWrapper(postService.createPost(userId, postLocale.iterator().next(), null, title, content, selCircles, attachsJson, tags, PostStatus.Drafted, defaultPostSource, appName, postType, promoteScore, lookTypeId, extLookUrl, null).getResult());
        String response = String.format("{\"postId\" : \"%d\"}", pw.getPostId());
        return json(response);
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
        
        for (final Post p : posts.getResults()) {
            MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, null, null, null);
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
    
    private Long docToPost(FileBean file, final Long userId) {
        String postDocDir = Constants.getStorageLocalRoot() + "/postDoc/";
        File dPostDocDir = new File(postDocDir);

        if (!dPostDocDir.exists())
        {
            if(!dPostDocDir.mkdir()) {
                logger.error("Failed to create directory :" + postDocDir);
                return null;
            }
        }
        
        Document document = null;
        DocPostConverter convertor = null;
        HWPFDocumentCore wordDocument = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            convertor = new DocPostConverter(document);
            wordDocument = new HWPFDocument(file.getInputStream());
            convertor.imageHandler = new ImageHandler() {
                public String handleImage(String dataUrl, String metadata, boolean isCover) {
                    FileItem fileItem = null;
                    try {
                        FileType fileType = FileType.Photo;
                        if(isCover)
                            fileType = FileType.PostCover;
                        fileItem = fileService.createImageFile(userId, dataUrl, metadata, fileType);
                    } catch (InvalidMetadataException | InvalidFileTypeException | IOException e) {
                        e.printStackTrace();
                        return "";
                    }
                    
                    if(fileItem == null)
                        return "";
                    
                    String attachment = "{\"fileId\":" + String.valueOf(fileItem.getFile().getId());
                    attachment += ",\"metadata\":" +  fileItem.getMetadata();
                    attachment += "}";
                    return attachment;
                }
            };
            convertor.processDocument(wordDocument);
            List<DocPostConverter.PostContent> posts = convertor.getPost();
            if(posts.size() < 1)
                return null;
            
            List<Long> defaultCircleCode = new ArrayList<Long>();
            defaultCircleCode.add((long)143);
            String jAttachments = null;
            MainPostContent mainPostContent = (MainPostContent)posts.get(0);
            if(mainPostContent.attachments.size() >= 0)
            {
                jAttachments = "{\"files\":[";
                for(int aIdx = 0; aIdx < mainPostContent.attachments.size(); aIdx++) {
                    if(aIdx != 0)
                        jAttachments += ",";
                    jAttachments += mainPostContent.attachments.get(aIdx);
                }
                jAttachments += "]}";
            }
            
            Set<String> postLocale = localeDao.getLocaleByType(locale, LocaleType.POST_LOCALE);
            Post mainPost = postService.createPost(userId, postLocale.iterator().next(), null, mainPostContent.title, mainPostContent.content, defaultCircleCode, jAttachments, null, PostStatus.Drafted, defaultPostSource, appName, null, promoteScore, null, null, null).getResult();
            if(mainPost == null)
                return null;
            
            List<Post> succeeededPosts = new ArrayList<Post>();
            Long mainPostId = mainPost.getId();
            succeeededPosts.add(mainPost);
            for(int idx = 1; idx < posts.size(); idx++) {
                PostContent curPostContent = posts.get(idx);
                String jSubAttachments = null;
                if(curPostContent.attachments.size() >= 0)
                {
                    jSubAttachments = "{\"files\":[";
                    for(int aIdx = 0; aIdx < curPostContent.attachments.size(); aIdx++) {
                        if(aIdx != 0)
                            jSubAttachments += ",";
                        jSubAttachments += curPostContent.attachments.get(aIdx);
                    }
                    jSubAttachments += "]}";
                }

                Post subPost = postService.createSubPost(userId, mainPostId, curPostContent.content, jSubAttachments, null, null, PostStatus.Published, null).getResult();
                if(subPost != null) {
                    succeeededPosts.add(subPost);
                }
                else {
                    mainPostId = null;
                    for(Post toDelPost : succeeededPosts) {
                        toDelPost.setIsDeleted(true);
                        postService.updatePost(toDelPost);
                    }
                    break;
                }
            }
            return mainPostId;
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public class OgClass {
        @JsonView(Views.Simple.class)
        String title = "";
        
        @JsonView(Views.Simple.class)
        Set<String> images = new HashSet<String>();;
        
        @JsonView(Views.Simple.class)
        String content = "";
    }
    
    public Resolution getDataUrl() {
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
            return new ErrorResolution(401, "Need to login");
        }

        try {
            String mimeType;
            int pushbackLimit = 100;
            extUrl = URLDecoder.decode(extUrl, "UTF-8");
            URL url = new URL(extUrl);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent",  this.getServletRequest().getHeader("User-Agent"));
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream urlStream = connection.getInputStream();
            PushbackInputStream pushUrlStream = new PushbackInputStream(urlStream, pushbackLimit);
            byte [] firstBytes = new byte[pushbackLimit];
            pushUrlStream.read(firstBytes);
            pushUrlStream.unread(firstBytes);

            ByteArrayInputStream bais = new ByteArrayInputStream(firstBytes);
            mimeType = URLConnection.guessContentTypeFromStream(bais);
            if (mimeType.startsWith("image/")) {
                BufferedImage inputImage = ImageIO.read(pushUrlStream);
                String imageType = mimeType.substring("image/".length());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write( inputImage, imageType, baos);
                baos.flush();
                Resolution result = json("data:" + mimeType + ";base64," + Base64.encodeBase64String(baos.toByteArray())); 
                baos.close();
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        
        
        return new ErrorResolution(400, "Bad request");
    }
    
    public Resolution getMetaTagFromUrl() {
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
            return new ErrorResolution(401, "Need to login");
        }
        
        try {
            String userAgent = this.getServletRequest().getHeader("User-Agent");
            URL aURL = new URL(extUrl);
            Connection con = Jsoup.connect(extUrl).userAgent(userAgent).timeout(3000);
            org.jsoup.nodes.Document doc = con.get();
            Elements metas = doc.select("head meta");
            OgClass og = new OgClass();
            for(int idx = 0; idx < metas.size(); idx++) {
                org.jsoup.nodes.Element meta = metas.get(idx);
                String propertyAttr = meta.attr("property");
                if(propertyAttr.equalsIgnoreCase("og:title")) {
                    og.title = meta.attr("content");
                }
                else if(propertyAttr.equalsIgnoreCase("og:image")) {
                    og.images.add(meta.attr("content"));
                }
                else if(propertyAttr.equalsIgnoreCase("og:description")) {
                    og.content = meta.attr("content");
                }
            }
            for(int idx = 0; idx < metas.size(); idx++) {
                org.jsoup.nodes.Element meta = metas.get(idx);
                String propertyAttr = meta.attr("name");
                if(propertyAttr.equalsIgnoreCase("description") && og.content.length() == 0) {
                    og.content = meta.attr("content");
                }
            }
            if(og.title.length() <= 0) {
                Elements title = doc.select("head title");
                if(title.size() > 0)
                    og.title = title.get(0).text();
            }
            if(og.images.size() <= 0) {
                Elements imgs = doc.select("body img");
                for(int idx = 0; idx < imgs.size(); idx++) {
                    Element img = imgs.get(idx);
                    String imgSrc = img.attr("data-original");
                    if(imgSrc == null || imgSrc.length() <= 0)
                        imgSrc = img.attr("src");
                    if(imgSrc == null || imgSrc.length() <= 0)
                        continue;
                    if(imgSrc.startsWith("http"))
                        og.images.add(imgSrc);
                    else if(imgSrc.startsWith("/"))
                        og.images.add(aURL.getProtocol() + "://" + aURL.getAuthority() + imgSrc);
                    else if(imgSrc.startsWith("."))
                        og.images.add(aURL.getProtocol() + "://" + aURL.getAuthority() + aURL.getPath().substring(0, aURL.getPath().lastIndexOf("/")) + imgSrc.substring(1));
                        
                }
            }
            return json(og);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return new ErrorResolution(400, "Bad request");
    }
    
    public Resolution importDoc() {
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
                locale = curUser.getRegion();
            }
        }
        
        if(!isLogin || userId == null) {
            return new StreamingResolution("text/html", "Need to login");
        }
        
        Long id = docToPost(docxPost, userId);
        if(id == null)
            return new StreamingResolution("text/html", "Failed to convert post");
        
        return new RedirectResolution("/post/queryPost.action?postId=" + String.valueOf(id));
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
    
    /*public Resolution getCircleTags() {
        
        List<CircleTag> cirTags = getCircleTagsFunction();
        return json(cirTags);
    }*/
    
    public Resolution listCircleTags() {
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
        
        List<Circle> cirs = circleDao.findByIds(selCircles.toArray(new Long[selCircles.size()]));
        if(cirs == null || cirs.size() <= 0)
            return json("");
        
        List<CircleTag> cirTags = getCircleTagsFunction(cirs);
        return json(cirTags);
    }
    
    public Resolution loadKeyword() {
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
            return new StreamingResolution("text/html", loginMessage);
        }
        
        String region = "en_US";
        Set<String> postLocale = localeDao.getLocaleByType(user.getRegion(), LocaleType.POST_LOCALE);
        if(postLocale != null && postLocale.size() > 0)
            region = postLocale.iterator().next();
        return new RedirectResolution("/post/DisputePostSystem.action?loadKeyword&selRegion=" + region);
    }
    
    public PageResult<MainPostSimpleWrapper> getPageResult() {
        return pageResult;
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

    public String getUserDisplayName() {
        return userDisplayName;
    }
    
    public String getUserAvater() {
        return userAvater;
    }

    private Long getFirstCircleId(Long userId) {
        Long originalCircleId = (long)143;
        List<String> circleLocales = new ArrayList<String>(localeDao.getLocaleByType(userDao.findById(userId).getRegion(), LocaleType.POST_LOCALE));
        List<Long> circleTypeIds = new ArrayList<Long>();
        PageResult<CircleType> circleTypeList = circleTypeDao.listTypesByLocales(circleLocales, null, new BlockLimit(0, 1));
        
        if(circleTypeList.getResults().size() > 0) {
            circleTypeIds.add(circleTypeList.getResults().get(0).getId());
            PageResult<Circle> circleList = circleDao.findByTypeIds(circleTypeIds, Long.valueOf(0), Long.valueOf(1));
            if(circleList.getResults().size() > 0) {
                originalCircleId = circleList.getResults().get(0).getId();
            }
        }
        return originalCircleId;
    }
    
}
