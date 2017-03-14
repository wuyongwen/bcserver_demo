package com.cyberlink.cosmetic.action.api.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.post.dao.LikeDao;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetSubType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.AsyncPostUpdateService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserBlockedDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;
import com.cyberlink.cosmetic.modules.user.model.UserBlocked;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.modules.user.model.result.UserInfoWrapper;
import com.cyberlink.cosmetic.modules.user.model.result.UserInfoWrapper.PersonalInfoView;
import com.cyberlink.cosmetic.modules.user.service.UserService;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/info.action")
public class UserInfoAction extends AbstractAction{
    @SpringBean("user.UserDao")
    private UserDao userDao;

    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;

    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("user.userAttrDao")
    private UserAttrDao userAttrDao;
    
    @SpringBean("user.UserBlockedDao")
	private UserBlockedDao userBlockedDao;
    
    @SpringBean("user.userService")
    protected UserService userService;
    
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("post.asyncPostUpdateService")
    private AsyncPostUpdateService asyncPostUpdateService;
    
    @SpringBean("post.LikeDao")
    private LikeDao likeDao;
    
	private Long userId;
    private Long curUserId;
    private Long likePostCount;
    private Long postCount;
    private Long likeCount;
    private Long yclLookCount;
    private Long followerCount;
    private Long followingCount;   
    private Long blockCount;
    private Long liveBrandCount;
    private Integer resetType;
    
    @DefaultHandler
    public Resolution route() {
		Boolean isPrivacy = Boolean.FALSE;
		if (userId == null && curUserId == null && authenticateByRedis()) {
			isPrivacy = Boolean.TRUE;
			userId = getCurrentUserId();
			curUserId = getCurrentUserId();
    	} 
		
        final Map<String, Object> results = new HashMap<String, Object>();

        List<User> userList = userDao.findByIds(userId);
        User user = null;
        if (userList.size() > 0) {
        	user = userList.get(0);
        }
        if (user == null) {
        	return new ErrorResolution(ErrorDef.InvalidUserId);
        }
        String mapAsJson = "{}";
        if (user.hasKeyInAttr("userAttr")) {
        	String userAttr = user.getStringInAttr("userAttr");
        	if (userAttr.length() > 0) {
        		mapAsJson = userAttr;
        	}
        } else {
        	final Map<String, Object> attributes = new HashMap<String, Object>();
        	for (Attribute attr : user.getAttributeList()) {
        		attributes.put(attr.getAttrName(), attr.getAttrValue());
        	}
        	try {
        		mapAsJson = new ObjectMapper().writeValueAsString(attributes);
        	} catch (JsonProcessingException e) {
        	}
        }
        user.setAttribute(mapAsJson);
    	user.setCurUserId(curUserId);
        if (curUserId != null) {
        	List<Subscribe> subs = subscribeDao.findBySubscriberAndSubscribees(curUserId, null, user.getId());
        	if (subs == null || subs.isEmpty())
        		user.setIsFollowed(false);
        	else
        		user.setIsFollowed(true);
        	
			UserBlocked userBlocked = userBlockedDao.findByTargetAndCreater(
					user.getId(), curUserId);
			if (userBlocked != null && !userBlocked.getIsDeleted())
				user.setIsBlocked(Boolean.TRUE);
			else
				user.setIsBlocked(Boolean.FALSE);
        }
        
        Long followerCount = null;
        Long followingCount = null;
        Long blockCount = null;
        Long liveBrandCount = null;
        Long likedPostCount = null;
        Long howToPostCount = null;
        Long likedPost = null;
        Long yclPostCount = null;
        UserAttr userAttr = userAttrDao.findByUserId(userId);
        if(userAttr != null) {
            followerCount = userAttr.getFollowerCount();
            followingCount = userAttr.getFollowingCount();
            blockCount = userAttr.getBlockCount();
            liveBrandCount = userAttr.getLiveBrandCount();
            yclPostCount = userAttr.getYclLookCount();
            likedPost = userAttr.getLikeCount();
            likedPostCount = userAttr.getLikeHowToCount();
            howToPostCount = userAttr.getHowToCount();
        }
        
        Boolean needUpdateUserAttr = false;
        if (blockCount == null) {
        	needUpdateUserAttr = true;
        	blockCount = Long.valueOf(userBlockedDao.findByUserOrderByName(userId, new BlockLimit(0, 0), true).getTotalSize());
        }
        if (followerCount == null) {
        	needUpdateUserAttr = true;
        	followerCount = Long.valueOf(subscribeDao.findBySubscribee(user.getId(), null, new BlockLimit(0, 0)).getTotalSize());
        }
        if (followingCount == null) {
        	needUpdateUserAttr = true;
        	followingCount = Long.valueOf(subscribeDao.findBySubscriber(user.getId(), null, new BlockLimit(0, 0)).getTotalSize());
        }
        if (liveBrandCount == null){
        	needUpdateUserAttr = true;
        	liveBrandCount = Long.valueOf(subscribeDao.findBySubscriber(user.getId(), SubscribeType.LiveBrand, new BlockLimit(0, 0)).getTotalSize());
        }
        if(likedPost == null) {
            needUpdateUserAttr = true;
            likedPost = likeDao.hardGetLikedPostCount(userId, TargetType.Post, TargetSubType.YCL_LOOK);
        }
        if(likedPostCount == null) {
            needUpdateUserAttr = true;
            likedPostCount = likeDao.hardGetLikedPostCount(userId, TargetType.Post, TargetSubType.HOW_TO);
        }
        if(yclPostCount == null) {
            PostApiResult<Integer> lookCountResult = getLookCount(userId, userAttr, PostType.YCL_LOOK);
            if(lookCountResult.success()) {
                needUpdateUserAttr = true;
                yclPostCount = Long.valueOf(lookCountResult.getResult());
            }
        }
        if(howToPostCount == null) {
            PostApiResult<Integer> howToCountResult = getLookCount(userId, userAttr, PostType.HOW_TO);
            if(howToCountResult.success()) {
                needUpdateUserAttr = true;
                howToPostCount = Long.valueOf(howToCountResult.getResult());
            }
        }
        
        if (needUpdateUserAttr) {
        	String url = "/api/user/info.action";
        	List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
            params.add(Pair.of("updateCount", ""));
            params.add(Pair.of("userId", userId.toString()));
            params.add(Pair.of("blockCount", blockCount.toString()));
            params.add(Pair.of("followerCount", followerCount.toString()));
            params.add(Pair.of("followingCount", followingCount.toString()));
            params.add(Pair.of("liveBrandCount", liveBrandCount.toString()));
            params.add(Pair.of("likeCount", likedPost.toString()));
            params.add(Pair.of("yclLookCount", yclPostCount.toString()));
            params.add(Pair.of("postCount", howToPostCount.toString()));
            params.add(Pair.of("likePostCount", likedPostCount.toString()));
            
            AsycnWriteReq r = new AsycnWriteReq(url, params);
            userService.asyncRun(r);
        }
        
        List<Object> eventImageList = new ArrayList<Object>();
        results.put("result", new UserInfoWrapper(user, likedPostCount, howToPostCount, likedPost, yclPostCount, followerCount, followingCount, blockCount, liveBrandCount, eventImageList));
        if (isPrivacy)
        	return json(results, PersonalInfoView.class);
        return json(results);
    }
    
    public Resolution updateCount() {
    	if(userId == null)
            return json("Complete");
    	UserAttr userAttr = userAttrDao.findByUserId(userId);
    	if (userAttr == null) {
    		userAttr = new UserAttr();
    		userAttr.setUserId(userId);
    		userAttr.setBlockCount(blockCount);
    		userAttr.setFollowerCount(followerCount);
    		userAttr.setFollowingCount(followingCount);
    		userAttr.setLiveBrandCount(liveBrandCount);
    		userAttr.setLikeCount(likeCount);
    		userAttr.setLikeHowToCount(likePostCount);
    		userAttr.setYclLookCount(yclLookCount);
    		userAttr.setHowToCount(postCount);
    		userAttrDao.create(userAttr);
    	} else {
    		userAttr.setBlockCount(blockCount);
    		userAttr.setFollowerCount(followerCount);
    		userAttr.setFollowingCount(followingCount);
    		userAttr.setLiveBrandCount(liveBrandCount);
    		userAttr.setLikeCount(likeCount);
    		userAttr.setLikeHowToCount(likePostCount);
            userAttr.setYclLookCount(yclLookCount);
            userAttr.setHowToCount(postCount);
    		userAttrDao.update(userAttr);
    	}
    	
    	return json("Complete");
    }
    
    public Resolution updateLikeCount() {
        if(userId == null)
            return json("Complete");
        asyncPostUpdateService.changeUserAttr(userId, likePostCount, postCount, likeCount, yclLookCount, resetType);
        return json("Complete");
    }
    
    private PostApiResult<Integer> getLookCount(Long userId, UserAttr userAttr, PostType postType) {
        List<Long> lookPostList = new ArrayList<Long>();
        BlockLimit lookPostLimit = new BlockLimit(0, 1);
        return postService.listLookPostByUser(userId, userAttr, postType, null, false, lookPostList, lookPostLimit);
    }
    
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}	

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}
    
    public Long getLikePostCount() {
        return likePostCount;
    }

    public void setLikePostCount(Long likePostCount) {
        this.likePostCount = likePostCount;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }
    
    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getYclLookCount() {
        return yclLookCount;
    }

    public void setYclLookCount(Long yclLookCount) {
        this.yclLookCount = yclLookCount;
    }
    
    public Long getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(Long followerCount) {
		this.followerCount = followerCount;
	}

	public Long getFollowingCount() {
		return followingCount;
	}

	public void setFollowingCount(Long followingCount) {
		this.followingCount = followingCount;
	}

    public Long getBlockCount() {
		return blockCount;
	}

	public void setBlockCount(Long blockCount) {
		this.blockCount = blockCount;
	}

	public Long getLiveBrandCount() {
		return liveBrandCount;
	}

	public void setLiveBrandCount(Long liveBrandCount) {
		this.liveBrandCount = liveBrandCount;
	}

	public Integer getResetType() {
        return resetType;
    }

    public void setResetType(Integer resetType) {
        this.resetType = resetType;
    }
}
