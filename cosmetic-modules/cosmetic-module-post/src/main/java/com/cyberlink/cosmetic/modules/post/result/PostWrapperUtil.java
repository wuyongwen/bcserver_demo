package com.cyberlink.cosmetic.modules.post.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.repository.PostViewRepository;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Creator;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.DPWCircle;
import com.cyberlink.cosmetic.modules.post.service.AsyncPostUpdateService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.repository.FollowRepository;
import com.cyberlink.cosmetic.statsd.StatsDUpdater;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PostWrapperUtil {
    private static PostService postService = BeanLocator.getBean("post.PostService");
    private static AsyncPostUpdateService asyncPostUpdateService = BeanLocator.getBean("post.asyncPostUpdateService");
    private static LikeService likeService = BeanLocator.getBean("post.LikeService");
    private static SubscribeDao subscribeDao = BeanLocator.getBean("user.SubscribeDao");
    private static PostViewRepository postViewRepository = BeanLocator.getBean("post.postViewRepository");
    private static FollowRepository followRepository = BeanLocator.getBean("user.followRepository");
    private static PostViewDao postViewDao = BeanLocator.getBean("post.PostViewDao");
    private static ObjectMapper objectMapper = BeanLocator.getBean("web.objectMapper");
    private static UserDao userDao = BeanLocator.getBean("user.UserDao");
    private static CircleDao circleDao = BeanLocator.getBean("circle.circleDao");
    private static LookTypeDao lookTypeDao = BeanLocator.getBean("look.LookTypeDao");
    
	static public List<MainPostSimpleWrapper> wrapSimplePostResult(List<Post> posts, Long curUserId, Boolean defaultIsLiked, Boolean defaultIsFollowed) {
		final List<MainPostSimpleWrapper> r = new ArrayList<MainPostSimpleWrapper>();
		List<Long> postIds = new ArrayList<Long>(0);
        Set<Long> creatorIds = new HashSet<Long>();
        Set<Long> lookTypeIds = new HashSet<Long>();
        for(Post c : posts) {
            postIds.add(c.getId());
            creatorIds.add(c.getCreator().getId());
            if(c.getLookTypeId() != null)
                lookTypeIds.add(c.getLookTypeId());
        }
        
        Map<Long, Map<PostAttrType, Long>> postAttrMap = postService.listPostsAttr(postIds);
        Map<Long, User> postCircleInSourceUsers = postService.listCircleInSourceUserByPosts(postIds);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(posts, ThumbnailType.Detail);//.List);
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(posts);
        Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        if(curUserId != null) {
            List<Long> likedPost = null;
            if(defaultIsLiked == null)
                likedPost = likeService.getLikeTarget(curUserId, TargetType.Post, postIds);
            Set<Long> subcribeeIds = null;
            if(defaultIsFollowed == null)
                subcribeeIds = subscribeDao.findIdBySubscriberAndSubscribees(curUserId, SubscribeType.User, creatorIds.toArray(new Long[creatorIds.size()]));
            for (final Post p : posts) {
                p.getCreator().setCurUserId(curUserId);
                LookType lt = lookTypeMap.get(p.getLookTypeId());
                MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, postFileItems.get(p.getId()), postCircles.get(p.getId()), lt);
                if(defaultIsLiked != null)
                    pw.setIsLiked(defaultIsLiked);
                else if(likedPost.contains(p.getId()))
                    pw.setIsLiked(true);
                if(defaultIsFollowed != null)
                    pw.getCreator().setIsFollowed(defaultIsFollowed);
                else if(subcribeeIds.contains(p.getCreator().getId()))
                    pw.getCreator().setIsFollowed(true);
                if(postAttrMap.containsKey(p.getId())) {
                    Map<PostAttrType, Long> attrs = postAttrMap.get(p.getId());
                    if(attrs.containsKey(PostAttribute.PostAttrType.PostLikeCount))
                        pw.setLikeCount(attrs.get(PostAttribute.PostAttrType.PostLikeCount));
                    if(attrs.containsKey(PostAttribute.PostAttrType.PostCommentCount))
                        pw.setCommentCount(attrs.get(PostAttribute.PostAttrType.PostCommentCount));
                    if(attrs.containsKey(PostAttribute.PostAttrType.PostCircleInCount))
                        pw.setCircleInCount(attrs.get(PostAttribute.PostAttrType.PostCircleInCount));
                    if(attrs.containsKey(PostAttribute.PostAttrType.LookDownloadCount))
                        pw.setLookDownloadCount(attrs.get(PostAttribute.PostAttrType.LookDownloadCount));
                }
                if(postCircleInSourceUsers.containsKey(p.getId()))
                    pw.setSourcePostCreatorByUser(postCircleInSourceUsers.get(p.getId()));
                r.add(pw);
            }
        }
        else {
            for (final Post p : posts) {
                LookType lt = lookTypeMap.get(p.getLookTypeId());
                MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, postFileItems.get(p.getId()), postCircles.get(p.getId()), lt);
                if(postAttrMap.containsKey(p.getId())) {
                    Map<PostAttrType, Long> attrs = postAttrMap.get(p.getId());
                    if(attrs.containsKey(PostAttribute.PostAttrType.PostLikeCount))
                        pw.setLikeCount(attrs.get(PostAttribute.PostAttrType.PostLikeCount));
                    if(attrs.containsKey(PostAttribute.PostAttrType.PostCommentCount))
                        pw.setCommentCount(attrs.get(PostAttribute.PostAttrType.PostCommentCount));
                    if(attrs.containsKey(PostAttribute.PostAttrType.PostCircleInCount))
                        pw.setCircleInCount(attrs.get(PostAttribute.PostAttrType.PostCircleInCount));
                    if(attrs.containsKey(PostAttribute.PostAttrType.LookDownloadCount))
                        pw.setLookDownloadCount(attrs.get(PostAttribute.PostAttrType.LookDownloadCount));
                }
                if(defaultIsLiked != null)
                    pw.setIsLiked(defaultIsLiked);
                if(defaultIsFollowed != null)
                    pw.getCreator().setIsFollowed(defaultIsFollowed);
                if(postCircleInSourceUsers.containsKey(p.getId()))
                    pw.setSourcePostCreatorByUser(postCircleInSourceUsers.get(p.getId()));
                r.add(pw);
            }
        }       
        return r;	
	}
	
	static public PageResult<MainPostSimpleWrapper> wrapSimplePostResult(PageResult<Post> posts, Long curUserId, Boolean defaultIsLiked, Boolean defaultIsFollowed) {
        final PageResult<MainPostSimpleWrapper> r = new PageResult<MainPostSimpleWrapper>();
        r.setTotalSize(posts.getTotalSize());
        r.setResults(wrapSimplePostResult(posts.getResults(), curUserId, defaultIsLiked, defaultIsFollowed));
        return r;
    }

	static public MainPostSimpleWrapper wrapSimplePostResult(Post post, Long curUserId, Boolean defaultIsLiked, Boolean defaultIsFollowed) {
        List<Post> posts = new ArrayList<Post>();
        posts.add(post);
        List<MainPostSimpleWrapper> result = wrapSimplePostResult(posts, curUserId, defaultIsLiked, defaultIsFollowed);
        if (result.size() > 0)
        	return result.get(0);
        return null;
    }

	private static PostView getPostView(Boolean isWriteable, Long postId, Long creatorId, String mainPost, String subPosts, PostViewAttr attribute) {
        PostView tmp = null;
        if(isWriteable) {
            try {
                tmp = postViewDao.createOrUpdate(postId, creatorId, mainPost, subPosts, attribute);
            }
            catch(Exception e) {
                tmp = new PostView();
                tmp.setPostId(postId);
                tmp.setMainPost(mainPost);
                tmp.setSubPosts(subPosts);
                tmp.setAttribute(attribute);
            }
        }
        else {
            tmp = new PostView();
            tmp.setPostId(postId);
            tmp.setMainPost(mainPost);
            tmp.setSubPosts(subPosts);
            tmp.setAttribute(attribute);
        }
        return tmp;
    }
    
    static public void quickWrappSimplePostResult(Map<Long, MainPostSimpleWrapper> mpswMap, 
            final ArrayList<Long> postIds, final Set<Long> creatorIds, final Set<Long> toLoadUserIds, 
            final Set<Long> toLoadCircleIds, final Set<Long> toCheckDeleteId, final PageResult<MainPostSimpleWrapper> r,
            Integer totalSize, Long curUserId, Boolean defaultIsLiked, Boolean defaultIsFollowed) {
        
        Map<Long, Creator> loadedUser = postViewRepository.getCreators(new ArrayList<Long>(toLoadUserIds));
        Map<Long, DPWCircle> loadedCircle = postViewRepository.getCircles(new ArrayList<Long>(toLoadCircleIds));
        Map<Long, Post> loadedCheckDeletePost = new HashMap<Long, Post>();
        if(toCheckDeleteId.size() > 0) {
            List<Post> posts = postService.findPostByIds(new ArrayList<Long>(toCheckDeleteId));
            ArrayList<Long> toUpdatePostView = new ArrayList<Long>();
            for(Post p : posts) {
                loadedCheckDeletePost.put(p.getId(), p);
                toUpdatePostView.add(p.getId());
            }
            asyncPostUpdateService.runLoadPostView(toUpdatePostView);
        }
        for(Long creatorId : loadedUser.keySet()) {
            if(loadedUser.get(creatorId) != null)
                toLoadUserIds.remove(creatorId);
        }
        for(Long circleId : loadedCircle.keySet()) {
            if(loadedCircle.get(circleId) != null)
                toLoadCircleIds.remove(circleId);
        }
        if(toLoadUserIds.size() > 0) {
            Map<Long, User> loadedBcUser = userDao.findUserMap(toLoadUserIds);
            for(Long uId : loadedBcUser.keySet()) {
                User u = loadedBcUser.get(uId);
                Creator postCreator = new Creator(u, null);
                loadedUser.put(uId, postCreator);
                postViewRepository.createOrUpdatePostViewUser(postCreator.userId, postCreator.avatar, postCreator.userType, postCreator.cover, postCreator.description, postCreator.displayName);
            }
        }
        if(toLoadCircleIds.size() > 0) {
            Map<Long, Circle> loadedBcCircle = circleDao.findCircleMap(toLoadCircleIds);
            for(Long cId : loadedBcCircle.keySet()) {
                Circle c = loadedBcCircle.get(cId);
                DPWCircle postCircle = new DPWCircle(c, null);
                loadedCircle.put(cId, postCircle);
                postViewRepository.createOrUpdatePostViewCircle(cId, postCircle.circleName, postCircle.getDisplay());
            }
        }
        
        List<MainPostSimpleWrapper> toReturnPost = new ArrayList<MainPostSimpleWrapper>();
        if(curUserId != null) {
            List<Long> likedPost = null;
            if(defaultIsLiked == null) {
                likedPost = new ArrayList<Long>();
                if(!Constants.getRedisLikeEnable()) {
                    Map<Long, Boolean> likedMap = postViewRepository.getLikes(postIds, curUserId);
                    for(Long postId : likedMap.keySet()) {
                        Boolean isLiked = likedMap.get(postId);
                        if(isLiked != null) {
                            postIds.remove(postId);
                            if(isLiked)
                                likedPost.add(postId);
                        }
                    }
                    if(postIds != null && postIds.size() > 0) {
                        likedPost.addAll(likeService.getLikeTarget(curUserId, TargetType.Post, postIds));
                        asyncPostUpdateService.runLoadPostLike(postIds);
                    }
                }
                else {
                    likedPost.addAll(likeService.getLikeTarget(curUserId, TargetType.Post, postIds));
                }
            }
            Set<String> followeeIds = null;
            if(defaultIsFollowed == null) {
                followeeIds = followRepository.getUserFollowing(curUserId);
            }
            for (final MainPostSimpleWrapper pw : r.getResults()) {
                Creator postCreator = pw.getCreator();
                if(postCreator != null && loadedUser.containsKey(postCreator.getUserId())) {
                    pw.creator = loadedUser.get(postCreator.getUserId());
                }
                Creator sourcePostCreator = pw.getSourcePostCreator();
                if(sourcePostCreator != null && loadedUser.containsKey(sourcePostCreator.getUserId())) {
                    pw.setSourcePostCreator(loadedUser.get(sourcePostCreator.getUserId()));
                }
                
                List<DPWCircle> dwpCircles = pw.getCircles();
                List<DPWCircle> updatedwpCircles = new ArrayList<DPWCircle>();
                if(dwpCircles != null) {
                    Boolean validCircle = true;
                    for(DPWCircle dwpCir : dwpCircles) {
                        if(loadedCircle.containsKey(dwpCir.circleId))
                            dwpCir = loadedCircle.get(dwpCir.circleId);
                        if(dwpCir == null || !dwpCir.getDisplay()) {
                            validCircle = false;
                            break;
                        }
                        updatedwpCircles.add(dwpCir);
                    }
                    if(!validCircle)
                        continue;
                    pw.setCircles(updatedwpCircles);
                }
                
                if(defaultIsLiked != null)
                    pw.setIsLiked(defaultIsLiked);
                else if(likedPost.contains(pw.getPostId()))
                    pw.setIsLiked(true);
                if(defaultIsFollowed != null)
                    pw.getCreator().setIsFollowed(defaultIsFollowed);
                else if(followeeIds != null && followeeIds.contains(pw.getCreator().getUserId().toString()))
                    pw.getCreator().setIsFollowed(true);
                if(loadedCheckDeletePost.containsKey(pw.getPostId())) {
                    Post reloadedPost = loadedCheckDeletePost.get(pw.getPostId());
                    if(reloadedPost.getIsDeleted() != null && !reloadedPost.getIsDeleted())
                        toReturnPost.add(pw);
                }
                else
                    toReturnPost.add(pw);
            }
        }
        else {
            for (final MainPostSimpleWrapper pw : r.getResults()) {
                Creator postCreator = pw.getCreator();
                if(postCreator != null && loadedUser.containsKey(postCreator.getUserId())) {
                    pw.creator = loadedUser.get(postCreator.getUserId());
                }
                Creator sourcePostCreator = pw.getSourcePostCreator();
                if(sourcePostCreator != null && loadedUser.containsKey(sourcePostCreator.getUserId())) {
                    pw.setSourcePostCreator(loadedUser.get(sourcePostCreator.getUserId()));
                }

                List<DPWCircle> dwpCircles = pw.getCircles();
                List<DPWCircle> updatedwpCircles = new ArrayList<DPWCircle>();
                if(dwpCircles != null) {
                    Boolean validCircle = true;
                    for(DPWCircle dwpCir : dwpCircles) {
                        if(loadedCircle.containsKey(dwpCir.circleId))
                            dwpCir = loadedCircle.get(dwpCir.circleId);
                        if(dwpCir == null || !dwpCir.getDisplay()) {
                            validCircle = false;
                            break;
                        }
                        updatedwpCircles.add(dwpCir);
                    }
                    if(!validCircle)
                        continue;
                    pw.setCircles(updatedwpCircles);
                }
                
                if(defaultIsLiked != null)
                    pw.setIsLiked(defaultIsLiked);
                if(defaultIsFollowed != null)
                    pw.getCreator().setIsFollowed(defaultIsFollowed);
                if(loadedCheckDeletePost.containsKey(pw.getPostId())) {
                    Post reloadedPost = loadedCheckDeletePost.get(pw.getPostId());
                    if(reloadedPost.getIsDeleted() != null && !reloadedPost.getIsDeleted())
                        toReturnPost.add(pw);
                }
                else
                    toReturnPost.add(pw);
            }
        }
        
        r.setResults(toReturnPost);
        r.setTotalSize(totalSize);
    }
    
	static public void fillMissingPost(List<Long> toLoadPostIds, Map<Long, MainPostSimpleWrapper> result, WrappingPostCallBack wrappingCallback) {
	    if(toLoadPostIds == null || toLoadPostIds.size() <= 0)
            return;
        	    
        ArrayList<Long> missPostIds = new ArrayList<Long>();
        Map<Long, PostView> postViews = postViewDao.getViewMapByPostIds(toLoadPostIds);
        for(Long id : postViews.keySet()) {
            PostView v = postViews.get(id);
            if(v == null) {
                missPostIds.add(id);
                continue;
            }
            try {
                MainPostSimpleWrapper mpsw = objectMapper.readValue(v.getMainPost(), MainPostSimpleWrapper.class);
                PostViewAttr pAttr = v.getAttribute();
                if(pAttr != null) {
                    mpsw.setLikeCount(pAttr.getLikeCount());
                    mpsw.setCommentCount(pAttr.getCommentCount());
                    mpsw.setCircleInCount(pAttr.getCircleInCount());
                    mpsw.setLookDownloadCount(pAttr.getLookDownloadCount());
                }
                    
                result.put(v.getPostId(), mpsw);
                postViewRepository.createOrUpdatePostView(v.getPostId(), 
                        objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(mpsw));
                wrappingCallback.each(mpsw);
            } catch (Exception e) {
                missPostIds.add(id);
            }
        }
        
        if(missPostIds.size() > 0) {
            List<Post> missPosts = postService.findPostByIds(missPostIds);
            PageResult<Post> missPostResult = new PageResult<Post>();
            missPostResult.setResults(missPosts);
            missPostResult.setTotalSize(missPosts.size());
            PageResult<MainPostSimpleWrapper> r = PostWrapperUtil.wrapSimplePostResult(missPostResult, null, null, null);
            Boolean isWriteable = Constants.getWebsiteIsWritable().equals("true"); 
            for(MainPostSimpleWrapper tmp : r.getResults()) {
                result.put(tmp.getPostId(), tmp);
                if(wrappingCallback != null)
                    wrappingCallback.each(tmp);
                try {
                    String mainPostView = objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(tmp);
                    PostViewAttr pAttr = new PostViewAttr();
                    pAttr.setLikeCount(tmp.getLikeCount());
                    pAttr.setCommentCount(tmp.getCommentCount());
                    pAttr.setCircleInCount(tmp.getCircleInCount());
                    pAttr.setLookDownloadCount(tmp.getLookDownloadCount());
                    PostView tmpView = getPostView(isWriteable, tmp.getPostId(), tmp.getCreator().getUserId(), mainPostView, null, pAttr);
                    postViews.put(tmpView.getPostId(), tmpView);
                } catch (JsonProcessingException e) {
                }
            }
            if(!isWriteable)
                asyncPostUpdateService.runLoadPostView(missPostIds);
        }
	}
	
	public interface WrappingPostCallBack {
	    void each(MainPostSimpleWrapper mpsw);
	}
	
	static public PageResult<MainPostSimpleWrapper> feedPostsToSimplePostResult(List<Long> feedPostIds, Integer totalSize, Long curUserId, Boolean defaultIsLiked, Boolean defaultIsFollowed) {
        final long begin = System.currentTimeMillis();
        final ArrayList<Long> postIds = new ArrayList<Long>(0);
        final Set<Long> creatorIds = new HashSet<Long>();
        final Set<Long> toLoadUserIds = new HashSet<Long>();
        final Set<Long> toLoadCircleIds = new HashSet<Long>();
        final Set<Long> toCheckDeleteId = new HashSet<Long>();
        final PageResult<MainPostSimpleWrapper> r = new PageResult<MainPostSimpleWrapper>();
        
        WrappingPostCallBack wrappingCallback = new WrappingPostCallBack() {

            @Override
            public void each(MainPostSimpleWrapper mpsw) {
                if(mpsw == null)
                    return;
                postIds.add(mpsw.getPostId());
                creatorIds.add(mpsw.getCreator().getUserId());
                toLoadUserIds.add(mpsw.getCreator().getUserId());
                Creator sourPostCreator = mpsw.getSourcePostCreator();
                if(sourPostCreator != null) {
                    toLoadUserIds.add(sourPostCreator.getUserId());
                }
                List<DPWCircle> dpwCircles = mpsw.getCircles();
                if(dpwCircles != null) {
                    for(DPWCircle cTmp : dpwCircles)
                        toLoadCircleIds.add(cTmp.circleId);
                }
                
                if(mpsw.getIsDeleted() == null) {
                    r.add(mpsw);
                    toCheckDeleteId.add(mpsw.getPostId());
                }
                else if(!mpsw.getIsDeleted())
                    r.add(mpsw);
            }
        };
        
        List<Long> missingPostIds = new ArrayList<Long>();
        Map<Long, MainPostSimpleWrapper> result = postViewRepository.getPostsByFeed(feedPostIds, missingPostIds, wrappingCallback);
        fillMissingPost(missingPostIds, result, wrappingCallback);
        quickWrappSimplePostResult(result, postIds, creatorIds, toLoadUserIds, toLoadCircleIds, toCheckDeleteId, r, totalSize, curUserId, defaultIsLiked, defaultIsFollowed);
        return r;
    }
}
