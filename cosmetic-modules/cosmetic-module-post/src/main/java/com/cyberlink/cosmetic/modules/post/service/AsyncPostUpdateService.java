package com.cyberlink.cosmetic.modules.post.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface AsyncPostUpdateService {
    
    void asyncRun(Runnable r);
    
    void runLoadPostLike(ArrayList<Long> postIds);
    
    void runLoadPostView(ArrayList<Long> postIds);
    
    void increaseUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount, Long yclLookCount);
    
    void decreaseUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount, Long yclLookCount);
    
    void setUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount, Long yclLookCount);
    
    void changeUserAttr(Long userId, Long likePostCount, Long postCount, Long likeCount, Long yclLookCount, Integer type);
    
    void insertRelatedPost(Long postId);
    
    void cleanPost(Long creatorId, Boolean isSecret, List<Long> postIds);
    
    void cleanPostByCircle(Long userId, Boolean isSecret, Long circleId);

    Map<Integer, Boolean> getWorkerStatus();

    void wakeUpWorker();

    Integer getTaskCount();

    void clearAllTask();

}
