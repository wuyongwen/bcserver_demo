package com.cyberlink.cosmetic.modules.post.result;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.model.AttachmentExtLink;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostTags;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.user.model.Account;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MainPostSimpleWrapper extends MainPostBaseWrapper {
    public static class DisputePostView extends Views.Public {
    } 
    
    private static final long serialVersionUID = -1049846906283548568L;

    public static class Creator {
        
        public String avatar;
        public String cover;
        public UserType userType;
        public String description;
        public Boolean isFollowed;
        private User creator;
        public Long userId;
        public String displayName;
        private Boolean starOfWeek = Boolean.FALSE;
		private String badge = "";
        
        public Creator() {
            
        }
        
		public Creator(User creator, List<FileItem> userItems)
        {
            if(creator == null)
                return;
            userType = UserType.Normal;
            description = "";
            isFollowed = false;
            this.creator = creator;
            userId = creator.getId();
            displayName = creator.getDisplayName();
            /*avatar = creator.getAvatarUrl();
            cover = creator.getCoverUrl();*/
            userType = creator.getUserType();
            description = creator.getDescription();
            isFollowed = creator.getIsFollowed();
            avatar = creator.getAvatarUrl();
            cover = creator.getCoverUrl();
            starOfWeek = creator.getStarOfWeek();
            badge = creator.getBadge();
        }
        
        @JsonView(Views.Basic.class)
        public Long getUserId() {
            return userId;
        }
        
        @JsonView(Views.Basic.class)
        public String getDisplayName()
        {
            return displayName;
        }
        
        @JsonView(Views.Basic.class)
        public String getAvatar()
        {
            return avatar;
        }

        @JsonView(Views.Public.class)
        public String getCover()
        {
            return cover;
        }
        
        @JsonView(Views.Basic.class)
        public UserType getUserType() {
			return userType;
		}

        @JsonView(Views.Public.class)
        public String getDescription() {
			return description;
		}
        
        public void setIsFollowed(Boolean isFollowed)
        {
            this.isFollowed = isFollowed;
        }
        
        @JsonView(Views.Public.class)
        public Boolean getIsFollowed() {
            return isFollowed;
        }
        
        @JsonView(Views.Basic.class)
		public Boolean getStarOfWeek() {
			return starOfWeek;
		}

		public void setStarOfWeek(Boolean starOfWeek) {
			this.starOfWeek = starOfWeek;
		}

		@JsonView(Views.Basic.class)
		public String getBadge() {
			return badge;
		}

		public void setBadge(String badge) {
			this.badge = badge;
		}
        
        @JsonIgnore
        public String getEmail() {
            if(creator == null)
                return "";
            List<Account> accs = creator.getAllEmailAccountList();
            if(accs == null || accs.size() <= 0)
                return "";
            return accs.get(0).getEmail();
        }
        
        @JsonIgnore
        public String getAccountSource() {
            if(creator == null)
                return "";
            List<Account> accs = creator.getAllEmailAccountList();
            if(accs == null || accs.size() <= 0)
                return "";
            return accs.get(0).getAccountSource().toString();
        }
    }
    
    public static class Look {
        @JsonView(Views.Public.class)
        public Long lookId;

        public Look() {
            
        }
        
        public Look(Long lookId) {
            this.lookId = lookId;
        }
        
        public Long getLookId() {
            return lookId;
        }

        public void setLookId(Long lookId) {
            this.lookId = lookId;
        }
    }
    
    public static class File {
        
        public File() {
            
        }
        
        public File(FileItem fileItem, FileType fileType) {
            ObjectMapper mapper = new ObjectMapper();
            com.cyberlink.cosmetic.modules.file.model.File file = fileItem.getFile();
            this.fileId = file.getId();
            this.fileType = fileType.toString();
            ObjectNode actualObj = (ObjectNode)fileItem.getMetadataJson();
            fillMetadata(actualObj);
            
            downloadUrl = fileItem.getOriginalUrl();
            actualObj.put("originalUrl", downloadUrl);
            
            try {
                metadata = mapper.writer((PrettyPrinter)null).writeValueAsString(actualObj);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        public void fillMetadata(ObjectNode actualObj) {
            if(actualObj == null)
                return;
            JsonNode attrNode = actualObj.get("redirectUrl");
            if(attrNode != null)
                redirectUrl = attrNode.asText();
            attrNode = actualObj.get("imageDescription");
            if(attrNode != null)
                imageDescription = attrNode.asText();     
            attrNode = actualObj.get("md5");
            if(attrNode != null)
                md5 = attrNode.asText();
            attrNode = actualObj.get("originalUrl");
            if(attrNode != null)
                downloadUrl = attrNode.asText();
            attrNode = actualObj.get("lookStoreUrl");
            if(attrNode != null)
                lookStoreUrl = attrNode.asText();
        }
        
        public File(AttachmentExtLink extLink) {
            ObjectMapper mapper = new ObjectMapper();
            this.fileId = null;
            this.fileType = extLink.getLinkType();
            this.metadata = extLink.getMetadata();
            ObjectNode actualObj = null;
            try {
                actualObj = (ObjectNode)mapper.readTree(this.metadata);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
            if(actualObj == null)
                return;
            JsonNode attrNode = actualObj.get("redirectUrl");
            if(attrNode != null)
                redirectUrl = attrNode.asText();
            attrNode = actualObj.get("imageDescription");
            if(attrNode != null)
                imageDescription = attrNode.asText();     
            attrNode = actualObj.get("originalUrl");
            if(attrNode != null)
                downloadUrl = attrNode.asText();       
        }
        
        @JsonView(Views.Basic.class)
        public Long fileId = (long)1;
        
        @JsonView(Views.Basic.class)
        public String fileType = "Photo";
        
        @JsonView(Views.Public.class)
        public Long downloadCount = (long)0;
        
        public String metadata;
        
        @JsonView(Views.Basic.class)
        public String getMetadata()
        {
            return metadata;
        }
        
        public String downloadUrl = "";
        public String redirectUrl = "";
        public String lookStoreUrl = null;
        public String imageDescription = "";
        public String md5;
        
        @JsonView(DisputePostView.class)
        public String getDownloadUrl()
        {
            return downloadUrl;
        }
        
        public String getRedirectUrl()
        {
            return redirectUrl;
        }
        
        public String getLookStoreUrl()
        {
            return lookStoreUrl;
        }
        
        public String getImageDescription()
        {
            return imageDescription;
        }
        
        public String getFileType(){
            return fileType;
        }
        
        public Long getFileId()
        {
            return fileId;
        }

        public String getMd5()
        {
            return md5;
        }
        
    }
    
    public static class Attachments {
        
        public List<File> files;
        public List<Look> looks = new ArrayList<Look>(0);
        
        public Attachments()
        {
            files = new ArrayList<File>(0);
        }
        
        public void setFileItem(List<Object> objs) {
            for(Object obj : objs) {
                if(obj instanceof FileItem) {
                    FileItem fileItem = (FileItem)obj;
                    FileType fileType = fileItem.getFile().getFileType();
                    files.add(new File(fileItem, fileType));
                }
            }
        }
        
        @JsonView(Views.Basic.class)
        public List<File> getFiles()
        {
            return files;
        }
        
        public void setFiles(List<File> files)
        {
            this.files = files;
        }
        
        @JsonView(Views.Basic.class)
        public List<Look> getLooks()
        {
            return looks;
        }
        
        public void setLooks(List<Look> looks)
        {
            this.looks = looks;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DPWCircle {
        
        public DPWCircle() {
            
        }
        
        public DPWCircle(Circle circle, Map<String, String> translatedCircleName) {
            this.circleId = circle.getId();
            this.circleName = circle.getCircleName();
            this.defaultType = circle.getDefaultType();
            this.circleTypeId = circle.getCricleTypeId();
            setIsSecret(circle.getIsSecret());
            if(translatedCircleName != null) {
                CircleType cT = circle.getCircleType();
                if(cT != null) {
                    String toTanslateName = cT.getCircleTypeGroup().getDefaultTypeName();
                    if(translatedCircleName.containsKey(toTanslateName))
                        this.translateCircleName = translatedCircleName.get(toTanslateName);
                }
            }
        }
        
        @JsonView(Views.Basic.class)
        public Long circleId = (long)1;
        
        @JsonView(Views.Basic.class)
        public String circleName = "Cyberlink";
        
        @JsonView(Views.Public.class)
        public String defaultType = null;
        
        @JsonView(Views.Public.class)
        public Long circleTypeId = null;
        
        @JsonView(Views.Public.class)
        public Boolean display = true;
        
        @JsonView(DisputePostView.class)
        public String translateCircleName = "Other";
        
        public String getDefaultType() {
            return defaultType;
        }
        
        public Long getCircleTypeId() {
            return circleTypeId;
        }
        
        public String getCircleName() {
            return circleName;
        }
        
        public String getTranslateCircleName() {
            return translateCircleName;
        }
        
        public void setIsSecret(Boolean isSecret) {
            this.display = isSecret == null ? true : !isSecret;
        }
        
        public Boolean getDisplay() {
            return display;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DPWLookType {
        public DPWLookType() {
            
        }
        
        public DPWLookType(LookType lookType) {
            id = lookType.getId();
            name = lookType.getName();
            codeName = lookType.getCodeName();
            bgImgId = lookType.getBgImgId();
            bgImgUrl = lookType.getBgImgUrl();
        }
        
        @JsonView(Views.Basic.class)
        public Long id;
        
        @JsonView(Views.Basic.class)
        public String name;
        
        @JsonView(Views.Basic.class)
        public String codeName;
        
        @JsonView(Views.Basic.class)
        public Long bgImgId;
        
        @JsonView(Views.Basic.class)
        public String bgImgUrl;
    }
    
    protected Creator creator = null;
    protected Creator sourcePostCreator = null;
    protected Attachments attachments;
    protected Boolean isLiked = false;
    protected Long likeCount = (long)0;
    protected Long commentCount = (long)0;
    protected Long circleInCount = (long)0;
    protected Long lookDownloadCount = (long)0;
    protected List<DPWCircle> circles = new ArrayList<DPWCircle>(0);
    protected PostStatus status;
    public Boolean gotProductTag = false;
    protected PostTags tags;
    public Long promoteScore = (long)0;
    public String postCreateTime;
    public Boolean isDeleted = null;
    public Long score = 0L;
    protected String postSource;
    protected DPWLookType lookType;
    protected String extLookUrl;
    protected PostType postType = PostType.NORMAL;

    static private SimpleDateFormat dateFormmater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public MainPostSimpleWrapper() {
        super();
        creator = null;
    }
    
    public MainPostSimpleWrapper(Post post, List<FileItem> userItems, List<Object> fileItems, List<Circle> circles, LookType lookType) {
        this(post, userItems, fileItems, circles,  lookType, null);
    }
    
    public MainPostSimpleWrapper(Post post, List<FileItem> userItems, List<Object> fileItems, List<Circle> circles, LookType lookType, SimpleDateFormat customDateFormatter) {
        this(post, userItems, fileItems, circles,  null,  lookType, customDateFormatter);
    }
    
    public MainPostSimpleWrapper(Post post, List<FileItem> userItems, List<Object> fileItems, List<Circle> circles, Map<String, String> translatedCircleName, LookType lookType, SimpleDateFormat customDateFormatter) {
        super(post);
        gotProductTag = post.getGotProductTag();
        creator = new Creator(post.getCreator(), userItems);
        attachments = new Attachments();
        status = post.getPostStatus();
        promoteScore = post.getPromoteScore();
        content = post.getContent();
        title = post.getTitle();
        isDeleted = post.getIsDeleted();
        score = post.getBasicSortBonus();
        postSource = post.getPostSource();
        tags = post.getPostTags();
        if(post.getPostType() != null)
            postType = post.getPostType();
        
        if(lookType != null)
            setLookType(new DPWLookType(lookType));
        extLookUrl = post.getExtLookUrl();
        if(customDateFormatter != null) {
            postCreateTime = customDateFormatter.format(post.getCreatedTime());
        }
        else
            postCreateTime = dateFormmater.format(post.getCreatedTime());

        if(circles != null) {
            for(Circle c : circles) {
                DPWCircle dwpCircle = new DPWCircle(c, translatedCircleName);
                this.circles.add(dwpCircle);
            }
        }
        if(fileItems != null)
            attachments.setFileItem(fileItems);
    }
    
    @JsonView(Views.Basic.class)
    public DPWLookType getLookType() {
        return lookType;
    }

    public void setLookType(DPWLookType lookType) {
        this.lookType = lookType;
    }
    
    @JsonView(Views.Basic.class)
    public String getExtLookUrl() {
        return extLookUrl;
    }

    public void setExtLookUrl(String extLookUrl) {
        this.extLookUrl = extLookUrl;
    }
    
    public void setCommentCount(Long commentCount)
    {
        this.commentCount = commentCount;
    }
    
    @JsonView(Views.Basic.class)
    public Long getCommentCount() {
        return commentCount;
    }
    
    public void setLikeCount(Long likeCount)
    {
        this.likeCount = likeCount;
    }
    
    @JsonView(Views.Basic.class)
    public Long getLikeCount() {
        return likeCount;
    }
    
    public void setCircleInCount(Long circleInCount)
    {
        this.circleInCount = circleInCount;
    }
    
    @JsonView(Views.Basic.class)
    public Long getCircleInCount() {
        return circleInCount;
    }
    
    public void setLookDownloadCount(Long lookDownloadCount) {
        this.lookDownloadCount = lookDownloadCount;
    }
    
    @JsonView(Views.Basic.class)
    public Long getLookDownloadCount() {
        return lookDownloadCount;
    }
    
    public void setSourcePostCreator(Creator sourcePostCreator) {
        this.sourcePostCreator = sourcePostCreator;
    }
    
    public void setSourcePostCreatorByUser(User sourcePostCreator)
    {
        if(sourcePostCreator == null)
            return;
        setSourcePostCreator(new Creator(sourcePostCreator, null));
    }
    
    public void setPostCreatorByUser(User postCreator) {
        if(postCreator == null)
            return;
        creator = new Creator(postCreator, null);
    }
    
    @JsonView(Views.Public.class)
    public Creator getSourcePostCreator() {
        return sourcePostCreator;
    }
    
    public void setIsLiked(Boolean isLiked)
    {
        this.isLiked = isLiked;
    }
    
    @JsonView(Views.Basic.class)
    public Boolean getisLiked() {
        return isLiked;
    }
    
    @JsonView(Views.Basic.class)
    public Attachments getAttachments() {
        return attachments;
    }
    
    @JsonView(Views.Basic.class)
    public String getContent() {
        return content;
    }
    
    @JsonView(Views.Basic.class)
    public String getTitle() {
        return title;
    }
    
    @JsonView(Views.Basic.class)
    public Creator getCreator()
    {
        return creator;
    }

    @JsonView(Views.Basic.class)
    public List<DPWCircle> getCircles() {
        return circles;
    }
    
    public void setCircles(List<DPWCircle> circles) {
        this.circles = circles;
    }
    
    @JsonView(Views.Basic.class)
    public Boolean getGotProductTag() {
        return gotProductTag;
    }
    
    @JsonView(Views.Public.class)
    public PostStatus getStatus() {
        return status;
    }
    
    public Long getPromoteScore() {
        return promoteScore;
    }
    
    public String getpostCreateTime() {
        return postCreateTime;
    }
    
    @JsonView(Views.Public.class)
    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
    
    @JsonView(Views.Basic.class)
    public String getPostSource() {
        return postSource;
    }

    public void setPostSource(String postSource) {
        this.postSource = postSource;
    }
    
    @JsonView(Views.Public.class)
    public PostTags getTags() {
        return tags;
    }

    public void setTags(PostTags tags) {
        this.tags = tags;
    }
    
    @JsonView(Views.Basic.class)
    public PostType getPostType() {
        if(postType == null)
            return PostType.NORMAL;
        
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }
    
    @JsonView(Views.Simple.class)
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    /*@JsonView(Views.Simple.class)
    public Date getLastModified() {
        return post.getLastModified();
    }*/
    
    /*@JsonView(Views.Public.class)
    public Long getPostId() {
        return post.getId();
    }*/
    
}
