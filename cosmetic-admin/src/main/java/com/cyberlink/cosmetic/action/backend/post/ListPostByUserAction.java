package com.cyberlink.cosmetic.action.backend.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.google.common.collect.ImmutableList;

@UrlBinding("/post/listUserPost.action")
public class ListPostByUserAction extends AbstractAction {
    @SpringBean("post.PostDao")
    private PostDao postDao;

    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("circle.circleDao")
    private CircleDao circleDao;
    
    @SpringBean("circle.circleTypeDao")
    private CircleTypeDao circleTypeDao;
    
    @SpringBean("circle.circleTagDao")
    private CircleTagDao circleTagDao;
    
    @SpringBean("circle.circleTagGroupDao")
    private CircleTagGroupDao circleTagGroupDao;

    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("file.fileDao")
    private FileDao fileDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("look.LookTypeDao")
    private LookTypeDao lookTypeDao;
    
    private List<Long> userIds = null;
    private Long curUserId = (long)0;
    private Boolean isLogin = false;
    private UserType userType = UserType.Normal;
    private Long deleteId;
    private Boolean isOwnerUser = false;
    private String pageType = "cl";
    private String locale = null;
    private String userLocale = "en_US";
    private List<String> availableRegion = new ArrayList<String>(0);
    private List<String> availablePostStatus = new ArrayList<String>(0);
    private String postStatus = PostStatus.Published.toString();
    private Boolean isAdmin = false;
    private String postType = "CL";
    private Long searchCreatorId;
    private List<Circle> circles;
    
    public String getPageType() {
        return pageType;
    }
    
    public void setPageType(String pageType) {
        this.pageType = pageType;
    }
    
    public Boolean getIsOwnerUser() {
        return isOwnerUser;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }
    
    public void setIsOwnerUser(Boolean isOwnerUser) {
        this.isOwnerUser = isOwnerUser;
    }

    public Long getDeleteId() {
        return deleteId;
    }

    public void setDeleteId(Long deleteId) {
        this.deleteId = deleteId;
    }

    public void setUserId(List<Long> userIds) {
        this.userIds = userIds;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public Boolean getIsLogin() {
        return isLogin;
    }
    
    public UserType getUserType() {
        return userType;
    }
    
    public Long getCurUserId() {
        return curUserId;
    }
    
    public void setPostStatus (String postStatus) {
        this.postStatus = postStatus;
    }
    
    public void setPostType(String postType) {
        this.postType = postType;
    }
    
    public String getPostType() {
        return this.postType;
    }
    
    public void setSearchCreatorId(Long searchCreatorId) {
        this.searchCreatorId = searchCreatorId;
    }
    
    public Long getSearchCreatorId() {
        return searchCreatorId;
    }
    
    public List<Circle> getCircles() {
        return circles;
    }
    
    private PageResult<MainPostSimpleWrapper> pageResult;
    
    private Map<Long, String> circleTypes = new HashMap<Long, String>(0);
    
    private void loadAvailableRegion() {
        availableRegion.clear();
        availableRegion.addAll(localeDao.getAvailableLocaleByType(LocaleType.USER_LOCALE));
    }
    
    private void loadAvailablePostStatus() {
        availablePostStatus.clear();
        availablePostStatus.add(PostStatus.Published.toString());
        availablePostStatus.add(PostStatus.Drafted.toString());
        availablePostStatus.add(PostStatus.Banned.toString());
        availablePostStatus.add("All");
    }
    
    private List<PostStatus> getPostStatusFromString(String ps) {
        List<PostStatus> statuses = new ArrayList<PostStatus>();
        try{
            statuses.add(PostStatus.valueOf(ps));
        }
        catch(Exception e) {
        }
        return statuses;
    }
    
    public void expertPost() {
        loadAvailablePostStatus();
        loadAvailableRegion();
        isLogin = false;
        userType = UserType.Expert;
        User curUser = null;
        HttpSession session = getContext().getRequest().getSession();
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                Session loginSession = sessionDao.findByToken(token);
                curUser = loginSession.getUser();
                curUserId = curUser.getId();
                userType = curUser.getUserType();
                isLogin = true;
                if (getCurrentUserAdmin()) {
                    isOwnerUser = true;
                    isAdmin = true;
                }
            }
        }
        
        if(userIds == null) 
        {
            userIds = new ArrayList<Long>(0);
            if(isLogin && locale == null) {
                locale = curUser.getRegion().toString();
            }
            else if(locale == null) {
                locale = getRequestLocale().toString();
            }
            List<String> relatedUserLocales = new ArrayList<String>(localeDao.getLocaleByType(locale, LocaleType.USER_LOCALE));
            List<UserType> interestedUserTypes = new ArrayList<UserType>();
            interestedUserTypes.add(UserType.Expert);
            if(relatedUserLocales.size() == 1)
                userLocale = relatedUserLocales.get(0);
            else
                userLocale = locale;
            Long offset = (long)0;
            Long limit = (long)100;
            do {
                PageResult<User> clUsers = userDao.findByUserType(interestedUserTypes, relatedUserLocales, offset, limit);
                for(User clUser : clUsers.getResults()) {
                    userIds.add(clUser.getId());
                }
                offset += limit;
                if(offset >= clUsers.getTotalSize())
                    break;
            }while(true);            
        }
        
        List<PostStatus> postStatuses = getPostStatusFromString(postStatus);        
        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit(pageLimit.getStartIndex(), pageLimit.getPageSize());
        blockLimit.addOrderBy("createdTime", false);
        Boolean withDeleted = postStatus.equalsIgnoreCase("all") ? null : false;
        final PageResult<Post> posts = postDao.findPostByUsers(userIds, postStatuses, null, withDeleted, blockLimit);
        pageResult = new PageResult<MainPostSimpleWrapper>();
        pageResult.setTotalSize(posts.getTotalSize());
        
        List<Long> postIds = new ArrayList<Long>(0);
        Set<Long> lookTypeIds = new HashSet<Long>();
        for(Post c : posts.getResults()) {
            postIds.add(c.getId());
            if(c.getLookTypeId() != null)
                lookTypeIds.add(c.getLookTypeId());
        }
        
        Map<Long, Long> postLikedCount = likeService.checkLikeCount(PostTargetType.POST, postIds);
        Map<Long, Long> postCommentCount = commentService.checkCommentCount(PostTargetType.POST, postIds);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(posts.getResults(), ThumbnailType.Detail);//.List);
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(posts.getResults());
        Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        if(userIds.size() > 0) {
            // ToDo : login user and get like count for login user
            List<Long> likedComment = likeService.getLikeTarget(userIds.get(0), TargetType.Post, postIds);
            for (final Post p : posts.getResults()) {
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
        }
        else {
            for (final Post p : posts.getResults()) {
                LookType lt = lookTypeMap.get(p.getLookTypeId());
                MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, postFileItems.get(p.getId()), postCircles.get(p.getId()), lt);
                if(postLikedCount.containsKey(p.getId()))
                    pw.setLikeCount(postLikedCount.get(p.getId()));
                if(postCommentCount.containsKey(p.getId()))
                    pw.setCommentCount(postCommentCount.get(p.getId()));
                pageResult.add(pw);
            }
        }
        
        List<CircleType> cirTypes = circleTypeDao.listAllTypes();
        for(CircleType cirType : cirTypes) {
            circleTypes.put(cirType.getId(), cirType.getCircleTypeName());
        }
        pageType = "cl";
    }
    
    public Resolution clpost() {
        if(postType.equals("EXPERT")) {
            expertPost();
            return forward();
        }
        
        loadAvailablePostStatus();
        loadAvailableRegion();
        isLogin = false;
        userType = UserType.Normal;
        User curUser = null;
        HttpSession session = getContext().getRequest().getSession();
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                Session loginSession = sessionDao.findByToken(token);
                curUser = loginSession.getUser();
                curUserId = curUser.getId();
                userType = curUser.getUserType();
                isLogin = true;
                if (getCurrentUserAdmin()) {
                    isOwnerUser = true;
                    isAdmin = true;
                }
            }
        }
        
        if(userIds == null) 
        {
            userIds = new ArrayList<Long>(0);
            if(isLogin && locale == null) {
                locale = curUser.getRegion().toString();
            }
            else if(locale == null) {
                locale = getRequestLocale().toString();
            }
            List<String> relatedUserLocales = new ArrayList<String>(localeDao.getLocaleByType(locale, LocaleType.USER_LOCALE));
            List<UserType> interestedUserTypes = new ArrayList<UserType>();
            interestedUserTypes.add(UserType.CL);
            if(relatedUserLocales.size() == 1)
                userLocale = relatedUserLocales.get(0);
            else
                userLocale = locale;
            Long offset = (long)0;
            Long limit = (long)100;
            do {
                PageResult<User> clUsers = userDao.findByUserType(interestedUserTypes, relatedUserLocales, offset, limit);
                for(User clUser : clUsers.getResults()) {
                    userIds.add(clUser.getId());
                }
                offset += limit;
                if(offset >= clUsers.getTotalSize())
                    break;
            }while(true);            
        }
        
        List<PostStatus> postStatuses = getPostStatusFromString(postStatus);
        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit(pageLimit.getStartIndex(), pageLimit.getPageSize());
        blockLimit.addOrderBy("createdTime", false);
        Boolean withDeleted = postStatus.equalsIgnoreCase("all") ? null : false;
        final PageResult<Post> posts = postDao.findPostByUsers(userIds, postStatuses, null, withDeleted, blockLimit);
        pageResult = new PageResult<MainPostSimpleWrapper>();
        pageResult.setTotalSize(posts.getTotalSize());
        Set<Long> lookTypeIds = new HashSet<Long>();
        
        List<Long> postIds = new ArrayList<Long>(0);
        for(Post c : posts.getResults()) {
            postIds.add(c.getId());
            if(c.getLookTypeId() != null)
                lookTypeIds.add(c.getLookTypeId());
        }
        
        Map<Long, Long> postLikedCount = likeService.checkLikeCount(PostTargetType.POST, postIds);
        Map<Long, Long> postCommentCount = commentService.checkCommentCount(PostTargetType.POST, postIds);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(posts.getResults(), ThumbnailType.Detail);//.List);
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(posts.getResults());
        Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        if(userIds.size() > 0) {
            // ToDo : login user and get like count for login user
            List<Long> likedComment = likeService.getLikeTarget(userIds.get(0), TargetType.Post, postIds);
            for (final Post p : posts.getResults()) {
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
        }
        else {
            for (final Post p : posts.getResults()) {
                LookType lt = lookTypeMap.get(p.getLookTypeId());
                MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, postFileItems.get(p.getId()), postCircles.get(p.getId()), lt);
                if(postLikedCount.containsKey(p.getId()))
                    pw.setLikeCount(postLikedCount.get(p.getId()));
                if(postCommentCount.containsKey(p.getId()))
                    pw.setCommentCount(postCommentCount.get(p.getId()));
                pageResult.add(pw);
            }
        }
        
        List<CircleType> cirTypes = circleTypeDao.listAllTypes();
        for(CircleType cirType : cirTypes) {
            circleTypes.put(cirType.getId(), cirType.getCircleTypeName());
        }
        pageType = "cl";
        return forward();
    }
    
    public Resolution delete() {
        User curUser = getCurrentUser();
        if(curUser == null)
            return new StreamingResolution("text/html", "Need to login");
        
        if (deleteId != null && postDao.exists(deleteId)) {
            Post p = postDao.findById(deleteId);
            if(!p.getCreatorId().equals(curUser.getId()) && !getCurrentUserAdmin() && !getAccessControl().getPostManagerAccess())
                return new StreamingResolution("text/html", "Not authorized to delete this post");
            postService.deletePost(p.getCreatorId(), deleteId);
            return new StreamingResolution("text/html", "OK");
        }
        
        return new StreamingResolution("text/html", "Error");
    }
    
    @DefaultHandler
    public Resolution listWall() {
        loadAvailablePostStatus();
        userIds = new ArrayList<Long>(0);
        isLogin = false;
        
        /* Debug */
        HttpSession session = getContext().getRequest().getSession();
        User curUser = null;
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                isLogin = true;
                Session loginSession = sessionDao.findByToken(token);
                curUser = loginSession.getUser();
                userType = curUser.getUserType();
                userIds.add(curUser.getId());
                isOwnerUser = true;
                if (getCurrentUserAdmin()) {
                    isAdmin = true;
                }
                
            }
        }
        
        if(!isLogin || userIds.size() <= 0) {
            return new StreamingResolution("text/html", "Need to login");
        }
        
        if(searchCreatorId != null && (isAdmin || getAccessControl().getPostManagerAccess())) {
            userIds.clear();
            userIds.add(searchCreatorId);
        }
        /*curUserId = (long)1;
        userType = UserType.CL;
        isLogin = true;
        isOwnerUser = true;
        userIds = new ArrayList<Long>(0);
        userIds.add(curUserId);*/
        /* End Debug */
        
        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit(pageLimit.getStartIndex(), pageLimit.getPageSize());
        blockLimit.addOrderBy("promoteScore", false);
        blockLimit.addOrderBy("createdTime", false);
        List<PostStatus> postStatuses = getPostStatusFromString(postStatus);
        Boolean withDeleted = postStatus.equalsIgnoreCase("all") ? null : false;
        final PageResult<Post> posts = postDao.findPostByUsers(userIds, postStatuses, null, withDeleted, blockLimit);
        pageResult = new PageResult<MainPostSimpleWrapper>();
        pageResult.setTotalSize(posts.getTotalSize());
        
        List<Long> postIds = new ArrayList<Long>(0);
        Set<Long> lookTypeIds = new HashSet<Long>();
        for(Post c : posts.getResults()) {
            postIds.add(c.getId());
            if(c.getLookTypeId() != null)
                lookTypeIds.add(c.getLookTypeId());
        }
        
        PageResult<Circle> circlePageResult = circleDao.findByUserIds(ImmutableList.of(curUser.getId()), true, new BlockLimit(0, 100));
        circles = new ArrayList<Circle>();
        for(Circle cir : circlePageResult.getResults()) {
            circles.add(cir);
        }
        
        Map<Long, Long> postLikedCount = likeService.checkLikeCount(PostTargetType.POST, postIds);
        Map<Long, Long> postCommentCount = commentService.checkCommentCount(PostTargetType.POST, postIds);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(posts.getResults(), ThumbnailType.Detail);//.List);
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(posts.getResults());   
        Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        if(userIds.size() > 0) {
            // ToDo : login user and get like count for login user
            List<Long> likedComment = likeService.getLikeTarget(userIds.get(0), TargetType.Post, postIds);
            for (final Post p : posts.getResults()) {
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
        }
        else {
            for (final Post p : posts.getResults()) {
                LookType lt = lookTypeMap.get(p.getLookTypeId());
                MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, postFileItems.get(p.getId()), postCircles.get(p.getId()), lt);
                if(postLikedCount.containsKey(p.getId()))
                    pw.setLikeCount(postLikedCount.get(p.getId()));
                if(postCommentCount.containsKey(p.getId()))
                    pw.setCommentCount(postCommentCount.get(p.getId()));
                pageResult.add(pw);
            }
        }
        
        List<CircleType> cirTypes = circleTypeDao.listAllTypes();
        for(CircleType cirType : cirTypes) {
            circleTypes.put(cirType.getId(), cirType.getCircleTypeName());
        }
        pageType = "wall";
        return forward();
    }
    
    public PageResult<MainPostSimpleWrapper> getPageResult() {
        return pageResult;
    }
    
    public String getUserLocale() {
        return userLocale;
    }
    
    public List<String> getAvailableRegion() {
        return availableRegion;
    }
    
    public List<String> getAvailablePostStatus() {
        return availablePostStatus;
    }
    
    public String getPostStatus() {
        return postStatus;
    }
    
}
