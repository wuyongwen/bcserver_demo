package com.cyberlink.cosmetic.action.backend.post;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostExProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostProduct;
import com.cyberlink.cosmetic.modules.post.model.PostProductTag;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.result.FullPostWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostDetailWrapper;
import com.cyberlink.cosmetic.modules.post.result.SubPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/post/msr.action")
public class MSRAction extends AbstractAction{
    public class MsrCircle {
        MsrCircle(Circle cir, String inputRegion) {
            id = cir.getId();
            lastModified = cir.getLastModified();
            circleName = cir.getCircleName();
            circleTypeId = cir.getCricleTypeId();
            iconUrl = cir.getIconUrl();
            region = inputRegion;
        }
        
        @JsonView(Views.Public.class)
        public Long id;
        
        @JsonView(Views.Public.class)
        public Date lastModified = null;
        
        @JsonView(Views.Public.class)
        public String circleName = "";
        
        @JsonView(Views.Public.class)
        public Long circleTypeId;
        
        @JsonView(Views.Public.class)
        public String iconUrl = "";
        
        @JsonView(Views.Public.class)
        public String region = "";
    }
    
    public class MsrMainPost extends MainPostDetailWrapper {
        public MsrMainPost(Post post, List<Object> fileItems, List<Circle> circles, LookType lookType, List<PostProductTag> postProductTags, List<PostExProductTag> postExProductTags) {
            super(post, null, fileItems, circles, lookType, postProductTags, postExProductTags);
            postIdValue = String.valueOf(post.getId());
        }
        
        @JsonView(Views.Simple.class)
        public String postIdValue;
    }
    
    public class MsrSubPost extends SubPostSimpleWrapper {
        public MsrSubPost(Post subPost, List<PostProductTag> productTag, List<Object> fileItems, List<PostExProductTag> postExProductTags) {
            super(subPost, productTag, fileItems, postExProductTags);
            subPostIdValue = String.valueOf(subPost.getId());
        }
        
        @JsonView(Views.Simple.class)
        public String subPostIdValue;
    }
    
    @SpringBean("post.PostDao")
    private PostDao postDao;

    @SpringBean("product.ProductDao")
    private ProductDao productDao;
    
    @SpringBean("circle.circleDao")
    private CircleDao circleDao;
    
    @SpringBean("circle.circleTypeDao")
    private CircleTypeDao circleTypeDao;
    
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("look.LookTypeDao")
    private LookTypeDao lookTypeDao;
    
    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
    private String duration;
    private PostStatus status;
    private String posts;
    private String region = null;
    
    public void setStatus(PostStatus status) {
        this.status = status;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public void setOffset(Long offset) {
        this.offset = offset;
    }
    
    public void setLimit(Long limit) {
        this.limit = limit;
    }
    
    public void setPosts(String posts) {
        this.posts = posts;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public class UpdatePostResult {
        public UpdatePostResult() {
            
        }
        
        @JsonView(Views.Simple.class)
        public Long postId = null;
        
        @JsonView(Views.Simple.class)
        public Boolean result = false;
    }
    
	@DefaultHandler
	public Resolution route() {
	    return new ErrorResolution(400, "Bad Request");
	}
	
    public Resolution queryByStatus() {
        if(status == null)
            return new ErrorResolution(400, "Invalid status");
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
            actualObj = mapper.readValue(duration, JsonNode.class);
        } catch (IOException e1) {
            e1.printStackTrace();
            return new ErrorResolution(400, "Invalid duration format");
        }     
        JsonNode startDate = actualObj.get("startDate");
        if(startDate == null)
            return new ErrorResolution(400, "Invalid duration format [No startDate]");
        
        JsonNode endDate = actualObj.get("endDate");
        if(endDate == null)
            return new ErrorResolution(400, "Invalid duration format [No endDate]");

        Date start = new Date(startDate.longValue() * 1000);
        Date end = new Date(endDate.longValue() * 1000);

        BlockLimit blockLimit = new BlockLimit(offset.intValue(), limit.intValue());
        blockLimit.addOrderBy("createdTime", false);
        PageResult<Post> pageResult = postDao.findMainPostByDateAndStatus(start, end, status, blockLimit);   
        Map<Long, List<Post>> postSubposts = postService.listSubPostByPosts(pageResult.getResults());
        List<Post> postToqueryAttachment = new ArrayList<Post>();
        postToqueryAttachment.addAll(pageResult.getResults());
        for(Long key : postSubposts.keySet()){
            postToqueryAttachment.addAll(postSubposts.get(key));
        }
        
        Set<Long> lookTypeIds = new HashSet<Long>();
        for(Post c : pageResult.getResults()) {
            if(c.getLookTypeId() != null)
                lookTypeIds.add(c.getLookTypeId());
        }
        
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(pageResult.getResults());
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(postToqueryAttachment, ThumbnailType.Detail);
        Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        PageResult<FullPostWrapper> list = new PageResult<FullPostWrapper>();
        list.setTotalSize(pageResult.getTotalSize());
        for (Post post : pageResult.getResults()) {
            List<PostProduct> relPPs = post.getPostProducts();
            List<PostProductTag> pts = new ArrayList<PostProductTag>(0);
            List<PostExProductTag> epts = new ArrayList<PostExProductTag>(0);
            for(PostProduct relPP : relPPs)
            {
				Long relProductId = relPP.getProductId();
				if (relProductId == null) {
					PostExProductTag ept = new PostExProductTag(relPP.getTagAttrs());
					epts.add(ept);
				} 
				else {
					if (!productDao.exists(relProductId))
						continue;
					Product product = productDao.findById(relProductId);
					PostProductTag pt = new PostProductTag(product, relPP.getTagAttrs());
					pts.add(pt);
				}
            }
            
            LookType lt = lookTypeMap.get(post.getLookTypeId());
            MsrMainPost mainPost = new MsrMainPost(post, postFileItems.get(post.getId()), postCircles.get(post.getId()), lt, pts, epts);
            FullPostWrapper fullPost = new FullPostWrapper();
            fullPost.mainPost = mainPost;
            List<SubPostSimpleWrapper> subposts = new ArrayList<SubPostSimpleWrapper>();
            if(postSubposts.containsKey(post.getId())) {
                List<Post> subBcSubPosts = postSubposts.get(post.getId());
                for(Post sp : subBcSubPosts){
                    List<PostProduct> relSPPs = sp.getPostProducts();
                    List<PostProductTag> spts = new ArrayList<PostProductTag>(0);
                    for(PostProduct srelPP : relSPPs)
                    {
                        Long relProductId = srelPP.getProductId();
                        if(!productDao.exists(relProductId))
                            continue;
                        Product product = productDao.findById(relProductId);
                        PostProductTag pt = new PostProductTag(product, srelPP.getTagAttrs());
                        spts.add(pt);
                    }
                    MsrSubPost spw = new MsrSubPost(sp, spts, postFileItems.get(sp.getId()), epts);
                    subposts.add(spw);
                }
                fullPost.subPosts = subposts;
            }
            list.add(fullPost);
        }

        return json(list);
    }
    
	public Resolution updatePostStatus() {
	    if(posts == null)
	        return new ErrorResolution(400, "Invalid posts");
	    
	    List<UpdatePostResult> results = new ArrayList<UpdatePostResult>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
            actualObj = mapper.readValue(posts, JsonNode.class);
        } catch (IOException e1) {
            e1.printStackTrace();
            return new ErrorResolution(400, "Invalid posts");
        }    
        
        if(!actualObj.isArray())
            return new ErrorResolution(400, "Invalid posts");
        
        
        for(JsonNode post : actualObj) {
	        JsonNode postIdObj = post.get("postId");
	        if(postIdObj == null) {
	            UpdatePostResult failedResult = new UpdatePostResult();
                failedResult.postId = null;
                failedResult.result = false;
                results.add(failedResult);
                return new ErrorResolution(400, "Update Failed : " + post.asText());
	        }
	        
	        Long postId = postIdObj.asLong();
	        JsonNode status = post.get("status");
	        if(status == null) {
	            UpdatePostResult failedResult = new UpdatePostResult();
                failedResult.postId = postId;
                failedResult.result = false;
                results.add(failedResult);
                return new ErrorResolution(400, "Update Failed : " + post.asText());
	        }
	        
	        PostStatus pStatus = null;
	        switch(status.asText()){
    	        case "Published":
    	            pStatus = PostStatus.Published; 
    	            break;
    	        case "Drafted":
    	            pStatus = PostStatus.Drafted;
    	            break;
    	        case "Hidden":
    	            pStatus = PostStatus.Hidden;
    	            break;
    	        case "Banned":
    	            pStatus = PostStatus.Banned;
    	            break;
	            default:
	                break;
	        }
	        
	        if(pStatus == null) {
	            UpdatePostResult failedResult = new UpdatePostResult();
                failedResult.postId = postId;
                failedResult.result = false;
                results.add(failedResult);
                return new ErrorResolution(400, "Update Failed : " + post.asText());
	        }
	        Post bcPost = postDao.findById(postId);
	        bcPost.setPostStatus(pStatus);
	        Post updatedPost = postDao.update(bcPost);
	        if(updatedPost == null) {
	            UpdatePostResult failedResult = new UpdatePostResult();
                failedResult.postId = postId;
                failedResult.result = false;
                return new ErrorResolution(400, "Update Failed : " + post.asText());
	        }
	        else {
	            UpdatePostResult failedResult = new UpdatePostResult();
                failedResult.postId = postId;
                failedResult.result = true;
                results.add(failedResult);
	        }
	    }
	    
        return new StreamingResolution("text/html", "");
	    //return json(results);
	}
	
	public Resolution listCircles() {
	    List<Circle> circles = null;
	    PageResult<MsrCircle> pgCircles = new PageResult<MsrCircle>();
	    if(region == null)
	        circles = circleDao.findAll();
	    else {
	        List<Long> circleTypeIds = new ArrayList<Long>();
            PageResult<CircleType> circleTypeList = circleTypeDao.listTypesByLocales(new ArrayList<String>(localeDao.getLocaleByType(region, LocaleType.POST_LOCALE)), null, new BlockLimit(0, 100));
            if(circleTypeList.getResults().size() > 0) {
                for(CircleType circleType : circleTypeList.getResults())
                    circleTypeIds.add(circleType.getId());
            }
            else
                circleTypeIds.add((long)14);
            PageResult<Circle> pgcirs = circleDao.findByTypeIds(circleTypeIds, (long)0, (long)100);
            circles = pgcirs.getResults();
	    }
	    
	    if(circles == null)
	        return new ErrorResolution(400, "Bad Request");
	    List<CircleType> circleTypes = circleTypeDao.findAll();
	    Map<Long, String> circleTypeRegionMap = new HashMap<Long, String>();
	    for(CircleType ct : circleTypes) {
	        circleTypeRegionMap.put(ct.getId(), ct.getLocale());
	    }
	    for(Circle cir : circles) {
	        pgCircles.add(new MsrCircle(cir, circleTypeRegionMap.get(cir.getCricleTypeId())));
	    }
	    pgCircles.setTotalSize(circles.size());
	    return json(pgCircles);
	}
}
