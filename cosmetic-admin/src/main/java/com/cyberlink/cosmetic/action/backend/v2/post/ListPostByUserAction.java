package com.cyberlink.cosmetic.action.backend.v2.post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTagGroupDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;

@UrlBinding("/v2/post/listUserPost.action")
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
    
    @SpringBean("circle.circleService")
    private CircleService circleService;
    
    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
    @SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;

    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("file.fileDao")
    private FileDao fileDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    private Long userId;
    PageResult<Circle> circles = new PageResult<Circle>();
    private Long circleId = new Long(-1);
    private int postStatus = -1;
    private Map<PostStatus, String> availablePostStatus = new LinkedHashMap<PostStatus, String>();
    private PageResult<MainPostSimpleWrapper> pageResult;
    private Long deleteId;
    private Map<Long, Long> totalCountMap = new HashMap<Long, Long>();//post likes count + comment count + circle in post count 
    private Long maxLimitTotalCount = 100L;
    		
    public void setCircleId(Long circleId) {
    	this.circleId = circleId;
    }
    
    public Long getCircleId() {
    	return circleId;
    }
    
    public PageResult<Circle> getCircles() {
        return circles;
    }
    
    public void setPostStatus (int postStatus) {
        this.postStatus = postStatus;
    }
    
    public int getPostStatus() {
        return postStatus;
    }
    
    public Map<PostStatus, String> getAvailablePostStatus() {
        return availablePostStatus;
    }
    
    public PageResult<MainPostSimpleWrapper> getPageResult() {
        return pageResult;
    }
    
    public void setDeleteId(Long deleteId) {
    	this.deleteId = deleteId;
    }
    
    public Long getDeleteId() {
    	return deleteId;
    }
    
    public Map<Long, Long> getTotalCountMap() {
		return totalCountMap;
	}
    
	public Long getMaxLimitTotalCount() {
		return maxLimitTotalCount;
	}

	private void loadAvailablePostStatus() {
        availablePostStatus.clear();
        availablePostStatus.put(PostStatus.Published, "Published");
        availablePostStatus.put(PostStatus.Drafted, "Drafted");
        availablePostStatus.put(PostStatus.Hidden, "Scheduled");
    }
    
    private void loadAvailableCircles() {
	    if(userId == null)
	        userId = getCurrentUserId();

        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit((pageLimit.getPageIndex() - 1 ) * pageLimit.getPageSize(), pageLimit.getPageSize());
        circles = circleService.listUserCircle(userId, true, getCurrentUserLocale(), true, blockLimit);
        List<Long> subcribedCircleIds = circleSubscribeDao.listSubcribeCircle(userId, circles.getResults());
        circles.setResults(circleAttributeDao.getCircleAttribute(circles.getResults(), userId, subcribedCircleIds));
    }
    
    private void loadPosts() {
    	List<PostStatus> postStatuses = new ArrayList<PostStatus>();
    	switch (postStatus) {
	    	case -1:
	    		postStatuses.add(PostStatus.Published);
	    		postStatuses.add(PostStatus.Drafted);
	    		postStatuses.add(PostStatus.Hidden);
	    		break;
	    	case 0:
	    		postStatuses.add(PostStatus.Published);
	    		break;
	    	case 1:
	    		postStatuses.add(PostStatus.Drafted);
	    		break;
	    	case 2:
                postStatuses.add(PostStatus.Hidden);
                break;
    	}
        
        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit(pageLimit.getStartIndex(), pageLimit.getPageSize());
        blockLimit.addOrderBy("createdTime", false);
        
        final PageResult<Post> posts;
        if (circleId == -1) {
        	posts = postService.listPostByUsers(Arrays.asList(userId), postStatuses, true, blockLimit).getResult();
        }
        else {
        	posts = postService.listPostByCircle_v3(circleId, null, null, postStatuses, "Date", null, blockLimit).getResult();
        }
        
        List<Long> postIds = new ArrayList<Long>(0);
        for(Post c : posts.getResults()) {
            postIds.add(c.getId());
        }
        //Calculate post's totalCount(like count + comment count + circle count) and find max total count.
        Map<Long, Map<PostAttrType, Long>> postsAttrMaps = postService.listPostsAttr(postIds);
        for(Long postId : postIds){
        	Long totalCount = 0L;
        	if(postsAttrMaps.containsKey(postId)){
        	Map<PostAttrType,Long> postAttrTypeMap = postsAttrMaps.get(postId);
	        	if(postAttrTypeMap.containsKey(PostAttrType.PostLikeCount))
	        		totalCount += postAttrTypeMap.get(PostAttrType.PostLikeCount);
	        	if(postAttrTypeMap.containsKey(PostAttrType.PostCommentCount))
	        		totalCount += postAttrTypeMap.get(PostAttrType.PostCommentCount);
	        	if(postAttrTypeMap.containsKey(PostAttrType.PostCircleInCount))
	        		totalCount += postAttrTypeMap.get(PostAttrType.PostCircleInCount);
        	}
        	if(maxLimitTotalCount < totalCount)
        		maxLimitTotalCount = totalCount;
        	totalCountMap.put(postId, totalCount);
        }
        
        pageResult = new PageResult<MainPostSimpleWrapper>();
        pageResult.setTotalSize(posts.getTotalSize());
        
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(posts.getResults());
        for (final Post p : posts.getResults()) {
            MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, null, postCircles.get(p.getId()), null);
            pageResult.add(pw);
        }
    }
    
    @DefaultHandler
	public Resolution route() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
    	
    	loadAvailablePostStatus();
    	loadAvailableCircles();
        
        loadPosts();
        
        return forward();
    }
    
    public Resolution delete() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
    	
        if (deleteId != null && postDao.exists(deleteId)) {
            Post p = postDao.findById(deleteId);
            postService.deletePost(p.getCreatorId(), deleteId);
            return new StreamingResolution("text/html", "OK");
        }
        
        return new StreamingResolution("text/html", "Error");
    }
}
