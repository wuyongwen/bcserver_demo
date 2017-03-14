package com.cyberlink.cosmetic.modules.circle.service.impl;

import java.util.List;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleSubscribeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute.CircleAttrType;
import com.cyberlink.cosmetic.modules.circle.service.CircleFollowReCountService;
import com.cyberlink.cosmetic.modules.user.dao.SubscribeDao;
import com.cyberlink.cosmetic.modules.user.model.Subscribe.SubscribeType;

public class CircleFollowReCountServiceImpl extends AbstractService implements CircleFollowReCountService{
	private SubscribeDao subscribeDao;
	private CircleAttributeDao circleAttributeDao;
	private CircleSubscribeDao circleSubscribeDao;
	private CircleDao circleDao;

	public SubscribeDao getSubscribeDao() {
		return subscribeDao;
	}

	public void setSubscribeDao(SubscribeDao subscribeDao) {
		this.subscribeDao = subscribeDao;
	}

	public CircleAttributeDao getCircleAttributeDao() {
		return circleAttributeDao;
	}

	public void setCircleAttributeDao(CircleAttributeDao circleAttributeDao) {
		this.circleAttributeDao = circleAttributeDao;
	}

	public CircleSubscribeDao getCircleSubscribeDao() {
		return circleSubscribeDao;
	}

	public void setCircleSubscribeDao(CircleSubscribeDao circleSubscribeDao) {
		this.circleSubscribeDao = circleSubscribeDao;
	}

	public CircleDao getCircleDao() {
		return circleDao;
	}

	public void setCircleDao(CircleDao circleDao) {
		this.circleDao = circleDao;
	}
	
	RunnableExecuteReCount reCountExecuteRunable;
	private Thread reCountExecuteThread;
	private int sleep = 100;
	private int offset = 0;
	private int limit = 100;
	
	private class RunnableExecuteReCount implements Runnable {
		private Boolean isRunning = Boolean.FALSE;
		private String taskStatus = "Circle Follow recount Idle...";
		
		@Override
		public void run() {
			if (isRunning)
				return;
			isRunning = Boolean.TRUE;
			logger.info("Circle Follow recount Start");
			do {
				PageResult<Circle> circles = circleDao.findAllCircle(new BlockLimit(offset, limit));
				taskStatus = String.format("current offset: %d, total circles: %d, sleep time : %d ms", offset, circles.getTotalSize(), sleep);
				logger.info(taskStatus);
				if(circles.getResults().size() <= 0)
					stop();
				
				for (Circle circle : circles.getResults()) {
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
					}
					String region = Constants.getPostRegion();
					List<CircleAttribute> cAttrPostCountCounts = circleAttributeDao.findCircleAttribute(region, circle, CircleAttrType.PostCount);
					// If didn't have PostCount attribute, this circle not be used. 
					if (cAttrPostCountCounts.isEmpty())
						continue;
					
					List<CircleAttribute> cAttrFollowerCounts = circleAttributeDao.findCircleAttribute(region, circle, CircleAttrType.FollowerCount);				
					// If circle IsSecret, set circle follow count = 0; 
					/*if (circle.getIsSecret()) {
						if (cAttrFollowerCounts.isEmpty())
							continue;
						for (CircleAttribute cAttrFollowerCount : cAttrFollowerCounts) {
							cAttrFollowerCount.setAttrValue("0");
							circleAttributeDao.update(cAttrFollowerCount);
						}
						continue;
					}*/	
					
					Long userFollowCount = Long.valueOf(subscribeDao.findBySubscribee(circle.getCreatorId(), SubscribeType.User, new BlockLimit(0, 0)).getTotalSize());
					Long circleFollowCount = (long) circleSubscribeDao.listByCircleId(circle.getId()).size();
					if (cAttrFollowerCounts.isEmpty() && (userFollowCount != 0 || circleFollowCount != 0))
						circleAttributeDao.createOrUpdateCircleAttr(circle, CircleAttrType.FollowerCount, String.valueOf(userFollowCount + circleFollowCount), true);
					else {
						for (CircleAttribute cAttrFollowerCount : cAttrFollowerCounts) {
							cAttrFollowerCount.setAttrValue(String.valueOf(userFollowCount + circleFollowCount));
							circleAttributeDao.update(cAttrFollowerCount);
						}
					}	
				}
				
				offset += limit;
				if(offset > circles.getTotalSize())
					stop();
				
			}while(isRunning);
			reCountExecuteThread = null;
			isRunning = Boolean.FALSE;
			taskStatus = "Circle Follow recount Idle...";
			offset = 0;
			logger.info("All of the circles recount follow finish. Circle Follow recount Stop");
		}
		
		public void start() {
			if (reCountExecuteThread == null) {
				reCountExecuteThread = new Thread(this, "CircleFollowReCountExecute");
				reCountExecuteThread.start();
			}
		}
		
		public void stop() {
        	isRunning = Boolean.FALSE;
        }
		
		public String getTaskStatus() {
			return taskStatus;
		}
		
		public Boolean getIsRunning() {
			return isRunning;
		}
	}

	@Override
	public void startReCountThread() {
		if (reCountExecuteRunable == null)
			reCountExecuteRunable = new RunnableExecuteReCount();
		reCountExecuteRunable.start();
	}

	@Override
	public void stopReCountThread() {
		if (reCountExecuteRunable != null) {
			logger.info("Circle Follow recount Force Stop!");
			logger.info(reCountExecuteRunable.getTaskStatus());
			reCountExecuteRunable.stop();
			reCountExecuteRunable = null;
		}
		logger.info("Circle Follow recount Stop");
	}

	@Override
	public String getStatus() {
		if (reCountExecuteRunable == null)
			return "There is no task executing.";
		
		if (reCountExecuteRunable.getIsRunning())
			return String.format("Circle Follow recount handling, " + reCountExecuteRunable.getTaskStatus());
		else
			return String.format("Circle Follow recount Idle, " + reCountExecuteRunable.getTaskStatus());
	}

	@Override
	public void setSleep(int sleep) {
		this.sleep = sleep;		
	}

	@Override
	public void setOffset(int offset) {
		if (reCountExecuteRunable != null && reCountExecuteRunable.getIsRunning())
			return;
		this.offset = offset;
	}

	@Override
	public void setLimit(int limit) {
		if (reCountExecuteRunable != null && reCountExecuteRunable.getIsRunning())
			return;
		this.limit = limit;
	}

	@Override
	public String getParam() {
		return String.format("sleep: %d, offset: %d, limit: %d", sleep, offset, limit);
	}
	
}