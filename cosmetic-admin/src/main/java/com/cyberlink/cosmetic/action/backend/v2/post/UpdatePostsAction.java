package com.cyberlink.cosmetic.action.backend.v2.post;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.result.MainPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.result.PostsWrapper;
import com.cyberlink.cosmetic.modules.post.result.SubPostBaseWrapper;
import com.cyberlink.cosmetic.modules.post.service.PostService;

@UrlBinding("/v2/post/update-posts.action")
public class UpdatePostsAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("circle.circleService")
    private CircleService circleService;
    
    @SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;
    
    @SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;

    private String mainPost;
    private List<String> updateSubPosts;
    private List<String> deleteSubPosts;
    private List<String> newSubPosts;
    PageResult<Circle> circles = new PageResult<Circle>();
    private Long postId;
    private List<Post> posts;
    private Long circleId;
    
    private final int maxSubPostCount = 100;
    
    public void setMainPost(String mainPost) {
        this.mainPost = mainPost;
    }
    
    public void setUpdateSubPosts(List<String> updateSubPosts) {
        this.updateSubPosts = updateSubPosts;
    }
    
    public void setDeleteSubPosts(List<String> deleteSubPosts) {
        this.deleteSubPosts = deleteSubPosts;
    }
    
    public void setNewSubPosts(List<String> newSubPosts) {
        this.newSubPosts = newSubPosts;
    }
    
    public PageResult<Circle> getCircles() {
        return this.circles;
    }
    
    public void setPostId(Long postId) {
    	this.postId = postId;
    }
    
    public Long getPostId() {
    	return this.postId;
    }
    
    public List<Post> getPosts() {
    	return this.posts;
    }
    
    public Long getCircleId() {
    	return this.circleId;
    }
    
    private void loadAvailableCircles() {
    	Long userId = getCurrentUserId();

        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit((pageLimit.getPageIndex() - 1 ) * pageLimit.getPageSize(), pageLimit.getPageSize());
        circles = circleService.listUserCircle(userId, true, getCurrentUserLocale(), true, blockLimit);
        List<Long> subcribedCircleIds = circleSubscribeDao.listSubcribeCircle(userId, circles.getResults());
        circles.setResults(circleAttributeDao.getCircleAttribute(circles.getResults(), userId, subcribedCircleIds));
    }
    
    private boolean loadAvailablePosts() {
        BlockLimit blockLimit = new BlockLimit(0, maxSubPostCount);
        final PostApiResult <PageResult<Post>> result = postService.listAllRelatedPost(postId, blockLimit);
        if(!result.success())
            return false;
        
        posts = result.getResult().getResults();
        return true;
    }
    
    @DefaultHandler
    public Resolution route() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
        
        if(!loadAvailablePosts())
        	return new ErrorResolution(ErrorDef.BadRequest);
        
        circleId = posts.get(0).getCircleId();
        
        loadAvailableCircles();
        return forward();
    }
    
    public Resolution update() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
    	
    	Long userId = getCurrentUserId();
    	
    	if(!loadAvailablePosts())
            return new ErrorResolution(ErrorDef.BadRequest);
    	
    	if (posts.size() > 1) {
        	deleteSubPosts = new ArrayList<String>();
            for(int i=1; i<posts.size(); i++) {
            	deleteSubPosts.add("{\"subPostId\":" + posts.get(i).getId() + "}");
            } 
    	}

    	
        PostApiResult <List<Post>> result = postService.updatePosts(userId, getCurrentUserLocale(), null, mainPost, null, null, updateSubPosts, deleteSubPosts, newSubPosts);
        if(!result.success())
            return new ErrorResolution(result.getErrorDef());
        
        List<Post> resultList = result.getResult();        
        PostsWrapper r = new PostsWrapper();
        for(int idx = 0; idx < resultList.size(); idx++) {
            Post p = resultList.get(idx);
            if(p.getParentId() == null)
                r.mainPost = new MainPostBaseWrapper(p);
            else
                r.subPosts.add(new SubPostBaseWrapper(p));
        }
        
        return json("Done");
    }

}
