package com.cyberlink.cosmetic.action.backend.service.impl;

import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.Resolution;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.action.backend.service.PostTotalCountService;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public class PostTotalCountServiceImpl extends AbstractService implements
		PostTotalCountService {

	private UserDao userDao;
	private PostDao postDao;
	private PostAttributeDao postAttributeDao;
	private TransactionTemplate transactionTemplate;

	static final String CRONEXPRESSION = "0 5 14 * * ? *";
	
	static private Boolean isRunning = Boolean.TRUE;

	@Override
	public void start() {
		isRunning = Boolean.TRUE;
	}

	@Override
	public void stop() {
		isRunning = Boolean.FALSE;
	}

	@Override
	public String getStatus() {
		if (!isRunning)
			return "PostTotalCountService isn't running";
		else
			return "PostTotalCountService is running";
	}

	@Override
	@BackgroundJob(cronExpression = CRONEXPRESSION)
	public void exec() {
		if (!isRunning) {
			logger.info("PostTotalCountService isn't running");
			return;
		} else
			logger.info("PostTotalCountService is running");
		
		int offset = 0;
		int limit = 100;

		List<Long> userList = userDao.findIdByUserTypeWithoutStatus(
				UserType.getTotalPostType(), null);

		do {
			if (userList.size() <= 0)
				break;

			try {
				final List<Long> userIds = userList.subList(offset,
						Math.min(offset + limit, userList.size()));
				final Map<Long, Long> countMap = postDao
						.countByUserIds(userIds);
				transactionTemplate
						.execute(new TransactionCallback<Resolution>() {

							@Override
							public Resolution doInTransaction(
									TransactionStatus status) {
								for (Long userId : userIds) {
									if (countMap.containsKey(userId)) {
										postAttributeDao
												.createOrUpdatePostAttr(
														"User",
														userId,
														PostAttrType.PostTotalCount,
														countMap.get(userId),
														true, true);
									} else {
										postAttributeDao
												.createOrUpdatePostAttr(
														"User",
														userId,
														PostAttrType.PostTotalCount,
														(long) 0, true, true);
									}

								}
								return null;
							}

						});
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			offset += limit;
			if (offset > userList.size())
				break;

		} while (true);
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public PostDao getPostDao() {
		return postDao;
	}

	public void setPostDao(PostDao postDao) {
		this.postDao = postDao;
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

}