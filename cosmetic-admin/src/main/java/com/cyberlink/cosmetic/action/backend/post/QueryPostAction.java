package com.cyberlink.cosmetic.action.backend.post;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostNewDao;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendDao;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendGroupDao;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendPoolDao;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostNew;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.result.CommentDetailWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostDetailWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.File;
import com.cyberlink.cosmetic.modules.post.result.SubPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;

@UrlBinding("/post/queryPost.action")
public class QueryPostAction extends AbstractAction {

    private Boolean isLogin = false;
    private UserType userType = UserType.Normal;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("file.fileDao")
    private FileDao fileDao;
    
    @SpringBean("look.LookTypeDao")
    private LookTypeDao lookTypeDao;
    
    private PageResult<SubPostSimpleWrapper> pageResult;
    private PageResult<CommentDetailWrapper> commentPageResult;
    private MainPostDetailWrapper mainPost;
    private PostStatus postStatus = PostStatus.Published;
    
    public MainPostDetailWrapper getMainPost() {
        return mainPost;
    }
    
    public PageResult<SubPostSimpleWrapper> getPageResult() {
        return pageResult;
    }
    
    public PageResult<CommentDetailWrapper> getCommentPageResult() {
        return commentPageResult;
    }
    
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("product.ProductDao")
    private ProductDao productDao;

    @SpringBean("post.PostDao")
    private PostDao postDao;
    
    @SpringBean("post.PostNewDao")
    private PostNewDao postNewDao;
    
	@SpringBean("post.psTrendPoolDao")
	private PsTrendPoolDao psTrendPoolDao;

	@SpringBean("post.psTrendDao")
	private PsTrendDao psTrendDao;
	
	@SpringBean("post.psTrendGroupDao")
	private PsTrendGroupDao psTrendGroupDao;
	
    private Long postId;
    private Long deleteId;
    private Boolean isOwnerUser = false;
    private Boolean isAdmin = false;
    private String pageType = "cl";
    private Long deleteCommentId;
    private Long deleteCommentUsrId;
    private int offset = 0;
    private int limit = 10;
    private Boolean isTrendingPost = false;
    
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

	@Validate(required = true, on = "wall")
    public void setPostId(Long postId) {
        this.postId = postId;
    }

	public void setOffset(int offset) {
        this.offset = offset;
    }
	
	public void setLimit(int limit) {
        this.limit = limit;
    }
	
	public void setDeleteCommentId(Long deleteCommentId) {
        this.deleteCommentId = deleteCommentId;
    }

    public void setDeleteCommentUsrId(Long deleteCommentUsrId) {
        this.deleteCommentUsrId = deleteCommentUsrId;
    }
    
    public Long getPostId() {
        return this.postId;
    }
    
    public Boolean getIsLogin() {
        return isLogin;
    }
    
    public UserType getUserType() {
        return userType;
    }
    
    public PostStatus getPostStatus() {
        return postStatus;
    }
    
	public Boolean getIsTrendingPost() {
		return isTrendingPost;
	}

	public void setIsTrendingPost(Boolean isTrendingPost) {
		this.isTrendingPost = isTrendingPost;
	}
    
    public void getRelatedComment(Long mainPostId) {
        BlockLimit blockLimit = new BlockLimit(0, 10);
        blockLimit.addOrderBy("createdTime", false);
        commentPageResult = new PageResult<CommentDetailWrapper>();
        PageResult<Comment> comments = commentService.listComment(PostTargetType.POST, mainPostId, blockLimit).getResult();
        for(int idx = comments.getResults().size() - 1; idx >= 0 ; idx--) {
            commentPageResult.add(new CommentDetailWrapper(comments.getResults().get(idx), null));
        }
    }
    
    public Boolean getRelatedSubPost() {
        isLogin = false;
        userType = UserType.Normal;
        Long userId = null;
        List<Long> postIds = new ArrayList<Long>(0);
        List<Post> posts = new ArrayList<Post>();
        /* Debug */
        HttpSession session = getContext().getRequest().getSession();
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                Session loginSession = sessionDao.findByToken(token);
                User curUser = loginSession.getUser();
                userId = curUser.getId();
                userType = curUser.getUserType();
                if (getCurrentUserAdmin()) {
                	isOwnerUser = true;
                	isAdmin = true;
                }
                isLogin = true;
            }
        }

        /*userId = (long)1;
        userType = UserType.CL;
        if (userType == UserType.CL)
            isOwnerUser = true;
        isLogin = true;*/
        /* End Debug */
        
        Post mPost = postService.queryPostById(postId).getResult();
        if(mPost == null)
            return false;
        postStatus = mPost.getPostStatus();
        postIds.add(mPost.getId());
        posts.add(mPost);
        
        pageResult = new PageResult<SubPostSimpleWrapper>();
        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit(pageLimit.getStartIndex(), pageLimit.getPageSize());
        final PageResult<Post> subPosts = postService.listSubPost(postId, blockLimit).getResult();
        pageResult.setTotalSize(subPosts.getTotalSize());
        
        for(Post spc : subPosts.getResults()) {
            postIds.add(spc.getId());
            posts.add(spc);
        }
        
        Map<Long, Long> postLikedCount = likeService.checkLikeCount(PostTargetType.POST, postIds);
        Map<Long, Long> postCommentCount = commentService.checkCommentCount(PostTargetType.POST, postIds);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(posts, ThumbnailType.Detail);
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(posts);
        
        List<Long> likedPost = likeService.getLikeTarget(userId, TargetType.Post, postIds);
        
        LookType lookType = null;
        if(mPost.getLookTypeId() != null)
            lookType = lookTypeDao.findById(mPost.getLookTypeId());
        mainPost = new MainPostDetailWrapper(mPost, null, postFileItems.get(mPost.getId()), postCircles.get(mPost.getId()), lookType, null, null);
        if(likedPost.contains(mPost.getId()))
            mainPost.setIsLiked(true);
        if(postLikedCount.containsKey(mPost.getId()))
            mainPost.setLikeCount(postLikedCount.get(mPost.getId()));
        if(postCommentCount.containsKey(mPost.getId()))
            mainPost.setCommentCount(postCommentCount.get(mPost.getId()));
        
        for (final Post sp : subPosts.getResults()) {
            SubPostSimpleWrapper pw = new SubPostSimpleWrapper(sp, null, postFileItems.get(sp.getId()), null);
            if(likedPost.contains(sp.getId()))
                pw.setIsLiked(true);
            if(postLikedCount.containsKey(sp.getId()))
                pw.setLikeCount(postLikedCount.get(sp.getId()));
            if(postCommentCount.containsKey(sp.getId()))
                pw.setCommentCount(postCommentCount.get(sp.getId()));
            pageResult.add(pw);
        }
        
        getRelatedComment(postId);
        ogTitle = mainPost.getTitle();
        List<File> attchFile =  mainPost.getAttachments().getFiles();
        if(attchFile != null && attchFile.size() > 0)
            ogImage =attchFile.get(0).getDownloadUrl();
        String mpContent = mainPost.getContent();
        if(mpContent != null)
            ogDescription = Jsoup.parse(mpContent).text();
        
        isTrendingPost = postNewDao.findByPost(postId, false).size() > 0;
        
        return true;
    }
    
    @DefaultHandler
    public Resolution wall() {
    	if(!getRelatedSubPost())
    	    return new StreamingResolution("text/html", "Invalid Post");
    	pageType = "wall";
    	isOwnerUser = true;
        return forward();
    }
    public Resolution delete() {
        HttpSession session = getContext().getRequest().getSession();
        Long userId = null;
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                Session loginSession = sessionDao.findByToken(token);
                User curUser = loginSession.getUser();
                userId = curUser.getId();
                userType = curUser.getUserType();
                if(getCurrentUserAdmin() || getAccessControl().getPostManagerAccess()) {
                    isOwnerUser = true;
                }
                if (getCurrentUserAdmin()) {
                    isAdmin = true;
                }
                isLogin = true;
            }
        }
        
        if(userId == null)
            return new StreamingResolution("text/html", "Error");
        
        Boolean isMainPost = true;
    	if (deleteId != null && postDao.exists(deleteId)) {
    		Post p = postDao.findById(deleteId);
    		Long creatorId = p.getCreatorId();
    		if(creatorId == null) {
    		    isMainPost = false;
    		    Post mainPost = postDao.findById(p.getParentId());
    		    creatorId = mainPost.getCreatorId();
    		}
    		if(isOwnerUser)
    		    userId = creatorId;
    		
    		if(isMainPost)
    		    postService.deletePost(userId, deleteId);
    		else
    		    postService.deleteSubPost(userId, deleteId);
    		
    	    return new StreamingResolution("text/html", "OK");
    	}
    	
    	return new StreamingResolution("text/html", "Error");
    }
    
    public Resolution clpost() {
        if(!getRelatedSubPost())
            return new StreamingResolution("text/html", "Invalid Post");
        pageType = "cl";
        return forward();
    }
    
    public Resolution deleteComment() {
        if (!getCurrentUserAdmin())
            return new ErrorResolution(401, "You not authorized to delete the comment");
        
        if(commentService.deleteComment(deleteCommentUsrId, deleteCommentId).getResult())
            return new StreamingResolution("text/html", "OK");
        
        return new ErrorResolution(400, "Bad request");
    }
    
    public Resolution share() {
        StreamingResolution sr = null;
        String qrFilePath = postService.getPostQRCode(postId);
        if(qrFilePath == null)
            return new StreamingResolution("text/html", "Error");
        
        try {
            sr = new StreamingResolution("image/png", new FileInputStream(qrFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new StreamingResolution("text/html", "Error");
        }
        
        return sr;
    }
    
    public Resolution listComment() {
        isLogin = false;
        userType = UserType.Normal;
        Long userId = null;
        HttpSession session = getContext().getRequest().getSession();
        if(session != null) {
            String token = (String) getContext().getRequest().getSession().getAttribute("token");
            if(token != null && token.length() > 0) {
                Session loginSession = sessionDao.findByToken(token);
                User curUser = loginSession.getUser();
                userId = curUser.getId();
                userType = curUser.getUserType();
                if (getCurrentUserAdmin()) {
                    isOwnerUser = true;
                    isAdmin = true;
                }
                isLogin = true;
            }
        }
        
        BlockLimit blockLimit = new BlockLimit(offset, limit);
        blockLimit.addOrderBy("createdTime", false);
        commentPageResult = new PageResult<CommentDetailWrapper>();
        PageResult<Comment> comments = commentService.listComment(PostTargetType.POST, postId, blockLimit).getResult();
        for(int idx = comments.getResults().size() - 1; idx >= 0 ; idx--) {
            commentPageResult.add(new CommentDetailWrapper(comments.getResults().get(idx), null));
        }
        commentPageResult.setTotalSize(comments.getTotalSize());
        return forward();
        //return new StreamingResolution("text/html", "<br>");
        //return json(result);
    }
    
    public Resolution removeFromTrending() {
    	List<PostNew> postNewList = postNewDao.findByPost(postId, false);
		if (postNewList.size() <= 0)
			return forward();
		
    	List<Long> circleTypeIds = new ArrayList<Long>();
    	for(PostNew postNew : postNewList) {
    		circleTypeIds.add(postNew.getCircleTypeId());
    	}
    	String locale = postNewList.get(0).getLocale();
    	
    	List<Long> postIds = new ArrayList<Long>(Arrays.asList(postId));
    	int updatedpost = postNewDao.batchCheck(postIds, true);
    	if(updatedpost <= 0)
    		return forward();
    	
    	psTrendPoolDao.deleteByPost(postId, circleTypeIds);

    	Map<String, Long> groupIdMap = psTrendGroupDao.getAvailableId();
    	psTrendDao.deleteByPost(postId, locale, groupIdMap.values());

    	return json("done");
    }
}
