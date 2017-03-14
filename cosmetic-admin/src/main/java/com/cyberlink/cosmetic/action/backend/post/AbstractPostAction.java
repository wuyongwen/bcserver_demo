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

import org.apache.commons.lang3.tuple.Triple;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.DPWCircle;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.File;
import com.cyberlink.cosmetic.modules.post.result.PostWrapperUtil;
import com.cyberlink.cosmetic.modules.post.service.AsyncPostUpdateService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AbstractPostAction extends AbstractAction {
    
    @SpringBean("post.PostService")
    protected PostService postService;
    
    @SpringBean("post.asyncPostUpdateService")
    protected AsyncPostUpdateService asyncPostUpdateService;
    
    @SpringBean("post.LikeService")
    protected LikeService likeService;
    
    @SpringBean("user.UserDao")
    protected UserDao userDao;
    
    @SpringBean("circle.circleTypeDao")
    protected CircleTypeDao circleTypeDao;
    
    @SpringBean("user.SubscribeDao")
    protected SubscribeDao subscribeDao;
    
    @SpringBean("post.PostViewDao")
    protected PostViewDao postViewDao;
    
    @SpringBean("post.PostDao")
    protected PostDao postDao;
    
    @SpringBean("web.objectMapper")
    protected ObjectMapper objectMapper;
    
    protected PageResult<DisputePostWrapper> wrapDisputePostViewResult(PageResult<PostView> views, Map<Long, String> defaultTypeNameMap, String timeFormat) {
        final PageResult<DisputePostWrapper> r = new PageResult<DisputePostWrapper>();
        r.setTotalSize(views.getTotalSize());
        
        List<Long> postIds = new ArrayList<Long>(0);
        Set<Long> toLoadCircleIds = new HashSet<Long>();
        try {
            for(PostView view : views.getResults()) {
                if(view == null)
                    continue;
                DisputePostWrapper tmp = objectMapper.readValue(view.getMainPost(), DisputePostWrapper.class);
                tmp.setRegionTime(timeFormat);
                PostViewAttr postAttr = view.getAttribute();
                tmp.setLikeCount(postAttr.getLikeCount());
                tmp.setCommentCount(postAttr.getCommentCount());
                tmp.setCircleInCount(postAttr.getCircleInCount());
                r.add(tmp);
                postIds.add(tmp.getPostId());
                List<DPWCircle> dpwCircles = tmp.getCircles();
                if(dpwCircles != null) {
                    for(DPWCircle cTmp : dpwCircles)
                        toLoadCircleIds.add(cTmp.getCircleTypeId());
                }
                List<File> files = tmp.getAttachments().getFiles();
                for(File f : files)
                    f.fillMetadata(objectMapper.readValue(f.getMetadata(), ObjectNode.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        List<CircleType> loadedCircleTypes = circleTypeDao.findByIds(toLoadCircleIds.toArray(new Long[toLoadCircleIds.size()]));
        Map<Long, Long> typeGroupMap = new HashMap<Long, Long>();
        for(CircleType ct : loadedCircleTypes) {
            typeGroupMap.put(ct.getId(), ct.getCircleTypeGroupId());
        }
        for (final DisputePostWrapper pw : r.getResults()) {   
            List<DPWCircle> dwpCircles = pw.getCircles();
            List<DPWCircle> updatedwpCircles = new ArrayList<DPWCircle>();
            if(dwpCircles != null) {
                for(DPWCircle dwpCir : dwpCircles) {
                    if(!typeGroupMap.containsKey(dwpCir.getCircleTypeId()))
                        continue;
                    Long typeGroupId = typeGroupMap.get(dwpCir.getCircleTypeId());
                    if(!defaultTypeNameMap.containsKey(typeGroupId))
                        continue;
                    dwpCir.translateCircleName = defaultTypeNameMap.get(typeGroupId);
                    updatedwpCircles.add(dwpCir);
                }
                pw.setCircles(updatedwpCircles);
            }
        }
        
        return r;
    }
    
    private class RunnableLoadPostView implements Runnable {
        private List<Long> postIds;
        public RunnableLoadPostView(List<Long> postIds) {
            this.postIds = postIds;
        }
        
        public void run() {
            if(postIds == null || postIds.size() <= 0)
                return;
            try {
                Connection conn = Jsoup.connect("http://" + Constants.getWebsiteWrite() + "/api/v3.0/post/list-post-by-circle.action")
                        .data("loadPostView", "");
                for(Long postId : postIds) {
                    conn.data("loadPostViewIds", String.valueOf(postId));
                }
                conn.ignoreContentType(true).post();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }        
    }

    private PostView getPostView(Boolean isWriteable, Long postId, Long creatorId, String mainPost, String subPosts, PostViewAttr attribute) {
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
    
    protected PageResult<DisputePostWrapper> postIdToPostView(Integer totalSize, List<Long> resultList, Map<Long, String> defaultTypeNameMap, String timeFormat) {
        List<Long> missPostIds = new ArrayList<Long>();
        Map<Long, PostView> postViews = postViewDao.getViewMapByPostIds(resultList);
        for(Long id : postViews.keySet()) {
            PostView v = postViews.get(id);
            if(v != null)
                continue;
            missPostIds.add(id);
        }
        
        if(missPostIds.size() > 0) {
            List<Post> missPosts = postService.findPostByIds(missPostIds);
            PageResult<Post> missPostResult = new PageResult<Post>();
            missPostResult.setResults(missPosts);
            missPostResult.setTotalSize(missPosts.size());
            PageResult<MainPostSimpleWrapper> r = PostWrapperUtil.wrapSimplePostResult(missPostResult, null, null, null);
            try {
                Boolean isWriteable = Constants.getWebsiteIsWritable().equals("true"); 
                for(MainPostSimpleWrapper tmp : r.getResults()) {
                    String mainPostView = objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(tmp);
                    PostViewAttr pAttr = new PostViewAttr();
                    pAttr.setLikeCount(tmp.getLikeCount());
                    pAttr.setCommentCount(tmp.getCommentCount());
                    pAttr.setCircleInCount(tmp.getCircleInCount());
                    PostView tmpView = getPostView(isWriteable, tmp.getPostId(), tmp.getCreator().getUserId(), mainPostView, null, pAttr);
                    postViews.put(tmpView.getPostId(), tmpView);
                }
                if(!isWriteable) {
                    asyncPostUpdateService.asyncRun(new RunnableLoadPostView(missPostIds));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        PageResult<PostView> postViewResult = new PageResult<PostView>();
        postViewResult.setTotalSize(totalSize);
        postViewResult.setResults(new ArrayList<PostView>(postViews.values()));
        return wrapDisputePostViewResult(postViewResult, defaultTypeNameMap, timeFormat);
    }
    
    public static class DisputePostWrapper extends MainPostSimpleWrapper {        
        private static final long serialVersionUID = 3861074198033340021L;
        
        private String twTime;
        private Date processScoreDate;
        private String noticeIconUrl;
        private List<String> noticeIconUrls;
        private String extPostLink;
        private Long popularity;
        private String descCirName = null;
        private String circleTypeIdStr = null;
        
        public DisputePostWrapper() {
            super();
        }
        
        public DisputePostWrapper(Post post, List<FileItem> userItems, List<Object> fileItems, List<Circle> circles, Map<String, String> translatedCircleName, String timeFormat) {
            super(post, userItems, fileItems, circles, translatedCircleName, null, null);
            setRegionTime(timeFormat);
        }

        public void setRegionTime(String timeFormat) {
            String twTimeZoneId = "GMT+8";
            TimeZone twTimeZone = TimeZone.getTimeZone(twTimeZoneId);
            SimpleDateFormat twCustomDateFormatter = new SimpleDateFormat(timeFormat);
            twCustomDateFormatter.setTimeZone(twTimeZone);
            setTwTime(twCustomDateFormatter.format(createdTime));
        }

        @JsonView(DisputePostView.class)
        public String getTwTime() {
            return twTime;
        }

        public void setTwTime(String twTime) {
            this.twTime = twTime;
        }
        
        @JsonView(DisputePostView.class)
        private String getPostIdStr() {
            return postId.toString();
        }
        
        public void setProcessScoreDate(Date processScoreDate) {
            this.processScoreDate = processScoreDate;
        }
        
        @JsonView(DisputePostView.class)
        public Date getProcessScoreDate() {
            return processScoreDate;
        }
        
        public void setNoticeIconUrl(String noticeIconUrl) {
            this.noticeIconUrl = noticeIconUrl;
        }
        
        @JsonView(DisputePostView.class)
        public String getNoticeIconUrl() {
            return noticeIconUrl;
        }
        
        public void setNoticeIconUrls(List<String> noticeIconUrls) {
            this.noticeIconUrls = noticeIconUrls;
        }
        
        @JsonView(DisputePostView.class)
        public String getExtPostLink() {
            return extPostLink;
        }
        
        public void setExtPostLink(String extPostLink) {
            this.extPostLink = extPostLink;
        }
        
        @JsonView(DisputePostView.class)
        public List<String> getNoticeIconUrls() {
            return noticeIconUrls;
        }
        
        public Long getPopularity() {
            return popularity;
        }

        @JsonView(DisputePostView.class)
        public void setPopularity(Long popularity) {
            this.popularity = popularity;
        }
        
        @JsonView(DisputePostView.class)
        public String getDescCirName() {
            if(descCirName == null) {
                if(getCircles().size() > 0)
                    descCirName = getCircles().get(0).getTranslateCircleName();
                else
                    descCirName = "Other";
            }
            return descCirName;
        }
        
        public void setDescCirName(String descCirName) {
            this.descCirName = descCirName;
        }

        @JsonView(DisputePostView.class)
        public String getCircleTypeIdStr() {
            if(circleTypeIdStr == null) {
                if(getCircles().size() > 0)
                    circleTypeIdStr = getCircles().get(0).getCircleTypeId().toString();
                else
                    circleTypeIdStr = "0";
            }
            return circleTypeIdStr;
        }
        
        public void setCircleTypeIdStr(String circleTypeIdStr) {
            this.circleTypeIdStr = circleTypeIdStr;
        }
    }
}
