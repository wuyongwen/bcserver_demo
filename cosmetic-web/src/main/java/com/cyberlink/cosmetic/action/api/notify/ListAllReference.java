package com.cyberlink.cosmetic.action.api.notify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.Circle.UserCicleView;
import com.cyberlink.cosmetic.modules.file.model.ThumbnailType;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;
import com.cyberlink.cosmetic.modules.notify.dao.NotifyDao;
import com.cyberlink.cosmetic.modules.notify.model.Notify;
import com.cyberlink.cosmetic.modules.notify.model.NotifyType;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/notify/list-reference.action")
public class ListAllReference extends AbstractAction {
    @SpringBean("notify.NotifyDao")
    private NotifyDao notifyDao;

    @SpringBean("post.LikeService")
    private LikeService likeService;
    
    @SpringBean("post.CommentService")
    private CommentService commentService;
    
    @SpringBean("post.PostService")
    private PostService postService;

    @SpringBean("post.PostDao")
    private PostDao postDao;
	
    @SpringBean("circle.circleDao")
	private CircleDao circleDao;
    
	@SpringBean("circle.circleAttributeDao")
    private CircleAttributeDao circleAttributeDao;
	
	@SpringBean("circle.circleSubscribeDao")
    private CircleSubscribeDao circleSubscribeDao;

	@SpringBean("look.LookTypeDao")
	private LookTypeDao lookTypeDao;
	
    private Long nId;
    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
    private Long curUserId;
    
	@DefaultHandler
    public Resolution route() {
    	final Map<String, Object> results = new HashMap<String, Object>();
    	if (!notifyDao.exists(nId)) {
    		return new ErrorResolution(ErrorDef.InvalidNotifyId);
    	}
    	Notify notify = notifyDao.findById(nId);
    	if (NotifyType.getSenderGroupType().contains(notify.getNotifyType())) {
    		PageResult<Long> page = null;
    		page = notify.getIdListWithOffset(offset, limit);
    		if (NotifyType.getPostType().contains(notify.getNotifyType())) {
    			results.put("totalSize", page.getTotalSize());
    			getPostByIds(page.getResults(), results);
    		} else if (NotifyType.getCircleType().contains(notify.getNotifyType())) {
    			results.put("totalSize", page.getTotalSize());
    			getCircleByIds(page.getResults(), results);    
    			return json(results, UserCicleView.class);
    		}
    	}
    	return json(results);
    }
	
	private void getPostByIds(List<Long> postIds, Map<String, Object> results) {
		List<Post> posts = postDao.findByIds(postIds.toArray(new Long[postIds.size()]));
		Map<Long, Post> idMap = new HashMap<Long, Post>();
		Set<Long> lookTypeIds = new HashSet<Long>();
		for (Post p : posts) {
			idMap.put(p.getId(), p);      
			if(p.getLookTypeId() != null)
                lookTypeIds.add(p.getLookTypeId());
		}
        final PageResult<MainPostSimpleWrapper> r = new PageResult<MainPostSimpleWrapper>();

        Map<Long, Long> postLikedCount = likeService.checkLikeCount(PostTargetType.POST, postIds);
        Map<Long, Long> postCommentCount = commentService.checkCommentCount(PostTargetType.POST, postIds);
        Map<Long, List<Object>> postFileItems = postService.listFileItemByPosts(posts, ThumbnailType.Detail);//.List);
        Map<Long, List<Circle>> postCircles = postService.listCircleByPosts(posts);       
        List<Long> likedComment = likeService.getLikeTarget(curUserId, TargetType.Post, postIds);
        Map<Long, LookType> lookTypeMap = lookTypeDao.findMapByIds(lookTypeIds);
        
        for (Long id : postIds) {
            Post p = null;
            if (idMap.containsKey(id)) {
            	p = idMap.get(id);
            } else {
            	r.add(null);
            	continue;
            }
            
            LookType lt = lookTypeMap.get(p.getLookTypeId());
        	MainPostSimpleWrapper pw = new MainPostSimpleWrapper(p, null, postFileItems.get(p.getId()), postCircles.get(p.getId()), lt);
            if(likedComment.contains(p.getId()))
                pw.setIsLiked(true);
            if(postLikedCount.containsKey(p.getId()))
                pw.setLikeCount(postLikedCount.get(p.getId()));
            if(postCommentCount.containsKey(p.getId()))
                pw.setCommentCount(postCommentCount.get(p.getId()));
            r.add(pw);
        }
        results.put("results", r.getResults());
	} 

	private void getCircleByIds(List<Long> circleIds, Map<String, Object> results) {
		List<Circle> circles = circleDao.findByIds(circleIds.toArray(new Long[circleIds.size()]));
		Map<Long, Circle> idMap = new HashMap<Long, Circle>();
        List<Long> subcribedCircleIds = circleSubscribeDao.listSubcribeCircle(curUserId, circles);
        circles = circleAttributeDao.getCircleAttribute(circles, curUserId, subcribedCircleIds);
		for (Circle c : circles) {
			idMap.put(c.getId(), c);            
		}
		
		List<Circle> list = new ArrayList<Circle>();
		for (Long id : circleIds) {
			Circle r = null;
			if (idMap.containsKey(id)) {
            	r = idMap.get(id);
            }	
			list.add(r);
		}
		results.put("results", list);
	} 
	
	public Long getnId() {
		return nId;
	}

	public void setnId(Long nId) {
		this.nId = nId;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Long getLimit() {
		return limit;
	}

	public void setLimit(Long limit) {
		this.limit = limit;
	}

	public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

	
}
