package com.cyberlink.cosmetic.modules.post.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.ScrollableResults;

import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.modules.post.dao.CommentDao;
import com.cyberlink.cosmetic.modules.post.dao.LikeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.event.PostViewUpdateEvent;
import com.cyberlink.cosmetic.modules.post.model.Like;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetSubType;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.PostType;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.model.PostViewAttr;
import com.cyberlink.cosmetic.modules.post.repository.LikeRepository;
import com.cyberlink.cosmetic.modules.post.result.PostApiResult;
import com.cyberlink.cosmetic.modules.post.service.AsyncPostUpdateService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;

public class LikeServiceImpl extends AbstractService implements LikeService {

    private UserDao userDao;
    private LikeDao likeDao;
    private PostDao postDao;
    private CommentDao commentDao;
    private PostAttributeDao postAttributeDao;
    private PostViewDao postViewDao;
    private UserAttrDao userAttrDao;
    private LikeRepository likeRepository;
    private AsyncPostUpdateService asyncPostUpdateService;
    
    public void setPostAttributeDao(PostAttributeDao postAttributeDao) {
        this.postAttributeDao = postAttributeDao;
    }
    
    public void setPostViewDao(PostViewDao postViewDao) {
        this.postViewDao = postViewDao;
    }
    
    public void setUserAttrDao(UserAttrDao userAttrDao) {
        this.userAttrDao = userAttrDao;
    }
    
    public void setLikeDao(LikeDao likeDao) {
        this.likeDao = likeDao;
    }
    
    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }
    
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }
    
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    public void setLikeRepository(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }
    
    public void setAsyncPostUpdateService(AsyncPostUpdateService asyncPostUpdateService) {
        this.asyncPostUpdateService = asyncPostUpdateService;
    }
    
    @Override
    public PostApiResult <LikeServiceResult> unlikeTarget(Long userId, TargetType refType, Long refId)
    {          
        String iRefType;
        PostAttrType attrRefType = null;
        PostApiResult <LikeServiceResult> result = new PostApiResult <LikeServiceResult>();
        result.setResult(LikeServiceResult.LIKE_REL_FAILED);
        
        if(!userDao.exists(userId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
        
		if(refType == null) {
			result.setErrorDef(ErrorDef.InvalidPostTargetType);
            return result;
		}
        switch(refType) {
        case Post:
        {
            iRefType = PostTargetType.POST;
            attrRefType = PostAttrType.PostLikeCount;
            if(!postDao.exists(refId)) {
                result.setErrorDef(ErrorDef.InvalidPostTargetId);
                return result;
            }
            break;
        }
        case Comment:
        {
            iRefType = PostTargetType.COMMENT;
            attrRefType = PostAttrType.CommentLikeCount;
            if(!commentDao.exists(refId)) {
                result.setErrorDef(ErrorDef.InvalidPostTargetId);
                return result;
            }
            break;
        }
        default:
        {
            result.setErrorDef(ErrorDef.InvalidPostTargetType);
            return result;
        }
        }
        
        User user = userDao.findById(userId);
        Like like = likeDao.getLike(user, refType, refId);
        if(like != null) {
            if(!like.getIsDeleted()) {
                like.setIsDeleted(true);
                likeDao.update(like);
                updateLikeCount(iRefType, attrRefType, refId, -1);
                updateLikes(iRefType, refId, userId, false);
                result.setResult(LikeServiceResult.LIKE_REL_OK);
                return result;
            }
            
            result.setResult(LikeServiceResult.LIKE_REL_NOT_AFFECTED);
            return result;
        }
        
        result.setResult(LikeServiceResult.LIKE_REL_NOT_AFFECTED);
        return result;
    }
    
    @Override
    public void processUnlikeTarget(Long userId, String refType, Long refId) {
        likeRepository.unlike(userId, refType, refId);
    }
    
    @Override
    public void unlikeTargets(Long userId, TargetType refType, List<Like> likes)
    {          
        String iRefType;
        PostAttrType attrRefType = null;       
		if(refType == null)
			return; 
        switch(refType)
        {
            case Post:
            {
                iRefType = PostTargetType.POST;
                attrRefType = PostAttrType.PostLikeCount;
                break;
            }
            case Comment:
            {
                iRefType = PostTargetType.COMMENT;
                attrRefType = PostAttrType.CommentLikeCount;
                break;
            }
            default:
            {
                return;
            }
        }
        
        for(Like like : likes) {
            if(like != null) {
                if(like.getIsDeleted())
                    continue;
                like.setIsDeleted(true);
                likeDao.update(like);
                updateLikeCount(iRefType, attrRefType, like.getRefId(), -1);
                updateLikes(iRefType, like.getRefId(), userId, false);
            }
        }
    }
    
    @Override
    public void processUnlikeTargets(Long userId, String refType, List<Long> refIds) {
        if(userId == null) {
            for(Long refId : refIds)
                likeRepository.deleteByTargetId(refType, refId);
        }
        else {
            for(Long refId : refIds) {
                likeRepository.unlike(userId, refType, refId);
            }
        }
    }
    
    @Override
    public PostApiResult <LikeServiceResult> likeTarget(Long userId, TargetType refType, TargetSubType refSubType, Long refId)
    {                
        String iRefType;
        PostAttrType attrRefType;
        PostApiResult <LikeServiceResult> result = new PostApiResult <LikeServiceResult>();
        result.setResult(LikeServiceResult.LIKE_REL_FAILED);
        
        if(!userDao.exists(userId)) {
            result.setErrorDef(ErrorDef.InvalidUserId);
            return result;
        }
		if(refType == null) {
			result.setErrorDef(ErrorDef.InvalidPostTargetType);
            return result;
		}
        switch(refType) {
            case Post:
            {
                iRefType = PostTargetType.POST;
                attrRefType = PostAttrType.PostLikeCount;
                if(!postDao.exists(refId)) {
                    result.setErrorDef(ErrorDef.InvalidPostTargetId);
                    return result;
                }
                break;
            }
            case Comment:
            {
                iRefType = PostTargetType.COMMENT;
                attrRefType = PostAttrType.CommentLikeCount;
                if(!commentDao.exists(refId)) {
                    result.setErrorDef(ErrorDef.InvalidPostTargetId);
                    return result;
                }
                break;
            }
            default:
            {
                result.setErrorDef(ErrorDef.InvalidPostTargetType);
                return result;
            }
        }
        
        User user = userDao.findById(userId);
        Like like = likeDao.getLike(user, refType, refId);
        if(like != null) {
            if(like.getIsDeleted()) {
                like.setIsDeleted(false);
                likeDao.update(like);
                updateLikeCount(iRefType, attrRefType, refId, 1);
                updateLikes(iRefType, refId, userId, true);
                result.setResult(LikeServiceResult.LIKE_REL_OK);
                return result;
            }
            
            result.setResult(LikeServiceResult.LIKE_REL_NOT_AFFECTED);
            return result;
        }
        
        like = new Like();
        like.setShardId(userId);
        like.setRefId(refId);
        like.setRefType(refType);
        like.setRefSubType(refSubType);
        like.setUserId(userId);
        like.setUser(user);
        Like resultLike = likeDao.create(like);
        if(resultLike != null)
        {
            updateLikeCount(iRefType, attrRefType, refId, 1);
            updateLikes(iRefType, refId, userId, true);
            result.setResult(LikeServiceResult.LIKE_REL_OK);
            return result;
        }
        
        result.setErrorDef(ErrorDef.UnknownPostError);
        return result;
    }
    
    @Override
    public void processLikeTarget(Long userId, String refType, Long refId, Long createdTime) {
        likeRepository.like(userId, refType, refId, createdTime);
    }
    
    @Override
    public void processLikeTargets(List<Like> likes) {
        for(Like like : likes) {
            likeRepository.like(like.getUserId(), like.getRefType().toString(), like.getRefId(), like.getCreatedTime().getTime());
        }
    }
    
    private void updatePostViewLikeCount(Long postId, Long count) {
        if(!Constants.getIsPostCacheView())
            return;
        PostView postView = postViewDao.findByPostId(postId);
        if(postView == null)
            return;
        PostViewAttr postAttr = postView.getAttribute();
        if(postAttr == null)
            postAttr = new PostViewAttr();
        if(postAttr.getLikeCount() != null && postAttr.getLikeCount().equals(count))
            return;
        postAttr.setLikeCount(count);
        postView.setAttribute(postAttr);
        try {
            postViewDao.update(postView);
        }
        catch(Exception e) {
        }
        publishDurableEvent(new PostViewUpdateEvent(postId, postAttr));
    }
    
    private void updateLikeCount(String refType, PostAttrType attrRefType, Long refId, int diff) {
        if(diff == 0)
            return;
        PostAttrType attrType = PostAttrType.PostLikeCount;
        Boolean isPost = true;
        if(refType.equals("Comment")) {
            attrType = PostAttrType.CommentLikeCount;
            isPost = false;
        }
        Long finalValue = postAttributeDao.createOrUpdateAttrValue(refType, refId, attrType, diff);
        if(finalValue == null || finalValue < 0)
            return;
        if(isPost)
            updatePostViewLikeCount(refId, finalValue);
    }
    
    public void updateLikes(String refType, Long refId, Long userId, Boolean isLiked) {
        if(refType.equals(PostTargetType.COMMENT))
            return;
        publishDurableEvent(new PostViewUpdateEvent(refId, userId, isLiked));
    }
    
    @Override
    public List<Long> getLikeTarget(Long userId, TargetType refType, List<Long> targetIds) {
        List<Long> resultList = new ArrayList<Long>();
		if(refType == null)
			return resultList;
        if(userId != null && targetIds.size() > 0) {
            switch(refType) {
            case Post: {
                resultList = likeRepository.getLikes(userId, PostTargetType.POST, targetIds);
                break;
            }
            case Comment: {
                resultList = likeRepository.getLikes(userId, PostTargetType.COMMENT, targetIds);
                break;
            }
            default:
                return resultList;
            }
                        
            if(resultList == null) 
                resultList = likeDao.listLikedTarget(userId, refType, targetIds);
        }
        
        return resultList;
    }
    
    @Override
    public Map<Long, Long> checkLikeCount(String refType, List<Long> targetIds) {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();
        if(targetIds.size() > 0) {
            Long [] ids = targetIds.toArray(new Long[targetIds.size()]);
            PostAttrType attrType = PostAttrType.PostLikeCount;
            if(refType.equals("Comment"))
                attrType = PostAttrType.CommentLikeCount;
            resultMap = postAttributeDao.checkPostAttriButeByIds(refType, attrType, ids);
        }
        return resultMap;
    }

    @Override
    public Map<Long, Long> checkLikeCountWithDate(String refType, Date startTime, Date endTime, List<Long> targetIds) {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();
        if(targetIds.size() > 0) {
            Long [] ids = targetIds.toArray(new Long[targetIds.size()]);
            //resultMap = likeDao.getLikeCountByTargetsWithDate(refType, startTime, endTime, ids);
        }
        return resultMap;
    }
    
    @Override
    public Map<Long, Map<String, Long>> checkLikeRegionCountWithDate(String refType, Date startTime, Date endTime, List<Long> targetIds) {
        Map<Long, Map<String, Long>> resultMap = new HashMap<Long, Map<String, Long>>();
        if(targetIds.size() > 0) {
            Long [] ids = targetIds.toArray(new Long[targetIds.size()]);
            //resultMap = likeDao.getLikeRegionCountByTargetWithDate(refType, startTime, endTime, ids);
        }
        return resultMap;
    }
    
    @Override
    public PostApiResult <PageResult<User>> listLikeUsrByTarget(TargetType targetType, Long targetId, BlockLimit blockLimit) {
        PostApiResult <PageResult<User>> result = new PostApiResult <PageResult<User>>();

        if(targetType == null) {
            result.setErrorDef(ErrorDef.InvalidPostTargetType);
            return result;
        }

        PostAttrType likeAttrType;
        String likeTargetType;
		if(targetType == null) {
			result.setErrorDef(ErrorDef.InvalidPostTargetType);
            return result;
		}
        switch(targetType) {
            case Post :
            {
                likeAttrType = PostAttrType.PostLikeCount;
                if(!postDao.exists(targetId)) {
                    result.setErrorDef(ErrorDef.InvalidPostTargetId);
                    return result;
                }
                break;
            }
            case Comment:
            {
                likeAttrType = PostAttrType.CommentLikeCount;
                if(!commentDao.exists(targetId)) {
                    result.setErrorDef(ErrorDef.InvalidPostTargetId);
                    return result;
                }
                break;
            }
            default :
            {
                result.setErrorDef(ErrorDef.InvalidPostTargetType);
                return result;
            }
        }
        PageResult<User> r = new PageResult<User>();
        PageResult<Long> likerIds = likeRepository.getLikers(targetType.toString(), targetId, blockLimit);
        if(likerIds != null) {
            Map<Long, User> userMap = userDao.findUserMap(new HashSet<Long>(likerIds.getResults()));
            List<User> userList = new ArrayList<User>();
            for(Long cuId : likerIds.getResults()) {
                if(userMap.containsKey(cuId))
                    userList.add(userMap.get(cuId));
            }
            r.setResults(userList);
            r.setTotalSize(likerIds.getTotalSize());
        }
        else {
            PageResult<Like> list = likeDao.blockQueryWithoutSize(targetType, targetId, blockLimit);
            PostAttribute postAttr = postAttributeDao.findByTarget(targetType.toString(), targetId, likeAttrType);
            if(postAttr != null)
                list.setTotalSize(postAttr.getAttrValue().intValue());
            else
                list.setTotalSize(0);
            if(list == null || list.getResults().size() <= 0) {
                result.setResult(r);
                return result;
            }
            
            r.setTotalSize(list.getTotalSize());
            for(Like like : list.getResults()){
                r.add(like.getUser());
            }
        }
        
        result.setResult(r);
        return result;
    }
    
    @Override
    public PostApiResult <Long> listLikedTargetCount(TargetType targetType, TargetSubType targetSubType, Long userId) {
        PostApiResult <Long> result = new PostApiResult <Long>();
        if(targetType == null) {
			result.setErrorDef(ErrorDef.InvalidPostTargetType);
            return result;
		}
		switch(targetType) {
            case Post :
            {
                Long likeCount = null;
                UserAttr userAttr = userAttrDao.findByUserId(userId);
                if(userAttr != null)
                    likeCount = userAttr.getLikeCount();
                if(likeCount == null) {
                    likeCount = likeDao.hardGetLikedPostCount(userId, targetType, targetSubType);
                    if(targetSubType != null) {
						switch(targetSubType) {
	                    case YCL_LOOK: {
	                        asyncPostUpdateService.setUserAttr(userId, null, null, likeCount, null);
	                        break;
	                    }
	                    case HOW_TO: {
	                        asyncPostUpdateService.setUserAttr(userId, likeCount, null, null, null);
	                        break;
	                    }
	                    default:
	                        break;
	                    }
					}
                }
                
                result.setResult(likeCount);
                break;
            }
            case Comment:
            {
                result.setResult(likeDao.hardGetLikedPostCount(userId, targetType, null));
                break;
            }
            default :
            {
                result.setErrorDef(ErrorDef.InvalidPostTargetType);
                return result;
            }
        }
        
        return result;
    }
    
    @Override
    public PostApiResult<Integer> listLikedTargetId(TargetType targetType, TargetSubType targetSubType, Long userId, List<Long> result, BlockLimit blockLimit) {
        PostApiResult <Integer> apiResult = new PostApiResult <Integer>();
        if(targetType == null) {
			apiResult.setErrorDef(ErrorDef.InvalidPostTargetType);
            return apiResult;
		}
		switch(targetType) {
            case Post :
            {
                PageResult<Long> pgLikedId = likeRepository.getLikedTarget(userId, targetType.toString(), blockLimit);
                if(pgLikedId == null) {
                    Integer likeCount = null;
                    UserAttr userAttr = userAttrDao.findByUserId(userId);
                    if(userAttr != null) {
						if(targetSubType != null) {
                        switch(targetSubType) {
	                        case HOW_TO: {
	                            likeCount = userAttr.getLikeHowToCount() == null ? null : userAttr.getLikeHowToCount().intValue();
	                            break;
	                        }
	                        case YCL_LOOK: {
	                            likeCount = userAttr.getLikeCount() == null ? null : userAttr.getLikeCount().intValue();
	                            break;
	                        }
	                        default:
	                            break;
	                    	}
						}
                    }
                    
                    if(likeCount == null) {
                        likeCount = likeDao.hardGetLikedTargetId(userId, targetType, targetSubType, result, blockLimit);
                        if(targetSubType != null) {
							switch(targetSubType) {
	                        case YCL_LOOK: {
	                            asyncPostUpdateService.setUserAttr(userId, null, null, likeCount.longValue(), null);
	                            break;
	                        }
	                        case HOW_TO: {
	                            asyncPostUpdateService.setUserAttr(userId, likeCount.longValue(), null, null, null);
	                            break;
	                        }
	                        default:
	                            break;
	                        }
						}
                    }
                    else
                        likeDao.getLikedTargetIdWithoutSize(userId, targetType, targetSubType, result, blockLimit);
                    
                    apiResult.setResult(likeCount);
                }
                else {
                    result.addAll(pgLikedId.getResults());
                    apiResult.setResult(pgLikedId.getTotalSize());
                }
                break;
            }
            case Comment:
            {
                apiResult.setResult(likeDao.hardGetLikedTargetId(userId, targetType, targetSubType, result, blockLimit));
                break;
            }
            default :
            {
                apiResult.setErrorDef(ErrorDef.InvalidPostTargetType);
                return apiResult;
            }
        }
        
        return apiResult;
    }
    
    @Override
	public PageResult<User> getTopLikedUserByListIds(List<List<Long>> idLists,
			Long offset, Long limit) {
    	
    	PageResult<User> page = new PageResult<User>();
    	Long totalSize = Long.valueOf(0);
    	List<Long> allList = new ArrayList<Long>();
    	
    	Long newOffset= null;
    	for (List<Long> list: idLists) {
    		if (newOffset == null && (totalSize + list.size()) > offset) {
    			newOffset = Long.valueOf(offset - totalSize);
    		}
    		totalSize += list.size();
    		if (totalSize <= offset) {
    			continue;
    		}
    		
    		if ((allList.size() - newOffset) >= limit) {
    			continue;
    		}
    		allList.addAll(list);
    	}
    	
    	page.setTotalSize(totalSize.intValue());
    	if (totalSize == 0) {
    		page.setResults(new ArrayList<User>());
    		return page;
    	} else if (offset >= totalSize) {
        	page.setResults(new ArrayList<User>());
        	return page;
        } else if (allList.size() == 0) {
        	page.setResults(new ArrayList<User>());
    		return page;
        }
    	Map<Long, Long> countMap = postAttributeDao.getLikeCountByUserIds(allList); 
    	Map<Long, Long> promoteMap = postAttributeDao.getPromoteByUserIds(allList);  
    	
    	List<SortItem> sortIdList = new ArrayList<SortItem>();
    	long curOffset = 0;
    	for (List<Long> idList: idLists) {
    		curOffset += idList.size();
    		if (curOffset <= offset)
    			continue;
    		if (sortIdList.size() - newOffset >= limit)
    			break;
    		
    		List<SortItem> sortList = new ArrayList<SortItem>();
        	for (Long id : idList) {
        		Long score = Long.valueOf(0);
        		if (countMap.containsKey(id))
        			score += countMap.get(id);
        		if (promoteMap.containsKey(id))
        			score += promoteMap.get(id);
    			sortList.add( new SortItem(id, score));
        	}
        	Collections.sort(sortList, Collections.reverseOrder());
        	sortIdList.addAll(sortList);
    	}
    	sortIdList = sortIdList.subList(newOffset.intValue(), Math.min(newOffset.intValue()+limit.intValue(), sortIdList.size()));
    	List<Long> sortedList = new ArrayList<Long>();
    	Map<Long, Long> sortMap = new HashMap<Long, Long>();
    	long i = 0;
    	for (SortItem s : sortIdList) {
    		sortedList.add(s.id);
    		sortMap.put(s.id, i);
    		i++;
    	}
        List<User> userList = userDao.findByIds(sortedList.toArray(new Long [sortedList.size()]));
    	for (User u : userList) {
    		u.setSortValue(sortMap.get(u.getId()));
    	}
    	Collections.sort(userList);
    	page.setResults(userList);    		
    	return page;
	}
    
    @Override
	public PageResult<User> getTopLikedUserByUserIds(List<Long> idList,
			Long offset, Long limit) {
    	PageResult<User> page = new PageResult<User>();
    	page.setTotalSize(idList.size());
    	if (idList.size() == 0) {
    		page.setResults(new ArrayList<User>());
    		return page;
    	} if (offset >= idList.size()) {
        	page.setResults(new ArrayList<User>());
        	return page;
        } 
    	
    	Map<Long, Long> countMap = postAttributeDao.getLikeCountByUserIds(idList); 
    	Map<Long, Long> promoteMap = postAttributeDao.getPromoteByUserIds(idList);  
        List<SortItem> sortList = new ArrayList<SortItem>();
    	for (Long id : idList) {
    		Long score = Long.valueOf(0);
    		if (countMap.containsKey(id))
    			score += countMap.get(id);
    		if (promoteMap.containsKey(id))
    			score += promoteMap.get(id);
			sortList.add( new SortItem(id, score));

    	}
    	Collections.sort(sortList, Collections.reverseOrder());
    	sortList = sortList.subList(offset.intValue(), Math.min(offset.intValue()+limit.intValue(), sortList.size()));

    	List<Long> sortedList = new ArrayList<Long>(); 
    	for (SortItem s : sortList) {
    		sortedList.add(s.id);
    	}
        List<User> userList = userDao.findByIds(sortedList.toArray(new Long [sortedList.size()]));
    	for (User u : userList) {
    		Long score = Long.valueOf(0);
    		if (countMap.containsKey(u.getId()))
    			score += countMap.get(u.getId());
    		if (promoteMap.containsKey(u.getId()))
    			score += promoteMap.get(u.getId());
    		u.setSortValue(score);
    	}
    	Collections.sort(userList, Collections.reverseOrder());
    	page.setResults(userList);
    	return page;
	}

    private abstract class ScollableResultTemplate<T> implements ScrollableResultsCallback {
        public T result;
    }
    
    @Override
    public Long initRedisLikeBetween(Long next, Long count) {
        if(next == null)
            next = 0L;
        
        ScollableResultTemplate<Long> callback = new ScollableResultTemplate<Long>() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                int i = 0;
                long b = System.currentTimeMillis();
                while (sr.next()) {
                    if ((++i) % 100 == 0) {
                        logger.error("begin - end (" + i + "): "
                                + (System.currentTimeMillis() - b));
                        b = System.currentTimeMillis();
                        likeDao.clear();
                    }
                    final Object[] o = sr.get();
                    final Long likeId = (Long) o[0];
                    final Long postId = (Long) o[1];
                    final Long userId = (Long) o[2];
                    final Date createdTime = (Date) o[3];
                    likeRepository.like(userId, "Post", postId, createdTime.getTime());
                    this.result = likeId;
                }
            }
        };
        likeDao.doWithAllLikeBetween(TargetType.Post, next, count, callback);
        
        return callback.result;
    }
    
    private class SortItem implements Comparable<SortItem>{
    	public Long id;
    	public Long value;
    	SortItem(Long id, Long value) {
    		this.id = id;
    		this.value = value;
    	}
    	
		@Override
		public int compareTo(SortItem o) {
			if (this.value > o.value) {
				return 1;
			} else if (this.value == o.value) {
				return 0;
			} else {
				return -1;
			}				
		}
    	
    }
}
