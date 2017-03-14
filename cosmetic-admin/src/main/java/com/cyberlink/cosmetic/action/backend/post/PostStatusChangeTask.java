package com.cyberlink.cosmetic.action.backend.post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreDao;
import com.cyberlink.cosmetic.modules.post.model.AppName;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostScore;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.ResultType;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.model.UserType;

@UrlBinding("/post/postStatusChange.action")
public class PostStatusChangeTask extends AbstractAction{
	@SpringBean("post.PostDao")
    private PostDao postDao;
	
	@SpringBean("post.PostService")
    private PostService postService;
	
	@SpringBean("post.PostScoreDao")
    private PostScoreDao postScoreDao;
	
	private static final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
	private int limit = 50;
	private int checkDuration = 5;
	private int timeOutMin = 5;
	
	public void setCheckDuration(int checkDuration) {
		this.checkDuration = checkDuration;
	}
	
	public int getCheckDuration() {
		return checkDuration;
	}
	
	public void setTimeOutMin(int timeOutMin) {
		this.timeOutMin = timeOutMin;
	}
	
	public int getTimeOutMin() {
		return timeOutMin;
	}
	
	@DefaultHandler
    public Resolution route() {
		Date end = new Date();
		Date start = new Date(end.getTime() - checkDuration * 60 * ONE_MINUTE_IN_MILLIS);
		
		int offset = 0;
		PageResult<Post> pageResult = postDao.findMainPostByCreatedDateAndStatus(start, end, PostStatus.Hidden, new BlockLimit(offset, limit));
		long startTime = System.currentTimeMillis();
		while (pageResult.getTotalSize() != 0) {
			List<Post> postList = pageResult.getResults();
			logger.info(String.format("update post status - starttime: %s, endtime: %s, offset: %d, totalSize: %d" , start.toString(), end.toString(), offset, pageResult.getTotalSize()));
			for (Post pt : postList) {
				//pt.setPostStatus(PostStatus.Published);
				//postDao.update(pt);
				PostApiResult<Post> updateResult = postService.updatePost(pt.getCreatorId(), null, pt.getId(), null, null, null, null, null, null, null, PostStatus.Published, null, null, null, null);
				if (updateResult == null)
					logger.info(String.format("PostId %d update Fail", pt.getId()));
				else if (UserType.Blogger.equals(pt.getCreator().getUserType())) {
					// push blogger's post to post score
					PostScore ps = new PostScore();
					ps.setPostId(pt.getId());
					ps.setPostLocale(pt.getLocale());
					AppName appNamge = pt.getAppName();
					if (appNamge != null)
						ps.setAppName(appNamge.toString());
					Circle postCircle = pt.getCircle();
					if(postCircle != null)
					    ps.setCircleTypeId(postCircle.getCircleTypeId());
					ps.setPostCreateDate(pt.getCreatedTime());
					ps.setScore(0);
					ps.setPoolType(PoolType.RetagScraped);
					ps.setResultType(null);
					ps.setReviewerId(null);
					ps.setIsHandled(null);
					ps = postScoreDao.create(ps);
					
					//2099-12-31 23:59:59
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						ps.setCreatedTime(sdf.parse("2099-12-31 23:59:59"));
					} catch (ParseException e) {
						logger.info(e.getMessage());
					}
					postScoreDao.update(ps);
				}
			}
			
			if (pageResult.getTotalSize() < limit)
				break;
			
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			if (totalTime > ONE_MINUTE_IN_MILLIS * timeOutMin) {
				logger.error("update post status timeout");
				break;
			}
			pageResult = postDao.findMainPostByCreatedDateAndStatus(start, end, PostStatus.Hidden, new BlockLimit(offset, limit));
		}
		
		return json("Update PostStatus Success");
    }
}