package com.cyberlink.cosmetic.action.api.feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.post.AbstractPostAction;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.service.FeedService;
import com.cyberlink.cosmetic.modules.feed.repository.FeedNotifyRepository;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/api/feed/list-my-feed.action")
public class ListMyFeed extends AbstractPostAction {
    @SpringBean("post.feedService")
    private FeedService feedService;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("look.LookTypeDao")
    private LookTypeDao lookTypeDao;
    
    @SpringBean("feed.feedNotifyRepository")
    private FeedNotifyRepository feedNotifyRepository;
    
    private Long userId;
    private List<String> locale;
    private Integer offset = 0;
    private Long next;
    private Integer limit = 10;
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public List<String> getLocale() {
        return locale;
    }
    
    public void setLocale(List<String> locale) {
        this.locale = locale;
    }
    
    public Integer getOffset() {
        return offset;
    }
    
    @Validate(minvalue = 0, required = false, on = "route")
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
    
    public void setNext(Long next) {
        this.next = next;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    @Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public Resolution listCLFeed_postView() {
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("createdTime", false);
        List<Long> resultList = new ArrayList<Long>();
        Integer totalSize = feedService.listCLFeedView(locale, resultList, blockLimit);
        PageResult<MainPostSimpleWrapper> result = postIdToPostView(totalSize, resultList, userId, null, null);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("results", result.getResults());
        resultMap.put("totalSize", result.getTotalSize());
        resultMap.put("next", offset + limit);
        return json(resultMap);
    }
    
    public Resolution listCLFeed() {
        if(Constants.getIsPostCacheView())
            return listCLFeed_postView();
        
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("createdTime", false);
        final PageResult<Post> posts = feedService.listCLFeed(locale, blockLimit);
        final PageResult<MainPostSimpleWrapper> r = new PageResult<MainPostSimpleWrapper>();
        r.setTotalSize(posts.getTotalSize());
        Set<Long> lookTypeIds = new HashSet<Long>();
        
        List<Long> postIds = new ArrayList<Long>(0);
        for(Post c : posts.getResults()) {
            postIds.add(c.getId());     
            if(c.getLookTypeId() != null)
                lookTypeIds.add(c.getLookTypeId());
        }
        
        Map<Long, Map<PostAttrType, Long>> postAttrMap = postService.listPostsAttr(postIds);
        Map<Long, User> postCircleInSourceUsers = postService.listCircleInSourceUserByPosts(postIds);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(posts.getResults(), ThumbnailType.List);
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(posts.getResults());       
        Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        for (final Post p : posts.getResults()) {
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
            if(postCircleInSourceUsers.containsKey(p.getId()))
                pw.setSourcePostCreatorByUser(postCircleInSourceUsers.get(p.getId()));
            r.add(pw);
        }
                
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("results", r.getResults());
        resultMap.put("totalSize", r.getTotalSize());
        resultMap.put("next", offset + limit);
        return json(resultMap);
    }

    public Resolution route_postView() {
        List<Long> resultList = new ArrayList<Long>();
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("createdTime", false);
        Integer totalSize = feedService.listMyFeedView(userId, locale, resultList, blockLimit);
        
        PageResult<MainPostSimpleWrapper> result = postIdToPostView(totalSize, resultList, userId, null, null);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("results", result.getResults());
        resultMap.put("totalSize", result.getTotalSize());
        resultMap.put("next", offset + limit);
        return json(resultMap);
    }
    
    @DefaultHandler
    public Resolution route() {
        if(next != null)
            offset = next.intValue();
        
        if(userId == null)
            return listCLFeed();
        
        feedNotifyRepository.removeNewFeedNotify(userId);
        if(Constants.getIsPostCacheView())
            return route_postView();
        
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("createdTime", false);
        final PageResult<Post> postPageResult = feedService.listMyFeed(userId, locale, blockLimit);
        final PageResult<MainPostSimpleWrapper> r = new PageResult<MainPostSimpleWrapper>();
        r.setTotalSize(postPageResult.getTotalSize());
        
        List<Long> postIds = new ArrayList<Long>(0);
        Set<Long> lookTypeIds = new HashSet<Long>();
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(postPageResult.getResults());       
        for(Post p : postPageResult.getResults()) {
            postIds.add(p.getId());
            
            if (userId != null && p.getCreator().getId().equals(userId))
                p.getCreator().setCurUserId(userId);
            else
                p.getCreator().setIsFollowed(true);
            
            if(p.getLookTypeId() != null)
                lookTypeIds.add(p.getLookTypeId());
            
        }
        
        Map<Long, Map<PostAttrType, Long>> postAttrMap = postService.listPostsAttr(postIds);
        Map<Long, User> postCircleInSourceUsers = postService.listCircleInSourceUserByPosts(postIds);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(postPageResult.getResults(), ThumbnailType.Detail);//.List);
        List<Long> likedComment = likeService.getLikeTarget(userId, TargetType.Post, postIds);
        Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        for (final Post p : postPageResult.getResults()) {
            LookType lt = lookTypeMap.get(p.getLookTypeId());
            MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, postFileItems.get(p.getId()), postCircles.get(p.getId()), lt);
            if(likedComment.contains(p.getId()))
                pw.setIsLiked(true);
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
                
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("results", r.getResults());
        resultMap.put("totalSize", r.getTotalSize());
        resultMap.put("next", offset + limit);
        return json(resultMap);
    }
}
