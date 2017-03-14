package com.cyberlink.cosmetic.modules.post.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.ScrollableResults;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.event.circle.CircleDeleteEvent;
import com.cyberlink.cosmetic.modules.post.dao.LikeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreDao;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.event.PostViewProcessEvent;
import com.cyberlink.cosmetic.modules.post.model.Like;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.repository.PostViewRepository;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostWrapperUtil;
import com.cyberlink.cosmetic.modules.post.service.AsyncPostUpdateService;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;
import com.cyberlink.cosmetic.modules.user.model.User.LookSource;
import com.cyberlink.cosmetic.utils.CosmeticWorkQueue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

public class AsyncPostUpdateServiceImpl extends AbstractService implements AsyncPostUpdateService {

    private UserDao userDao;
    private LikeDao likeDao;
    private PostDao postDao;
    private PostViewDao postViewDao;
    private PostScoreDao postScoreDao;
    private UserAttrDao userAttrDao;
    private TransactionTemplate transactionTemplate;
    private ObjectMapper objectMapper;
    private PostViewRepository postViewRepository;
    
    private static final Integer BATCH_SIZE = 100;
    CosmeticWorkQueue workQueue = new CosmeticWorkQueue(4, "PostService");
    
    public void setPostViewDao(PostViewDao postViewDao) {
        this.postViewDao = postViewDao;
    }
    
    public void setPostScoreDao(PostScoreDao postScoreDao) {
        this.postScoreDao = postScoreDao;
    }
    
    public void setUserAttrDao(UserAttrDao userAttrDao) {
        this.userAttrDao = userAttrDao;
    }
    
    public void setLikeDao(LikeDao likeDao) {
        this.likeDao = likeDao;
    }
    
    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }
    
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setPostViewRepository(PostViewRepository postViewRepository) {
        this.postViewRepository = postViewRepository;
    }
    
    @Override
    public void runLoadPostView(ArrayList<Long> postIds) {
        if(postIds == null || postIds.size() <= 0)
            return;
        
        Boolean isWriteable = Constants.getWebsiteIsWritable().equals("true"); 
        if(isWriteable) {
            asyncLoadPostViews(postIds);
            return;
        }
        
        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        params.add(Pair.of("loadPostView", ""));
        for(Long postId : postIds) {
            params.add(Pair.of("loadPostViewIds", String.valueOf(postId)));
        }
        RunnableAsyncToWriteServer r = new RunnableAsyncToWriteServer("/api/v3.0/post/list-post-by-circle.action", params);
        asyncRun(r);
    }   
    
    @BackgroundJob
    @Override
    public void runLoadPostLike(ArrayList<Long> postIds) {
        if(postIds == null || postIds.size() <= 0)
            return;
        
        if(!Constants.getIsRedisFeedEnable())
            return;
        
        Map<Long, Boolean> likeMap = postViewRepository.getLikes(postIds, -1L);
        for(Long pid : likeMap.keySet()) {
            if(likeMap.get(pid) == null)
                continue;
            postIds.remove(pid);
        }
        if(postIds.size() <= 0)
            return;
        
        for(Long postId : postIds) {
            postViewRepository.createOrUpdatePostLikes(postId, null, true);
        }
        
        likeDao.doWithAllLike(TargetType.Post, postIds, new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                int i = 0;
                while (sr.next()) {
                    if ((++i) % BATCH_SIZE == 0) {
                        likeDao.clear();
                    }
                    final Object[] o = sr.get();
                    final Long postId = (Long) o[0];
                    final Long userId = (Long) o[1];
                    postViewRepository.createOrUpdatePostLikes(postId, userId, true);
                }
            }
        });
    }
    
    private void asyncLoadPostViews(List<Long> postIds) {
        if(postIds == null || postIds.size() <= 0)
            return;
        workQueue.execute( new RunnableUpdatePostViews(postIds));
    }
    
    @Override
    public void asyncRun(Runnable r) {
        workQueue.execute(r);
    }
    
    private class RunnableUpdatePostViews implements Runnable {
        private List<Long> postIds;
        
        RunnableUpdatePostViews(List<Long> postIds){
            this.postIds = postIds;
        }       
        public void run() {
            transactionTemplate.execute(new TransactionCallback<List<Throwable>>() {
                @Override
                public List<Throwable> doInTransaction(TransactionStatus status) {
                    List<Post> posts = postDao.findByIds(postIds.toArray(new Long[postIds.size()]));
                    List<MainPostSimpleWrapper> tmps = PostWrapperUtil.wrapSimplePostResult(posts, null, null, null);
                    
                    try {
                        for(MainPostSimpleWrapper tmp : tmps) {
                            String mainPostView = objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(tmp);
                            PostViewAttr pAttr = new PostViewAttr();
                            pAttr.setLikeCount(tmp.getLikeCount());
                            pAttr.setCommentCount(tmp.getCommentCount());
                            pAttr.setCircleInCount(tmp.getCircleInCount());
                            postViewDao.createOrUpdate(tmp.getPostId(), tmp.getCreator().getUserId(), mainPostView, null, pAttr);
                            if(Constants.getIsRedisFeedEnable()) {
                                publishDurableEvent(new PostViewProcessEvent(tmp.getPostId(), mainPostView));
                                publishDurableEvent(new PostViewProcessEvent(tmp.getPostId(), null, true));
                            }
                        }
                    } catch (JsonProcessingException e) {
                    }
                    return null;
                }
            });
        }
    }
    
    private class RunnableInsertRelatedPost implements Runnable {
        
        private Long postId;
        
        RunnableInsertRelatedPost(Long postId){
            this.postId = postId;
        }       
        
        public void run() {
            if(postId == null)
                return;
            
            try {
                Connection conn = Jsoup.connect("http://" + Constants.getWebsiteDomain() + "/api/post/query-complete-post.action?insertRelatedPost&postId=" + postId.toString());
                conn.ignoreContentType(true).post();
            } catch (IOException e) {
            }
            
        }
    }
    
    
    private class RunnableUpdateUserAttr implements Runnable {
        private Long userId;
        private Long likePostCount;
        private Long postCount;
        private Long likeCount;
        private Long yclLookCount;
        private int updateType; //0 set, 1 increase, 2 decrease
        
        public RunnableUpdateUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount, Long yclLookCount, int updateType) {
            this.userId = userId;
            this.likePostCount = likePostCount;
            this.postCount = postCount;
            this.likeCount = likeCount;
            this.yclLookCount = yclLookCount;
            this.updateType = updateType;
        }  
        
        @Override
        public void run() {
            transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(TransactionStatus status) {
                    if(updateType == 0) {
                        UserAttr userAttr = userAttrDao.findByUserId(userId);
                        if(userAttr == null) {
                            userAttr = new UserAttr();
                            userAttr.setUserId(userId);
                            UserAttr newUserAttr = userAttrDao.create(userAttr);
                            if(newUserAttr == null)
                                return false;
                        }
                        userAttr.setLikeCount(likeCount.longValue());
                        try {
                            userAttrDao.create(userAttr);
                        }
                        catch(Exception e) {
                        }
                    }
                    syncUpdateUserAttr(userId, likePostCount, postCount, likeCount, yclLookCount, updateType);
                    return true;
                }
            });
        }
    }
    
    private class RunnableCleanPost implements Runnable {
        private List<Long> toDeletePostIds;
        private Long creatorId;
        private Boolean isSecret;
        
        RunnableCleanPost(Long creatorId, Boolean isSecret, List<Long> toDeletePostIds){
            this.toDeletePostIds = toDeletePostIds;
            this.creatorId = creatorId;
            this.isSecret = isSecret;
        }
        
        public void run() {
            if(toDeletePostIds == null || toDeletePostIds.size() <= 0)
                return;
            transactionTemplate.execute(new TransactionCallback<Boolean>() {

                @Override
                public Boolean doInTransaction(TransactionStatus status) {
                    cleanByDeletePost(creatorId, isSecret, toDeletePostIds);
                    return true;
                }
                
            });
            
            
        }
    }
    
    private class RunnableDeletePostByCircle implements Runnable {
        private Long circleId;
        private Long circleCreatorId;
        private Boolean isSecret;
        
        RunnableDeletePostByCircle(Long circleCreatorId, Boolean isSecret, Long circleId){
            this.circleId = circleId;
            this.circleCreatorId = circleCreatorId;
            this.isSecret = isSecret;
        }
        public void run() {
            List<Long> toDeletedPostId = transactionTemplate.execute(new TransactionCallback<List<Long>>() {

                @Override
                public List<Long> doInTransaction(TransactionStatus status) {
                    List<Long> toDeletedPostId = postDao.findByCreatorOrCircleAndStatus(null, ImmutableSet.of(circleId), null);
                    postDao.bacthDeletePostByCircle(circleId);
                    cleanByDeletePost(circleCreatorId, isSecret, toDeletedPostId);
                    return toDeletedPostId;
                }
                            
            });
            publishDurableEvent(new CircleDeleteEvent(circleCreatorId, circleId, toDeletedPostId));
        }
    }
    
    private void cleanByDeletePost(Long creatorId, Boolean isSecret, List<Long> toDeletedPostId) {
        Map<Long, Long> userLikePostMap = new HashMap<Long, Long>();
        Map<Long, Long> userLikeMap = new HashMap<Long, Long>();
        int offset = 0;
        do {
            BlockLimit blockLimit = new BlockLimit(offset, 100);
            PageResult<Like> userIds = likeDao.getAllLikeUserIds(toDeletedPostId, false, blockLimit);
            if(userIds.getResults().size() <= 0)
                break;
            for(Like like : userIds.getResults()) {
                Long diff = 1L;
                Long uId = like.getUserId();
                switch(like.getRefSubType()) {
                    case HOW_TO: {
                        if(userLikePostMap.containsKey(uId)) {
                            diff += userLikePostMap.get(uId);
                        }
                        userLikePostMap.put(uId, diff);
                        break;
                    }
                    case YCL_LOOK: {
                        if(userLikeMap.containsKey(uId)) {
                            diff += userLikeMap.get(uId);
                        }
                        userLikeMap.put(uId, diff);
                        break;
                    }
                    default:
                        break;
                }
            }
            offset += 100;
            if(offset > userIds.getTotalSize())
                break;
        } while(true);
        
        for(Long uid : userLikeMap.keySet()) {
            syncUpdateUserAttr(uid, null, null, userLikeMap.get(uid), null, 2);
        }
        for(Long uid : userLikePostMap.keySet()) {
            syncUpdateUserAttr(uid, userLikePostMap.get(uid), null, null, null, 2);
        }
        
        userAttrDao.setNonNullValue(creatorId, "YCL_LOOK_COUNT", null);
        userAttrDao.setNonNullValue(creatorId, "POST_COUNT", null);
        
        likeDao.bacthDeleteByTargets(TargetType.Post, toDeletedPostId);        
        postViewDao.bacthDeleteByPostId(toDeletedPostId);
        postScoreDao.batchDeleteByPostIds(toDeletedPostId);
        if(!Constants.getIsRedisFeedEnable())
            return;
        publishDurableEvent(new PostViewProcessEvent(toDeletedPostId));
    }
    
    private class RunnableAsyncToWriteServer implements Runnable {

        private List<Pair<String, String>> params;
        private String apiUrl;
        
        public RunnableAsyncToWriteServer(String apiUrl, List<Pair<String, String>> params) {
            this.apiUrl = apiUrl;
            this.params = params;
        }
        @Override
        public void run() {
            if(apiUrl == null || params == null)
                return;
            
            try {
                Connection conn = Jsoup.connect("http://" + Constants.getWebsiteWrite() + apiUrl);
                for(Pair<String, String> param : params) {
                    conn.data(param.getKey(), param.getValue());
                }
                conn.ignoreContentType(true).post();
            } catch (IOException e) {
            }
            
        }
        
    }
    
    private class RunnableUpdateLookSource implements Runnable {
        private Long userId;
        
        RunnableUpdateLookSource(Long userId) {
            this.userId = userId;
        } 
        
        public void run() {
            transactionTemplate.execute(new TransactionCallback<Boolean>() {

                @Override
                public Boolean doInTransaction(TransactionStatus status) {
                    UserAttr userAttr = userAttrDao.findByUserId(userId);
                    if(userAttr == null)
                        return true;
                    
                    LookSource lookSource = null;
                    if(userAttr.getYclLookCount() == null)
                        return true;
                    
                    if(userAttr.getYclLookCount() > 0)
                        lookSource = LookSource.YCL;
                    User curUser = userDao.findById(userId);
                    curUser.setLookSource(lookSource);
                    userDao.update(curUser);
                    return true;
                }
                
            });
        }
    }
    
    private void syncUpdateUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount, Long yclLookCount, int type) {
        switch(type) {
        case 0: {            
            UserAttr userAttr = userAttrDao.findByUserId(userId);
            if(userAttr == null)
                break;
            if(likePostCount != null && !likePostCount.equals(userAttr.getLikeHowToCount())) 
                userAttrDao.setNonNullValue(userId, "LIKE_HOW_TO_COUNT", likePostCount);
            if(postCount != null && !postCount.equals(userAttr.getHowToCount())) 
                userAttrDao.setNonNullValue(userId, "POST_COUNT", postCount);
            if(likeCount != null && !likeCount.equals(userAttr.getLikeCount())) 
                userAttrDao.setNonNullValue(userId, "LIKE_COUNT", likeCount);
            if(yclLookCount != null && !yclLookCount.equals(userAttr.getYclLookCount())) 
                userAttrDao.setNonNullValue(userId, "YCL_LOOK_COUNT", yclLookCount);
            break;
        }
        case 1:{
            if(likePostCount != null) 
                userAttrDao.increaseNonNullValueBy(userId, "LIKE_HOW_TO_COUNT", likePostCount);
            if(postCount != null) 
                userAttrDao.increaseNonNullValueBy(userId, "POST_COUNT", postCount);
            if(likeCount != null) 
                userAttrDao.increaseNonNullValueBy(userId, "LIKE_COUNT", likeCount);
            if(yclLookCount != null) 
                userAttrDao.increaseNonNullValueBy(userId, "YCL_LOOK_COUNT", yclLookCount);
            break;
        }
        case 2:{
            if(likePostCount != null) 
                userAttrDao.decreaseNonNullValueBy(userId, "LIKE_HOW_TO_COUNT", likePostCount);
            if(postCount != null) 
                userAttrDao.decreaseNonNullValueBy(userId, "POST_COUNT", postCount);
            if(likeCount != null) 
                userAttrDao.decreaseNonNullValueBy(userId, "LIKE_COUNT", likeCount);
            if(yclLookCount != null) 
                userAttrDao.decreaseNonNullValueBy(userId, "YCL_LOOK_COUNT", yclLookCount);
            break;
        }
            default: break;
        }
        
        if(yclLookCount != null) {
            RunnableUpdateLookSource updateLookSource = new RunnableUpdateLookSource(userId);
            workQueue.execute(updateLookSource);
        }
    }
    
    private void asyncUpdateUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount, Long yclLookCount, int type) {
        workQueue.execute(new RunnableUpdateUserAttr(userId, likePostCount, postCount, likeCount, yclLookCount, type));
    }
    
    @Override
    public void insertRelatedPost(Long postId) {
        if(postId == null)
            return;
        workQueue.execute( new RunnableInsertRelatedPost(postId));
    }

    @Override
    public void cleanPost(Long creatorId, Boolean isSecret, List<Long> postIds) {
        if(postIds == null || postIds.size() <= 0)
            return;
        
        Boolean isWriteable = Constants.getWebsiteIsWritable().equals("true"); 
        if(!isWriteable) {
            // ToDo : Currently we no need to clean post in read API
            return;
        }
        
        workQueue.execute(new RunnableCleanPost(creatorId, isSecret, postIds));
    }

    @Override
    public void cleanPostByCircle(Long userId, Boolean isSecret, Long circleId) {
        if(circleId == null || userId == null)
            return;
        
        Boolean isWriteable = Constants.getWebsiteIsWritable().equals("true"); 
        if(!isWriteable) {
            // ToDo : Currently we no need to clean post in read API
            return;
        }
        
        RunnableDeletePostByCircle r1 = new RunnableDeletePostByCircle(userId, isSecret, circleId);
        workQueue.execute(r1);
    }

    @Override
    public void changeUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount,
            Long yclLookCount, Integer type) {
        if(userId == null || type == null)
            return;
        Boolean isWriteable = Constants.getWebsiteIsWritable().equals("true"); 
        if(isWriteable) {
            asyncUpdateUserAttr(userId, likePostCount, postCount, likeCount, yclLookCount, type);
            return;
        }
        
        String url = "/api/user/info.action";
        List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
        params.add(Pair.of("updateLikeCount", ""));
        params.add(Pair.of("userId", userId.toString()));
        params.add(Pair.of("resetType", type.toString()));
        if(likePostCount != null)
            params.add(Pair.of("likePostCount", likePostCount.toString()));
        if(postCount != null)
            params.add(Pair.of("postCount", postCount.toString()));
        if(likeCount != null)
            params.add(Pair.of("likeCount", likeCount.toString()));
        if(yclLookCount != null)
            params.add(Pair.of("yclLookCount", yclLookCount.toString()));
        
        RunnableAsyncToWriteServer r = new RunnableAsyncToWriteServer(url, params);
        asyncRun(r);
    }
    @Override 
    public void increaseUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount,
            Long yclLookCount) {
        changeUserAttr(userId, likePostCount, postCount, likeCount, yclLookCount, 1);
    }

    @Override
    public void decreaseUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount,
            Long yclLookCount) {
        changeUserAttr(userId, likePostCount, postCount, likeCount, yclLookCount, 2);
        
    }

    @Override
    public void setUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount, Long yclLookCount) {
        changeUserAttr(userId, likePostCount, postCount, likeCount, yclLookCount, 0);
    }
    
    @Override
    public Map<Integer, Boolean> getWorkerStatus() {
        return workQueue.getWorkerStatus();
    }
    
    @Override
    public void wakeUpWorker() {
        workQueue.initWorker();
    }
    
    @Override
    public Integer getTaskCount() {
        return workQueue.getTaskCount();
    }
    
    @Override
    public void clearAllTask() {
        workQueue.clearAllTask();
    }
}
