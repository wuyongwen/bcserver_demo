package com.cyberlink.cosmetic.modules.post.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleSubscribe;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute.CircleAttrType;
import com.cyberlink.cosmetic.modules.post.dao.CommentDao;
import com.cyberlink.cosmetic.modules.post.dao.LikeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.model.Comment;
import com.cyberlink.cosmetic.modules.post.model.Like;
import com.cyberlink.cosmetic.modules.post.model.Like.TargetType;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.service.CommentService;
import com.cyberlink.cosmetic.modules.post.service.DeleteUserService;
import com.cyberlink.cosmetic.modules.post.service.LikeService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.product.dao.ProductCommentDao;
import com.cyberlink.cosmetic.modules.product.model.ProductComment;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.event.UserDeleteEvent;
import com.cyberlink.cosmetic.modules.user.model.Subscribe;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;
import com.cyberlink.cosmetic.modules.user.model.UserAttr;

public class DeleteUserServiceImpl extends AbstractService implements DeleteUserService{
	
	private UserDao userDao;
	private CommentService commentService;
	private CommentDao commentDao;
	private ProductCommentDao productCommentDao;
	private LikeService likeService;
	private LikeDao likeDao;
	private PostViewDao postViewDao;
	private PostAttributeDao postAttributeDao;
	private TransactionTemplate transactionTemplate;
    private SubscribeDao subscribeDao;
	private CircleSubscribeDao circleSubscribeDao;
    private CircleAttributeDao circleAttributeDao;
    private CircleDao circleDao;
    private PostService postService;
    private PostDao postDao;
    private UserAttrDao userAttrDao;
    
    public void setPostService (PostService postService) {
        this.postService = postService;
    }
    
    public PostDao getPostDao() {
		return postDao;
	}

	public void setPostDao(PostDao postDao) {
		this.postDao = postDao;
	}

	public SubscribeDao getSubscribeDao() {
		return subscribeDao;
	}

	public void setSubscribeDao(SubscribeDao subscribeDao) {
		this.subscribeDao = subscribeDao;
	}

	public CircleSubscribeDao getCircleSubscribeDao() {
		return circleSubscribeDao;
	}

	public void setCircleSubscribeDao(CircleSubscribeDao circleSubscribeDao) {
		this.circleSubscribeDao = circleSubscribeDao;
	}

	public CircleAttributeDao getCircleAttributeDao() {
		return circleAttributeDao;
	}

	public void setCircleAttributeDao(CircleAttributeDao circleAttributeDao) {
		this.circleAttributeDao = circleAttributeDao;
	}

	public CircleDao getCircleDao() {
		return circleDao;
	}

	public void setCircleDao(CircleDao circleDao) {
		this.circleDao = circleDao;
	}
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setCommentService(CommentService commentService) {
	    this.commentService = commentService;
	}
	
	public CommentDao getCommentDao() {
		return commentDao;
	}

	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}
	
	public ProductCommentDao getProductCommentDao() {
		return productCommentDao;
	}

	public void setProductCommentDao(ProductCommentDao productCommentDao) {
		this.productCommentDao = productCommentDao;
	}

	public LikeDao getLikeDao() {
		return likeDao;
	}

	public void setLikeDao(LikeDao likeDao) {
		this.likeDao = likeDao;
	}

	public void setLikeService(LikeService likeService) {
	    this.likeService=  likeService;
	}
	
	public PostViewDao getPostViewDao() {
		return postViewDao;
	}

	public void setPostViewDao(PostViewDao postViewDao) {
		this.postViewDao = postViewDao;
	}

	public PostAttributeDao getPostAttributeDao() {
		return postAttributeDao;
	}

	public void setPostAttributeDao(PostAttributeDao postAttributeDao) {
		this.postAttributeDao = postAttributeDao;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public UserAttrDao getUserAttrDao() {
		return userAttrDao;
	}

	public void setUserAttrDao(UserAttrDao userAttrDao) {
		this.userAttrDao = userAttrDao;
	}

	Queue<Long> userQueue = new LinkedList<Long>();
	RunnableDeleteUser deletetUserRunable;
	private Thread deletetUserThread;
	
	private class RunnableDeleteUser implements Runnable {
		private Boolean isRunning = Boolean.FALSE;
		private String taskStatus = "Delete User Idle...";

		@Override
		public void run() {
			if (isRunning)
				return;
			isRunning = Boolean.TRUE;
			logger.info("Delete User Start");
			do{
				Long userId = userQueue.poll();
				if (userId != null) {
					try{
					    publishDurableEvent(new UserDeleteEvent(userId));
						// UnLike All Comment
						batchDeleteSelfLike(userId, TargetType.Comment, PostAttrType.CommentLikeCount);
						// UnLike All Post
						batchDeleteSelfLike(userId, TargetType.Post, PostAttrType.PostLikeCount);
						
						// Delete All Comment
						// Update Post Comment Count
						batchDeletePostCommet(userId, PostTargetType.POST);
						batchDeletePostCommet(userId, PostTargetType.COMMENT);
						batchDeleteProductCommet(userId);
						
						// Unfollow All User
						batchUnfollowUser(userId);
						
						// Delete be Following
						batchDeleteBeFollowing(userId);

						// Unfollow All Circle
						batchUnfollowCircle(userId);
						
						// Delete Circle
						// Delete Post
						// Update User Liked List
						batchDeleteCircle(userId);
						
						// Delete User Attribute
						userAttrDao.deleteByUserId(userId);
						
						// Delete EventUser?
						// Delete Notify?
						// Update Solr User Search Index?
						// Update FriendShip (OK if not delete)
						
						// Delete User
						// Delete Account
						// Delete Session
					
					
					} catch (Exception e){
						logger.error(e.getMessage());
					}
				} else
					stop();
				// todo
				
			} while(isRunning);
			deletetUserThread = null;
			isRunning = Boolean.FALSE;
			taskStatus = "Delete User Idle...";
			logger.info("All Users have been delete. Delete User Stop");
		}
		
		public void start() {
			if (deletetUserThread == null) {
				deletetUserThread = new Thread(this, "DeleteUserEx");
				deletetUserThread.start();
			}
		}
		
		public void stop() {
			//deletetUserThread.interrupt();
			isRunning = Boolean.FALSE;
		}
		
		public String getTaskStatus() {
			return taskStatus;
		}
		
		public Boolean getIsRunning() {
			return isRunning;
		}
	}
	
	private void batchDeletePostCommet(final Long userId, final String targetType) {
		logger.info("start delete post comment");
		int offset = 0;
        int limit = 50;
        do {
        	final PageResult<Comment> result = commentDao.findByUserId(userId, targetType, new BlockLimit(offset, limit));
        	try {
	        	if (result.getResults().size() <= 0)
	        		break;
	        	
	        	// delete comment
	        	transactionTemplate.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(TransactionStatus status) {
					    commentService.deleteComments(userId, targetType, result.getResults());
						return true;
					}      		
	        	});
	        	
	        	Thread.sleep(1000);
        	} catch(Exception e) {
        		offset += limit;
        	}
        	
        	if(limit > result.getTotalSize())
        		break;
        	
        }while(true);
	}
	
	private void batchDeleteProductCommet(Long userId) {
		logger.info("start delete product comment");
		int offset = 0;
        int limit = 50;
        
        final PageResult<ProductComment> productCommentResult = productCommentDao.findByCreatorId(userId, Long.valueOf(offset), Long.valueOf(limit));
        do{
	        try{
	        	// delete product comment
		    	transactionTemplate.execute(new TransactionCallback<Long>() {
					@Override
					public Long doInTransaction(TransactionStatus status) {
						for(ProductComment productComment : productCommentResult.getResults()) {
							try {
								productComment.setIsDeleted(Boolean.TRUE);
								productCommentDao.update(productComment);
							} catch (Exception e) {
								if (productComment.getId() != null)
									logger.error("delete product comment fail,product commentId: " + String.valueOf(productComment.getId()));
								else
									logger.error("delete product comment fail");
								logger.error(e.getMessage());
								continue;
							}
			        	}
						return null;
					}      		
		    	});
		    	
		    	if(productCommentResult.getTotalSize() < limit)
		    		break;
	    	} catch(Exception e) {
	    		offset += limit;
	    	}
        }while(true);
	}
	
	private void batchUnfollowUser(final Long deleteUserId) {
		logger.info("start unfollow user");
		int offset = 0;
        int limit = 50;
        
		List<Long> userId = subscribeDao.findBySubscriber(deleteUserId, null);
		do{
			List<Long> subList = userId.subList(offset, Math.min(offset + limit, userId.size()));
			final List<Subscribe> subscribeList = subscribeDao.findBySubscriberAndSubscribees(deleteUserId, null, subList.toArray(new Long[subList.size()]));
			final List<Long> deleteList = new ArrayList<Long>();
			transactionTemplate.execute(new TransactionCallback<List<Throwable>>() {
	            @Override
	            public List<Throwable> doInTransaction(TransactionStatus status) {
	                for (Subscribe subscribe : subscribeList) {
	                	if (!subscribe.getIsDeleted()) {
	                		deleteList.add(subscribe.getId());
	        	            userAttrDao.decreaseNonNullValue(subscribe.getSubscribeeId(), "FOLLOWER_COUNT");
	                	}
	                }
	            	return null;
	            }
	        });
			subscribeDao.batchDelete(deleteList);
			
			offset += limit;
			if (offset >= userId.size())
				break;
		} while(true);
	}
	
	private void batchDeleteBeFollowing(final Long deleteUserId) {
		logger.info("start delete be following");
		int offset = 0;
        int limit = 50;
        
		List<Long> userId = subscribeDao.findBySubscribee(deleteUserId, null);
		do{
			List<Long> subList = userId.subList(offset, Math.min(offset + limit, userId.size()));
			final List<Subscribe> subscribeList = subscribeDao.findBySubscribeeAndSubscribers(deleteUserId, null, subList.toArray(new Long[subList.size()]));
			final List<Long> deleteList = new ArrayList<Long>();
			transactionTemplate.execute(new TransactionCallback<List<Throwable>>() {
	            @Override
	            public List<Throwable> doInTransaction(TransactionStatus status) {
	                for (Subscribe subscribe : subscribeList) {
	                	if (!subscribe.getIsDeleted()) {
	                		deleteList.add(subscribe.getId());
	        	        	userAttrDao.decreaseNonNullValue(subscribe.getSubscriberId(), "FOLLOWING_COUNT");
	                	}
	                }
	            	return null;
	            }
	        });
			subscribeDao.batchDelete(deleteList);
			
			offset += limit;
			if (offset >= userId.size())
				break;
		} while(true);
	}

	private void batchUnfollowCircle(final Long deleteUserId) {
		logger.info("start unfollow circle");

		final List<CircleSubscribe> circleSubscribes = circleSubscribeDao.findSubscribeByUserId(deleteUserId);
        transactionTemplate.execute(new TransactionCallback<List<Throwable>>() {
            @Override
            public List<Throwable> doInTransaction(TransactionStatus status) {
        		for(CircleSubscribe circleSubscribe : circleSubscribes) {       
        			if (!circleSubscribe.getIsDeleted()) {
        				circleSubscribe.setIsDeleted(true);
        				circleSubscribeDao.update(circleSubscribe);
        				circleAttributeDao.createOrUpdateCircleAttr(circleSubscribe.getCircle(), CircleAttrType.FollowerCount, "-1", false);
        			}
                }
            	return null;
            }
        });
        subscribeDao.bacthDeleteBySubscriber(deleteUserId, SubscribeType.Circle);
	}

	private void batchDeleteCircle(final Long deleteUserId) {
		logger.info("start delete circle"); 
		int offset = 0;
        int limit = 50;
        List<Long> userIds = new ArrayList<Long>();
        userIds.add(deleteUserId);
        do{
    		final PageResult<Circle> circles = circleDao.findByUserIds(userIds, true, new BlockLimit(offset, limit));
    		if(circles.getResults().size() <= 0)
    		    break;
    		transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(TransactionStatus status) {
                    for(Circle c : circles.getResults()) {
                        c.setIsDeleted(true);
                        circleDao.update(c);
                        postService.deletePostByCircle(deleteUserId, c.getIsSecret(), c.getId());
                    }
                    return true;
                }
            });
    		try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
    		offset += limit;
    		if(offset > circles.getTotalSize())
    		    break;
        } while(true);
        circleSubscribeDao.bacthDeleteByCircleCreator(deleteUserId);
	}

	private void batchDeleteSelfLike(final Long userId, final TargetType refType, final PostAttrType attrRefType) {
		logger.info("start delete like, refType: " + refType);
		int offset = 0;
        int limit = 50;      
        do {
        	final PageResult<Like> result = likeDao.findByUserId(userId, refType, false, new BlockLimit(offset, limit));        	
        	try {
        		if (result.getResults().size() <= 0)
            		break;
        		
	        	transactionTemplate.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(TransactionStatus status) {
				        likeService.unlikeTargets(userId, refType, result.getResults());
				        return true;
					}      		
	        	});
	        	
	        	Thread.sleep(1000);
        	} catch(Exception e) {
        		offset += limit;
        	}
        	
        	if(limit > result.getTotalSize())
        		break;
        	
        }while(true);
	}

	@Override
	public void startAutoPostThread() {
		if (deletetUserRunable == null)
			deletetUserRunable = new RunnableDeleteUser();
		deletetUserRunable.start();		
	}

	@Override
	public void stopAutoPostThread() {
		if (deletetUserRunable != null) {
			deletetUserRunable.stop();
			deletetUserRunable = null;
		}
		
	}

	@Override
	public void pushUser(Long userId) {
		userQueue.offer(userId);
		logger.info(getStatus());
		
	}

	@Override
	public String getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
	
}