package com.cyberlink.cosmetic.action.backend.feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.service.FeedService;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/feed/feed-manage.action")
public class FeedManageAction extends AbstractAction {
    @SpringBean("post.feedService")
    private FeedService feedService;
    
    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("look.LookTypeDao")
    private LookTypeDao lookTypeDao;
    
    private static final String errorMessage = "You aren't an administrator";
    
    private Long userId;
    private PageResult<MainPostSimpleWrapper> pageResult;
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PageResult<MainPostSimpleWrapper> getPageResult() {
        return pageResult;
    }

    @DefaultHandler
    public Resolution list() {
        if(!getCurrentUserAdmin())
            return new StreamingResolution("text/html", errorMessage);
        
        if(userId == null)
            userId = getCurrentUserId();

        if (!userDao.exists(userId))
            return new StreamingResolution("text/html", "Invalid UserId");

        final User user = userDao.findById(userId);
        final List<String> locale = new ArrayList<String>(localeDao.getLocaleByType(user.getRegion(), LocaleType.USER_LOCALE));
        
        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit(pageLimit.getStartIndex(), pageLimit.getPageSize());
        blockLimit.addOrderBy("createdTime", false);
        final PageResult<Post> postPageResult = feedService.listMyFeed(userId, locale, blockLimit);
        pageResult = new PageResult<MainPostSimpleWrapper>();
        pageResult.setTotalSize(postPageResult.getTotalSize());
        
        List<Long> postIds = new ArrayList<Long>(0);
        Set<Long> lookTypeIds = new HashSet<Long>();
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(postPageResult.getResults());
        for(Post c : postPageResult.getResults()) {
            postIds.add(c.getId());
            if (userId != null && c.getCreator().getId().equals(userId))
                c.getCreator().setCurUserId(userId);
            else
                c.getCreator().setIsFollowed(true);
            
            if(c.getLookTypeId() != null)
                lookTypeIds.add(c.getLookTypeId());
        }
        
        Map<Long, Long> postLikedCount = likeService.checkLikeCount(PostTargetType.POST, postIds);
        Map<Long, Long> postCommentCount = commentService.checkCommentCount(PostTargetType.POST, postIds);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(postPageResult.getResults(), ThumbnailType.Detail);//.List);        
        List<Long> likedComment = likeService.getLikeTarget(userId, TargetType.Post, postIds);
        Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        for (final Post p : postPageResult.getResults()) {
            LookType lt = lookTypeMap.get(p.getLookTypeId());
            MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, postFileItems.get(p.getId()), postCircles.get(p.getId()), lt);
            if(likedComment.contains(p.getId()))
                pw.setIsLiked(true);
            if(postLikedCount.containsKey(p.getId()))
                pw.setLikeCount(postLikedCount.get(p.getId()));
            if(postCommentCount.containsKey(p.getId()))
                pw.setCommentCount(postCommentCount.get(p.getId()));
            pageResult.add(pw);
        }
        
        return forward();
    }
    
    private String getDbLocale(String requestLocale){
        if( requestLocale == null ){
            return "en_US" ;
        }
        String dbLocale ;
        switch( requestLocale ){
            case "en_US" :
            case "en-US" :
            case "US" :
                dbLocale = "en_US";
                break;
            case "en_CA" :
            case "en-CA" :
            case "CA" :
            case "fr_CA" :
            case "fr-CA" :
                dbLocale = "en_CA";
                break;
            case "en_GB" :
            case "en-GB" :
            case "GB" :
                dbLocale = "en_GB";
                break;
            case "ja_JP" :
            case "ja-JP" :
            case "JP" :
                dbLocale = "ja_JP";
                break;
            case "de_DE" :
            case "de-DE" :
            case "DE" :
                dbLocale = "de_DE";
                break;
            case "fr_FR" :
            case "fr-FR" :
            case "FR" :
                dbLocale = "fr_FR";
                break;
            case "zh_TW" :
            case "zh-TW" :
            case "TW" :
                dbLocale = "zh_TW";
                break;
            case "zh_CN" :
            case "zh-CN" :
            case "CN" :
            case "zh_HK" :
            case "zh-HK" :
            case "HK" :
                dbLocale = "zh_CN";
                break;
            default:
                dbLocale = "en_US" ;
                break;
        }
        return dbLocale;
    }
}
