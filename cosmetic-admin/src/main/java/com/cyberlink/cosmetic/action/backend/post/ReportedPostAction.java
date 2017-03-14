package com.cyberlink.cosmetic.action.backend.post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.mail.service.MailInappropPostCommentService;
import com.cyberlink.cosmetic.modules.post.dao.PostReportedDao;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostReported;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedReason;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedResult;
import com.cyberlink.cosmetic.modules.post.model.PostReported.PostReportedStatus;
import com.cyberlink.cosmetic.modules.post.result.CommentDetailWrapper;
import com.cyberlink.cosmetic.modules.post.result.PostReportedWrapper;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Session;
import com.cyberlink.cosmetic.modules.user.model.User;

@UrlBinding("/post/ReportedPost.action")
public class ReportedPostAction extends AbstractPostAction {
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("circle.circleService")
    private CircleService circleService;
    
    @SpringBean("mail.mailInappropPostCommentService")
    private MailInappropPostCommentService mailInappropPostCommentService;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;

    @SpringBean("post.PostReportedDao")
    private PostReportedDao postReportedDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    private String timeFormat = "yyyy-MM-dd HH:mm:ss";
    
    public class ReportedTarget {
        public String type;
        public Long count;
        public DisputePostWrapper post;
        public CommentDetailWrapper comment;
        public List<PostReported> reasons = new ArrayList<PostReported>();
        public String result = "";
        
        public String getType() {
            return type;
        }
        
        public String getResult() {
            return result;
        }
        
        public Long getCount() {
            return count;
        }
        
        public List<PostReported> getReasons() {
            return reasons;
        }
        
        public DisputePostWrapper getPost() {
            return post;
        }
        
        public CommentDetailWrapper getComment() {
            return comment;
        }
    }
    
    public class ReportedPost extends ReportedTarget {
        public DisputePostWrapper target;
        
        public ReportedPost(DisputePostWrapper p, List<PostReported> reasons){
            super();
            this.type = "Post";
            post = p;
            if(reasons == null)
                return;
            this.count = (long) reasons.size();
            if(reasons.size() > 0) {
                PostReportedResult rel = reasons.get(0).getResult();
                if(rel != null)
                    this.result = rel.toString();
            }
            for(PostReported reason : reasons) {
                this.reasons.add(reason);
            }
        }
    }
    
    public class ReportedComment extends ReportedTarget {
        public CommentDetailWrapper target;
        
        public ReportedComment(Comment c, List<PostReported> reasons){
            super();
            this.type = "Comment";
            comment = new CommentDetailWrapper(c, null);
            if(reasons == null)
                return;
            this.count = (long) reasons.size();
            if(reasons.size() > 0) {
                PostReportedResult rel = reasons.get(0).getResult();
                if(rel != null)
                    this.result = rel.toString();
            }
            for(PostReported reason : reasons) {
                this.reasons.add(reason);
            }
        }
    }
    
    // Route
    private PageResult<ReportedTarget> pageResult = new PageResult<ReportedTarget>();
    private int offset = 0;
    private int size = 100;
    private List<String> availableRegion = new ArrayList<String>(0);
    private String selRegion = "en_US";
    private PostReportedStatus selStatus = PostReportedStatus.NewReported;
    private Long searchAuthorId;
    private Long searchReportedId;
    
    // Handle
    private String targetType = "Post";
    private Long targetId;
    private String result;
    private String remark;
    private String reason;
    
    private void loadAvailableRegion() {
        availableRegion.clear();
        availableRegion.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
    }
    
    public List<String> getAvailableRegion() {
        return availableRegion;
    }
    
    public void setOffset(int offset){
        this.offset = offset;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public void setSize(int size){
        this.size = size;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setTargetType(String targetType){
        this.targetType = targetType;
    }
    
    public void setTargetId(Long targetId){
        this.targetId = targetId;
    }
    
    public void setResult(String result){
        this.result = result;
    }
    
    public void setRemark(String remark){
        this.remark = remark;
    }
    
    public void setReason(String reason){
        this.reason = reason;
    }
    
    public PageResult<ReportedTarget> getPageResult() {
        return pageResult;
    }
    
    public String getSelRegion() {
        return selRegion;
    }
    
    public void setSelRegion(String selRegion) {
        this.selRegion = selRegion;
    }
    
    public PostReportedStatus getSelStatus() {
        return selStatus;
    }
    
    public void setSelStatus(PostReportedStatus selStatus) {
        this.selStatus = selStatus;
    }
    
    public void setSearchAuthorId(Long searchAuthorId) {
        this.searchAuthorId = searchAuthorId;
    }
    
    public Long getSearchAuthorId() {
        return searchAuthorId;
    }
    
    public void setSearchReportedId(Long searchReportedId) {
        this.searchReportedId = searchReportedId;
    }
    
    public Long getSearchReportedId() {
        return searchReportedId;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    @DefaultHandler
    public Resolution route() {  
        if (!getCurrentUserAdmin() && !getAccessControl().getReportManagerAccess()) {
            return new ErrorResolution(403, "Need to login");
        }
        
        loadAvailableRegion();
        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit((pageLimit.getPageIndex() - 1 ) * size, size);
        if(targetType.equals("All"))
            targetType = null;
        PageResult<PostReportedWrapper> objs = postReportedDao.getReportedPostCount(searchAuthorId, searchReportedId, targetType, selStatus, selRegion, blockLimit);
        
        List<Long> postIds = new ArrayList<Long>();
        List<Long> commentIds = new ArrayList<Long>();
        Map<Long, Integer> postSortingIdx = new LinkedHashMap<Long, Integer>();
        Map<Long, Integer> commentSortingIdx = new LinkedHashMap<Long, Integer>();
        int sortingIdxCount = 0;
        for(PostReportedWrapper obj : objs.getResults()) {
            if(obj.getRefType().equals("Post")) {
                postIds.add(obj.getRefId());
                postSortingIdx.put(obj.getRefId(), sortingIdxCount++);
            }
            else if(obj.getRefType().equals("Comment")) {
                commentIds.add(obj.getRefId());
                commentSortingIdx.put(obj.getRefId(), sortingIdxCount++);
            }
        }
        
        List<Circle> bcDefaiultCircles = circleService.getBcDefaultCircle("en_US");
        Map<Long, String> defaultTypeNameMap = new HashMap<Long, String>();
        PageResult<DisputePostWrapper> postWrappers = postIdToPostView(postIds.size(), postIds, defaultTypeNameMap, timeFormat);
        List<Comment> comments = commentService.findPostByIds(commentIds);
        
        Map<Long, List<PostReported>> postsReason = postService.getReportedPostReason(selStatus, postIds);
        Map<Long, List<PostReported>> commentsReason = commentService.getReportedCommentReason(selStatus, commentIds);
        
        ReportedTarget [] reportedTargetArray = new ReportedTarget[postWrappers.getResults().size() + comments.size()];
        for(DisputePostWrapper p : postWrappers.getResults()) {
            List<PostReported> reportedReason = postsReason.get(p.getPostId());
            ReportedTarget t = new ReportedPost(p, reportedReason);
            t.type = "Post";
            reportedTargetArray[postSortingIdx.get(p.getPostId())] = t;
        }
        for(Comment c : comments) {
            List<PostReported> reportedReason = commentsReason.get(c.getId());
            ReportedTarget t = new ReportedComment(c, reportedReason);
            t.type = "Comment";
            reportedTargetArray[commentSortingIdx.get(c.getId())] = t;
        }

        pageResult.setResults(Arrays.asList(reportedTargetArray));
        pageResult.setTotalSize(objs.getTotalSize());
        return forward();
    }

    public Resolution getRelatedPostComment() {  
        if (!getCurrentUserAdmin() && !getAccessControl().getReportManagerAccess()) {
            return new ErrorResolution(403, "Need to login");
        }
        
        loadAvailableRegion();
        PageLimit pageLimit = getPageLimit("row");
        BlockLimit blockLimit = new BlockLimit((pageLimit.getPageIndex() - 1 ) * pageLimit.getPageSize(), pageLimit.getPageSize());
        PageResult<PostReportedWrapper> objs = postReportedDao.getRelatedPostComment(searchAuthorId, selStatus, selRegion, blockLimit);
        
        List<Long> postIds = new ArrayList<Long>();
        List<Long> commentIds = new ArrayList<Long>();
        for(PostReportedWrapper obj : objs.getResults()) {
            if(obj.getRefType().equals("Post"))
                postIds.add(obj.getRefId());
            else if(obj.getRefType().equals("Comment"))
                commentIds.add(obj.getRefId());
        }
        
        List<Circle> bcDefaiultCircles = circleService.getBcDefaultCircle("en_US");
        Map<Long, String> defaultTypeNameMap = new HashMap<Long, String>();
        PageResult<DisputePostWrapper> postWrappers = postIdToPostView(postIds.size(), postIds, defaultTypeNameMap, timeFormat);
        List<Comment> comments = commentService.findPostByIds(commentIds);
        
        Map<Long, List<PostReported>> postsReason = postService.getReportedPostReason(selStatus, postIds);
        Map<Long, List<PostReported>> commentsReason = commentService.getReportedCommentReason(selStatus, commentIds);
        
        for(DisputePostWrapper p : postWrappers.getResults()) {
            List<PostReported> reportedReason = postsReason.get(p.getPostId());
            ReportedTarget t = new ReportedPost(p, reportedReason);
            t.type = "Post";
            pageResult.add(t);
        }
        for(Comment c : comments) {
            List<PostReported> reportedReason = commentsReason.get(c.getId());
            ReportedTarget t = new ReportedComment(c, reportedReason);
            t.type = "Comment";
            pageResult.add(t);
        }

        pageResult.setTotalSize(objs.getTotalSize());//postsReason.size() + commentsReason.size());
        return new ForwardResolution("ReportedPost-route.jsp");
    }
    
    public Resolution handleReported() {
        if (!getCurrentUserAdmin() && !getAccessControl().getReportManagerAccess()) {
            return new ErrorResolution(403, "Need to login");
        }
        
        HttpSession session = getContext().getRequest().getSession();
        User reviewer = null;
        if(session == null) 
            return new ErrorResolution(403, "Need to login");
        
        String token = (String) getContext().getRequest().getSession().getAttribute("token");
        if(token != null && token.length() > 0) {
            Session loginSession = sessionDao.findByToken(token);
            reviewer = userDao.findById(loginSession.getUserId());
        }
        
        if(reviewer == null)
            return new ErrorResolution(403, "Need to login");
        
        Boolean success = false;
        List<Long> ids = new ArrayList<Long>();
        ids.add(targetId);
        Throwable postError = null;
        switch(targetType)
        {
        case "Post":
        {
            List<Post> relPosts = postService.findPostByIds(ids);
            if(relPosts.size() <= 0)
                return new ErrorResolution(400, "Bad Request");
            PageResult<PostReported> pt = postReportedDao.findByTarget(relPosts.get(0), new BlockLimit(0,1));
            if(pt.getTotalSize() <= 0) {
                reason = PostReportedReason.Other.toString();
                postService.reportPost(reviewer.getId(), targetId, reason);                
            }
            else {
                reason = pt.getResults().get(0).getReason().toString();
            }
            postError = postService.handleReportPost(targetId, reviewer, result, remark);
            if(postError.getMessage().contains("Succeed to handle the post"))
            	success = true;
            else
            	success = false;
            break;
        }
        case "Comment":
        {
            List<Comment> relComments = commentService.findPostByIds(ids);
            if(relComments.size() <= 0)
                return new ErrorResolution(400, "Bad Request");
            PageResult<PostReported> pt = postReportedDao.findByTarget(relComments.get(0), new BlockLimit(0,1));
            if(pt.getTotalSize() <= 0) {
                reason = PostReportedReason.Other.toString();
                commentService.reportComment(reviewer.getId(), targetId, reason);           
            }
            else {
                reason = pt.getResults().get(0).getReason().toString();
            }
                
            success = commentService.handleReportComment(targetId, reviewer, result, remark);
            break;
        }
        default:
            break;
        }
        
        if(success) {
            if(result.equals(PostReportedResult.Banned.toString())) {
                mailInappropPostCommentService.send(targetType, targetId, reason);
				if (targetType.equals("Post"))
					return json(postError.getMessage());
			}
            return new StreamingResolution("text/html", "OK");
        }
        else
            return new ErrorResolution(400, "Bad Request");
    }
    
    public Resolution reportContestPost() {
    	return json(postService.reportContestPost(targetId, null));
    }
}
