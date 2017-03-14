package com.cyberlink.cosmetic.modules.post.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.post.service.FeedService;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

public class FeedServiceImpl extends AbstractService implements FeedService {
    private SubscribeDao subscribeDao;
    private CircleSubscribeDao circleSubscribeDao;
    private UserDao userDao;
    private PostDao postDao;
    
    public void setSubscribeDao(SubscribeDao subscribeDao) {
        this.subscribeDao = subscribeDao;
    }

    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    public void setCircleSubscribeDao(CircleSubscribeDao circleSubscribeDao) {
        this.circleSubscribeDao = circleSubscribeDao;
    }
    
    public PageResult<Post> listCLFeed(List<String> locale, BlockLimit blockLimit) {
        List<PostStatus> postStatus = new ArrayList<PostStatus>();
        postStatus.add(PostStatus.Published);
        postStatus.add(PostStatus.Review);
        
        List<Long> creators = new ArrayList<Long>();
        creators.addAll(userDao.findIdByUserType(UserType.CL, locale));
        
        if (creators.size() == 0)
            return new PageResult<Post>();
        
        return postDao.findPostByCLUsers(creators, postStatus, blockLimit);
    }
    
    public Integer listCLFeedView(List<String> locale, List<Long> result, BlockLimit blockLimit) {
        List<PostStatus> postStatus = new ArrayList<PostStatus>();
        postStatus.add(PostStatus.Published);
        postStatus.add(PostStatus.Review);
        
        List<Long> creators = new ArrayList<Long>();
        creators.addAll(userDao.findIdByUserType(UserType.CL, locale));
        
        if (creators.size() == 0)
            return 0;
        return postDao.findPostIdsByCLUsers(creators, postStatus, result, blockLimit);
    }
    
    public PageResult<Post> listMyFeed(Long userId, List<String> locale, BlockLimit blockLimit) {
        List<PostStatus> postStatus = new ArrayList<PostStatus>();
        postStatus.add(PostStatus.Published);
        postStatus.add(PostStatus.Review);
        
        Set<Long> creators = new HashSet<Long>();
        Set<Long> circles = new HashSet<Long>();
        if (userId != null) {
            creators.addAll(subscribeDao.findBySubscriber(userId, SubscribeType.User));
            circles.addAll(circleSubscribeDao.findByUserId(userId));
        }
        
        creators.addAll(userDao.findIdByUserType(UserType.CL, locale));
        
        return postDao.findByCreatorOrCircle(creators, circles, blockLimit);
    }
	
	@Override
    public Integer listMyFeedView(Long userId, List<String> locale, List<Long> result, BlockLimit blockLimit) {
        List<PostStatus> postStatus = new ArrayList<PostStatus>();
        postStatus.add(PostStatus.Published);
        postStatus.add(PostStatus.Review);
        
        Set<Long> creators = new HashSet<Long>();
        Set<Long> circles = new HashSet<Long>();
        if (userId != null) {
            creators.addAll(subscribeDao.findBySubscriber(userId, SubscribeType.User));
            circles.addAll(circleSubscribeDao.findByUserId(userId));
        }
        
        creators.addAll(userDao.findIdByUserType(UserType.CL, locale));
        return postDao.findPostViewByCreatorOrCircle(creators, circles, postStatus, result, blockLimit);
    }	
}