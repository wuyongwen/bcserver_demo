package com.cyberlink.cosmetic.action.api.post;

import java.util.ArrayList;
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
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.file.dao.FileDao;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.result.FullPostWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostDetailWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.result.SubPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.PostProduct;
import com.cyberlink.cosmetic.modules.post.model.PostProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostExProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostTags.MainPostDetailView;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

@UrlBinding("/api/post/query-complete-post.action")
public class QueryCompletePostAction extends AbstractAction {
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("product.ProductDao")
    private ProductDao productDao;
    
    @SpringBean("circle.circleDao")
    private CircleDao circleDao;
    
    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;
    
    @SpringBean("file.fileDao")
    private FileDao fileDao;
    
    @SpringBean("user.SubscribeDao")
    private SubscribeDao subscribeDao;
    
    @SpringBean("look.LookTypeDao")
    private LookTypeDao lookTypeDao;
    
    private Long curUserId = null;
    private Long postId;
    private int maxSubPostCount = 100;
    
    public void setCurUserId(Long curUserId) {
        this.curUserId = curUserId;
    }
    
    @Validate(required = true, on = "route")
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    @DefaultHandler
    public Resolution route() {
        FullPostWrapper fPost = new FullPostWrapper();
        ErrorDef err = getCompletePost(fPost);
        if(err != null)
            new ErrorResolution(err);
        
        return json(fPost, MainPostDetailView.class);
    }
    
    private ErrorDef getCompletePost(FullPostWrapper fPost) {
        BlockLimit blockLimit = new BlockLimit(0, maxSubPostCount);
        final PostApiResult <PageResult<Post>> result = postService.listAllRelatedPost(postId, blockLimit);
        if(!result.success())
            return result.getErrorDef();
        
        final PageResult<Post> relPosts = result.getResult() ;
        List<Long> postIds = new ArrayList<Long>(0);
        List<Long> likeAblePostId = new ArrayList<Long>();
        List<Post> likeAblePosts = new ArrayList<Post>();
        Set<Long> creatorIds = new HashSet<Long>();
        Set<Long> lookTypeIds = new HashSet<Long>();
        
        for(Post p : relPosts.getResults()) {
            postIds.add(p.getId());
            if(p.getParentId() == null) {
                likeAblePostId.add(p.getId());
                likeAblePosts.add(p);
                creatorIds.add(p.getCreator().getId());
                if(p.getLookTypeId() != null)
                    lookTypeIds.add(p.getLookTypeId());
            }
        }        
        
        Map<Long, Map<PostAttrType, Long>> postAttrMap = postService.listPostsAttr(likeAblePostId);
        Map<Long, User> postCircleInSourceUsers = postService.listCircleInSourceUserByPosts(likeAblePostId);
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(likeAblePosts);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(relPosts.getResults(), ThumbnailType.Detail);    
        Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        if(curUserId != null) {
            List<Long> likedComment = likeService.getLikeTarget(curUserId, TargetType.Post, likeAblePostId);
            Set<Long> subcribeeIds = subscribeDao.findIdBySubscriberAndSubscribees(curUserId, SubscribeType.User, creatorIds.toArray(new Long[creatorIds.size()]));
            
            for (final Post rP : relPosts.getResults()) {
                List<PostProduct> relPPs = rP.getPostProducts();
                List<PostProductTag> pts = new ArrayList<PostProductTag>(0);
                List<PostExProductTag> epts = new ArrayList<PostExProductTag>(0);
                for(PostProduct relPP : relPPs)
                {
                	Long relProductId = relPP.getProductId();
                	if(relProductId == null) {
                		PostExProductTag ept = new PostExProductTag(relPP.getTagAttrs());
                		epts.add(ept);
                	}
                	else {
                        Product product = productDao.findById(relProductId);
                        PostProductTag pt = new PostProductTag(product, relPP.getTagAttrs());
                        pts.add(pt);
                	}
                }
                
                if(rP.getParentId() == null) {
                    rP.getCreator().setCurUserId(curUserId);
                    LookType lt = lookTypeMap.get(rP.getLookTypeId());
                    MainPostDetailWrapper dpw = new MainPostDetailWrapper(rP, null, postFileItems.get(rP.getId()), postCircles.get(rP.getId()), lt, pts, epts);
                    if(likedComment.contains(rP.getId()))
                        dpw.setIsLiked(true);
                    if(subcribeeIds.contains(rP.getCreator().getId()))
                        dpw.getCreator().setIsFollowed(true);
                    if(postAttrMap.containsKey(rP.getId())) {
                        Map<PostAttrType, Long> attrs = postAttrMap.get(rP.getId());
                        if(attrs.containsKey(PostAttribute.PostAttrType.PostLikeCount))
                            dpw.setLikeCount(attrs.get(PostAttribute.PostAttrType.PostLikeCount));
                        if(attrs.containsKey(PostAttribute.PostAttrType.PostCommentCount))
                            dpw.setCommentCount(attrs.get(PostAttribute.PostAttrType.PostCommentCount));
                        if(attrs.containsKey(PostAttribute.PostAttrType.PostCircleInCount))
                            dpw.setCircleInCount(attrs.get(PostAttribute.PostAttrType.PostCircleInCount));
                        if(attrs.containsKey(PostAttribute.PostAttrType.LookDownloadCount))
                            dpw.setLookDownloadCount(attrs.get(PostAttribute.PostAttrType.LookDownloadCount));
                    }
                    if(postCircleInSourceUsers.containsKey(rP.getId()))
                        dpw.setSourcePostCreatorByUser(postCircleInSourceUsers.get(rP.getId()));
                    fPost.mainPost = dpw;
                }
                else
                {
                    SubPostSimpleWrapper spw = new SubPostSimpleWrapper(rP, pts, postFileItems.get(rP.getId()), epts);
                    fPost.subPosts.add(spw);
                }
            }
        }
        else {
            for (final Post rP : relPosts.getResults()) {
                List<PostProduct> relPPs = rP.getPostProducts();
                List<PostProductTag> pts = new ArrayList<PostProductTag>(0);
                List<PostExProductTag> epts = new ArrayList<PostExProductTag>(0);
                for(PostProduct relPP : relPPs)
                {
                	Long relProductId = relPP.getProductId();
                	if(relProductId == null) {
                		PostExProductTag ept = new PostExProductTag(relPP.getTagAttrs());
                		epts.add(ept);
                	}
                	else {
	                    Product product = productDao.findById(relProductId);
	                    PostProductTag pt = new PostProductTag(product, relPP.getTagAttrs());
	                    pts.add(pt);
                	}
                }
                
                if(rP.getParentId() == null) {
                    LookType lt = lookTypeMap.get(rP.getLookTypeId());
                    MainPostDetailWrapper dpw = new MainPostDetailWrapper(rP, null, postFileItems.get(rP.getId()), postCircles.get(rP.getId()), lt, pts, epts);
                    if(postAttrMap.containsKey(rP.getId())) {
                        Map<PostAttrType, Long> attrs = postAttrMap.get(rP.getId());
                        if(attrs.containsKey(PostAttribute.PostAttrType.PostLikeCount))
                            dpw.setLikeCount(attrs.get(PostAttribute.PostAttrType.PostLikeCount));
                        if(attrs.containsKey(PostAttribute.PostAttrType.PostCommentCount))
                            dpw.setCommentCount(attrs.get(PostAttribute.PostAttrType.PostCommentCount));
                        if(attrs.containsKey(PostAttribute.PostAttrType.PostCircleInCount))
                            dpw.setCircleInCount(attrs.get(PostAttribute.PostAttrType.PostCircleInCount));
                        if(attrs.containsKey(PostAttribute.PostAttrType.LookDownloadCount))
                            dpw.setLookDownloadCount(attrs.get(PostAttribute.PostAttrType.LookDownloadCount));
                    }
                    if(postCircleInSourceUsers.containsKey(rP.getId()))
                        dpw.setSourcePostCreatorByUser(postCircleInSourceUsers.get(rP.getId()));
                    fPost.mainPost = dpw;
                }
                else
                {
                    SubPostSimpleWrapper spw = new SubPostSimpleWrapper(rP, pts, postFileItems.get(rP.getId()), epts);
                    fPost.subPosts.add(spw);
                }
            }
        }
        return null;
    }
}
