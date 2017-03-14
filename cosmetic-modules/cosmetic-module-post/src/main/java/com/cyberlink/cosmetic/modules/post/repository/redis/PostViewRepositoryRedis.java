package com.cyberlink.cosmetic.modules.post.repository.redis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.repository.PostViewRepository;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Creator;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.DPWCircle;
import com.cyberlink.cosmetic.modules.post.result.PostWrapperUtil.WrappingPostCallBack;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.KeyUtils;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PostViewRepositoryRedis extends AbstractRedisRepository implements
    PostViewRepository {

    private ObjectMapper objectMapper;
    private Long POST_VIEW_CACHE_DURATION = 7L;
    private TimeUnit POST_VIEW_CACHE_UNIT = TimeUnit.DAYS;
    private Long POST_LIKES_CACHE_DURATION = POST_VIEW_CACHE_DURATION;
    private TimeUnit POST_LIKES_CACHE_UNIT = POST_VIEW_CACHE_UNIT;
    
    private enum KeyPostfix {
        view, creator, circle, likes;
    };
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public void createOrUpdatePostView(Long postId, String postJson) {
        try {
            opsForValue().set(KeyUtils.postView(postId, KeyPostfix.view.name()), postJson, POST_VIEW_CACHE_DURATION, POST_VIEW_CACHE_UNIT);
        }
        catch(Exception e) {
        }
    }
    
    @Override
    public void createOrUpdatePostView(Long postId, MainPostSimpleWrapper post) throws Exception {
        String postJson = objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(post);
        createOrUpdatePostView(postId, postJson);   
    }

    @Override
    public void updatePostAttr(Long postId, PostViewAttr postViewAttr) throws Exception {
        if(postViewAttr == null)
            return;
        
        String postViewJson = null;
        try {
            postViewJson = opsForValue().get(KeyUtils.postView(postId, KeyPostfix.view.name()));
        }
        catch(Exception e) {
        }
        
        if(postViewJson == null || postViewJson.length() <= 0)
            return;
        MainPostSimpleWrapper postView = objectMapper.readValue(postViewJson, MainPostSimpleWrapper.class);
        if(postViewAttr.getCircleInCount() != null)
            postView.setCircleInCount(postViewAttr.getCircleInCount());
        if(postViewAttr.getCommentCount() != null)
            postView.setCommentCount(postViewAttr.getCommentCount());
        if(postViewAttr.getLikeCount() != null)
            postView.setLikeCount(postViewAttr.getLikeCount());
        if(postViewAttr.getLookDownloadCount() != null)
            postView.setLookDownloadCount(postViewAttr.getLookDownloadCount());
        createOrUpdatePostView(postId, postView);
    }
    
    @Override
    public void createOrUpdatePostViewUser(Long userId, String avatar,
            UserType userType, String cover, String description, String displayName) {
        if(userId == null)
            return;
            
        String creatorJson = null;
        try {
            creatorJson = opsForValue().get(KeyUtils.postView(userId, KeyPostfix.creator.name()));
        }
        catch(Exception e) {
        }
        
        Creator creator = null;
        if(creatorJson == null) {
            creator = new Creator();
        }
        else {
            try {
                creator = objectMapper.readValue(creatorJson, Creator.class);
            } catch (Exception e) {
                creator = new Creator();
            }
        }
         
        if(userId != null)
            creator.userId = userId;
        if(avatar != null)
            creator.avatar = avatar;
        if(userType != null)
            creator.userType = userType;
        if(cover != null)
            creator.cover = cover;
        if(description != null)
            creator.description = description;
        if(displayName != null)
            creator.displayName = displayName;
        
        try {
            creatorJson = objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(creator);
            opsForValue().set(KeyUtils.postView(userId, KeyPostfix.creator.name()), creatorJson, POST_VIEW_CACHE_DURATION, POST_VIEW_CACHE_UNIT);   
        } catch (Exception e) {
        }
        
    }

    @Override
    public void createOrUpdatePostViewCircle(Long circleId, String circleName, Boolean display) {
        if(circleId == null)
            return;
            
        String circleJson = null;
        try {
            circleJson = opsForValue().get(KeyUtils.postView(circleId, KeyPostfix.circle.name()));
        }
        catch(Exception e) {
        }
        
        DPWCircle circle = null;
        if(circleJson == null) {
            circle = new DPWCircle();
        }
        else {
            try {
                circle = objectMapper.readValue(circleJson, DPWCircle.class);
            } catch (Exception e) {
                circle = new DPWCircle();
            }
        }
         
        if(circleId != null)
            circle.circleId = circleId;
        if(circleName != null)
            circle.circleName = circleName;
        if(display != null)
            circle.display = display;
        
        try {
            circleJson = objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(circle);
            opsForValue().set(KeyUtils.postView(circleId, KeyPostfix.circle.name()), circleJson, POST_VIEW_CACHE_DURATION, POST_VIEW_CACHE_UNIT);   
        } catch (Exception e) {
        }
    }

    @Override
    public void createOrUpdatePostLikes(Long postId, Long userId, Boolean liked) {
        if(postId == null)
            return;
        
        try {
            String key = KeyUtils.postView(postId, KeyPostfix.likes.name());
            if(userId == null) {
                opsForSet().add(key, "-1");
                return;
            }
                
            if(liked)
                opsForSet().add(key, userId.toString());
            else
                opsForSet().remove(key, userId.toString());
            expire(key, POST_LIKES_CACHE_DURATION, POST_LIKES_CACHE_UNIT);
        }
        catch(Exception e) {
        }
        
    }
    
    @Override
    public Map<Long, MainPostSimpleWrapper> getPosts(List<Long> postIds) {
        Map<Long, MainPostSimpleWrapper> result = new LinkedHashMap<Long, MainPostSimpleWrapper>();       
        if(postIds == null || postIds.size() <= 0)
            return result;
        
        List<String> keys = new ArrayList<String>();
        for(Long postId : postIds) {
            result.put(postId, null);
            keys.add(KeyUtils.postView(postId, KeyPostfix.view.name()));
        }
        
        List<String> posts = null;
        try {
            posts = opsForValue().multiGet(keys);
        }
        catch (Exception e) {
        }
        
        if(posts == null || posts.size() <= 0)
            return result;
        
        for(String post : posts) {
            if(post == null)
                continue;
            
            try {
                MainPostSimpleWrapper postView = objectMapper.readValue(post, MainPostSimpleWrapper.class);
                result.put(postView.getPostId(), postView);
            } catch (Exception e) {
            }
        }
        return result;
    }

    @Override
    public Map<Long, MainPostSimpleWrapper> getPostsByFeed(List<Long> feedPostIds, List<Long> missPostId, WrappingPostCallBack callback) {
        Map<Long, MainPostSimpleWrapper> result = new LinkedHashMap<Long, MainPostSimpleWrapper>();       
        if(feedPostIds == null || feedPostIds.size() <= 0)
            return result;
        
        List<String> keys = new ArrayList<String>();
        for(Long feedPostId : feedPostIds) {
            result.put(feedPostId, null);
            keys.add(KeyUtils.postView(feedPostId, KeyPostfix.view.name()));
        }

        List<String> posts = null;
        try {
            posts = opsForValue().multiGet(keys);
        }
        catch (Exception e) {
            missPostId.addAll(feedPostIds);
        }
        
        if(posts == null || posts.size() <= 0)
            return result;
        
        Iterator<Long> resultPostIdIt = result.keySet().iterator();
        int index = -1;
        while(resultPostIdIt.hasNext() && ++index < posts.size()) {
            Long curPostId = resultPostIdIt.next();
            String post = posts.get(index);
            if(post == null) {
                missPostId.add(curPostId);
                continue;
            }
            try {
                MainPostSimpleWrapper postView = objectMapper.readValue(post, MainPostSimpleWrapper.class);
                if(callback != null)
                    callback.each(postView);
                result.put(curPostId, postView);
            } catch (IOException e) {
            }
        }
        
        return result;
    }
    
    @Override
    public Map<Long, Creator> getCreators(List<Long> userIds) {
        Map<Long, Creator> result = new LinkedHashMap<Long, Creator>();       
        if(userIds == null || userIds.size() <= 0)
            return result;
        
        List<String> keys = new ArrayList<String>();
        for(Long userId : userIds) {
            result.put(userId, null);
            keys.add(KeyUtils.postView(userId, KeyPostfix.creator.name()));
        }
        List<String> creators = null;
        
        try {
            creators = opsForValue().multiGet(keys);
        }
        catch(Exception e) {
        }
        
        if(creators == null || creators.size() <= 0)
            return result;
        
        for(String creator : creators) {
            if(creator == null)
                continue;
            
            try {
                Creator creatorView = objectMapper.readValue(creator, Creator.class);
                result.put(creatorView.getUserId(), creatorView);
            } catch (IOException e) {
            }
        }
        return result;
    }

    @Override
    public Map<Long, DPWCircle> getCircles(List<Long> circleIds) {
        Map<Long, DPWCircle> result = new LinkedHashMap<Long, DPWCircle>();       
        if(circleIds == null || circleIds.size() <= 0)
            return result;
        
        List<String> keys = new ArrayList<String>();
        for(Long circleId : circleIds) {
            result.put(circleId, null);
            keys.add(KeyUtils.postView(circleId, KeyPostfix.circle.name()));
        }
        List<String> circles = null;
        
        try {
            circles = opsForValue().multiGet(keys);
        }
        catch(Exception e) {
        }
        
        if(circles == null || circles.size() <= 0)
            return result;
        
        for(String circle : circles) {
            if(circle == null)
                continue;
            
            try {
                DPWCircle circleView = objectMapper.readValue(circle, DPWCircle.class);
                result.put(circleView.circleId, circleView);
            } catch (IOException e) {
            }
        }
        return result;
    }

    @Override
    public Map<Long, Boolean> getLikes(List<Long> postIds, Long userId) {
        Map<Long, Boolean> results = new HashMap<Long, Boolean>();

        try {
            for(Long postId : postIds) {
                if(!exists(KeyUtils.postView(postId, KeyPostfix.likes.name())))
                    results.put(postId, null);
                else
                    results.put(postId, opsForSet().isMember(KeyUtils.postView(postId, KeyPostfix.likes.name()), userId.toString()));
            }
        }
        catch(Exception e) {
        }
        return results;
    }
    
    @Override
    public int batchDeleteByPostIds(List<Long> postIds) {   
        int result = 0;
        if(postIds == null || postIds.size() <= 0)
            return result;
        
        List<String> keys = new ArrayList<String>();
        for(Long postId : postIds) {
            keys.add(KeyUtils.postView(postId, KeyPostfix.view.name()));
        }
        
        List<String> posts = null;
        try {
            posts = opsForValue().multiGet(keys);
        }
        catch (Exception e) {
        }
        
        if(posts == null || posts.size() <= 0)
            return result;
        
        for(String post : posts) {
            if(post == null)
                continue;
            
            try {
                MainPostSimpleWrapper postView = objectMapper.readValue(post, MainPostSimpleWrapper.class);
                postView.setIsDeleted(true);
                String postJson = objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(post);
                opsForValue().set(KeyUtils.postView(postView.getPostId(), KeyPostfix.view.name()), postJson, POST_VIEW_CACHE_DURATION, POST_VIEW_CACHE_UNIT);
                result++;
            } catch (Exception e) {
            }
        }
        return result;
    }
}
