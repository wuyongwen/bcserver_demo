package com.cyberlink.cosmetic.modules.post.service.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.event.post.OfficialPostCreateEvent;
import com.cyberlink.cosmetic.event.post.PostCreateEvent;
import com.cyberlink.cosmetic.event.post.PostDeleteEvent;
import com.cyberlink.cosmetic.event.post.PostFanOutEvent;
import com.cyberlink.cosmetic.event.post.PostUpdateEvent;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute.CircleAttrType;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.common.model.Locale;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.dao.FileItemDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.look.dao.LookDao;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.Look;
import com.cyberlink.cosmetic.modules.post.dao.AttachmentDao;
import com.cyberlink.cosmetic.modules.post.dao.AttachmentExtLinkDao;
import com.cyberlink.cosmetic.modules.post.dao.LikeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostAutoArticleDao;
import com.cyberlink.cosmetic.modules.post.dao.PostCircleInDao;
import com.cyberlink.cosmetic.modules.post.dao.PostCurateKeywordDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostNewDao;
import com.cyberlink.cosmetic.modules.post.dao.PostProductDao;
import com.cyberlink.cosmetic.modules.post.dao.PostReportedDao;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreDao;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreTrendDao;
import com.cyberlink.cosmetic.modules.post.dao.PostTopKeywordDao;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.event.PersonalTrendEvent;
import com.cyberlink.cosmetic.modules.post.event.PostViewUpdateEvent;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Attachment;
import com.cyberlink.cosmetic.modules.post.model.AttachmentExtLink;
import com.cyberlink.cosmetic.modules.post.model.MainPostWrapper;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAttachments;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.PostScoreTrend;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle;
import com.cyberlink.cosmetic.modules.post.model.PostCircleIn;
import com.cyberlink.cosmetic.modules.post.model.PostFile;
import com.cyberlink.cosmetic.modules.post.model.PostNew;
import com.cyberlink.cosmetic.modules.post.model.PostProduct;
import com.cyberlink.cosmetic.modules.post.model.PostProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostExProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostReported;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedResult;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedStatus;
import com.cyberlink.cosmetic.modules.post.model.PostScore;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.ResultType;
import com.cyberlink.cosmetic.modules.post.model.PostTags.MainPostDbView;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostTags;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.model.SubPostWrapper;
import com.cyberlink.cosmetic.modules.post.repository.PostHeatRepository;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.AsyncPostUpdateService;
import com.cyberlink.cosmetic.modules.post.service.PostPopularityService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.post.service.RelatedPostService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.event.UserBadgeEvent;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;
import com.cyberlink.cosmetic.modules.user.model.UserStatus;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.twitter.Extractor;

public class PostServiceImpl extends AbstractService implements PostService {
    
    private PostDao postDao;
    private PostProductDao postProductDao;
    private PostNewDao postNewDao;
    private PostReportedDao postReportedDao;
    private UserDao userDao;
    private CircleDao circleDao;
    private AttachmentDao attachmentDao;
    private AttachmentExtLinkDao attachmentExtLinkDao;
    private FileDao fileDao;
    private LookDao lookDao;
    private LookTypeDao lookTypeDao;
    private FileItemDao fileItemDao;
    private ObjectMapper objectMapper;
    private LikeDao likeDao;
    private CircleAttributeDao circleAttributeDao;
    private PostCircleInDao postCircleInDao;
    private PostAttributeDao postAttributeDao;
    private SubscribeDao subscribeDao;
    private LocaleDao localeDao;
    private CircleService circleService;
    private PostPopularityService postPopularityService;
    private AsyncPostUpdateService asyncPostUpdateService;
    private RelatedPostService relatedPostService;
    private PostTopKeywordDao postTopKeywordDao;
    private PostScoreDao postScoreDao;
    private PostScoreTrendDao postScoreTrendDao;
    private PostAutoArticleDao postAutoArticleDao;
    private CircleSubscribeDao circleSubscribeDao;
    private PostViewDao postViewDao;
    private PostCurateKeywordDao postCurateKeywordDao;
    private UserAttrDao userAttrDao;
	private String CIRCLE_IN_POST_SOURCE = "circle_in_posting";
	// As Jau requested, default quality for post created by Blogger is 2
	private Integer DEF_BLOGGER_POST_QUALITY = 2;
	// As Hendry team requested, default quality for curated post/ post created by user other than normal is 1 
	private Integer DEF_SPECIAL_POST_QUALITY = 1; 
	
    private enum PushPostNewType {
        NO_PUSH, TRY_PUSH, FORCE_PUSH;
    }
    
	public PostViewDao getPostViewDao() {
		return postViewDao;
	}

	public void setPostCurateKeywordDao(PostCurateKeywordDao postCurateKeywordDao) {
        this.postCurateKeywordDao = postCurateKeywordDao;
    }
	
	public void setUserAttrDao(UserAttrDao userAttrDao) {
	    this.userAttrDao = userAttrDao;
	}
	
	public void setPostViewDao(PostViewDao postViewDao) {
		this.postViewDao = postViewDao;
	}
    
	public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }
    
    public void setPostProductDao(PostProductDao postProductDao) {
        this.postProductDao = postProductDao;
    }
    
    public void setPostNewDao(PostNewDao postNewDao) {
        this.postNewDao = postNewDao;
    }

    public void setPostReportedDao(PostReportedDao postReportedDao) {
        this.postReportedDao = postReportedDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    public void setAttachmentDao(AttachmentDao attachmentDao) {
        this.attachmentDao = attachmentDao;
    }
    
    public void setAttachmentExtLinkDao(AttachmentExtLinkDao attachmentExtLinkDao) {
        this.attachmentExtLinkDao = attachmentExtLinkDao;
    }
    
    public void setFileDao(FileDao fileDao) {
        this.fileDao = fileDao;
    }
    
    public void setLookDao(LookDao lookDao) {
        this.lookDao = lookDao;
    }
    
    public void setLookTypeDao(LookTypeDao lookTypeDao) {
        this.lookTypeDao = lookTypeDao;
    }
    
    public void setFileItemDao(FileItemDao fileItemDao) {
        this.fileItemDao = fileItemDao;
    }
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setCircleDao(CircleDao circleDao) {
        this.circleDao = circleDao;
    }    
    
    public void setLikeDao(LikeDao likeDao) {
        this.likeDao = likeDao;
    }
    
    public void setCircleAttributeDao(CircleAttributeDao circleAttributeDao) {
        this.circleAttributeDao = circleAttributeDao;
    }
    
    public void setPostCircleInDao(PostCircleInDao postCircleInDao) {
        this.postCircleInDao = postCircleInDao;
    }
    
    public void setPostAttributeDao(PostAttributeDao postAttributeDao) {
        this.postAttributeDao = postAttributeDao;
    }
     
    public void setSubscribeDao(SubscribeDao subscribeDao) {
        this.subscribeDao = subscribeDao;
    }
     
    public void setLocaleDao(LocaleDao localeDao) {
        this.localeDao = localeDao;
    }

    public void setCircleService(CircleService circleService) {
        this.circleService = circleService;
    }
     
    public void setPostPopularityService(PostPopularityService postPopularityService) {
        this.postPopularityService = postPopularityService;
    }

    public void setAsyncPostUpdateService(
            AsyncPostUpdateService asyncPostUpdateService) {
        this.asyncPostUpdateService = asyncPostUpdateService;
    }
    
    public void setRelatedPostService(RelatedPostService relatedPostService) {
        this.relatedPostService = relatedPostService;
    }
    
    public void setPostTopKeywordDao(PostTopKeywordDao postTopKeywordDao) {
        this.postTopKeywordDao = postTopKeywordDao;
    }
    
    public void setPostScoreDao(PostScoreDao postScoreDao) {
        this.postScoreDao = postScoreDao;
    }
    
    public void setPostScoreTrendDao(PostScoreTrendDao postScoreTrendDao) {
		this.postScoreTrendDao = postScoreTrendDao;
	}

	public PostAutoArticleDao getPostAutoArticleDao() {
    	return this.postAutoArticleDao;
    }
    
    public void setPostAutoArticleDao(PostAutoArticleDao postAutoArticleDao){
    	this.postAutoArticleDao = postAutoArticleDao;
    }
    
    public CircleSubscribeDao getCircleSubscribeDao() {
    	return this.circleSubscribeDao;
    }
    
    public void setCircleSubscribeDao(CircleSubscribeDao circleSubscribeDao){
    	this.circleSubscribeDao = circleSubscribeDao;
    }
    
    private PostTags transferTags(String tags) throws InvalidFormatException {
        PostTags result = new PostTags();        
        if(tags == null)
            return result;
        try {
            result = objectMapper.readValue(tags,
                    new TypeReference<PostTags>() {
                    });
        } catch (Exception e) {
            logger.error("", e);
            if(tags.length() == 0)
                return result;
            else
                throw new InvalidFormatException(tags, e, null);
        }
        return result;
    }

    private PostAttachments transferAttachments(String attachments) throws InvalidFormatException {
        PostAttachments result = new PostAttachments();        
        if(attachments == null)
            return result;
        try {
            result = objectMapper.readValue(attachments,
                    new TypeReference<PostAttachments>() {
                    });
        } catch (Exception e) {
            logger.error("", e);
            if(attachments.length() == 0)
                return result;
            else
                throw new InvalidFormatException(attachments, e, null);
        }
        return result;
    }
    
    private PostApiResult <Post> createBcPost(Long creatorId, String locale, String countryCode, String title, String content, List<Long> circleIds, 
            PostAttachments attachments, PostTags tags, PostStatus postStatus, String source, AppName appName, 
            PostType postType, Long promoteScore, Long lookTypeId, String extLookUrl, Date createdTime, Map<PostOption, String> opts) throws JsonProcessingException {
        PostApiResult <Post> result = new PostApiResult <Post>();
        if(opts == null)
            opts = new HashMap<PostOption, String>();
        if(!userDao.exists(creatorId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        if(title == null || title.length() <= 0) {
            result.setErrorDef(ErrorDef.InvalidPostTitle);
            return result;
        }
        
        User creator = userDao.findById(creatorId);
        List<Attachment> postAttachments = new ArrayList<Attachment>(0);
        List<PostProduct> postProducts = new ArrayList<PostProduct>(0);
        List<Look> relatedLooks = new ArrayList<Look>();
        
        if(postStatus == null)
            postStatus = PostStatus.Published;
        if(postStatus.equals(PostStatus.Published)) {
            UserStatus userStatus = creator.getUserStatus();
            if(userStatus != null && userStatus.equals(UserStatus.Hidden))
                postStatus = PostStatus.Unpublished;
        }
        
        Post newPost = new Post();
        newPost.setShardId(creatorId);
        newPost.setCreatorId(creatorId);
        newPost.setCreator(creator);
        newPost.setTitle(title);
        newPost.setContent(content);
        newPost.setAttachments(postAttachments);
        newPost.setPostProducts(postProducts);
        newPost.setLocale(locale);
        if(countryCode == null && locale != null) {
            Integer delimiterIdx = locale.indexOf("_") + 1;
            if(delimiterIdx.compareTo(0) > 0 && delimiterIdx.compareTo(locale.length() - 1) < 0)
                countryCode = locale.substring(locale.indexOf("_") + 1);
        }
        if(creator.getUserType() != null) {
            switch(creator.getUserType()) {
            case Blogger:
                newPost.setQuality(DEF_BLOGGER_POST_QUALITY);
                break;
            case Normal:
                newPost.setQuality(null);
                break;
            default:
                newPost.setQuality(DEF_SPECIAL_POST_QUALITY);
                break;
            }
        }
        
        newPost.setCountryCode(countryCode);
        newPost.setPostStatus(postStatus);
        newPost.setGotProductTag(false);
        newPost.setPostSource(source);
        newPost.setAppName(appName);
        newPost.setPromoteScore(promoteScore);
        newPost.setTags(objectMapper.writer((PrettyPrinter)null).withView(MainPostDbView.class).writeValueAsString(tags));
        newPost.setLookTypeId(lookTypeId);
        newPost.setExtLookUrl(extLookUrl);
        if (createdTime != null)
            newPost.setCreatedTime(createdTime);
        if(postType != null)
            newPost.setPostType(postType);
        List<Circle> circles = new ArrayList<Circle>(0);
        if(circleIds != null) {            
            for(Long cirId : circleIds){
                if(circleDao.exists(cirId)) {
                    Circle postCircle = circleDao.findById(cirId);
                    circles.add(postCircle);
                    break; // Only allow one circle
                }
            }
        }
        
        Circle circleToUpdate = null;
        for(Circle circle : circles)
        {
            if(!circle.getCreatorId().equals(creatorId)) {
                result.setErrorDef(ErrorDef.InvalidCircleId);
                return result;
            }

            if(circleToUpdate == null)
                circleToUpdate = circle;
            newPost.setCircleId(circle.getId());
        }
        
        if(tags != null) {            
            if(!tags.IsNullProductTags()) {
                if(tags.getProductTags().size() > 0)
                    newPost.setGotProductTag(true);
                
                for(PostProductTag postProductTag : tags.getProductTags())
                {
                    PostProduct ppt = new PostProduct();
                    ppt.setShardId(creatorId);
                    ppt.setProductId(postProductTag.productId);
                    ppt.setTagAttrs(objectMapper.writer((PrettyPrinter)null).writeValueAsString(postProductTag.tagPoint));
                    ppt.setPost(newPost);
                    postProducts.add(ppt);
                }
            }
            if(!tags.IsNullExProductTags()) {
                for(PostExProductTag postExProductTag : tags.getExProductTags())
                {
                    PostProduct pept = new PostProduct();
                    pept.setShardId(creatorId);
                    pept.setProductId(null);
                    pept.setTagAttrs(objectMapper.writer((PrettyPrinter)null).writeValueAsString(postExProductTag.tagInfo));
                    pept.setPost(newPost);
                    postProducts.add(pept);
                }
            }
        }
            
        String postCoverUrl = null;
        Long iconFileId = null;
        if(attachments != null && attachments.files != null) {
            for(PostFile postFile : attachments.files)
            {
                Attachment a = new Attachment();
                a.setShardId(creatorId);
                Object attachTarget = null;
                if(postFile.fileId == null) {
                    AttachmentExtLink newLink = new AttachmentExtLink();
                    newLink.setShardId(creatorId);
                    newLink.setUserId(creatorId);
                    newLink.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(postFile.getMetadata()));
                    newLink.setLinkType(postFile.fileType);;
                    attachmentExtLinkDao.create(newLink);
                    attachTarget = newLink;
                }
                else {
                    com.cyberlink.cosmetic.modules.file.model.File bcFile = fileDao.findById(postFile.fileId);
                    attachTarget = bcFile;
                    if((bcFile.getFileType().equals(FileType.Photo)) || ((bcFile.getFileType().equals(FileType.PostCoverOri) && postCoverUrl == null)) || ((bcFile.getFileType().equals(FileType.PostCover) && postCoverUrl == null))) {
                        Long [] fileIds = new Long[1];
                        fileIds[0] = bcFile.getId();
                        List<FileItem> fileItems = fileItemDao.findThumbnails(fileIds, ThumbnailType.List);
                        if(fileItems != null && fileItems.size() > 0) {
                            postCoverUrl = fileItems.get(0).getOriginalUrl();
                            iconFileId = fileItems.get(0).getFile().getId();
                        }
                    }
                }
                a.setTarget(attachTarget);
                a.setPostId(newPost.getId());
                postAttachments.add(a);
            }
        }
        
        newPost.setBasicSortBonus(postPopularityService.getPostBasicSortBonus(newPost));
        Boolean isSecret = true;
        if(circleToUpdate != null)
            isSecret = circleToUpdate.getIsSecret();
        Post newCreatedPost = createMainPost(newPost, isSecret, createdTime);
        
        if(newCreatedPost == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        
        if(circleToUpdate != null && postCoverUrl != null && postStatus.getViewable()) {
            Boolean forceHideInAll = !opts.containsKey(PostOption.ForceHideInAll) ? null : Boolean.valueOf(opts.get(PostOption.ForceHideInAll));
            List<Long> circleTypeIds = new ArrayList<Long>();
            circleTypeIds.add(circleToUpdate.getCircleTypeId());
            postPopularityService.pushToNewImmediate(newCreatedPost, circleTypeIds, newPost.getBasicSortBonus(), forceHideInAll, false, null);
            
            if(!circleToUpdate.getCreatorId().equals(creatorId))
                iconFileId = null;
            updateCircleThumbnail(circleToUpdate, postCoverUrl, iconFileId);
        	CircleAttribute cAttrPostCount = circleAttributeDao.createOrUpdateCircleAttr(circleToUpdate, CircleAttrType.PostCount, "1", true);
        	// If user post(Published) in a new circle, need handle circle follow of the new circle.
        	if (cAttrPostCount.getAttrType() == CircleAttrType.PostCount && cAttrPostCount.getAttrValue().equals("1")) {
        		String region = Constants.getPostRegion();
        		List<CircleAttribute> cAttrFollowerCounts = circleAttributeDao.findCircleAttribute(region, circleToUpdate, CircleAttrType.FollowerCount);
        		if (cAttrFollowerCounts.isEmpty()) {
        			String subscriberCount = String.valueOf(subscribeDao.findBySubscribee(circleToUpdate.getCreatorId(), null, new BlockLimit(0, 0)).getTotalSize());
        			circleAttributeDao.createOrUpdateCircleAttr(circleToUpdate, CircleAttrType.FollowerCount, subscriberCount, true);
        		}
        	}
        }
        
        for(Look rLook : relatedLooks) {
            rLook.setPostId(newCreatedPost.getId());
            lookDao.update(rLook);
        }
        result.setResult(newCreatedPost);
        return result;
    }

    @Override
    public Post createMainPost(Post post, Boolean isSecret, Date createdTime)
    {
        Post newCreatedPost = postDao.create(post);
        if(newCreatedPost == null) {
            return null;
        }
        
        // workadound: can not set created time when create a new post.
        if (createdTime != null) {
            newCreatedPost.setCreatedTime(createdTime);
            postDao.update(newCreatedPost);
        }
        
        if(newCreatedPost.getParentId() != null)
            return newCreatedPost;
        
        if (Constants.getIsPostCacheView()) {
            ArrayList<Long> postIds = new ArrayList<Long>();
            postIds.add(newCreatedPost.getId());
            asyncPostUpdateService.runLoadPostView(postIds);
        }

        User postCreator = post.getCreator();
		if(newCreatedPost.getPostStatus() == PostStatus.Published) {
		    Long circleId = post.getCircleId();
	        if(UserType.CL.equals(postCreator.getUserType())) {
				if(circleId != null)
	                publishDurableEvent(new OfficialPostCreateEvent(post.getId(), post.getLocale(), circleId, post.getCreatorId(), post.getCreatedTime()));
	        }
	        else if(UserType.Normal.equals(postCreator.getUserType()))
	            publishDurableEvent(UserBadgeEvent.CreateAddPostEvent(post.getId(), post.getCreatorId(), post.getLocale(), post.getCreatedTime()));
	        
	        String postSource = post.getPostSource();
	        // should not trigger this event for circle-in, since we did not have rootId
			if (circleId != null && postSource != null && !postSource.equals(CIRCLE_IN_POST_SOURCE)) {
		    	publishDurableEvent(new PostCreateEvent(newCreatedPost.getId(), circleId, newCreatedPost.getLocale(), postCreator.getId(), null, newCreatedPost.getCreatedTime()));
                publishDurableEvent(new PostFanOutEvent(newCreatedPost.getId(), circleId, postCreator.getId(), null, newCreatedPost.getCreatedTime()));
			}
			if(post.getPostType() != null && post.getPostStatus().getViewable()) {
			    switch(post.getPostType()) {
			    case HOW_TO: {
			        asyncPostUpdateService.increaseUserAttr(postCreator.getId(), null, 1L, null, null);
			        break;
			    }
			    case YCL_LOOK: {
			        asyncPostUpdateService.increaseUserAttr(postCreator.getId(), null, null, null, 1L);
			        break;
			    }
			    default:
			        break;
			    }
			}
		}
        return newCreatedPost;
    }
    
    @Override
    public Post updateMainPost(Post post, Boolean descPostCount, PostStatus originalStatus, Date createdTime, Boolean updatePost)
    {
        if (createdTime != null)
            post.setCreatedTime(createdTime);

        Post updatedPost = postDao.update(post);
        if(updatedPost == null) {
            return null;
        }
        
        if (updatedPost.getCreator().getUserType() == UserType.Blogger) {
        	PostAutoArticle postAutoArticle = postAutoArticleDao.findByPostId(updatedPost.getId());
        	if (postAutoArticle != null) {
            	postAutoArticle.setLocale(updatedPost.getLocale());
            	postAutoArticle.setTitle(updatedPost.getTitle());
            	postAutoArticle.setContent(updatedPost.getContent());
            	postAutoArticle.setPostStatus(updatedPost.getPostStatus());
            	postAutoArticle.setIsDeleted(updatedPost.getIsDeleted());
            	postAutoArticleDao.update(postAutoArticle);
        	}
        	
        }
        
        if(updatedPost.getParentId() != null)
            return updatedPost;

        if (Constants.getIsPostCacheView()) {
            ArrayList<Long> postIds = new ArrayList<Long>();
            postIds.add(updatedPost.getId());
            asyncPostUpdateService.runLoadPostView(postIds);
        }
        Long circleId = updatedPost.getCircleId();        
        User postCreator = updatedPost.getCreator();
        if(circleId != null && originalStatus !=null && !originalStatus.getViewable() && updatedPost.getPostStatus().getViewable()) {
            if(UserType.CL.equals(postCreator.getUserType()))
                publishDurableEvent(new OfficialPostCreateEvent(post.getId(), post.getLocale(), circleId, post.getCreatorId(), post.getCreatedTime()));
            Long rootId = null;
            if (updatedPost.getPostSource().equals(CIRCLE_IN_POST_SOURCE)) {
                PostCircleIn pCirIn = postCircleInDao.findByPostId(updatedPost.getId());
                if(pCirIn != null)
                    rootId = pCirIn.getRootPostId();
            }
            if(descPostCount != null && post.getPostType() != null) {
                switch(post.getPostType()) {
                case HOW_TO:{
                    if(descPostCount)
                        asyncPostUpdateService.decreaseUserAttr(postCreator.getId(), null, 1L, null, null);
                    else
                        asyncPostUpdateService.increaseUserAttr(postCreator.getId(), null, 1L, null, null);
                    break;
                }
                case YCL_LOOK: {
                    if(descPostCount)
                        asyncPostUpdateService.decreaseUserAttr(postCreator.getId(), null, null, null, 1L);
                    else
                        asyncPostUpdateService.increaseUserAttr(postCreator.getId(), null, null, null, 1L);
                    break;
                }
                default:
                    break;
                }
            }
            
            publishDurableEvent(new PostCreateEvent(updatedPost.getId(), circleId, updatedPost.getLocale(), postCreator.getId(), rootId, updatedPost.getCreatedTime()));
            publishDurableEvent(new PostFanOutEvent(updatedPost.getId(), circleId, postCreator.getId(), rootId, updatedPost.getCreatedTime()));
        }
        
        if(post.getPostType() != null && originalStatus != null &&!originalStatus.getViewable() && updatedPost.getPostStatus().getViewable()) {
            switch(post.getPostType()) {
            case HOW_TO:{
                asyncPostUpdateService.increaseUserAttr(postCreator.getId(), null, 1L, null, null);
                break;
            }
            case YCL_LOOK: {
                asyncPostUpdateService.increaseUserAttr(postCreator.getId(), null, null, null, 1L);
                break;
            }
            default:
                break;
            }
        }
        
        return updatedPost;
    }
    
    @Override
    public PostApiResult <Post> createPost(Long creatorId, String locale, String countryCode, String title, String content, List<Long> circleIds, String jAttachments, 
            String jTags, PostStatus postStatus, String source, AppName appName, PostType postType, Long promoteScore, Long lookTypeId, 
            String extLookUrl, Map<PostOption, String> opts) {
        return createPost(creatorId, locale, countryCode, title, content, circleIds, jAttachments, jTags, postStatus, source, appName, postType, promoteScore, 
                lookTypeId, extLookUrl, null, opts);
    }
    
    @Override
    public PostApiResult <Post> createPost(Long creatorId, String locale, String countryCode, String title, String content, List<Long> circleIds, String jAttachments, 
            String jTags, PostStatus postStatus, String source, AppName appName, PostType postType, Long promoteScore, Long lookTypeId, 
            String extLookUrl, Date createdTime, Map<PostOption, String> opts) {
        PostApiResult <Post> result = new PostApiResult <Post>();   
        try {
            PostAttachments postAttachment = null;
            if(jAttachments != null)
                postAttachment = transferAttachments(jAttachments);
            PostTags tags = null;
            if(jTags != null)
                tags = transferTags(jTags);
            List<Circle> postCircle = circleDao.findByIds(circleIds.toArray(new Long[circleIds.size()]));
            List<Long> userSelfCircleId = new ArrayList<Long>();
            for(Circle c : postCircle) {
                Circle userCircle = circleService.getUserAccessibleCircle(c, creatorId, true);
                if(userCircle == null)
                    continue;
                userSelfCircleId.add(userCircle.getId());
            }
            result = createBcPost(creatorId, locale, countryCode, title, content, userSelfCircleId, postAttachment, tags, postStatus, source, appName, postType, 
                    promoteScore, lookTypeId, extLookUrl, createdTime, opts);
            if(result.success()) {
                Post p = result.getResult();
                if(p != null)
                    asyncPostUpdateService.insertRelatedPost(p.getId());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
            return result;
        }
        return result;
    }

    private PostApiResult <Post> createBcSubPost(Long userId, Long postId, String content, PostAttachments attachments, PostTags tags, String extLookUrl, PostStatus postStatus, PostType postType) throws JsonProcessingException {
        PostApiResult <Post> result = new PostApiResult <Post>();   
        if(!userDao.exists(userId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        if(!postDao.exists(postId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        
        if(postStatus == null)
            postStatus = PostStatus.Published;
        
        Post mainPost = postDao.findById(postId);
        Long creatorId = mainPost.getCreator().getId();
        if(!creatorId.equals(userId)) {
            result.setErrorDef(ErrorDef.InvalidPostNotAuth);
            return result;
        }
        
        List<Attachment> postAttachments = new ArrayList<Attachment>(0);
        List<PostProduct> postProducts = new ArrayList<PostProduct>(0);
        
        Post newSubPost = new Post();
        newSubPost.setShardId(creatorId);
        newSubPost.setContent(content);
        newSubPost.setAttachments(postAttachments);
        newSubPost.setPostProducts(postProducts);
        newSubPost.setParentId(postId);
        newSubPost.setExtLookUrl(extLookUrl);
        newSubPost.setPostStatus(postStatus);
		if (postType != null)
			newSubPost.setPostType(postType);
        newSubPost.setTags(objectMapper.writer((PrettyPrinter)null).withView(MainPostDbView.class).writeValueAsString(tags));
        
        if(tags != null) {
            if(!tags.IsNullProductTags()) {
                if(tags.getProductTags().size() > 0 && !mainPost.getGotProductTag()) {
                    mainPost.setGotProductTag(true);
                    postDao.update(mainPost);
                }
                
                for(PostProductTag postProductTag : tags.getProductTags())
                {
                    PostProduct ppt = new PostProduct();
                    ppt.setShardId(creatorId);
                    ppt.setProductId(postProductTag.productId);
                    ppt.setTagAttrs(objectMapper.writer((PrettyPrinter)null).writeValueAsString(postProductTag.tagPoint));
                    ppt.setPost(newSubPost);
                    postProducts.add(ppt);
                }
            }
            if(!tags.IsNullExProductTags()) {
                for(PostExProductTag postExProductTag : tags.getExProductTags())
                {
                    PostProduct pept = new PostProduct();
                    pept.setShardId(creatorId);
                    pept.setProductId(null);
                    pept.setTagAttrs(objectMapper.writer((PrettyPrinter)null).writeValueAsString(postExProductTag.tagInfo));
                    pept.setPost(newSubPost);
                    postProducts.add(pept);
                }
            }
        }
            
        if(attachments != null) {
            if(attachments.files != null) {
                for(PostFile postFile : attachments.files)
                {
                    Attachment a = new Attachment();
                    a.setShardId(creatorId);
                    Object attachTarget = null;
                    if(postFile.fileId == null) {
                        AttachmentExtLink newLink = new AttachmentExtLink();
                        newLink.setShardId(creatorId);
                        newLink.setUserId(creatorId);
                        newLink.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(postFile.getMetadata()));
                        newLink.setLinkType(postFile.fileType);;
                        attachmentExtLinkDao.create(newLink);
                        attachTarget = newLink;
                    }
                    else
                        attachTarget = fileDao.findById(postFile.fileId); 
                    a.setTarget(attachTarget);
                    a.setPostId(newSubPost.getId());
                    postAttachments.add(a);
                }
            }
        }
        
        Post newCreatedSubpost = postDao.create(newSubPost);
        if(newCreatedSubpost == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        
        result.setResult(newCreatedSubpost);
        return result;
    }
    
    @Override
    public PostApiResult <Post> createSubPost(Long userId, Long postId, String content, String jAttachments, String jTags, String extLookUrl, PostStatus postStatus, PostType postType) {
        PostApiResult <Post> result = new PostApiResult <Post>(); 
        try {
            PostTags tags = transferTags(jTags);
            PostAttachments postAttachment = transferAttachments(jAttachments);
            result = createBcSubPost(userId, postId, content, postAttachment, tags, extLookUrl, postStatus, postType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
            return result;
        }
        
        return result;
    }
    
    private void updatePostViewCircleInCount(Long postId, Long count) {
        if(!Constants.getIsPostCacheView())
            return;
        PostView postView = postViewDao.findByPostId(postId);
        if(postView == null)
            return;
        PostViewAttr postAttr = postView.getAttribute();
        if(postAttr == null)
            postAttr = new PostViewAttr();
        if(postAttr.getCircleInCount() != null && postAttr.getCircleInCount().equals(count))
            return;
        postAttr.setCircleInCount(count);
        postView.setAttribute(postAttr);
        postViewDao.update(postView);
        publishDurableEvent(new PostViewUpdateEvent(postId, postAttr));
    }
    
    private void updateCircleInCount(Long postId, Long repostCount) {
        if(repostCount == null) {
            repostCount = (long)0;
            Map<Long, Long> countMap = postCircleInDao.getCircleInCounts(ImmutableList.of(postId));
            if(countMap.containsKey(postId))
                repostCount = countMap.get(postId);
        }

        updatePostViewCircleInCount(postId, repostCount);
        PostAttrType attrType = PostAttrType.PostCircleInCount;
        PostAttribute postAttr = postAttributeDao.findByTarget("Post", postId, attrType);
        if(postAttr != null) {
            if(postAttr.getAttrValue().equals(repostCount))
                return;
            postAttr.setAttrValue(repostCount);
            postAttributeDao.update(postAttr);
            return;
        }

        postAttr = new PostAttribute();
        postAttr.setRefType("Post");
        postAttr.setRefId(postId);
        postAttr.setAttrType(attrType);
        postAttr.setAttrValue(repostCount);
        try {
            postAttributeDao.create(postAttr);
        }
        catch(Exception e) {
            return;
        }
    }
    
    private void updateCircleThumbnail(Circle circle, String newThumbnailUrl, Long iconId) {
        circle.setLastModified(Calendar.getInstance().getTime());
        List<Long> circleTypeIds = new ArrayList<Long>();
        circleTypeIds.add(circle.getCricleTypeId());
        PageResult<Circle> defaultCircle = circleDao.findBcDefaultCircleByCircleTypeIds(circleTypeIds, (long)0, (long)100);
        Long defaultIconId = null;
        if(defaultCircle.getResults().size() > 0)
            defaultIconId = defaultCircle.getResults().get(0).getIconId();
        
        if(iconId != null) {
            Boolean changeIcon = false;
            if(circle.getIconId() == null)
                changeIcon = true;
            if(defaultIconId != null && circle.getIconId() != null && defaultIconId.equals(circle.getIconId()))
                changeIcon = true;
            
            User circleCreator = circle.getCreator(); 
            if(circleCreator != null) {
                UserType userType = circleCreator.getUserType();
                if(userType != null && userType.equals(UserType.CL))
                    changeIcon = false;
            }
            if(changeIcon)
                circle.setIconId(iconId);
        }
        circleDao.update(circle);
        
        if(newThumbnailUrl == null || newThumbnailUrl.length() <= 0)
            return;
        
        List<CircleAttribute> circleAttrs = circleAttributeDao.findCircleAttribute(null, circle, CircleAttrType.Thumbnail);
        if(circleAttrs == null || circleAttrs.size() < Circle.maxPostThumbnailSize) {
            CircleAttribute cirAttr = new CircleAttribute();
            cirAttr.setRegion(Constants.getPostRegion());
            cirAttr.setCircle(circle);
            cirAttr.setAttrType(CircleAttrType.Thumbnail);
            cirAttr.setAttrValue(newThumbnailUrl);
            circleAttributeDao.create(cirAttr);
            return;
        }
        
        if(circleAttrs.size() > Circle.maxPostThumbnailSize) {
            for(int idx = Circle.maxPostThumbnailSize; idx < circleAttrs.size(); idx++) {
                CircleAttribute cirAttrTmp = circleAttrs.get(idx);
                cirAttrTmp.setIsDeleted(true);
                circleAttributeDao.update(cirAttrTmp);
            }
        }
        
        int oldestThumbnailIdx = (circleAttrs.size() > Circle.maxPostThumbnailSize ? Circle.maxPostThumbnailSize - 1 : circleAttrs.size()) - 1; 
        CircleAttribute tpUpdateCirAttr = circleAttrs.get(oldestThumbnailIdx);
        tpUpdateCirAttr.setAttrValue(newThumbnailUrl);
        circleAttributeDao.update(tpUpdateCirAttr);
    }
    
    @Override
    public PostApiResult <Post> circleInPost(Long creatorId, String countryCode, Long postId, Long circleId, String newTitle, PostType postType) {
        PostApiResult <Post> result = new PostApiResult <Post>(); 
        if(postId == null || circleId == null || !postDao.exists(postId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
            
        PostCircleIn sorucePostCircleIn = postCircleInDao.findByPostId(postId);
        Post oriPost = postDao.findById(postId);       
        if(oriPost.getIsDeleted())  {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        
        List<Long> newCircleIds = new ArrayList<Long>();
        newCircleIds.add(circleId);
        Post newCreatedPost = duplicatePost(oriPost, creatorId, countryCode, newTitle, newCircleIds, CIRCLE_IN_POST_SOURCE, oriPost.getAppName(), postType, PostStatus.Published, null, null, PushPostNewType.NO_PUSH);
        if(newCreatedPost == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        
        result.setResult(newCreatedPost);
        PostCircleIn postCircleIn = new PostCircleIn();
        postCircleIn.setUserId(creatorId);
        postCircleIn.setPostId(newCreatedPost.getId());
        postCircleIn.setCircleId(circleId);
        postCircleIn.setSourceUserId(oriPost.getCreatorId());
        postCircleIn.setSourcePostId(postId);
        Long rootId = null;
        if(sorucePostCircleIn != null && sorucePostCircleIn.getRootPostId() != null)
            rootId = sorucePostCircleIn.getRootPostId();
        else
            rootId = postId;
        postCircleIn.setRootPostId(rootId);
        postCircleInDao.create(postCircleIn);
        updateCircleInCount(postId, null);
        publishDurableEvent(new PostCreateEvent(newCreatedPost.getId(), circleId, newCreatedPost.getLocale(), creatorId, rootId, newCreatedPost.getCreatedTime()));
        publishDurableEvent(new PostFanOutEvent(newCreatedPost.getId(), circleId, creatorId, rootId, newCreatedPost.getCreatedTime()));
        publishDurableEvent(UserBadgeEvent.CreateCircleInEvent(postId, oriPost.getCreatorId()));
        return result;
    }
    
    @Override
    public PageResult<User> listCircleInUser(Long postId, BlockLimit blockLimit) {
        PageResult<User> result = postCircleInDao.listCircleInUser(postId, blockLimit); 
        updateCircleInCount(postId, result.getTotalSize().longValue());
        return result;
    }
    
    @Override
    public PageResult<Circle> listCircleInCircle(Long postId, BlockLimit blockLimit) {
    	PageResult<Circle> result = postCircleInDao.listCircleInCircle(postId, blockLimit);
		return result;
    }
    
    @Override
    public Map<Long, Long> listCircleInCount(List<Long> postIds) {
        return postAttributeDao.checkPostAttriButeByIds("Post", PostAttrType.PostCircleInCount, postIds.toArray(new Long[postIds.size()]));
    }
    
    @Override
    public Map<Long, Map<PostAttrType, Long>> listPostsAttr(List<Long> postIds) {
        return postAttributeDao.listPostAttriButeByIds("Post", postIds.toArray(new Long[postIds.size()]));
    }
    
    @Override
    public Map<Long, User> listCircleInSourceUserByPosts(List<Long> postIds) {
        return postCircleInDao.getSourceUser(postIds);
    }
    
    @Override
    public PageResult<Circle> listExCircleInCircle(Long userId, Long postId, BlockLimit blockLimit) {
    	return postCircleInDao.listCircleInCircle(userId, postId, blockLimit);
    	//List<Long> postList = postCircleInDao.listPostIdBySource(userId, postId);
    	//return postCircle2Dao.findCircleByPosts(new HashSet<Long>(postList), blockLimit);
    }
    
    private MainPostWrapper transferMainPost(String mainPost) throws InvalidFormatException {
        MainPostWrapper result = new MainPostWrapper();        
        if(mainPost == null)
            return result;
        try {
            result = objectMapper.readValue(mainPost,
                    new TypeReference<MainPostWrapper>() {
                    });
        } catch (Exception e) {
            logger.error("", e);
            if(mainPost.length() == 0)
                return result;
            else
                throw new InvalidFormatException(mainPost, e, null);
        }
        return result;
    }
    
    private SubPostWrapper transferSubPost(String subPost) throws InvalidFormatException {
        SubPostWrapper result = new SubPostWrapper();        
        if(subPost == null)
            return result;
        try {
            result = objectMapper.readValue(subPost,
                    new TypeReference<SubPostWrapper>() {
                    });
        } catch (Exception e) {
            logger.error("", e);
            if(subPost.length() == 0)
                return result;
            else
                throw new InvalidFormatException(subPost, e, null);
        }
        return result;
    }
    
    @Override
    public PostApiResult <List<Post>> createPosts_v2(Long creatorId, String locale, String countryCode, String mainPost, String source, AppName appName, Long promoteScore,
            List<String> subPosts, Map<PostOption, String> opts) {
        PostApiResult <List<Post>> result = new PostApiResult <List<Post>>();
        if(creatorId == null) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        if(mainPost == null) {
            result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
            return result;
        }

        MainPostWrapper mainPostObj = null;
        List<SubPostWrapper> subPostObjs = new ArrayList<SubPostWrapper>();
        List<Post> postResults = new ArrayList<Post>();
        result.setResult(postResults);
        
        try {
            mainPostObj = transferMainPost(mainPost);
            if(subPosts != null)
                for(String subPost : subPosts)
                    subPostObjs.add(transferSubPost(subPost));
            
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
            return result;
        }
        
        Post mainBcPost = null;
        PostApiResult <Post> mainBcPostResult = null;
        Long userCircleId = null;
        if(mainPostObj.valid()) {
            List<Circle> postCircle = circleDao.findByIds(mainPostObj.circleIds.toArray(new Long[mainPostObj.circleIds.size()]));
            if(postCircle.size() > 0) {
                Long relCircleId = postCircle.get(0).getId(); // PM's spec, for old version, we only use one circle as well
                Circle tmpCircle = circleDao.findById(relCircleId);
                if(!tmpCircle.getCreatorId().equals(creatorId)) {
                    PageResult<Circle> defaultCircle = circleDao.findBcDefaultCircleByCircleTypeIds(ImmutableList.of(tmpCircle.getCricleTypeId()), (long)0, (long)1);
                    if(defaultCircle.getResults().size() <= 0) {
                        result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                        return result;
                    } 
                    userCircleId = defaultCircle.getResults().get(0).getId();
                }
                else {
                    userCircleId = relCircleId;
                }
            }
            
            List<Long> userSelfCircleId = new ArrayList<Long>();
            Circle defaultCircle = circleDao.findById(userCircleId);
            Circle userCircle = circleService.getUserAccessibleCircle(defaultCircle, creatorId, true);
            if(userCircle == null) {
                result.setErrorDef(ErrorDef.InvalidCircleId);
                return result;
            }
            userSelfCircleId.add(userCircle.getId());
            try {
                mainBcPostResult = createBcPost(creatorId, locale, countryCode, mainPostObj.title, mainPostObj.content, userSelfCircleId, mainPostObj.attachments, mainPostObj.tags, mainPostObj.postStatus, source, appName, mainPostObj.postType, promoteScore, mainPostObj.lookTypeId, mainPostObj.extLookUrl, mainPostObj.createdTime, opts);
            } catch (JsonProcessingException e) {
                result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                return result;
            }
            if(mainBcPostResult != null && mainBcPostResult.success()) {
                postResults.add(mainBcPostResult.getResult());
                mainBcPost = mainBcPostResult.getResult();
            }
            else {
                result.setErrorDef(ErrorDef.UnknownPostError);
                return result;
            }
        }
        else {
            result.setErrorDef(ErrorDef.InvalidPostTitle);
            return result;
        }
        
        for(SubPostWrapper subPostObj : subPostObjs) {
            if(subPostObj.valid()){
                Post subBcPost = null;
                PostApiResult <Post> subBcPostResult = null;
                try {
                    subBcPostResult = createBcSubPost(creatorId, mainBcPost.getId(), subPostObj.content, subPostObj.attachments, subPostObj.tags, subPostObj.extLookUrl, PostStatus.Published, null);
                } catch (JsonProcessingException e) {
                    result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                    return result;
                }
                if(subBcPostResult != null && subBcPostResult.success()) {
                    subBcPost = subBcPostResult.getResult();
                    postResults.add(subBcPost);
                }
                else {
                    result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                    break;
                }
            }
            else {
                result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                break;
            }
        }
        
        if(result.success()) {
            for(Post p : result.getResult()) {
                if(p == null)
                    continue;
                if(p.getParentId() != null)
                    continue;
                asyncPostUpdateService.insertRelatedPost(p.getId());
                break;
            }
        }
        
        return result;
    }
    
    @Override
    public PostApiResult <List<Post>> createPosts(Long creatorId, String locale, String countryCode, String mainPost, String source, AppName appName, Long promoteScore,
            List<String> subPosts, Map<PostOption, String> opts) {
        PostApiResult <List<Post>> result = new PostApiResult <List<Post>>();
        if(creatorId == null) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        if(mainPost == null) {
            result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
            return result;
        }

        MainPostWrapper mainPostObj = null;
        List<SubPostWrapper> subPostObjs = new ArrayList<SubPostWrapper>();
        List<Post> postResults = new ArrayList<Post>();
        result.setResult(postResults);

        try {
            mainPostObj = transferMainPost(mainPost);
            if(subPosts != null)
                for(String subPost : subPosts)
                    subPostObjs.add(transferSubPost(subPost));
            
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
            return result;
        }

        Post mainBcPost = null;
        PostApiResult <Post> mainBcPostResult = null;
        if(mainPostObj.valid()) {
            List<Long> userSelfCircleId = new ArrayList<Long>();
            if(mainPostObj.circleIds != null && mainPostObj.circleIds.size() > 0) {
            List<Circle> postCircle = circleDao.findByIds(mainPostObj.circleIds.toArray(new Long[mainPostObj.circleIds.size()]));
                for(Circle c : postCircle) {
                    Circle userCircle = circleService.getUserAccessibleCircle(c, creatorId, true);
                    if(userCircle == null)
                        continue;
                    userSelfCircleId.add(userCircle.getId());
                }
                if(userSelfCircleId.size() <= 0) {
                    result.setErrorDef(ErrorDef.InvalidCircleId);
                    return result;
                }
            }
            try {
                mainPostObj.postType = mainPostObj.postType == null ? PostType.YCL_LOOK : mainPostObj.postType;
                mainBcPostResult = createBcPost(creatorId, locale, countryCode, mainPostObj.title, mainPostObj.content, userSelfCircleId, mainPostObj.attachments, mainPostObj.tags, mainPostObj.postStatus, source, appName, mainPostObj.postType, promoteScore, mainPostObj.lookTypeId, mainPostObj.extLookUrl, mainPostObj.createdTime, opts);
            } catch (JsonProcessingException e) {
                result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                return result;
            }
            if(mainBcPostResult != null && mainBcPostResult.success()) {
                postResults.add(mainBcPostResult.getResult());
                mainBcPost = mainBcPostResult.getResult();
            }
            else {
                result.setErrorDef(ErrorDef.UnknownPostError);
                return result;
            }
        }
        else {
            result.setErrorDef(ErrorDef.InvalidPostTitle);
            return result;
        }
        
        for(SubPostWrapper subPostObj : subPostObjs) {
            if(subPostObj.valid()){
                Post subBcPost = null;
                PostApiResult <Post> subBcPostResult = null;
                try {
                    subBcPostResult = createBcSubPost(creatorId, mainBcPost.getId(), subPostObj.content, subPostObj.attachments, subPostObj.tags, subPostObj.extLookUrl, PostStatus.Published, null);
                } catch (JsonProcessingException e) {
                    result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                    return result;
                }
                if(subBcPostResult != null && subBcPostResult.success()) {
                    subBcPost = subBcPostResult.getResult();
                    postResults.add(subBcPost);
                }
                else {
                    result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                    break;
                }
            }
            else {
                result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                break;
            }
        }
        
        if(result.success()) {
            for(Post p : result.getResult()) {
                if(p == null)
                    continue;
                if(p.getParentId() != null)
                    continue;
                
                asyncPostUpdateService.insertRelatedPost(p.getId());
                break;
            }
        }
        return result;
    }
    
    private PostApiResult <Post> updateSubPost(Long creatorId, Long mainPostId, Long subPostId, String content, PostAttachments attachments, String extLookUrl, PostTags tags) {
        PostApiResult <Post> result = new PostApiResult <Post>();
        if(!userDao.exists(creatorId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        if(!postDao.exists(mainPostId) || !postDao.exists(subPostId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        
        Post relMainPost = postDao.findById(mainPostId);        
        if(!creatorId.equals(relMainPost.getCreatorId())) {
            result.setErrorDef(ErrorDef.InvalidPostNotAuth);
            return result;
        }

        Post relPost = postDao.findById(subPostId);
        if(content != null) {
            relPost.setContent(content);
        }
        
		if(attachments != null) {
		    if(attachments.files != null) {
    			if(attachments.files.size() <= 0) {
    				List<Attachment> exAttachs = relPost.getAttachments();
    				for(Attachment exA : exAttachs) {
    					exA.setIsDeleted(true);
    					attachmentDao.update(exA);
    				}
    			}
    			else {
    				List<Long> toAddFileId = new ArrayList<Long>(0);
    				for(PostFile pFile : attachments.files) {
    					toAddFileId.add(pFile.fileId);
    				}
    				
    				List<Attachment> exAttachs = relPost.getAttachments();
    				for(Attachment exA : exAttachs) {
    					Object target = exA.getTarget();
    					if(target instanceof com.cyberlink.cosmetic.modules.file.model.File) {                    
    						com.cyberlink.cosmetic.modules.file.model.File attachFile = (com.cyberlink.cosmetic.modules.file.model.File)target;
    						Long attachFileId = attachFile.getId();
    						if(attachFileId.equals((long)0)) {
    							exA.setIsDeleted(true);
    							attachmentDao.update(exA);
    						}
    						else if(toAddFileId.contains(attachFileId))
    							toAddFileId.remove(attachFileId);
    						else {
    							exA.setIsDeleted(true);
    							attachmentDao.update(exA);
    						}
    					}
    					else if(target instanceof AttachmentExtLink) {
    						exA.setIsDeleted(true);
    						attachmentDao.update(exA);
    					}
    				}
    				
    				for(PostFile postFile : attachments.files)
    				{
    					if(!toAddFileId.contains(postFile.fileId))
    						continue;
    					
    					Attachment a = new Attachment();
    					a.setShardId(creatorId);
    					Object attachTarget = null;
    					if(postFile.fileId == null) {
    						AttachmentExtLink newLink = new AttachmentExtLink();
    						newLink.setShardId(creatorId);
    						newLink.setUserId(creatorId);
    						try {
    							newLink.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(postFile.getMetadata()));
    						} catch (JsonProcessingException e) {
    							e.printStackTrace();
    							continue;
    						}
    						newLink.setLinkType(postFile.fileType);;
    						attachmentExtLinkDao.create(newLink);
    						attachTarget = newLink;
    					}
    					else
    						attachTarget = fileDao.findById(postFile.fileId); 
    					a.setTarget(attachTarget);
    					a.setPostId(subPostId);
    					attachmentDao.create(a);
    				}
    			}
		    }
		}

        if(tags != null) {
            PostTags relPostTag = relPost.getPostTags();
			if (relPostTag == null)
				relPostTag = new PostTags();
			if (tags.lookTag != null) {
				if(tags.lookTag.equals(""))
					relPostTag.lookTag = null;
				else
					relPostTag.lookTag = tags.lookTag;
			}
			if (tags.horoscopeTag != null) {
				if (tags.horoscopeTag.horoscopeType.equals(""))
					relPostTag.horoscopeTag = null;
				else
					relPostTag.horoscopeTag = tags.horoscopeTag;
			}
            
			try {
				relPost.setTags(objectMapper.writer((PrettyPrinter) null).withView(Views.Public.class).writeValueAsString(relPostTag));
			} catch (JsonProcessingException e) {
				result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
				return result;
			}

            if(!tags.IsNullProductTags()) {
                if(tags.getProductTags().size() <= 0) {
                    List<PostProduct> exPostProducts = postProductDao.listByPost(relPost, false);
                    for(PostProduct expp : exPostProducts){
                        expp.setIsDeleted(true);
                        postProductDao.update(expp);
                    }    
                }
                else {
                    List<PostProduct> exPostProducts = postProductDao.listByPost(relPost, false);
                    for(PostProduct expp : exPostProducts){
                        expp.setIsDeleted(true);
                        postProductDao.update(expp);
                    }                
                
                    for(PostProductTag postProductTag : tags.getProductTags())
                    {
                        PostProduct ppt = new PostProduct();
                        ppt.setShardId(creatorId);
                        ppt.setProductId(postProductTag.productId);
                        try {
                            ppt.setTagAttrs(objectMapper.writer((PrettyPrinter)null).writeValueAsString(postProductTag.tagPoint));
                        } catch (JsonProcessingException e) {
                        }
                        ppt.setPost(relPost);
                        postProductDao.create(ppt);
                    }
                }
            }
        }

        if(extLookUrl != null)
            relPost.setExtLookUrl(extLookUrl);
        Post updatedPost = postDao.update(relPost);
        if(updatedPost == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        
        result.setResult(updatedPost);
        return result;
    }
    
    private PostApiResult <Post> updatePost(Long creatorId, String locale, Long postId, AppName appName, PostType postType, String title, String content, List<Long> circleIds, PostAttachments attachments, PostTags tags, PostStatus postStatus, String extLookUrl, Long lookTypeId, Date createdTime, Long promoteScore) {
        PostApiResult <Post> result = new PostApiResult <Post>();
        if(!userDao.exists(creatorId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        if(!postDao.exists(postId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        
        Post relPost = postDao.findById(postId);
        if(relPost.getParentId() != null) {
            Long mainPostId = relPost.getParentId();
            return updateSubPost(creatorId, mainPostId, postId, content, attachments, extLookUrl, tags);
        }
        
        if(!creatorId.equals(relPost.getCreatorId())) {
            result.setErrorDef(ErrorDef.InvalidPostNotAuth);
            return result;
        }
        
        PostStatus originalPostStatus = relPost.getPostStatus();
        if(postStatus != null) {
            User creator = userDao.findById(creatorId);
            if(postStatus.equals(PostStatus.Published)) {
                UserStatus userStatus = creator.getUserStatus();
                if(userStatus != null && userStatus.equals(UserStatus.Hidden))
                    postStatus = PostStatus.Unpublished;
            }
            relPost.setPostStatus(postStatus);
        }
        
        PostStatus finalPostStatus = relPost.getPostStatus();
        
        if(locale != null)
            relPost.setLocale(locale);
        
        if(appName != null)
            relPost.setAppName(appName);
        
        if(postType != null)
            relPost.setPostType(postType);
        
        if(title != null) {
            if(title.length() <= 0) {
                result.setErrorDef(ErrorDef.InvalidPostTitle);
                return result;
            }
            relPost.setTitle(title);
        }
        
        if(content != null) {
            relPost.setContent(content);
        }
            
        Circle oldCircle = null, finalCircle = null;
        Long originalCircleId = relPost.getCircleId();
        int newCirclePostCount = 0;
        List<Look> relatedLooks = new ArrayList<Look>(0);
        
        if(extLookUrl != null) {
            if(extLookUrl.length() <= 0)
                relPost.setExtLookUrl(null);
            else
                relPost.setExtLookUrl(extLookUrl);
        }
        
        if(lookTypeId != null) {
        	if(lookTypeId == -1)
        		relPost.setLookTypeId(null);
        	else
        		relPost.setLookTypeId(lookTypeId);
        }
        
		if(attachments != null) {
		    List<Attachment> exAttachs = relPost.getAttachments();
		    if(attachments.files != null) {
		        if(finalPostStatus.getViewable() && finalCircle == null && relPost.getCircleId() != null)
		            finalCircle = circleDao.findById(relPost.getCircleId());
    			if(attachments.files.size() <= 0) {
    				for(Attachment exA : exAttachs) {
    				    Object target = exA.getTarget();
                        if(target instanceof com.cyberlink.cosmetic.modules.file.model.File)
                            exA.setIsDeleted(true);
    				}
    			}
    			else {
    				List<Long> toAddFileId = new ArrayList<Long>(0);
    				
    				for(PostFile pFile : attachments.files) {
    					toAddFileId.add(pFile.fileId);
    				}
    				for(Attachment exA : exAttachs) {
    					Object target = exA.getTarget();
    					if(target instanceof com.cyberlink.cosmetic.modules.file.model.File) {                    
    						com.cyberlink.cosmetic.modules.file.model.File attachFile = (com.cyberlink.cosmetic.modules.file.model.File)target;
    						Long attachFileId = attachFile.getId();
    						if(attachFileId.equals((long)0)) {
    							exA.setIsDeleted(true);
    							attachmentDao.update(exA);
    						}
    						else if(toAddFileId.contains(attachFileId))
    							toAddFileId.remove(attachFileId);
    						else {
    							exA.setIsDeleted(true);
    						}
    					}
    					else if(target instanceof AttachmentExtLink) {
    						exA.setIsDeleted(true);
    					}
    				}
    				
    				for(PostFile postFile : attachments.files)
    				{
    					if(!toAddFileId.contains(postFile.fileId))
    						continue;
    					
    					Attachment a = new Attachment();
    					a.setShardId(creatorId);
    					Object attachTarget = null;
    					if(postFile.fileId == null) {
    						AttachmentExtLink newLink = new AttachmentExtLink();
    						newLink.setShardId(creatorId);
    						newLink.setUserId(creatorId);
    						try {
    							newLink.setMetadata(objectMapper.writer((PrettyPrinter)null).writeValueAsString(postFile.getMetadata()));
    						} catch (JsonProcessingException e) {
    							e.printStackTrace();
    							continue;
    						}
    						newLink.setLinkType(postFile.fileType);;
    						attachmentExtLinkDao.create(newLink);
    						attachTarget = newLink;
    					}
    					else
    						attachTarget = fileDao.findById(postFile.fileId); 
    					a.setTarget(attachTarget);
    					a.setPostId(postId);
    					exAttachs.add(a);
    				}
    			}
		    }
        }
		
        if(circleIds != null){       
            Circle userCircle = null;
            Long selectedCircleId = null;
            if(circleIds.size() > 0) {
                Long tmpCirId = circleIds.get(0);
                if(circleDao.exists(tmpCirId)) {
                    Circle tmpCircle = circleDao.findById(tmpCirId);
                    userCircle = circleService.getUserAccessibleCircle(tmpCircle, creatorId, true);
                    if(userCircle != null)
                        selectedCircleId = userCircle.getId();
                }
            }
            
            if(selectedCircleId != null && !selectedCircleId.equals(relPost.getCircleId())) {
                if(finalPostStatus.getViewable()) {
                    oldCircle = circleDao.findById(relPost.getCircleId());
                    finalCircle = userCircle;
                    newCirclePostCount = 1;
                }
                relPost.setCircleId(selectedCircleId);
            }
        }
        
        Boolean updatePostNew = false;
        if(promoteScore != null) {
            if(promoteScore.compareTo(Long.valueOf(0)) < 0)
                relPost.setPromoteScore(null);
            else
                relPost.setPromoteScore(promoteScore);
            Circle pC = circleDao.findById(relPost.getCircleId());
            if(pC != null)
                updatePostNew = true;
        }
        
        if(tags != null) {    
            PostTags relPostTag = relPost.getPostTags();
			if (relPostTag == null)
				relPostTag = new PostTags();
            if(!tags.IsNullUserDefTags())
                relPostTag.setUserDefTags(tags.getUserDefTags());
            if(!tags.IsNullKeywords())
                relPostTag.setKeywords(tags.getKeywords());
			if (tags.lookTag != null) {
				if (tags.lookTag.equals(""))
					relPostTag.lookTag = null;
				else
					relPostTag.lookTag = tags.lookTag;
			}
			if (tags.horoscopeTag != null) {
				if (tags.horoscopeTag.horoscopeType.equals(""))
					relPostTag.horoscopeTag = null;
				else
					relPostTag.horoscopeTag = tags.horoscopeTag;
			}
            
            try {
                relPost.setTags(objectMapper.writer((PrettyPrinter)null).withView(MainPostDbView.class).writeValueAsString(relPostTag));
            } catch (JsonProcessingException e) {
                result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                return result;
            }
			if(!tags.IsNullProductTags()) {
    			if(tags.getProductTags().size() <= 0) {
                    List<PostProduct> exPostProducts = postProductDao.listByPost(relPost, false);
                    for(PostProduct expp : exPostProducts){
                        expp.setIsDeleted(true);
                        postProductDao.update(expp);
                    }    
                }
                else {
    				if(tags.getProductTags().size() > 0) {
    					List<PostProduct> exPostProducts = postProductDao.listByPost(relPost, false);
    					for(PostProduct expp : exPostProducts){
    						expp.setIsDeleted(true);
    						postProductDao.update(expp);
    					}
    					
    					for(PostProductTag postProductTag : tags.getProductTags())
    					{
    						PostProduct ppt = new PostProduct();
    						ppt.setShardId(creatorId);
    						ppt.setProductId(postProductTag.productId);
    						try {
    							ppt.setTagAttrs(objectMapper.writer((PrettyPrinter)null).writeValueAsString(postProductTag.tagPoint));
    						} catch (JsonProcessingException e) {
    							e.printStackTrace();
    						}
    						ppt.setPost(relPost);
    						postProductDao.create(ppt);
    					}
    				}
    			}
			}
			if (!tags.IsNullExProductTags()) {
				if (tags.getExProductTags().size() <= 0) {
					List<PostProduct> exPostProducts = postProductDao.listByPost(relPost, true);
					for (PostProduct expp : exPostProducts) {
						expp.setIsDeleted(true);
						postProductDao.update(expp);
					}
				} 
				else {
					List<PostProduct> exPostProducts = postProductDao.listByPost(relPost, true);
					for (PostProduct expp : exPostProducts) {
						expp.setIsDeleted(true);
						postProductDao.update(expp);
					}

					for (PostExProductTag postExProductTag : tags.getExProductTags()) {
						PostProduct pept = new PostProduct();
						pept.setShardId(creatorId);
						pept.setProductId(null);
						try {
							pept.setTagAttrs(objectMapper.writer((PrettyPrinter) null).writeValueAsString(postExProductTag.tagInfo));
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
						pept.setPost(relPost);
						postProductDao.create(pept);
					}
				}
			}
		}

        Boolean isSecret = null;
        // If change circle
        if(oldCircle != null && finalCircle != null) {
            if(oldCircle.getIsSecret() != finalCircle.getIsSecret()) {
                isSecret = finalCircle.getIsSecret();
            }
        }
        // If change post status
        if(!originalPostStatus.getViewable() && finalPostStatus != null && finalPostStatus.getViewable()) {
            if(finalCircle == null) {
                finalCircle = circleDao.findById(relPost.getCircleId());
                newCirclePostCount = 1;
            }    
            if(!finalCircle.getIsSecret())
                isSecret = finalCircle.getIsSecret();
        }
        
        Post updatedPost = updateMainPost(relPost, isSecret, originalPostStatus, createdTime, true);
        if(updatedPost == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }

        result.setResult(updatedPost);

        for(Look rLook : relatedLooks) {
            rLook.setPostId(updatedPost.getId());
            lookDao.update(rLook);
        }
        
        if(oldCircle != null)
            updateCircleAttribute(oldCircle, -1);
        
        if(finalCircle != null) {
            updateCircleAttribute(finalCircle, newCirclePostCount);
            while(finalCircle != null) {
                List<Attachment> attachs = updatedPost.getAttachments();
                if(attachs.size() <= 0)
                    break;
                com.cyberlink.cosmetic.modules.file.model.File photoFile = null;
                for(Attachment pA : attachs) {
                    if(pA.getIsDeleted())
                        continue;
                    photoFile = pA.getAttachmentFile();
                    if(photoFile != null)
                        break;
                }
                if(photoFile == null)
                    break;
                updateCircleThumbnail(finalCircle, null, photoFile.getId());
                break;
            }
        }
        
        if(originalPostStatus != null && finalPostStatus != null
                && !originalPostStatus.getViewable() && finalPostStatus.getViewable())
            updatePostNew = true;
        if(updatePostNew) {
            Long bonus = relPost.getBasicSortBonus();
            Boolean forceAdd = false;
            if(promoteScore != null) {
                if(promoteScore.compareTo(Long.valueOf(0)) < 0)
                    bonus = updatedPost.getBasicSortBonus();
                else
                    bonus += promoteScore;
                forceAdd = true;
            }
            List<Long> circleTypeIds = new ArrayList<Long>();
            circleTypeIds.add(relPost.getCircle().getCircleTypeId());
            relPost = postPopularityService.pushToNewImmediate(relPost, circleTypeIds, bonus, false, false, forceAdd);
        }
        publishDurableEvent(new PostUpdateEvent(postId, creatorId,
                originalCircleId, relPost.getCircleId()));
        return result;
    }
    
    @Override
    public PostApiResult <Post> updatePost(Long creatorId, String locale, Long postId, AppName appName, PostType postType, String title, String content, List<Long> circleIds, String jAttachments, String jTags, PostStatus postStatus, Long promoteScore, String extLookUrl, Long lookTypeId, Date createdTime) {
        PostApiResult <Post> result = new PostApiResult <Post>();
        PostAttachments postAttachment = null;
        PostTags tags = null;
        try {
            if(jAttachments != null)
                postAttachment = transferAttachments(jAttachments);
            if(jTags != null)
                tags = transferTags(jTags);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
            return result;
        }
        
        return updatePost(creatorId, locale, postId, appName, postType, title, content, circleIds, postAttachment, tags, postStatus, extLookUrl, lookTypeId, createdTime, promoteScore);
    }
    
    @Override
    public PostApiResult <Post> updateSubPost(Long creatorId, Long mainPostId, Long subPostId, String content, String jAttachments, String extLookUrl, String jTags) {
        PostApiResult <Post> result = new PostApiResult <Post>();
        PostAttachments postAttachment = null;
        PostTags tags = null;
        try {
            postAttachment = transferAttachments(jAttachments);
            tags = transferTags(jTags);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
            return result;
        }
        
        return updateSubPost(creatorId, mainPostId, subPostId, content, postAttachment, extLookUrl, tags);
    }
    
    @Override
    public PostApiResult <List<Post>> updatePosts(Long creatorId, String locale, AppName appName, String mainPost, String source, Long promoteScore, List<String> updateSubPosts, List<String> deleteSubPosts, List<String> newSubPosts) {
        PostApiResult <List<Post>> result = new PostApiResult <List<Post>>();
        if(mainPost == null || mainPost.length() <= 0) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
            
        MainPostWrapper mainPostObj = null;
        List<SubPostWrapper> updateSubPostObjs = new ArrayList<SubPostWrapper>();
        List<SubPostWrapper> deleteSubPostObjs = new ArrayList<SubPostWrapper>();
        List<SubPostWrapper> newSubPostObjs = new ArrayList<SubPostWrapper>();
        List<Post> postResults = new ArrayList<Post>();
        result.setResult(postResults);
        
        try {
            mainPostObj = transferMainPost(mainPost);
            if(updateSubPosts != null) {
                for(String subPost : updateSubPosts) {
                    if(subPost != null)
                        updateSubPostObjs.add(transferSubPost(subPost));
                }
            }
            if(deleteSubPosts != null) {
                for(String subPost : deleteSubPosts) {
                    if(subPost != null)
                        deleteSubPostObjs.add(transferSubPost(subPost));
                }
            }
            if(newSubPosts != null) {
                for(String subPost : newSubPosts) {
                    if(subPost != null)
                        newSubPostObjs.add(transferSubPost(subPost));
                }
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
            return result;
        }
        
        Long mainPostId = mainPostObj.postId;
        if(mainPostId == null) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        
        PostApiResult<Post> mainPostResult = updatePost(creatorId, locale, mainPostObj.postId, appName, null, mainPostObj.title, mainPostObj.content, mainPostObj.circleIds, mainPostObj.attachments, mainPostObj.tags, mainPostObj.postStatus, mainPostObj.extLookUrl, null, mainPostObj.createdTime, promoteScore);
        if(!mainPostResult.success()) {
            result.setErrorDef(mainPostResult.getErrorDef());
            return result;
        }
        else {
            postResults.add(mainPostResult.getResult());
        }
        
        if(updateSubPostObjs.size() > 0) {                
            for(SubPostWrapper subPost : updateSubPostObjs) {
                PostApiResult<Post> subPostResult = updateSubPost(creatorId, mainPostId, subPost.subPostId, subPost.content, subPost.attachments, subPost.extLookUrl, subPost.tags);
                if(!subPostResult.success()) {
                    result.setErrorDef(subPostResult.getErrorDef());
                    return result;
                }
                else {
                    postResults.add(subPostResult.getResult());
                }
            }
        }
        
        if(deleteSubPostObjs.size() > 0) {
            for(SubPostWrapper subPostWrapper : deleteSubPostObjs) {
                Long subPostId = subPostWrapper.subPostId;
                if(subPostId == null || !postDao.exists(subPostId)) {
                    result.setErrorDef(ErrorDef.InvalidPostTargetId);
                    return result;
                }
                Post subPost = postDao.findById(subPostId);
                subPost.setIsDeleted(true);
                Post deletedSubPost = postDao.update(subPost);
                if(deletedSubPost == null) {
                    result.setErrorDef(ErrorDef.UnknownPostError);
                    return result;
                }
                else {
                    postResults.add(deletedSubPost);
                }
            }
        }
        
        if(newSubPostObjs.size() > 0) {
            try {
                for(SubPostWrapper subPostWrapper : newSubPostObjs) {
                    PostApiResult<Post> createSubPostResult = createBcSubPost(creatorId, mainPostId, subPostWrapper.content, subPostWrapper.attachments, subPostWrapper.tags, subPostWrapper.extLookUrl, PostStatus.Published, null);
                    if(!createSubPostResult.success()) {
                        result.setErrorDef(createSubPostResult.getErrorDef());
                        return result;
                    }
                    else {
                        postResults.add(createSubPostResult.getResult());
                    }
                }
            }
            catch (JsonProcessingException e) {
                result.setErrorDef(ErrorDef.InvalidPostJsonFormat);
                return result;
            }
        }

        updatePostGotProduct(mainPostResult.getResult());        
        return result;
    }
    
    private void updatePostGotProduct(Post post) {
        Boolean exGotProductTag = post.getGotProductTag();
        Boolean updatedGotProductTag = null;
        if(postProductDao.getPostProductCount(post) > Long.valueOf(0))
            updatedGotProductTag = true;
        else
            updatedGotProductTag = false;
        if(!exGotProductTag.equals(updatedGotProductTag)) {
            post.setGotProductTag(updatedGotProductTag);
            updateMainPost(post, null, post.getPostStatus(), null, true);
        }
    }
    
    private void updateCircleAttribute(Circle circle, int postCount) {
        if(circle == null)
            return;
        
        List<PostStatus> postStatus = new ArrayList<PostStatus>();
        postStatus.add(PostStatus.Published);
        postStatus.add(PostStatus.Unpublished);
		postStatus.add(PostStatus.Review);
        CircleAttribute cAttrPostCount = circleAttributeDao.createOrUpdateCircleAttr(circle, CircleAttrType.PostCount, String.valueOf(postCount), true);
        
        // If user post(Published) in a new circle, need handle circle follow of the new circle.
        if (postCount > 0 && cAttrPostCount.getAttrType() == CircleAttrType.PostCount && cAttrPostCount.getAttrValue().equals(String.valueOf(postCount))) {
        	String region = Constants.getPostRegion();
    		List<CircleAttribute> cAttrFollowerCounts = circleAttributeDao.findCircleAttribute(region, circle, CircleAttrType.FollowerCount);
    		if (cAttrFollowerCounts.isEmpty()) {
    			String subscriberCount = String.valueOf(subscribeDao.findBySubscribee(circle.getCreatorId(), null, new BlockLimit(0, 0)).getTotalSize());
    			circleAttributeDao.createOrUpdateCircleAttr(circle, CircleAttrType.FollowerCount, subscriberCount, true);
    		}
        }
        
        List<CircleAttribute> circleAttrs = circleAttributeDao.findCircleAttribute(null, circle, CircleAttrType.Thumbnail);
        BlockLimit postCircleLimit = new BlockLimit(0, Circle.maxPostThumbnailSize);
        postCircleLimit.addOrderBy("createdTime", false);
        PostApiResult<PageResult<Post>> postsInCircleReult = listPostByCircle_v3(circle.getId(), null, null, postStatus, "Date", null, postCircleLimit);
        if(!postsInCircleReult.success())
            return;
        int thumbIdx = 0;
        PageResult<Post> postsInCircls = postsInCircleReult.getResult();
        if(postsInCircls.getResults().size() <= 0) {
            circle.setIconId(null);
            circleDao.update(circle);
        }
        
        for(int postRevIdx = postsInCircls.getResults().size() - 1; postRevIdx >= 0; postRevIdx--) {
            Post p = postsInCircls.getResults().get(postRevIdx);
            List<Attachment> attachs = p.getAttachments();
            if(attachs == null)
                continue;
            String newPostThumbnailUrl = null;
            for(Attachment attach : attachs) {
                if(attach.getIsDeleted())
                    continue;
                if(newPostThumbnailUrl != null)
                    break;
                com.cyberlink.cosmetic.modules.file.model.File bcFile = attach.getAttachmentFile();
                if(bcFile == null)
                    continue;
                if(bcFile.getFileType().equals(FileType.Photo) || (bcFile.getFileType().equals(FileType.PostCoverOri)) || ((bcFile.getFileType().equals(FileType.PostCover)))) {
                    newPostThumbnailUrl = bcFile.getFileItems().get(0).getOriginalUrl();
                    for(int itemIdx = 1; itemIdx < bcFile.getFileItems().size(); itemIdx++) {
                        FileItem it = bcFile.getFileItems().get(itemIdx);
                        if(it.getThumbnailType().equals(ThumbnailType.List)) {
                            newPostThumbnailUrl = it.getOriginalUrl();
                            break;
                        }
                    }
                }
            }
            
            if(newPostThumbnailUrl == null)
                continue;
            
            if(circleAttrs.size() > thumbIdx) {
                CircleAttribute newCircleAttr = circleAttrs.get(thumbIdx);
                newCircleAttr.setAttrValue(newPostThumbnailUrl);
                circleAttributeDao.update(newCircleAttr);
            }
            else {
                CircleAttribute newCircleAttr = new CircleAttribute();
                newCircleAttr.setCircle(circle);
                newCircleAttr.setAttrType(CircleAttrType.Thumbnail);
                newCircleAttr.setAttrValue(newPostThumbnailUrl);
                circleAttributeDao.create(newCircleAttr);
            }
            thumbIdx++;
        }
        if(circleAttrs.size() > postsInCircls.getResults().size()) {
            for(int toRemoveIdx = postsInCircls.getResults().size(); toRemoveIdx < circleAttrs.size(); toRemoveIdx++) {
                CircleAttribute newCircleAttr = circleAttrs.get(toRemoveIdx);
                newCircleAttr.setIsDeleted(true);
                circleAttributeDao.update(newCircleAttr);
            }
        }
    }
    
    private Post postProcessDeletePost(Post relPost, PostStatus originalStatus, Boolean updatePost) {
        Post deletedPost = updateMainPost(relPost, null, originalStatus, null, updatePost);
        if(deletedPost == null) {
            return null;
        }
        
        if((relPost.getIsDeleted() && originalStatus.getViewable()) || (originalStatus.getViewable() && !relPost.getPostStatus().getViewable())) {
            Long circleId = relPost.getCircleId();
            Boolean isSecret = false;
            if(circleId != null) {
                Circle c = circleDao.findById(circleId);    
                isSecret = c.getIsSecret();
                updateCircleAttribute(c, -1);
            }
            asyncPostUpdateService.cleanPost(relPost.getCreatorId(), isSecret, Arrays.asList(relPost.getId()));
            publishDurableEvent(new PostDeleteEvent(relPost.getId(), relPost.getCreatorId(), circleId));
        }
        
        return deletedPost;
    }
    
    @Override
    public PostApiResult <Boolean> deletePost(Long userId, Long postId) {
        PostApiResult <Boolean> result = new PostApiResult <Boolean> ();
        result.setResult(false);
        if(!userDao.exists(userId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        if(!postDao.exists(postId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        
        Post relPost = postDao.findById(postId);
        if(relPost.getIsDeleted()) {
            result.setResult(true);
            return result;
        }
        
        User user = userDao.findById(userId);
        if(!relPost.getCreator().getId().equals(user.getId())){
            result.setErrorDef(ErrorDef.InvalidPostNotAuth);
            return result;
        }
        
        relPost.setIsDeleted(true);
        Post deletedPost = postProcessDeletePost(relPost, relPost.getPostStatus(), true);
        if(deletedPost == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        result.setResult(true);
        publishDurableEvent(new PostViewUpdateEvent(new MainPostSimpleWrapper(deletedPost, null, null, null, null)));
        return result;
    }
    
    @Override
    public void deletePostByCircle(Long userId, Boolean isSecret, Long circleId) {
        asyncPostUpdateService.cleanPostByCircle(userId, isSecret, circleId);
    }
    
	private class RunnableCheckPostNewByCircle implements Runnable {
		private Long creatorId;
		private Long circleId;
		private Boolean isSecret;

		RunnableCheckPostNewByCircle(Long creatorId, Long circleId, Boolean isSecret) {
		    this.creatorId = creatorId;
			this.circleId = circleId;
			this.isSecret = isSecret;
		}

		public void run() {
		    List<PostStatus> toUpdatePostCountStatus = new ArrayList<PostStatus>();
		    toUpdatePostCountStatus.add(PostStatus.Published);
		    toUpdatePostCountStatus.add(PostStatus.Unpublished);
		    toUpdatePostCountStatus.add(PostStatus.Review);
		    List<PostStatus> toCheckPost = new ArrayList<PostStatus>(Arrays.asList(PostStatus.values()));
		    toCheckPost.removeAll(toUpdatePostCountStatus);
		    Set<Long> circleIds = ImmutableSet.of(circleId);
			List<Long> toUpdatePostCountIds = postDao.findByCreatorOrCircleAndStatus(null, circleIds, toUpdatePostCountStatus);
			List<Long> toCheckPostId = postDao.findByCreatorOrCircleAndStatus(null, circleIds, toCheckPost);
			toCheckPostId.addAll(toUpdatePostCountIds);
			if(toUpdatePostCountIds != null && toUpdatePostCountIds.size() > 0) {
			    userAttrDao.setNonNullValue(creatorId, "YCL_LOOK_COUNT", null);
                userAttrDao.setNonNullValue(creatorId, "POST_COUNT", null);
			}
			if (toCheckPostId == null || toCheckPostId.size() <= 0)
				return;
			postNewDao.batchCheck(toCheckPostId, isSecret);
		}

	}

	@Override
	public void checkPostNewByCircle(Long creatorId, Long circleId, Boolean isSecret) {
		if (circleId == null || isSecret == null)
			return;
		RunnableCheckPostNewByCircle r = new RunnableCheckPostNewByCircle(creatorId, circleId, isSecret);
		asyncPostUpdateService.asyncRun(r);
	}
    
    @Override
    public PostApiResult <Boolean> deleteSubPost(Long userId, Long postId) {
        PostApiResult <Boolean> result = new PostApiResult <Boolean> ();
        result.setResult(false);
        if(!userDao.exists(userId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        if(!postDao.exists(postId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        
        Post relPost = postDao.findById(postId);        
        User user = userDao.findById(userId);
        Long relPostCreatorId = postDao.findById(relPost.getParentId()).getCreatorId();
        if(relPostCreatorId == null || !relPostCreatorId.equals(user.getId())){
            result.setErrorDef(ErrorDef.InvalidPostNotAuth);
            return result;
        }
        
        relPost.setIsDeleted(true);
        Post deletedPost = postDao.update(relPost);
        if(deletedPost == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        result.setResult(true);
        return result;
    }
    
    @Override
    public PostApiResult <Post> queryPostById(Long postId) {
        PostApiResult <Post> result = new PostApiResult <Post>();
        if(!postDao.exists(postId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        Post post = postDao.findById(postId); 
        if(post == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        
        result.setResult(post);
        return result;
    }
    
    @Override
    public PostApiResult <PageResult<Post>> listNewPostByCircle(Long circleId, Long circleTagId, List<String> locales, List<PostStatus> postStatus, Boolean withLook, BlockLimit blockLimit) {
        PostApiResult <PageResult<Post>> result = new PostApiResult <PageResult<Post>>();
        PageResult<Post> r = new PageResult<Post>();
        List<PostStatus> postStatusToGet = new ArrayList<PostStatus>();
        if(postStatus != null && postStatus.size() > 0)
            postStatusToGet.addAll(postStatus);
        else
            postStatusToGet.add(PostStatus.Published);

        if(circleId != null) {
            r = postDao.findByCircle(circleId, locales, postStatusToGet, blockLimit);
        }
        else {
            r = postDao.findPostByLocale(locales, postStatusToGet, blockLimit);
        }
        
        result.setResult(r);
        return result;
    }
    
    @Override
    public PostApiResult <PageResult<Post>> listNewPostByCircle_v3(Long circleId, Long circleTypeId, List<String> locales, List<PostStatus> postStatus, Boolean withLook, BlockLimit blockLimit) {
        PostApiResult <PageResult<Post>> result = new PostApiResult <PageResult<Post>>();
        
        List<PostStatus> postStatusToGet = new ArrayList<PostStatus>();
        if(postStatus != null && postStatus.size() > 0)
            postStatusToGet.addAll(postStatus);
        else
            postStatusToGet.add(PostStatus.Published);
        postStatusToGet.add(PostStatus.Review);
        
        PageResult<Post> r = null;
        if(circleId != null) {
            r = postDao.findByCircle(circleId, null, postStatusToGet, blockLimit);
        }
        else {
            r = postNewDao.findNewPost(circleTypeId, locales, postStatusToGet, withLook, blockLimit);
        }
        
        result.setResult(r);
        return result;
    }

    @Override
    public PostApiResult <PageResult<Post>> listPostByCircle(Long circleId, Long circleTagId, List<String> locales, List<PostStatus> postStatus, String sortBy, Boolean withLook, BlockLimit blockLimit) {
        PostApiResult <PageResult<Post>> result = listNewPostByCircle(circleId, circleTagId, locales, postStatus, withLook, blockLimit);
        return result;
    }
    
    @Override
    public PostApiResult <PageResult<Post>> listPostByCircle_v3(Long circleId, Long circleTypeId, List<String> locales, List<PostStatus> postStatus, String sortBy, Boolean withLook, BlockLimit blockLimit) {
        PostApiResult <PageResult<Post>> result = listNewPostByCircle_v3(circleId, circleTypeId, locales, postStatus, withLook, blockLimit);
        return result;
    }
    
    @Override
    public PostApiResult <Integer> listNewPostByCircle_v3_1(Long circleId, Long circleTypeId, List<String> locales, List<PostStatus> postStatus, List<Long> result, Boolean withLook, BlockLimit blockLimit, boolean disableCache) {
        PostApiResult <Integer> apiResult = new PostApiResult <Integer>();
        
        List<PostStatus> postStatusToGet = new ArrayList<PostStatus>();
        if(postStatus != null && postStatus.size() > 0)
            postStatusToGet.addAll(postStatus);
        else
            postStatusToGet.add(PostStatus.Published);
        postStatusToGet.add(PostStatus.Review);
        
        Integer r = null;
        if(circleId != null) {
            r = postDao.findPostViewByCircle(circleId, null, postStatusToGet, result, blockLimit);
        }
        else {
            r = postNewDao.findNewPostView(circleTypeId, locales, postStatusToGet, result, withLook, blockLimit, disableCache);
        }
        
        if(r == null) {
            apiResult.setErrorDef(ErrorDef.UnknownPostError);
            return apiResult;
        }
        apiResult.setResult(r);
        return apiResult;
    }
    
    @Override
    public PostApiResult <Integer> listPostByCircle_v3_1(Long circleId, Long circleTypeId, List<String> locales, List<PostStatus> postStatus, String sortBy, List<Long> results, Boolean withLook, BlockLimit blockLimit, boolean disableCache) {
        PostApiResult <Integer> result = listNewPostByCircle_v3_1(circleId, circleTypeId, locales, postStatus, results, withLook, blockLimit, disableCache);        
        return result;
    }
    
    @Override
    public Map<Long, List<Object>> listFileItemByPosts(List<Post> posts, ThumbnailType thumbnailType) {
        Map<Long, List<Object>> result = new HashMap<Long, List<Object>>();
        final List<Long> fileIds = new ArrayList<Long>();
        Map<Long, List<Long>>postFileIdMap = new HashMap<Long, List<Long>>();
        Set<Long> postIds = new HashSet<Long>();
        for(Post p : posts) {
            postIds.add(p.getId());
            result.put(p.getId(), new ArrayList<Object>());  
        }
        
        int offset = 0;
        int limit = 100;
        do {
            BlockLimit blockLimit = new BlockLimit(offset, limit);
            PageResult<Attachment> attachments = attachmentDao.getAttachmentByPostId(postIds, blockLimit);
            for(Attachment attach : attachments.getResults()) {
                Object target = attach.getTarget();
                if(target instanceof com.cyberlink.cosmetic.modules.file.model.File) {
                    if(postFileIdMap.get(((com.cyberlink.cosmetic.modules.file.model.File)target).getId()) == null)
                        postFileIdMap.put(((com.cyberlink.cosmetic.modules.file.model.File)target).getId(), new ArrayList<Long>());
                    List<Long> tmpL = postFileIdMap.get(((com.cyberlink.cosmetic.modules.file.model.File)target).getId());
                    tmpL.add(attach.getPostId());
                    fileIds.add(((com.cyberlink.cosmetic.modules.file.model.File)target).getId());
                }               
                else if(target instanceof com.cyberlink.cosmetic.modules.post.model.AttachmentExtLink) {
                    result.get(attach.getPostId()).add(target);
                }
                else if(target instanceof com.cyberlink.cosmetic.modules.look.model.Look) {
                    result.get(attach.getPostId()).add(target);
                }
            }
            
            offset += limit;
            if(offset > attachments.getTotalSize())
                break;
        } while(true);
        
        List<FileItem> fileItems;
        if(thumbnailType != null)
            fileItems = fileItemDao.findThumbnails(fileIds.toArray(new Long[fileIds.size()]), thumbnailType);
        else
            fileItems = fileItemDao.findOriginals(fileIds.toArray(new Long[fileIds.size()]));
        Collections.sort(fileItems, new Comparator<FileItem>() {

            @Override
            public int compare(FileItem o1, FileItem o2) {
                return Integer.compare(fileIds.indexOf(o1.getFile().getId()), fileIds.indexOf(o2.getFile().getId()));
            }
            
        });
        
        for(FileItem fileItem : fileItems) {
            List<Long> tmpL = postFileIdMap.get(fileItem.getFile().getId());
            for(Long pId : tmpL) {
                result.get(pId).add(fileItem);
            }
        }
        
        return result;
    }
    
    @Override
    public Map<Long, List<FileItem>> listUserItem(List<User> users) {
        Map<Long, List<FileItem>> result = new HashMap<Long, List<FileItem>>();
        List<Long> avatarIds = new ArrayList<Long>();
        List<Long> coverIds = new ArrayList<Long>();
        Map<Long, List<Long>>userFileIdMap = new HashMap<Long, List<Long>>();
        Set<Long> userIds = new HashSet<Long>();
        for(User u : users) {
            userIds.add(u.getId());
            result.put(u.getId(), new ArrayList<FileItem>());
            if(u.getAvatarId() != null) {
                avatarIds.add(u.getAvatarId());
                if(!userFileIdMap.containsKey(u.getAvatarId()))
                    userFileIdMap.put(u.getAvatarId(), new ArrayList<Long>());
                userFileIdMap.get(u.getAvatarId()).add(u.getId());
            }
                
            if(u.getAvatarId() != null) {
                coverIds.add(u.getCoverId());
                if(!userFileIdMap.containsKey(u.getCoverId()))
                    userFileIdMap.put(u.getCoverId(), new ArrayList<Long>());
                userFileIdMap.get(u.getCoverId()).add(u.getId());
            }
        }
        
        List<FileItem> avatarFileItems = fileItemDao.findThumbnails(avatarIds.toArray(new Long[avatarIds.size()]), ThumbnailType.Avatar);
        List<FileItem> coverFileItems = fileItemDao.findThumbnails(coverIds.toArray(new Long[coverIds.size()]), ThumbnailType.Detail);
        for(FileItem afi : avatarFileItems) {
            List<Long> inUser = userFileIdMap.get(afi.getFile().getId());
            for(Long uid : inUser) {
                result.get(uid).add(afi);
            }
        }
        for(FileItem afi : coverFileItems) {
            List<Long> inUser = userFileIdMap.get(afi.getFile().getId());
            for(Long uid : inUser) {
                result.get(uid).add(afi);
            }
        }
        
        return result;
    }
    
    @Override
    public Map<Long, List<Circle>> listCircleByPosts(List<Post> posts) {
        Map<Long, List<Circle>> result = new HashMap<Long, List<Circle>>();
        Map<Long, Circle> circleMap = new HashMap<Long, Circle>();
        Long [] circleIds = new Long[posts.size()];
        for(int idx = 0; idx < posts.size(); idx++) {
            circleIds[idx] = posts.get(idx).getCircleId();
        }
        List<Circle> circles = circleDao.findByIds(circleIds);
        for(Circle c : circles) {
            circleMap.put(c.getId(), c);
        }
        for(Post p : posts) {
            List<Circle> pcs = new ArrayList<Circle>();
            Circle relC = circleMap.get(p.getCircleId());
            if(relC != null) {
                pcs.add(relC);
            }
            result.put(p.getId(), pcs);
        }
        
        return result;
    }
    
    @Override
    public PostApiResult <PageResult<Post>> listPostByUsers(List<Long> userIds, List<PostStatus> postStatus, Boolean withSecret, BlockLimit blockLimit) {
        PostApiResult <PageResult<Post>> result = new PostApiResult <PageResult<Post>>();
        PageResult<Post> r = new PageResult<Post>();
        if(postStatus == null || postStatus.size() <= 0) {
            postStatus = new ArrayList<PostStatus>();
            postStatus.add(PostStatus.Published);
        }
        
        postStatus.add(PostStatus.Unpublished);
        postStatus.add(PostStatus.Review);
        
        if(userIds != null && userIds.size() > 0)
                r = postDao.findPostByUsers(userIds, postStatus, withSecret, false, blockLimit);
        
        result.setResult(r);
        return result;
    }
    
    @Override
    public PostApiResult <Integer> listPostByUsers_v3_1(List<Long> userIds, List<PostStatus> postStatus, Boolean withSecret, List<Long> result, BlockLimit blockLimit) {
        if(postStatus == null || postStatus.size() <= 0) {
            postStatus = new ArrayList<PostStatus>();
            postStatus.add(PostStatus.Published);
        }

        postStatus.add(PostStatus.Unpublished);
        postStatus.add(PostStatus.Review);
        
        Integer totalSize = null;
        if(userIds != null && userIds.size() > 0) {
            if(userIds.size() == 1 && !withSecret) {
                UserAttr userAttr = userAttrDao.findByUserId(userIds.get(0));
                if(userAttr != null && userAttr.getHowToCount() != null)
                    totalSize = userAttr.getHowToCount().intValue();
            }
            Boolean withSize = totalSize == null;
            Integer resultSize = postDao.findPostIdsByUsers(userIds, postStatus, withSecret, withSize, result, blockLimit);
            if(withSize)
                totalSize = resultSize;
        }
        
        PostApiResult <Integer> apiResult = new PostApiResult<Integer>();
        apiResult.setResult(totalSize);
        return apiResult;
    }
    
    @Override
    public PostApiResult <Integer> listLookPostByUser(Long userId, UserAttr userAttr, PostType postType, List<PostStatus> postStatus, Boolean withSecret, List<Long> result, BlockLimit blockLimit) {
        if(postStatus == null || postStatus.size() <= 0) {
            postStatus = new ArrayList<PostStatus>();
            postStatus.add(PostStatus.Published);
        }

        postStatus.add(PostStatus.Unpublished);
        postStatus.add(PostStatus.Review);
        
        Boolean withSize = userAttr == null;
        if(userAttr != null && postType != null) {
            switch(postType) {
                case YCL_LOOK: {
                    if(userAttr.getYclLookCount() == null)
                        withSize = true;
                    break;
                }
                case HOW_TO: {
                    if(userAttr.getHowToCount() == null)
                        withSize = true;
                    break;
                }
                default:
                    break;
            }
        }
        
        Integer totalSize = postDao.findLookPostIdsByUser(userId, postType, postStatus, withSecret, withSize, result, blockLimit);
        PostApiResult <Integer> apiResult = new PostApiResult<Integer>();
        if(withSize)
            apiResult.setResult(totalSize);
        else {
            if(PostType.YCL_LOOK.equals(postType))
                apiResult.setResult(userAttr.getYclLookCount().intValue());
            else if(PostType.HOW_TO.equals(postType))
                apiResult.setResult(userAttr.getHowToCount().intValue());
            else
                apiResult.setResult(Integer.MAX_VALUE);
        }
        return apiResult;
    }
    
    @Override
    public PostApiResult<Integer> listLookPostByUserType(UserType userType, List<String> locale, PostType postType, List<PostStatus> postStatus, List<Long> result, BlockLimit blockLimit) {
        PostApiResult <Integer> apiResult = new PostApiResult<Integer>();
        if(userType == null) {
            apiResult.setErrorDef(ErrorDef.UnknownPostError);
            return apiResult;
        }
        
        if(postStatus == null || postStatus.size() <= 0) {
            postStatus = new ArrayList<PostStatus>();
            postStatus.add(PostStatus.Published);
        }

        postStatus.add(PostStatus.Unpublished);
        postStatus.add(PostStatus.Review);
        
        List<Long> userIds = new ArrayList<Long>();
        userIds.addAll(userDao.findIdByUserType(userType, locale));
        Integer totalSize = postDao.findLookPostIdsByUsers(userIds, postType, postStatus, result, blockLimit);
        apiResult.setResult(totalSize);
        return apiResult;
    }
    
    @Override
    public PostApiResult <Long> listPostCountByUsers_v3_1(List<Long> userIds, List<PostStatus> postStatus, Boolean withSecret) {
        if(postStatus == null || postStatus.size() <= 0) {
            postStatus = new ArrayList<PostStatus>();
            postStatus.add(PostStatus.Published);
        }

        postStatus.add(PostStatus.Unpublished);
        postStatus.add(PostStatus.Review);
        Long totalSize = null;
        if(userIds != null && userIds.size() > 0)
            totalSize = postDao.findPostIdsCountByUsers(userIds, postStatus, withSecret);
        
        PostApiResult <Long> apiResult = new PostApiResult<Long>();
        apiResult.setResult(totalSize);
        return apiResult;
    }
    
    @Override
    public PostApiResult<Integer> listPostByLookType(Long lookTypeId, PostType postType, String locale, List<PostStatus> postStatus, List<Long> result, BlockLimit blockLimit) {
        PostApiResult <Integer> apiResult = new PostApiResult <Integer>();
        
        List<PostStatus> postStatusToGet = new ArrayList<PostStatus>();
        if(postStatus != null && postStatus.size() > 0)
            postStatusToGet.addAll(postStatus);
        else
            postStatusToGet.add(PostStatus.Published);
        postStatusToGet.add(PostStatus.Review);
        
        Integer r = postNewDao.findNewPostViewByLook(lookTypeId, locale, postType, postStatusToGet, result, blockLimit);
        
        if(r == null) {
            apiResult.setErrorDef(ErrorDef.UnknownPostError);
            return apiResult;
        }
        apiResult.setResult(r);
        return apiResult;
    }
    
    @Override
    public PostApiResult <PageResult<Post>> listSubPost(Long postId, BlockLimit blockLimit) {
        PostApiResult <PageResult<Post>> result = new PostApiResult <PageResult<Post>>();
        if(!postDao.exists(postId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        PageResult<Post> r = new PageResult<Post>();
        if(postId > 0)
            r = postDao.findSubPostByPost(postId, blockLimit);
        
        result.setResult(r);
        return result;
    }
    
    @Override
    public PostApiResult <PageResult<Post>> listAllRelatedPost(Long postId, BlockLimit blockLimit) {
        PostApiResult <PageResult<Post>> result = new PostApiResult <PageResult<Post>>();
        Post mainPost = postDao.existAndNonDeleted(postId);
        if(mainPost == null) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        PageResult<Post> r = new PageResult<Post>();
        if(postId > 0)
            r = postDao.findAllRelatedPostByPost(postId, blockLimit);
        
        if(r.getTotalSize() <= 0) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        
        result.setResult(r);
        return result;
    }
    
    @Override
    public Post updatePost(Post post)
    {
        return postDao.update(post);
    }
    
    @Override
    public String getPostQRCode(Long postId)
    {
        String myCodeText = "ybc://Post/" + String.valueOf(postId);
        String postQrDir = Constants.getStorageLocalRoot() + "/postQRcode/";
        File dPostQrDir = new File(postQrDir);

        if (!dPostQrDir.exists())
        {
            if(!dPostQrDir.mkdir()) {
                logger.error("Failed to create directory :" + postQrDir);
                return null;
            }
        }
        
        String filePath = postQrDir + String.valueOf(postId) + ".png";
        File postQrFile = new File(filePath);
        if (postQrFile.exists())
        {
            return filePath;
        }
        
        int qrDimension = 128;
        String fileType = "png";
        
        try {
            postQrFile.createNewFile();
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText,BarcodeFormat.QR_CODE, qrDimension, qrDimension, hintMap);
            int imgWidth = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(imgWidth, imgWidth, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();
 
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, imgWidth, imgWidth);
            graphics.setColor(Color.BLACK);
 
            for (int i = 0; i < imgWidth; i++) {
                for (int j = 0; j < imgWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            ImageIO.write(image, fileType, postQrFile);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }

    @Override
    public PageResult<Post> listPostBetweenDate(Date start, Date end, PostStatus postStatus, BlockLimit blockLimit) {
        return postDao.findMainPostByCreatedDateAndStatus(start, end, postStatus, blockLimit);
    }
    
    @Override
    public PostApiResult <Boolean> reportPost(Long reportedId, Long postId, String reason) {
        PostApiResult <Boolean> result = new PostApiResult <Boolean>();
        result.setResult(true);
        
        if(!userDao.exists(reportedId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        if(!postDao.exists(postId)) {
            result.setErrorDef(ErrorDef.InvalidPostTargetId);
            return result;
        }
        if(reason == null || reason.length() <= 0) {
            result.setErrorDef(ErrorDef.InvalidPostReportReason);
            return result;
        }
        
        PostReported.PostReportedReason reasonE = null;
        switch(reason)
        {
        case "Inappropriate":
            reasonE = PostReported.PostReportedReason.Inappropriate;
            break;
        case "Copyright":
            reasonE = PostReported.PostReportedReason.Copyright;
            break;
        case "Other":
            reasonE = PostReported.PostReportedReason.Other;
            break;
        default:
            break;
        }
        
        if(reasonE == null) {
            result.setErrorDef(ErrorDef.InvalidPostReportReason);
            return result;
        }
        
        result.setResult(true);
        User reporter = userDao.findById(reportedId);
        Post relPost = postDao.findById(postId);
        User relPostCreator = relPost.getCreator();
        if(relPostCreator == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        UserType relPostCreatorType = relPostCreator.getUserType();
        if(relPostCreatorType == null) {
            result.setErrorDef(ErrorDef.UnknownPostError);
            return result;
        }
        if(!relPostCreatorType.equals(UserType.Normal)) {
            if(relPostCreatorType.equals(UserType.Blogger))
                return result;
            result.setErrorDef(ErrorDef.ReportCLAccount);
            return result;
        }
        
        PostStatus originalPostStatus = relPost.getPostStatus();
        BlockLimit blockLimit = new BlockLimit(0, 1);
        PageResult<PostReported> exReportedPosts = postReportedDao.findByTargetAndUser(relPost, reporter, blockLimit);
        if(exReportedPosts.getTotalSize() > 0) {
            PostReported exReportedPost = exReportedPosts.getResults().get(0);
            if(exReportedPost.getStatus().equals(PostReportedStatus.NewReported)) {
                relPost.setPostStatus(PostStatus.Review);
                updateMainPost(relPost, null, originalPostStatus, null, true);
            }
        }
        else {
            relPost.setPostStatus(PostStatus.Review);
            updateMainPost(relPost, null, originalPostStatus, null,  true);
            
            PostReported reportedPost = new PostReported();
            reportedPost.setReporter(reporter);
            reportedPost.setTarget(relPost);
            reportedPost.setReason(reasonE);
            reportedPost.setStatus(PostReportedStatus.NewReported);
            postReportedDao.create(reportedPost);
        }
        
        postProcessDeletePost(relPost, originalPostStatus, true);
        return result;
    }
    
    @Override
    public Map<Long, List<PostReported>> getReportedPostReason(PostReportedStatus status, List<Long> postIds) {
        Map<Long, List<PostReported>> result = new HashMap<Long, List<PostReported>>();
        if(postIds == null || postIds.size() <= 0)
            return result;

        List<PostReported> reportedPosts = postReportedDao.getByTargets("Post", status, postIds.toArray(new Long[postIds.size()]));
        for(PostReported p : reportedPosts) {
            Post relPost = (Post)p.getTarget();
            if(result.get(relPost.getId()) == null)
                result.put(relPost.getId(), new ArrayList<PostReported>());
            result.get(relPost.getId()).add(p);
        }
        return result;
    }
    
    @Override
    public Throwable handleReportPost(Long postId, User reviewer, String result, String remark) {
        if(!postDao.exists(postId))
            return new Throwable("Invalid post id");
        
        Post relPost = postDao.findById(postId);
        PostStatus originalPostStatus = relPost.getPostStatus();
        PostReportedResult pResult = null;
        switch(result)
        {
            case "Published" : {
                pResult = PostReportedResult.Published;
                relPost.setPostStatus(PostStatus.Published);
                Long postCircleId = relPost.getCircleId();
                if(postCircleId != null) {
                    Circle cir = circleDao.findById(postCircleId);
                    if(cir != null && !originalPostStatus.getViewable())
                        updateCircleAttribute(cir, 1);
                }
                break;
            }
            case "Banned" : {
                pResult = PostReportedResult.Banned;
                relPost.setPostStatus(PostStatus.Banned);
                List<Long> toDeletedPostId = new ArrayList<Long>();
                toDeletedPostId.add(postId);
                postProcessDeletePost(relPost, originalPostStatus, true);
                break;
            }
            default:
                break;
        }
        if(pResult == null)
            return new Throwable("PostReportedResult is null");
        
        int offset = 0;
        int limit = 10;
        do
        {
            BlockLimit blockLimit = new BlockLimit(offset, limit);
            PageResult<PostReported> prs = postReportedDao.findByTarget(relPost, blockLimit);
            for(PostReported pr : prs.getResults()) {
                if(remark != null && remark.length() > 0)
                    pr.setRemark(remark);
                pr.setResult(pResult);
                pr.setStatus(PostReportedStatus.Reviewed);
                pr.setReviewer(reviewer);
                postReportedDao.update(pr);
            }
            offset += limit;
            if(offset > prs.getTotalSize())
                break;
        } while(true);
        
        String isHandled = "Succeed to handle the post";
        if(result.equals("Banned") && relPost.getPostSource().equals("contest")){
        	Boolean isReportContest = reportContestPost(postId, relPost.getLocale());
        	if(isReportContest == null || !isReportContest)
        		return new Throwable(isHandled + ", but failed to report contest post");
        }
        return new Throwable(isHandled);
    }
    
    @Override
    public Boolean reportContestPost(Long postId, String locale) {
    	String contestDomain = Constants.getContestDomain();
    	if(contestDomain == null || contestDomain.length() <= 0)
        	return false;
    	String reportPath = "/prog/contest/report-post-service.do?";
    	contestDomain += reportPath;
    	String apiUrl = String.format("http://%slocale=%s&postId=%d&report=1&apiVersion=null", contestDomain, locale, postId);
    	try {
    		Connection conn = Jsoup.connect(apiUrl);
			Document doc = conn.ignoreContentType(true).get();
			if (doc == null)
				return false;
			Element body = doc.body();
			if (body == null)
				return false;
			String returnJson = body.text();
			if (returnJson == null || returnJson.length() <= 0)
				return false;
			JSONObject jObject = new JSONObject(returnJson);
			jObject = jObject.getJSONObject("result");
			if(jObject.length() <= 0)
				return false;
			Object obj = jObject.get("status");
			String Status = "";
			if(obj != null)
				Status = obj.toString();
			if(Status.equals("OK"))
				return true;
			else if(Status.equals("Error"))
				return false;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
    	return null;
    }
    
    @Override
    public Map<Long, List<Post>> listSubPostByPosts(List<Post> posts) {
        Map<Long, List<Post>> results = new HashMap<Long, List<Post>>();
        if(posts == null || posts.size() <= 0)
            return results;
        
        Long [] postIds = new Long[posts.size()];
        for(int idx = 0; idx < posts.size(); idx++) {
            postIds[idx] = posts.get(idx).getId();
        }
        results = postDao.findSubPostByPostIds(postIds);
        return results;
    }
    
    @Override
    public List<Post> findPostByIds(List<Long>ids) {
        return postDao.findByIds(ids.toArray(new Long[ids.size()]));
    }
    
    @Override
    public Post duplicatePost(Post src, Long userId, String countryCode, Long circleIdOffset) {      
        List<Long> newCircleIds = new ArrayList<Long>();
        newCircleIds.add(src.getCircleId() + circleIdOffset);
        
        Post duplicatedPost = duplicatePost(src, userId, countryCode, null, newCircleIds, src.getPostSource(), src.getAppName(), src.getPostType(), src.getPostStatus(), 
                null, null, PushPostNewType.TRY_PUSH);
        return duplicatedPost;
    }
    
    private Post duplicatePost(Post src, Long userId, String countryCode, String newTitle, List<Long> newCircleIds, String postSource, AppName appName, PostType postType, PostStatus postStatus, Long basicSortBonus, Boolean forceHideInAll, PushPostNewType forceAddType) {
        Post duplicatedPost = new Post();
        duplicatedPost.setShardId(userId);
        Circle circleToUpdate = null;
        String postCoverUrl = null;
        Long iconFileId = null;
        
        List<Attachment> postAttachments = new ArrayList<Attachment>(0);
        List<PostProduct> postProducts = new ArrayList<PostProduct>(0);
        
        Circle userCircle = null;
        for(Long cirId : newCircleIds) 
        {
            Circle relatedCircle = circleDao.findById(cirId);
            if(relatedCircle == null)
                continue;
            userCircle = circleService.getUserAccessibleCircle(relatedCircle, userId, true);
            if(userCircle == null)
                continue;
            
            if(circleToUpdate == null)
                circleToUpdate = userCircle;
            duplicatedPost.setCircleId(userCircle.getId());
            break; // Only allow one circle
        }
        
        for(Attachment attachment : src.getAttachments())
        {
            Attachment newAttachment = new Attachment();
            newAttachment.setShardId(userId);
            newAttachment.setTarget(attachment.getTarget());
            newAttachment.setPostId(duplicatedPost.getId());
            postAttachments.add(newAttachment);
            com.cyberlink.cosmetic.modules.file.model.File bcFile = attachment.getAttachmentFile();
            if(bcFile == null)
                continue;
            if((bcFile.getFileType().equals(FileType.Photo)) || ((bcFile.getFileType().equals(FileType.PostCoverOri) && postCoverUrl == null)) || ((bcFile.getFileType().equals(FileType.PostCover) && postCoverUrl == null))) {
                Long [] fileIds = new Long[1];
                fileIds[0] = bcFile.getId();
                List<FileItem> fileItems = fileItemDao.findThumbnails(fileIds, ThumbnailType.List);
                if(fileItems != null && fileItems.size() > 0) {
                    postCoverUrl = fileItems.get(0).getOriginalUrl();
                    iconFileId = fileItems.get(0).getFile().getId();
                }
            }
        }
        
        for(PostProduct postProduct : src.getPostProducts())
        {
            PostProduct newpostProduct = new PostProduct();
            newpostProduct.setShardId(userId);
            newpostProduct.setProductId(postProduct.getProductId());
            newpostProduct.setTagAttrs(postProduct.getTagAttrs());
            newpostProduct.setPost(duplicatedPost);
            postProducts.add(newpostProduct);
        }
        
        String newSpecifiedTitle = (newTitle == null || newTitle.length() <= 0) ? src.getTitle() : newTitle;
        User creator = userDao.findById(userId);
        Locale rowLocale = localeDao.getDefaultLocale();
        String postLocale = LocaleType.POST_LOCALE.getValue(rowLocale);
        Set<String> postLocales = localeDao.getLocaleByType(creator.getRegion(), LocaleType.POST_LOCALE);
        if(postLocales != null && postLocales.iterator().hasNext())
            postLocale = postLocales.iterator().next();
        duplicatedPost.setCreator(creator);
        duplicatedPost.setCreatorId(userId);
        duplicatedPost.setTitle(newSpecifiedTitle);
        duplicatedPost.setContent(src.getContent());
        duplicatedPost.setLookTypeId(src.getLookTypeId());
        duplicatedPost.setExtLookUrl(src.getExtLookUrl());
        duplicatedPost.setTags(src.getTags());
        duplicatedPost.setAttachments(postAttachments);
        duplicatedPost.setPostProducts(postProducts);
        duplicatedPost.setGotProductTag(src.getGotProductTag());
        duplicatedPost.setPostSource(postSource);
        duplicatedPost.setAppName(appName);
        duplicatedPost.setLocale(postLocale);
        if(countryCode == null && postLocale != null) {
            Integer delimiterIdx = postLocale.indexOf("_") + 1;
            if(delimiterIdx.compareTo(0) > 0 && delimiterIdx.compareTo(postLocale.length() - 1) < 0)
                countryCode = postLocale.substring(postLocale.indexOf("_") + 1);
        }
        duplicatedPost.setCountryCode(countryCode);
        duplicatedPost.setPostStatus(postStatus);
        if(postType != null)
        	duplicatedPost.setPostType(postType);
        else
        	duplicatedPost.setPostType(src.getPostType());
        
        if(basicSortBonus != null)
            duplicatedPost.setBasicSortBonus(basicSortBonus);
        
        Boolean isSecret = null;
        if(userCircle != null)
            isSecret = userCircle.getIsSecret();
        Post newDuplicatePost = createMainPost(duplicatedPost, isSecret, null);
        if(newDuplicatePost == null)
            return null;
        
        if(circleToUpdate != null && postCoverUrl != null && src.getPostStatus().getViewable()) {
            if(!circleToUpdate.getCreatorId().equals(userId))
                iconFileId = null;
            updateCircleThumbnail(circleToUpdate, postCoverUrl, iconFileId);
        	CircleAttribute cAttrPostCount = circleAttributeDao.createOrUpdateCircleAttr(circleToUpdate, CircleAttrType.PostCount, "1", true);
        	// If user post(Published) in a new circle, need handle circle follow of the new circle.
        	if (cAttrPostCount.getAttrType() == CircleAttrType.PostCount && cAttrPostCount.getAttrValue().equals("1")) {
        		String region = Constants.getPostRegion();
        		List<CircleAttribute> cAttrFollowerCounts = circleAttributeDao.findCircleAttribute(region, circleToUpdate, CircleAttrType.FollowerCount);
        		if (cAttrFollowerCounts.isEmpty()) {
        			String subscriberCount = String.valueOf(subscribeDao.findBySubscribee(circleToUpdate.getCreatorId(), null, new BlockLimit(0, 0)).getTotalSize());
        			circleAttributeDao.createOrUpdateCircleAttr(circleToUpdate, CircleAttrType.FollowerCount, subscriberCount, true);
        		}
        	}
        }
        
        List<Long> circleTypeIds = new ArrayList<Long>();
        circleTypeIds.add(userCircle.getCircleTypeId());
        switch(forceAddType) {
        case TRY_PUSH :
            duplicatedPost = postPopularityService.pushToNewImmediate(newDuplicatePost, circleTypeIds, basicSortBonus, forceHideInAll, false, false);
        case FORCE_PUSH:
            duplicatedPost = postPopularityService.pushToNewImmediate(newDuplicatePost, circleTypeIds, basicSortBonus, forceHideInAll, false, true);
        case NO_PUSH:
            default:
                break;
        }
        
        int offset = 0;
        int limit = 100;
        do {
            BlockLimit blockLimit = new BlockLimit(offset, limit);
            PostApiResult<PageResult<Post>> subpostResults = listSubPost(src.getId(), blockLimit);
            if(!subpostResults.success())
                break;
            PageResult<Post> subposts = subpostResults.getResult();
            for(Post subPost : subposts.getResults()) {
                Post newSubPost = new Post();
                newSubPost.setShardId(userId);
                List<Attachment> subPostAttachments = new ArrayList<Attachment>(0);
                List<PostProduct> subPostProducts = new ArrayList<PostProduct>(0);
                for(Attachment attachment : subPost.getAttachments())
                {
                    Attachment newAttachment = new Attachment();
                    newAttachment.setShardId(userId);
                    newAttachment.setTarget(attachment.getTarget());
                    newAttachment.setPostId(newSubPost.getId());
                    subPostAttachments.add(newAttachment);
                }
                
                for(PostProduct postProduct : subPost.getPostProducts())
                {
                    PostProduct newpostProduct = new PostProduct();
                    newpostProduct.setShardId(userId);
                    newpostProduct.setProductId(postProduct.getProductId());
                    newpostProduct.setTagAttrs(postProduct.getTagAttrs());
                    newpostProduct.setPost(newSubPost);
                    subPostProducts.add(newpostProduct);
                }
                
                newSubPost.setContent(subPost.getContent());
                newSubPost.setAttachments(subPostAttachments);
                newSubPost.setPostProducts(subPostProducts);
                newSubPost.setLookTypeId(subPost.getLookTypeId());
                newSubPost.setExtLookUrl(subPost.getExtLookUrl());
                newSubPost.setTags(subPost.getTags());
                newSubPost.setParentId(newDuplicatePost.getId());
                newSubPost.setPostStatus(PostStatus.Published);
                postDao.create(newSubPost);
            }
            
            offset += limit;
            if(offset > subposts.getTotalSize())
                break;
        } while(true);
        
        if(newDuplicatePost != null)
            asyncPostUpdateService.insertRelatedPost(newDuplicatePost.getId());
        
        return newDuplicatePost;
    }
    
    private Post curatePost(PostScore cPs, ResultType resultType, Long postId, List<Long> circleTypeIds, 
            List<Map<String, Object>> keywords, Integer quality, Boolean forceHideInAll, String info, 
            Boolean needAddLookType, Boolean skipPool, Map<String, Object> rescueTask, List<Throwable> errors) {
        if(errors == null)
            return null;
        
        try {
            List<Post> posts = postDao.findByIds(postId);
            if(posts == null || posts.size() <= 0)
                return null;
            Post cPost = posts.get(0);
            List<PostNew> pns = postNewDao.getPostNewByPosts(posts, null);
            if(pns != null && pns.size() > 0) {
                changePostKeyword(cPost, pns, cPs, resultType, circleTypeIds, keywords, quality, skipPool, rescueTask, errors);
                return posts.get(0);
            }
            
            cPost.setBasicSortBonus(postPopularityService.getPostNewMinThreshold().longValue());
            if(quality != null)
                cPost.setQuality(quality);
            else
                cPost.setQuality(DEF_SPECIAL_POST_QUALITY);
            
            if(circleTypeIds == null || circleTypeIds.size() <= 0) {
                circleTypeIds = new ArrayList<Long>();
                circleTypeIds.add(cPost.getCircle().getCircleTypeId());
            }
            
            List<Circle> descCirs = null;
            PageResult<Circle> pgCirs = circleDao.findBcDefaultCircleByCircleTypeIds(circleTypeIds, 0L, 100L);
            if(pgCirs.getResults() != null && pgCirs.getResults().size() > 0) {
                descCirs = new ArrayList<Circle>();
                descCirs.addAll(pgCirs.getResults());
            }
            
            List<String> toAddKWs = new ArrayList<String>();
            if(descCirs != null && descCirs.size() > 0) {
                for(Circle descCir : descCirs)
                    toAddKWs.add(descCir.getCircleName());
            }
            else if(cPost.getCircle() != null)
                toAddKWs.add(cPost.getCircle().getCircleName());
            
            List<Long> toUpdateFrequency = new ArrayList<Long>();
            if(keywords != null) {
                for(Map<String, Object> kwMap : keywords) {
                    if(!kwMap.containsKey("kword") || !kwMap.containsKey("kid"))
                        continue;
                    toAddKWs.add((String) kwMap.get("kword"));
                    toUpdateFrequency.add((Long)kwMap.get("kid"));
                }
            }
            if(toAddKWs.size() > 0) {
                PostTags tags = transferTags(cPost.getTags());
                if(tags == null)
                    tags = new PostTags();
                tags.getKeywords().addAll(toAddKWs);
                cPost.setTags(objectMapper.writer((PrettyPrinter)null).withView(MainPostDbView.class).writeValueAsString(tags));
                if(tags.getKeywords() != null && tags.getKeywords().size() > 0) {
                    Integer kwBucketId = relatedPostService.getKeywordBucketId(new Date());
                    postTopKeywordDao.updateKeywordsFreq(cPost.getLocale(), tags.getKeywords(), kwBucketId);
                }
            }
            updateMainPost(cPost, null, cPost.getPostStatus(), null, true);
            postPopularityService.pushToNewImmediate(cPost, circleTypeIds, cPost.getBasicSortBonus(), forceHideInAll, skipPool, null);
            cPs.setCircleTypeId(circleTypeIds.get(0));
            cPs.setIsHandled(true);
            cPs.setResultType(resultType);
            cPs.setInfo(info);
            postScoreDao.update(cPs);
            postCurateKeywordDao.updateFrequency(toUpdateFrequency);
            return cPost;
        }
        catch(Exception e) {
            errors.add(e);
            return null;
        }
    }
    
    private void changePostKeyword(Post post, List<PostNew> postNews, PostScore cPs, ResultType resultType, List<Long> inDescCirIds, 
            List<Map<String, Object>> keywords, Integer quality, Boolean skipPool, Map<String, Object> rescueTask, List<Throwable> errors) {
        Set<String> toAddKWs = new HashSet<String>();
        List<Long> toUpdateFrequency = new ArrayList<Long>();
        List<Long> descCirIds = new ArrayList<Long>();
        if(inDescCirIds != null && inDescCirIds.size() > 0) {
            descCirIds.addAll(inDescCirIds);
        }
        else {
            Long mainTypeId = null;
            for(PostNew pn : postNews) {
                if(pn.getMainType())
                    mainTypeId = pn.getCircleTypeId();
                else
                    descCirIds.add(pn.getCircleTypeId());
            }
            descCirIds.add(0, mainTypeId);
        }
        
        PageResult<Circle> pgCirs = circleDao.findBcDefaultCircleByCircleTypeIds(descCirIds, 0L, 100L);
        if(pgCirs.getResults() != null && pgCirs.getResults().size() > 0) {
            for(Circle descCir : pgCirs.getResults())
                toAddKWs.add(descCir.getCircleName());
        }
        List<Post> posts = new ArrayList<Post>();
        posts.add(post);
        List<PostNew> pns = postNewDao.getPostNewByPosts(posts, null);
        if(pns == null || pns.size() <= 0) {
            cPs.setResultType(resultType);
            cPs.setIsHandled(true);
            postScoreDao.update(cPs);
            return;
        }
        else {
            Boolean bHide = rescueTask.containsKey("hiInAl") ? (Boolean) rescueTask.get("hiInAl") : null;
            Boolean adjustDate = !ResultType.ChangeKeyWord.equals(resultType);
            postPopularityService.updatePostNew(post, postNews, descCirIds, post.getBasicSortBonus(), adjustDate, skipPool, bHide, null);
            // ToDo : update redis trend
            /*if(Constants.getPersonalTrendEnable()) {
                for(Circle c : descCirs) {
                    if(c == null)
                        continue;
                    CircleType ct = c.getCircleType();
                    if(ct == null)
                        continue;
                    String oriKey = ct.getDefaultType();
                    if(oriKey == null)
                        continue;
                    oriKey = oriKey.toLowerCase();
                    publishDurableEvent(PersonalTrendEvent.CreateModifyEvent(post.getLocale(), 
                            c.getCircleTypeId(), descCirId, oriKey, 
                            oriKey, postId));
                }
            }*/
        }
        
        if(keywords != null) {
            for(Map<String, Object> kwMap : keywords) {
                if(!kwMap.containsKey("kword") || !kwMap.containsKey("kid"))
                    continue;
                toAddKWs.add((String) kwMap.get("kword"));
                toUpdateFrequency.add((Long)kwMap.get("kid"));
            }
        }
        Boolean needUpdate = false;
        if(quality != null) {
            post.setQuality(quality);
            needUpdate = true;
        }
        if(toAddKWs.size() > 0) {
            needUpdate = true;
            try {
                PostTags tags = transferTags(post.getTags());
                if(tags == null)
                    tags = new PostTags();
                tags.getKeywords().addAll(toAddKWs);
                post.setTags(objectMapper.writer((PrettyPrinter)null).withView(MainPostDbView.class).writeValueAsString(tags));
                if(tags.getKeywords() != null && tags.getKeywords().size() > 0) {
                    Integer kwBucketId = relatedPostService.getKeywordBucketId(new Date());
                    postTopKeywordDao.updateKeywordsFreq(post.getLocale(), tags.getKeywords(), kwBucketId);
                }
            }
            catch(Exception ex) {
                errors.add(ex);
            }
        }
        if(needUpdate)
            updateMainPost(post, null, post.getPostStatus(), null, true);
        
        cPs.setResultType(resultType);
        cPs.setIsHandled(true);
        postScoreDao.update(cPs);
        postCurateKeywordDao.updateFrequency(toUpdateFrequency);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Throwable> handleRescueTask(Long handlerId, Map<String, Object> rescueTask) {
        List<Throwable> errs = new ArrayList<Throwable>();
        if(rescueTask == null) {
            errs.add(new Throwable("Empy tasks"));
            return errs;
        }
        
        if(handlerId == null || !userDao.exists(handlerId)) {
            errs.add(new Throwable("Invalid user"));
            return errs;
        }
        
        User handler = userDao.findById(handlerId);

        Long bwcDescCirId = rescueTask.containsKey("descCirId") ? (Long) rescueTask.get("descCirId") : null;
        List<Long> descCirIds = rescueTask.containsKey("descCirIds") ? (List<Long>) rescueTask.get("descCirIds") : null;
        Integer quality = rescueTask.containsKey("quality") ? (Integer) rescueTask.get("quality") : null;
        List<Map<String, Object>> keywords = rescueTask.containsKey("keywords") ? (List<Map<String, Object>>) rescueTask.get("keywords") : null;
        Long postId = rescueTask.containsKey("postId") ? (Long) rescueTask.get("postId") : null;
        Boolean skipPool = rescueTask.containsKey("skip") ? (Boolean) rescueTask.get("skip") : false;
        ResultType rescueType = rescueTask.containsKey("type") ? (ResultType) rescueTask.get("type") : null;
        if(postId == null || rescueType == null) {
            errs.add(new Throwable("Empy postId or rescueType"));
            return errs;
        }
        List<Long> postIds = new ArrayList<Long>();
        postIds.add(postId);
        List<PostScore> ps = postScoreDao.findByPostIds(postIds, false);
        if(ps == null || ps.size() <= 0) {
            errs.add(new Throwable(String.format("Empy postScore. PostId : %d.", postId)));
            return errs;
        }
        PostScore cPs = ps.get(0);
        if(!cPs.getReviewerId().equals(handlerId)) {
            errs.add(new Throwable(String.format("Wrong handler for post %d. OHandler : %d. CHandler : %d", postId, cPs.getReviewerId(), handlerId)));
            return errs;
        }
        
        if(bwcDescCirId != null) {
            if(descCirIds == null)
                descCirIds = new ArrayList<Long>();
            descCirIds.add(bwcDescCirId);
        }
        
        try {
            switch(rescueType) {
            case CatAndTrend: {
                curatePost(cPs, rescueType, postId, descCirIds, keywords, quality, false, 
                        objectMapper.writeValueAsString(rescueTask), true, skipPool, rescueTask, errs);                    
                break;
            }
            case CatOnly:
            case SelfieOnly:{
                curatePost(cPs, rescueType, postId, descCirIds, keywords, quality, true, 
                        objectMapper.writeValueAsString(rescueTask), false, skipPool, rescueTask, errs);
                break;
            }
            case ChangeKeyWord:{
                List<Post> posts = postDao.findByIds(postId);
                if(posts == null || posts.size() <= 0)
                    break;
                Post cPost = posts.get(0);
                List<PostNew> pns = postNewDao.getPostNewByPosts(posts, null);
                if(pns == null || pns.size() <= 0)
                    break;
                changePostKeyword(cPost, pns, cPs, rescueType, descCirIds, keywords, quality, true, rescueTask, errs);
                break;
            }
            case Abandon:{
                if(!PoolType.Pgc.equals(cPs.getPoolType()) && !PoolType.RetagScraped.equals(cPs.getPoolType()))
                    reportPost(handler.getId(), postId, "Other");
                else {
                    Post post = postDao.findById(postId);
                    deletePost(post.getCreatorId(), postId);
                }
                cPs.setResultType(rescueType);
                cPs.setIsHandled(true);
                postScoreDao.update(cPs);
                break;
            }
            case Reviewed: {
                if(PoolType.Qualified.equals(cPs.getPoolType())) {
                    cPs.setPoolType(PoolType.RevQualified);
                    cPs.setReviewerId(null);
                    cPs.setResultType(null);
                    cPs.setIsHandled(null);
                }
                else if(PoolType.QualifiedNail.equals(cPs.getPoolType())) {
                    cPs.setPoolType(PoolType.RevQualifiedNail);
                    cPs.setReviewerId(null);
                    cPs.setResultType(null);
                    cPs.setIsHandled(null);
                }
                else if(PoolType.RevQualified.equals(cPs.getPoolType())
                        || PoolType.RevQualifiedNail.equals(cPs.getPoolType())
                        || PoolType.Disqualified.equals(cPs.getPoolType())
                        || PoolType.RetagScraped.equals(cPs.getPoolType())) {
                    cPs.setResultType(rescueType);
                    cPs.setIsDeleted(true);
                    cPs.setIsHandled(true);
                }
                else if(PoolType.Pgc.equals(cPs.getPoolType())) {
                    cPs.setResultType(rescueType);
                    cPs.setIsHandled(true);
                }
                postScoreDao.update(cPs);
                break;
            }
            case Remove: {
                if(!postDao.exists(postId)) {
                    errs.add(new Throwable("Invalid post"));
                    break;
                }
                Post post = postDao.findById(postId);
                List<Post> posts = new ArrayList<Post>();
                posts.add(post);
                List<PostNew> pns = postNewDao.getPostNewByPosts(posts, false);
                for(PostNew postNew : pns) {
                    postNew.setIsDeleted(true);
                    postNewDao.update(postNew);
                    if(!Constants.getPersonalTrendEnable())
                        continue;
                    List<CircleType> ctList = circleService.getCircleTypes(postNew.getCircleTypeId());
                    if(ctList == null || ctList.size() <= 0)
                        continue;
                    publishDurableEvent(PersonalTrendEvent.CreateRemoveEvent(postNew.getLocale(), 
                        postNew.getCircleTypeId(), ctList.get(0).getDefaultType().toLowerCase(), postId)); 
                }
                cPs.setResultType(rescueType);
                cPs.setIsHandled(true);
                cPs.setIsDeleted(true);
                postScoreDao.update(cPs);
                break;
            }
            default:
                break;
            }
        }
        catch(Exception e) {
            errs.add(e);
        }
    
        return errs;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Throwable> handleTrendRescueTask(Long handlerId, Map<String, Object> rescueTask, Long circleTypeId) {
    	List<Throwable> errs = new ArrayList<Throwable>();
        if(rescueTask == null) {
            errs.add(new Throwable("Empy tasks"));
            return errs;
        }
        
        if(handlerId == null || !userDao.exists(handlerId)) {
            errs.add(new Throwable("Invalid user"));
            return errs;
        }
        
        User handler = userDao.findById(handlerId);

        Long descCirId = rescueTask.containsKey("descCirId") ? (Long) rescueTask.get("descCirId") : null;
        Integer quality = rescueTask.containsKey("quality") ? (Integer) rescueTask.get("quality") : null;
        List<Map<String, Object>> keywords = rescueTask.containsKey("keywords") ? (List<Map<String, Object>>) rescueTask.get("keywords") : null;
        Long postId = rescueTask.containsKey("postId") ? (Long) rescueTask.get("postId") : null;
        ResultType rescueType = rescueTask.containsKey("type") ? (ResultType) rescueTask.get("type") : null;
        if(postId == null || rescueType == null) {
            errs.add(new Throwable("Empy postId or rescueType"));
            return errs;
        }
        List<Long> postIds = new ArrayList<Long>();
        postIds.add(postId);
        List<PostScoreTrend> psts = postScoreTrendDao.findByPostIds(postIds, false);
        if(psts == null || psts.size() <= 0) {
            errs.add(new Throwable("Empy postScore"));
            return errs;
        }
        for (PostScoreTrend cPst : psts) {
            if(!cPst.getReviewerId().equals(handlerId) && !rescueType.equals(ResultType.ChangeKeyWord)) {
                errs.add(new Throwable("Wrong handler"));
                return errs;
            }
        }
        
        Circle descCir = null;
        String descCirTypeName = null;
        if(descCirId != null) {
            List<Long> circleTypeIds = new ArrayList<Long>();
            circleTypeIds.add(descCirId);
            PageResult<Circle> descCirs = circleDao.findBcDefaultCircleByCircleTypeIds(circleTypeIds, 0L, 100L);
            if(descCirs.getResults() != null && descCirs.getResults().size() > 0) {
                descCir = descCirs.getResults().get(0);
                descCirTypeName = descCir.getDefaultType().toLowerCase();
            }
        }
        try {
            switch(rescueType) {
            case CatAndTrend: {
                //curatePost(handler, cPs, rescueType, postId, descCir, descCirId, keywords, quality, false, objectMapper.writeValueAsString(rescueTask), true, errs);                    
                break;
            }
            case CatOnly:
            case SelfieOnly:{
                //curatePost(handler, cPs, rescueType, postId, descCir, descCirId, keywords, quality, true, objectMapper.writeValueAsString(rescueTask), false, errs);
                break;
            }
            case ChangeKeyWord:{
                if(!postDao.exists(postId)) {
                    errs.add(new Throwable("Invalid post"));
                    break;
                }
                Post post = postDao.findById(postId);
                Set<String> toAddKWs = new HashSet<String>();
                List<Long> toUpdateFrequency = new ArrayList<Long>();
                if(descCir != null)
                    toAddKWs.add(descCir.getCircleName());
                
                if(descCirId != null) {
                    int count = 0;
                    for (PostScoreTrend cPst : psts) {
                        cPst.setCircleTypeId(descCirId);
                        List<Post> posts = new ArrayList<Post>();
                        posts.add(post);
                        List<PostNew> pns = postNewDao.getPostNewByPosts(posts, null);
                        if(pns == null || pns.size() <= 0) {
                            cPst.setResultType(rescueType);
                            cPst.setIsHandled(true);
                            postScoreTrendDao.update(cPst);
                            break;
                        }
                        else {
                            // ToDo : update trending 
                            /*if(count == 0) {
                                PostNew postNew = pns.get(0);
                                postNew.setCircleTypeId(descCirId);
                                postNewDao.update(postNew);
                                if(Constants.getPersonalTrendEnable()) {
                                    if(descCirTypeName != null && postNew.getCircleTypeId() != null) {
                                        Circle c = post.getCircle();
                                        if(c != null) {
                                            CircleType ct = c.getCircleType();
                                            if(ct != null) {
                                                String oriKey = ct.getDefaultType();
                                                if(oriKey != null) {
                                                    oriKey = oriKey.toLowerCase();
                                                    publishDurableEvent(PersonalTrendEvent.CreateModifyEvent(postNew.getLocale(), 
                                                            postNew.getCircleTypeId(), descCirId, oriKey, 
                                                            descCirTypeName, postId));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            count++;*/
                        }
                    }
                }
                
                if(keywords != null) {
                    for(Map<String, Object> kwMap : keywords) {
                        if(!kwMap.containsKey("kword") || !kwMap.containsKey("kid"))
                            continue;
                        toAddKWs.add((String) kwMap.get("kword"));
                        toUpdateFrequency.add((Long)kwMap.get("kid"));
                    }
                }
                if(toAddKWs.size() > 0) {
                    PostTags tags = transferTags(post.getTags());
                    if(tags == null)
                        tags = new PostTags();
                    tags.getKeywords().addAll(toAddKWs);
                    post.setTags(objectMapper.writer((PrettyPrinter)null).withView(MainPostDbView.class).writeValueAsString(tags));
                    updateMainPost(post, null, post.getPostStatus(), null, true);
                }
                for (PostScoreTrend cPst : psts) {
                    cPst.setResultType(rescueType);
                    cPst.setIsHandled(true);
                    postScoreTrendDao.update(cPst);
                }
                postCurateKeywordDao.updateFrequency(toUpdateFrequency);
                break;
            }
            case Abandon:{
                break;
            }
            case Reviewed: {
                break;
            }
            case Remove: {
                if(!postDao.exists(postId)) {
                    errs.add(new Throwable("Invalid post"));
                    break;
                }
                Post post = postDao.findById(postId);
                List<Post> posts = new ArrayList<Post>();
                posts.add(post);
                List<PostNew> pns = postNewDao.getPostNewByPosts(posts, false);
                for(PostNew postNew : pns) {
                    postNew.setIsDeleted(true);
                    postNewDao.update(postNew);
                    if(Constants.getPersonalTrendEnable()) {
                        List<CircleType> ctList = circleService.getCircleTypes(postNew.getCircleTypeId());
                        if(ctList != null && ctList.size() > 0) {
                            publishDurableEvent(PersonalTrendEvent.CreateRemoveEvent(postNew.getLocale(), 
                                postNew.getCircleTypeId(), ctList.get(0).getDefaultType().toLowerCase(), postId)); 
                        }
                    }
                }
                for (PostScoreTrend cPst : psts) {
                    cPst.setResultType(rescueType);
                    cPst.setIsHandled(true);
                    cPst.setIsDeleted(true);
                    postScoreTrendDao.update(cPst);
                }
                break;
            }
            default:
                break;
            }
        }
        catch(Exception e) {
            errs.add(e);
        }
    
        return errs;
    }

	@Override
	public PageResult<User> getTopPostCountUserByUserIds(List<Long> idList,
			Long offset, Long limit) {
		PageResult<User> page = new PageResult<User>();
		page.setTotalSize(idList.size());
		if (idList.size() == 0) {
			page.setResults(new ArrayList<User>());
			return page;
		}
		if (offset >= idList.size()) {
			page.setResults(new ArrayList<User>());
			return page;
		}

		List<Long> countOrder = postAttributeDao.listUserIdsByPostCount(idList);
		List<Long> promoteOrder = postAttributeDao.listUserIdsByPromote(idList);
		countOrder.removeAll(promoteOrder);
		idList.removeAll(countOrder);
		idList.removeAll(promoteOrder);
		
		if (!idList.isEmpty()) {
			List<Long> likeOrder = new ArrayList<Long>();
			Map<Long, Long> likeMap = postAttributeDao
					.getLikeCountByUserIds(idList);
			List<SortItem> sortList = new ArrayList<SortItem>();
			for (Long id : idList) {
				Long score = Long.valueOf(0);
				if (likeMap.containsKey(id))
					score += likeMap.get(id);
				sortList.add(new SortItem(id, score));

			}
			Collections.sort(sortList, Collections.reverseOrder());
			for (SortItem s : sortList) {
				likeOrder.add(s.id);
			}

			promoteOrder.addAll(countOrder);
			promoteOrder.addAll(likeOrder);
		} else
			promoteOrder.addAll(countOrder);

		List<Long> sortedList = promoteOrder.subList(
				offset.intValue(),
				Math.min(offset.intValue() + limit.intValue(),
						promoteOrder.size()));
		Map<Long, Long> sortedMap = new HashMap<Long, Long>();
		for (int i=0 ; i<sortedList.size() ; i++) {
			sortedMap.put(sortedList.get(i), Long.valueOf(i));
		}
		
		List<User> userList = userDao.findByIds(sortedList
				.toArray(new Long[sortedList.size()]));
		for (User u : userList) {
    		Long score = Long.valueOf(0);
    		if (sortedMap.containsKey(u.getId()))
    			score += sortedMap.get(u.getId());
    		u.setSortValue(score);
    	}
		Collections.sort(userList);
		page.setResults(userList);

		return page;
	}
	
	private class SortItem implements Comparable<SortItem>{
    	public Long id;
    	public Long value;
    	SortItem(Long id, Long value) {
    		this.id = id;
    		this.value = value;
    	}
    	
		@Override
		public int compareTo(SortItem o) {
			if (this.value > o.value) {
				return 1;
			} else if (this.value == o.value) {
				return 0;
			} else {
				return -1;
			}				
		}
    	
    }
    
    @Override
    public Set<String> extractHashtagsFromText(String text){
    	Extractor extractor = new Extractor();
    	List<String> hashTags = extractor.extractHashtags(text);
    	return new HashSet<String>(hashTags);
    }
}
