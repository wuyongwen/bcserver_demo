package com.cyberlink.cosmetic.action.api.post;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.BeanLocator;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.result.MainPostBasicWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Creator;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.DPWCircle;
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

public class AbstractPostAction extends AbstractAction {
    
    @SpringBean("post.PostService")
    protected PostService postService;
    
    @SpringBean("post.asyncPostUpdateService")
    protected AsyncPostUpdateService asyncPostUpdateService;
    
    @SpringBean("post.LikeService")
    protected LikeService likeService;
    
    @SpringBean("user.UserDao")
    protected UserDao userDao;
    
    @SpringBean("circle.circleDao")
    protected CircleDao circleDao;
    
    @SpringBean("user.SubscribeDao")
    protected SubscribeDao subscribeDao;
    
    @SpringBean("post.PostViewDao")
    protected PostViewDao postViewDao;
    
    @SpringBean("web.objectMapper")
    protected ObjectMapper objectMapper;
    
    public class PageResultWithNext<T> implements Serializable {

        private static final long serialVersionUID = 8158709579758873953L;
        private Long next = 0L;
        private List<T> results = Collections.emptyList();
        
        public PageResultWithNext(Long next, PageResult<T> pgResult) {
            this.results = pgResult.getResults();
            this.next = next;
        }
        
        @JsonView(Views.Public.class)
        public Long getNext() {
            return next;
        }
        public void setNext(Long next) {
            this.next = next;
        }
        
        @JsonView(Views.Public.class)
        public List<T> getResults() {
            return results;
        }

        public void setResults(List<T> results) {
            this.results = results;
        }
    }
    
    public final Resolution mainPostJson(String groupId, PageResult<MainPostSimpleWrapper> simpleMainPost) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Class<?> view = Views.Public.class;
        if(Constants.enableNewMainPostJsonView()) {
            PageResult<MainPostBasicWrapper> tmp = wrapBasicPostViewResult(simpleMainPost);
            resultMap.put("results", tmp.getResults());
            resultMap.put("totalSize", tmp.getTotalSize());
            view = Views.Basic.class;
        }
        else {
            resultMap.put("results", simpleMainPost.getResults());
            resultMap.put("totalSize", simpleMainPost.getTotalSize());
        }
        if(groupId != null)
            resultMap.put("groupId", groupId);
        return json(resultMap, view);
    }
    
    public final Resolution mainPostJson(PageResult<MainPostSimpleWrapper> simpleMainPost) {
        return mainPostJson(null, simpleMainPost);
    }
    
    protected PageResult<MainPostBasicWrapper> wrapBasicPostViewResult(PageResult<MainPostSimpleWrapper> simpleMainPost) {
    	PageResult<MainPostBasicWrapper> basicMainPost = new PageResult<MainPostBasicWrapper>();
        List<MainPostBasicWrapper> results = new ArrayList<MainPostBasicWrapper>();
        for(MainPostSimpleWrapper mainPost : simpleMainPost.getResults()){
        	results.add(new MainPostBasicWrapper(mainPost));
        }
        basicMainPost.setResults(results);
        basicMainPost.setTotalSize(simpleMainPost.getTotalSize());
        return basicMainPost;
    }
    
    protected PageResult<MainPostSimpleWrapper> wrapSimplePostViewResult(PageResult<PostView> views, Long curUserId, Boolean defaultIsLiked, Boolean defaultIsFollowed) {
        final PageResult<MainPostSimpleWrapper> r = new PageResult<MainPostSimpleWrapper>();
        r.setTotalSize(views.getTotalSize());
        
        List<Long> postIds = new ArrayList<Long>(0);
        Set<Long> creatorIds = new HashSet<Long>();
        Set<Long> toLoadUserIds = new HashSet<Long>();
        Set<Long> toLoadCircleIds = new HashSet<Long>();
        try {
            for(PostView view : views.getResults()) {
                if(view == null)
                    continue;
                MainPostSimpleWrapper tmp = objectMapper.readValue(view.getMainPost(), MainPostSimpleWrapper.class);
                PostViewAttr postAttr = view.getAttribute();
                tmp.setLikeCount(postAttr.getLikeCount());
                tmp.setCommentCount(postAttr.getCommentCount());
                tmp.setCircleInCount(postAttr.getCircleInCount());
                tmp.setLookDownloadCount(postAttr.getLookDownloadCount());
                r.add(tmp);
                postIds.add(tmp.getPostId());
                creatorIds.add(tmp.getCreator().getUserId());
                toLoadUserIds.add(tmp.getCreator().getUserId());
                Creator sourPostCreator = tmp.getSourcePostCreator();
                if(sourPostCreator != null) {
                    toLoadUserIds.add(sourPostCreator.getUserId());
                }
                List<DPWCircle> dpwCircles = tmp.getCircles();
                if(dpwCircles != null) {
                    for(DPWCircle cTmp : dpwCircles)
                        toLoadCircleIds.add(cTmp.circleId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        Map<Long, User> loadedUser = userDao.findUserMap(toLoadUserIds);
        Map<Long, Circle> loadedCircle = circleDao.findCircleMap(toLoadCircleIds);
        if(curUserId != null) {
            List<Long> likedPost = null;
            if(defaultIsLiked == null)
                likedPost = likeService.getLikeTarget(curUserId, TargetType.Post, postIds);
            Set<Long> subcribeeIds = null;
            if(defaultIsFollowed == null)
                subcribeeIds = subscribeDao.findIdBySubscriberAndSubscribees(curUserId, SubscribeType.User, creatorIds.toArray(new Long[creatorIds.size()]));
            for (final MainPostSimpleWrapper pw : r.getResults()) {
                Creator postCreator = pw.getCreator();
                if(postCreator != null && loadedUser.containsKey(postCreator.getUserId())) {
                    pw.setPostCreatorByUser(loadedUser.get(postCreator.getUserId()));
                }
                Creator sourcePostCreator = pw.getSourcePostCreator();
                if(sourcePostCreator != null && loadedUser.containsKey(sourcePostCreator.getUserId())) {
                    pw.setSourcePostCreatorByUser(loadedUser.get(sourcePostCreator.getUserId()));
                }
                
                List<DPWCircle> dwpCircles = pw.getCircles();
                List<DPWCircle> updatedwpCircles = new ArrayList<DPWCircle>();
                if(dwpCircles != null) {
                    for(DPWCircle dwpCir : dwpCircles) {
                        if(loadedCircle.containsKey(dwpCir.circleId))
                            dwpCir = new DPWCircle(loadedCircle.get(dwpCir.circleId), null);
                        updatedwpCircles.add(dwpCir);
                    }
                    pw.setCircles(updatedwpCircles);
                }
                
                if(defaultIsLiked != null)
                    pw.setIsLiked(defaultIsLiked);
                else if(likedPost.contains(pw.getPostId()))
                    pw.setIsLiked(true);
                if(defaultIsFollowed != null)
                    pw.getCreator().setIsFollowed(defaultIsFollowed);
                else if(subcribeeIds.contains(pw.getCreator().getUserId()))
                    pw.getCreator().setIsFollowed(true);
            }
        }
        else {
            for (final MainPostSimpleWrapper pw : r.getResults()) {
                Creator postCreator = pw.getCreator();
                if(postCreator != null && loadedUser.containsKey(postCreator.getUserId())) {
                    pw.setPostCreatorByUser(loadedUser.get(postCreator.getUserId()));
                }
                Creator sourcePostCreator = pw.getSourcePostCreator();
                if(sourcePostCreator != null && loadedUser.containsKey(sourcePostCreator.getUserId())) {
                    pw.setSourcePostCreatorByUser(loadedUser.get(sourcePostCreator.getUserId()));
                }

                List<DPWCircle> dwpCircles = pw.getCircles();
                List<DPWCircle> updatedwpCircles = new ArrayList<DPWCircle>();
                if(dwpCircles != null) {
                    for(DPWCircle dwpCir : dwpCircles) {
                        if(loadedCircle.containsKey(dwpCir.circleId))
                            dwpCir = new DPWCircle(loadedCircle.get(dwpCir.circleId), null);
                        updatedwpCircles.add(dwpCir);
                    }
                    pw.setCircles(updatedwpCircles);
                }
                
                if(defaultIsLiked != null)
                    pw.setIsLiked(defaultIsLiked);
                else
                    pw.setIsLiked(false);
                if(defaultIsFollowed != null)
                    pw.getCreator().setIsFollowed(defaultIsFollowed);
                else if(pw.getCreator().getIsFollowed() != null)
                    pw.getCreator().setIsFollowed(false);
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
    
    protected PageResult<MainPostSimpleWrapper> postIdToPostView(Integer totalSize, List<Long> resultList, Long curUserId, Boolean defaultIsLiked, Boolean defaultIsFollowed) {
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
            PageResult<MainPostSimpleWrapper> r = PostWrapperUtil.wrapSimplePostResult(missPostResult, curUserId, null, null);
            try {
                Boolean isWriteable = Constants.getWebsiteIsWritable().equals("true"); 
                for(MainPostSimpleWrapper tmp : r.getResults()) {
                    String mainPostView = objectMapper.writer((PrettyPrinter)null).withView(Views.Public.class).writeValueAsString(tmp);
                    PostViewAttr pAttr = new PostViewAttr();
                    pAttr.setLikeCount(tmp.getLikeCount());
                    pAttr.setCommentCount(tmp.getCommentCount());
                    pAttr.setCircleInCount(tmp.getCircleInCount());
                    pAttr.setLookDownloadCount(tmp.getLookDownloadCount());
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
        return wrapSimplePostViewResult(postViewResult, curUserId, defaultIsLiked, defaultIsFollowed);
    }
}
