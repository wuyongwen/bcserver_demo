package com.cyberlink.cosmetic.modules.post.listener;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.event.post.PostCreateEvent;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.file.model.File;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.FileType;
import com.cyberlink.cosmetic.modules.file.service.FileService;
import com.cyberlink.cosmetic.modules.file.service.ImageService;
import com.cyberlink.cosmetic.modules.file.service.PhotoProcessService;
import com.cyberlink.cosmetic.modules.file.service.PhotoProcessService.ImageViolationType;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreDao;
import com.cyberlink.cosmetic.modules.post.dao.PostTopKeywordDao;
import com.cyberlink.cosmetic.modules.post.model.Attachment;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostScore;
import com.cyberlink.cosmetic.modules.post.model.PostScore.CreatorType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.service.RelatedPostService;
import com.cyberlink.cosmetic.utils.IdGenerator;
import com.cyberlink.cosmetic.utils.Tool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.FileBean;

public class PostCreateEventListener extends
    AbstractEventListener<PostCreateEvent> {

    private PhotoProcessService photoProcessService;
    private PostDao postDao;
    private PostScoreDao postScoreDao;
    private CircleTypeDao circleTypeDao;
    private TransactionTemplate transactionTemplate;
    private ObjectMapper objectMapper;
    private ImageService imageService;
    private FileService fileService;
    private RelatedPostService relatedPostService;
	private PostTopKeywordDao postTopKeywordDao;

	private int SCORE_THRESHOLD = 70;
    private Long NAIL_CIRCLE_TYPE_GROUP_ID = 5L;
    private String LOCALE_TO_DETECT = "zh_CN";
    private static List<Long> nailCircleTypeIds = null;
    
    private String FILE_MATADATA_KEY = "Metadata";
    private String FILE_URL_KEY = "Url";
    private String FILE_RETRY_URL_KEY = "RetryUrl";
    private String FILE_LOCAL_PATH_KEY = "LocalPath";
    private String POST_USER_TYPE_KEY = "UserType";
    private String POST_USER_ID_KEY = "UserId";
    private String POST_KEYWORD_KEY = "Keyword";
    
    public void setPhotoProcessService(PhotoProcessService photoProcessService) {
        this.photoProcessService = photoProcessService;
    }

    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }
    
    public void setPostScoreDao(PostScoreDao postScoreDao) {
        this.postScoreDao = postScoreDao;
    }
    
    public void setCircleTypeDao(CircleTypeDao circleTypeDao) {
        this.circleTypeDao = circleTypeDao;
    }
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }
    
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }
    
	public void setRelatedPostService(RelatedPostService relatedPostService) {
		this.relatedPostService = relatedPostService;
	}
	
	public void setPostTopKeywordDao(PostTopKeywordDao postTopKeywordDao) {
		this.postTopKeywordDao = postTopKeywordDao;
	}
	
    @SuppressWarnings("unchecked")
    @Override
    public void onEvent(PostCreateEvent e) {
        if(!Constants.enablePostPhotoProcess())
            return;
        
        if(e.getPostId() == null || !postDao.exists(e.getPostId()))
            return;
        
        final Long postId = e.getPostId();
        final String postLocale = e.getLocale();
        final Map<String, Object> postInfo = new HashMap<String, Object>();
        try {
            postInfo.putAll(getPostInfo(postId));
            processPostScore(postId, postLocale, postInfo);
        }
        catch(Exception ex) {
            logger.error("ProcessPostScore error", ex);
        }
        
        if (postLocale == null)
            return;
        
        Set<String> postKeywords = (Set<String>) postInfo.get(POST_KEYWORD_KEY);
        if (postKeywords == null || postKeywords.size() <= 0)
            return;
        final Set<String> keywords = new HashSet<String>();
        for(String kw : postKeywords){
            kw = StringUtils.strip(kw.replaceAll("[\\u00A0\\u2007\\u202F\\s]+", " ")); //remove leading & trailing spaces and \r, \n & \t from keyword string
            keywords.add(kw);
        }
        Date currentDate = new Date();
        final Integer kwBucketId = relatedPostService.getKeywordBucketId(currentDate);
        if (kwBucketId == null)
            return;

		transactionTemplate.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				postTopKeywordDao.updateKeywordsFreq(postLocale, keywords, kwBucketId);
				return true;
			}
		});
    }
   
    private Map<String, Object> getPostInfo(final Long postId) {
        return transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
            @Override
            public Map<String, Object> doInTransaction(TransactionStatus status) {
                Post post = postDao.findById(postId);
                List<Attachment> attachments = post.getAttachments();
                if(attachments == null || attachments.size() <= 0)
                    return null;
                File file = attachments.get(0).getAttachmentFile();
                List<FileItem> fileItems = file.getOriginalItems();
                if(fileItems == null || fileItems.size() <= 0)
                    return null;
                Map<String, Object> info = new HashMap<String, Object>();
                info.put(FILE_URL_KEY, fileItems.get(0).getOriginalUrl());
                info.put(FILE_RETRY_URL_KEY, "http://" + Constants.getCdnDomain() + "/" + fileItems.get(0).getFilePath());
                info.put(FILE_MATADATA_KEY, fileItems.get(0).getMetadata()); 
                info.put(FILE_LOCAL_PATH_KEY, fileItems.get(0).getLocalFilePath(""));
                String userType = "Normal";
                if(post.getCreator().getUserType() != null)
                    userType = post.getCreator().getUserType().toString();
                info.put(POST_USER_TYPE_KEY, userType);
                info.put(POST_USER_ID_KEY, post.getCreatorId());
                if(post.getPostTags() != null)
                    info.put(POST_KEYWORD_KEY, post.getPostTags().getKeywords());
                return info;
            }
        });
    }
    
    private Pair<BufferedImage, Integer> getImgInfo(Map<String, Object> postInfo) throws IOException {
        try {
            return photoProcessService.getBufferAndLengthFromUrl((String)postInfo.get(FILE_URL_KEY), 5000);
        } catch (IOException e) {
            String retryUrl = (String)postInfo.get(FILE_RETRY_URL_KEY);
            logger.error(e.getMessage() + " - RetryUrl : " + retryUrl);
            return photoProcessService.getBufferAndLengthFromUrl(retryUrl, 5000);
        }
    }
    
    private void processPostScore(final Long postId, final String postLocale, final Map<String, Object> postInfo) throws IOException {       
        
        if(postInfo == null)
            return;
        
        final Pair<BufferedImage, Integer> pImgInfo = getImgInfo(postInfo);
        if(pImgInfo == null)
            return;
        final List<ImageViolationType> voilated = new ArrayList<ImageViolationType>();
        if(postLocale.equalsIgnoreCase("zh_CN")){
            /*
            if(DetectForbiddenWordUtils.hasForbiddenWord(post.getTitle()) ||DetectForbiddenWordUtils.hasForbiddenWord(post.getContent())){
                voilated.add(ImageViolationType.ForbiddenWord);
            }
            */
            if(((String)postInfo.get(POST_USER_TYPE_KEY)).equalsIgnoreCase("Normal"))
                voilated.addAll(checkPhotoVoilation(postLocale, pImgInfo));
        }
        
        transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                Post post = postDao.findById(postId);
                if(voilated == null || voilated.size() <= 0)
                    addToCurate(post, pImgInfo);
                else
                    addToViolate(post, pImgInfo, voilated);
                return true;
            }
        });
        
        if(voilated == null || voilated.size() <= 0){
            getImageURLFromAppendImagesInPost(postId, postInfo);
        }
        else{
            Tool.makeDir("/vol/log/postScore");
            Tool.writeMessageToFile("/vol/log/postScore/detectVoilatedLogFile.txt",
                    "[Voilated Data]post ID:" + postId+ "; Creator ID: " + postInfo.get(POST_USER_ID_KEY) + "; voilated: " + voilated, true);
        }
    }
    
    private String getImageURLFromAppendImagesInPost(Long postId, Map<String, Object> postInfo){
        Long creatorId = (Long) postInfo.get(POST_USER_ID_KEY);
    	String uploadImageURL = null;
    	try {
    		JsonNode jns = null;
    		try{
	    	String metadata =  (String)postInfo.get(FILE_MATADATA_KEY);
	    	//String metadata = "{\"fileSize\":46662,\"orientation\":0,\"photoInfos\":[{\"imageUrl\":\"http://media1.popsugar-assets.com/files/2015/05/05/648/n/1922398/40b3b20b_edit_img_cover_file_845239_1430762283_Plank-coverB5gh21.preview/i/3-Minute-Plank-Workout.jpg\",\"width\":550,\"height\":373},{\"imageUrl\":\"http://media1.popsugar-assets.com/files/2015/05/05/648/n/1922398/40b3b20b_edit_img_cover_file_845239_1430762283_Plank-coverB5gh21.preview/i/3-Minute-Plank-Workout.jpg\",\"width\":550,\"height\":373},{\"imageUrl\":\"http://media1.popsugar-assets.com/files/2015/05/05/648/n/1922398/40b3b20b_edit_img_cover_file_845239_1430762283_Plank-coverB5gh21.preview/i/3-Minute-Plank-Workout.jpg\",\"width\":550,\"height\":373},{\"imageUrl\":\"http://media1.popsugar-assets.com/files/2015/05/05/648/n/1922398/40b3b20b_edit_img_cover_file_845239_1430762283_Plank-coverB5gh21.preview/i/3-Minute-Plank-Workout.jpg\",\"width\":550,\"height\":373},{\"imageUrl\":\"http://media1.popsugar-assets.com/files/2015/05/05/648/n/1922398/40b3b20b_edit_img_cover_file_845239_1430762283_Plank-coverB5gh21.preview/i/3-Minute-Plank-Workout.jpg\",\"width\":550,\"height\":373},{\"imageUrl\":\"http://media1.popsugar-assets.com/files/2015/05/05/648/n/1922398/40b3b20b_edit_img_cover_file_845239_1430762283_Plank-coverB5gh21.preview/i/3-Minute-Plank-Workout.jpg\",\"width\":550,\"height\":373},{\"imageUrl\":\"http://media1.popsugar-assets.com/files/2015/05/05/648/n/1922398/40b3b20b_edit_img_cover_file_845239_1430762283_Plank-coverB5gh21.preview/i/3-Minute-Plank-Workout.jpg\",\"width\":550,\"height\":373}],\"height\":373,\"dominantedColor\":\"#F7F9EF\",\"width\":550,\"md5\":\"dd75b5ee35263c405d4d2b6d81c275c7\",\"collageMode\":1,\"redirectUrl\":\"http://www.popsugar.com/fitness/3-Minute-Plank-Workout-37408004?crlt.pid=camp.BQVc1fAMFY3P\",\"isSamplePhoto\":false,\"originalUrl\":\"http://cdn.beautycircle.com/5/6336005/589/9cb58c55-cce3-467b-ad74-fc1810b73e55.jpg\"}";
	    	jns = objectMapper.readValue(metadata, JsonNode.class).findValue("photoInfos");
    		}catch(Exception e){ 
    			return uploadImageURL;
    		}
	    	//Behalf of post from the browser
	    	if(jns == null)
	    		return null;
	    	List<String> imageDLPaths = new ArrayList<String>(); 
	    	String imageFilePath = postInfo.get(FILE_LOCAL_PATH_KEY) + "appendImage";
	    	Tool.makeDir(imageFilePath);
	    	List<String> imageUrlList = new ArrayList<String>();
	    	int appendImageNum = 0;
	    	if (jns.isArray()) {
	    		if(jns.size() <= 1)
	    			return uploadImageURL;
	    	    for (final JsonNode objNode : jns) {
	    	    	if(appendImageNum == 5)
	    	    		break;
	    	        String imageFileFullPath = Tool.pathJoin(imageFilePath,IdGenerator.generate(null)+".jpg");
	    	        imageDLPaths.add(imageFileFullPath);
	    	        Tool.downloadFileFromURL(objNode.findValue("imageUrl").asText(),imageFileFullPath);
	    	        imageUrlList.add(objNode.findValue("imageUrl").asText());
	    	        appendImageNum++;
	    	    }
	    	}else{
	    		return uploadImageURL;
	    	}
	    	
	    	if(imageDLPaths.size() > 1){
		    	String outcomeFilePath = Tool.pathJoin(imageFilePath,IdGenerator.generate(null)+".jpg");
		    	if(imageDLPaths.size() > 5){
		    		imageDLPaths = imageDLPaths.subList(0, 5);
		    	}
		    	imageService.appendImages(500, "vertical", outcomeFilePath, imageDLPaths);
		    	for(String imageFullFilePath : imageDLPaths){
		    		Tool.delFile(imageFullFilePath);
		    	}
		    	java.io.File newFile = new java.io.File(outcomeFilePath);
		    	//fileItem = fileService.uploadToS3(creatorID, new FileBean(newFile,new MimetypesFileTypeMap().getContentType(newFile),newFile.getName()), metadata, FileType.Photo, false);
		    	uploadImageURL = fileService.uploadRawToS3(creatorId, new FileBean(newFile,new MimetypesFileTypeMap().getContentType(newFile),newFile.getName()), FileType.Photo);
                Tool.makeDir("/vol/log/postScore");
				Tool.writeMessageToFile("/vol/log/postScore/detectVoilatedLogFile.txt",
						"[UploadAppendImage Data]Post ID:" + postId + "; Creator ID: " + creatorId + "; Append Image URL: "
								+ imageUrlList + "; Append Image URL: " + uploadImageURL,true);
	    	}
    	} catch (Exception e) {
    		logger.error("getImageURLFromAppendImagesInPost", e);
			return uploadImageURL;
		}
    	return uploadImageURL;
    }
    
    private List<ImageViolationType> checkPhotoVoilation(String postLocale, Pair<BufferedImage, Integer> pImgInfo) {
        List<ImageViolationType> result = new ArrayList<ImageViolationType>();
        if(postLocale == null || !LOCALE_TO_DETECT.equalsIgnoreCase(postLocale))
            return result;
        
        if(pImgInfo == null)
            return result;
        List<ImageViolationType> detectTypes = new ArrayList<ImageViolationType>();
        detectTypes.add(ImageViolationType.Porn);
        /*
         * Disable violence detection temporary since the
         * result accuracy is low
         */
        //detectTypes.add(ImageViolationType.Violence);
        Map<ImageViolationType, Boolean> r = photoProcessService.DetectImageViolation(pImgInfo.getLeft(), detectTypes, null);
        for(ImageViolationType t : r.keySet()) {
            Boolean rel = r.get(t);
            if(rel != null && rel)
                result.add(t);
        }

        return result;
    }

    private void addToCurate(Post p, Pair<BufferedImage, Integer> pImgInfo) {
        if("circle_in_posting".equals(p.getPostSource()))
            return;
        if(p.getBasicSortBonus() != null && p.getBasicSortBonus() >= 200)
            return;
        
        HandlePost(p, pImgInfo, p.getCircle(), null, null);
    }
    
    private void addToViolate(Post p, Pair<BufferedImage, Integer> pImgInfo, List<ImageViolationType> violatedType) {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("violated", violatedType);
        HandlePost(p, pImgInfo, p.getCircle(), PoolType.Violate, info);
    }
    
    private PostScore HandlePost(Post post, Pair<BufferedImage, Integer> pImgInfo, Circle circle, PoolType poolType, Map<String, Object> info) {
        PostScore postScore = null;
        if(post == null || pImgInfo == null || circle == null)
            return postScore;
        
        if(post.getIsDeleted() || circle.getIsDeleted() || circle.getIsSecret())
            return postScore;
        
        List<Long> postIds = new ArrayList<Long>();
        postIds.add(post.getId());
        List<PostScore> exPostScores = postScoreDao.findByPostIds(postIds, false);
        if(exPostScores != null && exPostScores.size() > 0)
            return exPostScores.get(0);
        
        try {
            postScore = new PostScore();
            postScore.setPostId(post.getId());
            postScore.setPostLocale(post.getLocale());
            if(post.getAppName() != null)   
				postScore.setAppName(post.getAppName().toString());
            postScore.setPostCreateDate(post.getCreatedTime());
            postScore.setCircleTypeId(circle.getCircleTypeId());
            Long score = 0L;
            if(poolType == null) {
                poolType = PoolType.Disqualified;
                if(nailCircleTypeIds == null) {
                    nailCircleTypeIds = new ArrayList<Long>();
                    List<CircleType> cts = circleTypeDao.listTypesByTypeGroup(NAIL_CIRCLE_TYPE_GROUP_ID, null);
                    for(CircleType ct : cts) {
                        nailCircleTypeIds.add(ct.getId());
                    }
                }
                if(pImgInfo != null)
                    score = photoProcessService.GetScore(pImgInfo).longValue();
                

                switch (post.getCreator().getUserType()) {
                    case Publisher: {
                        poolType = PoolType.Pgc;
                        postScore.setCreatorType(CreatorType.Publication);
                        break;
                    }
                    case Expert:
                    case Master: {
                        poolType = PoolType.Pgc;
                        postScore.setCreatorType(CreatorType.Beautyist);
                        break;
                    }
                    case Brand: {
                        poolType = PoolType.Pgc;
                        postScore.setCreatorType(CreatorType.Brand);
                        break;
                    }
                    case Normal: 
                    default :{
                    if(score >= SCORE_THRESHOLD) {
                        if(nailCircleTypeIds.contains(circle.getCircleTypeId()))
                            poolType = PoolType.QualifiedNail;
                        else
                            poolType = PoolType.Qualified;
                    }
                    break;
                    }
                }
            
            }
            if(info != null) {
                String infoStr = objectMapper.writeValueAsString(info);
                postScore.setInfo(infoStr);
            }
            postScore.setPoolType(poolType);
            postScore.setScore(score.intValue());
            postScoreDao.create(postScore);
        }
        catch(Exception e) {
            logger.error("HandlePost error", e);
        }
        return postScore;
    }
}
