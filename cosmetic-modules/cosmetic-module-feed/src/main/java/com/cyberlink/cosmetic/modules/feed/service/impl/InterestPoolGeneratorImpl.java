package com.cyberlink.cosmetic.modules.feed.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.ScrollableResults;

import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.feed.model.PoolPost;
import com.cyberlink.cosmetic.modules.feed.model.PoolType;
import com.cyberlink.cosmetic.modules.feed.repository.InterestPoolRepository;
import com.cyberlink.cosmetic.modules.feed.service.InterestPoolGenerator;

import net.sourceforge.stripes.integration.spring.SpringBean;

public class InterestPoolGeneratorImpl extends AbstractService implements InterestPoolGenerator {

	private static String TAG = "[InterestPoolService]";
	private static int BATCH_SIZE = 100;
	
	@SpringBean("post.PostAttributeDao")
    private PostAttributeDao postAttributeDao;
	
	@SpringBean("post.PostDao")
    private PostDao postDao;
	
	@SpringBean("post.UserDao")
	private UserDao userDao;
	
	@SpringBean("common.localeDao")
	private LocaleDao localeDao;
	
	@SpringBean("feed.InterestPoolRepository")
	private InterestPoolRepository poolRepository;
	
	private Map<String, Map<String, Double>> currentPostMap = null;
	private int localePostMapFilled = 0;
	private boolean EndOfPost = false;
	
	public void setPostAttributeDao(PostAttributeDao dao) {
		this.postAttributeDao=dao;
	}
	
	public void setPostDao(PostDao dao) {
		this.postDao=dao;
	}
	
	public void setUserDao(UserDao dao) {
		this.userDao=dao;
	}
	
	public void setLocaleDao(LocaleDao dao) {
		this.localeDao=dao;
	}
	
	public void setPoolRepository(InterestPoolRepository repository) {
		this.poolRepository = repository;
	}
	
	@Override
	public void generate() {
		//System.out.println(TAG+"start");

		Date beginDate = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(beginDate); 
		c.add(Calendar.DATE, -7);
		//c.add(Calendar.DATE, -30);
		beginDate = c.getTime();
		int offset = 0, size = BATCH_SIZE;
		
		List<String> availableLocale = new ArrayList<String>();
		availableLocale.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
		final Map<String, Map<String, Double>> localePostMap = new HashMap<String,Map<String, Double>>();
		for (int i = 0 ; i < availableLocale.size(); i++) {
			localePostMap.put(availableLocale.get(i), new HashMap<String, Double>());
		}
		
		//fill up top liked post by every locale
		localePostMapFilled = 0;
		EndOfPost = false;
		while (localePostMapFilled < availableLocale.size() && !EndOfPost) {
			BlockLimit blockLimit = new BlockLimit(offset, size);
	        blockLimit.addOrderBy("attrType", false);
	        postAttributeDao.doOnTopLikedPostAfter(beginDate, blockLimit, new ScrollableResultsCallback() {
	            @Override
	            public void doInHibernate(ScrollableResults sr) {
	                int i = 0;
	                while (sr.next()) {
	                    if ((++i) % BATCH_SIZE == 0) {
	                    	postAttributeDao.clear();
	                    }
	                    final Object[] o = sr.get();
	                    final Long postId = (Long) o[0];
	                    final Long postlikeCount = (Long) o[1];
	                    final Date postCreatedTime = (Date) o[2];
	                    
	        			Post post = postDao.findById(postId);
	        			User user = post.getCreator();
	        			if (user == null)
	        				continue;
	        			Long creatorId = user.getId();
	        	        Long circleId = post.getCircleId();
	        	        if(circleId == null)
                            continue;
	        	        
	        	        // add into every user's pool
	        	        PoolPost pp = new PoolPost(postId, creatorId, circleId);
	        	        Double score = getScore(postlikeCount, postCreatedTime);
        	        	String locale = post.getLocale();
        	        	Map<String, Double> postMap = localePostMap.get(locale);
        	        	if (postMap != null) {
	        	        	if (postMap.size() < PoolType.Interest.getMaxlength()){
	        	        		postMap.put(pp.getValueInPool(), score.doubleValue());
	        	        		if (postMap.size() == PoolType.Interest.getMaxlength())
	        	        			localePostMapFilled++;	// mark locale has filled 
	        	        	}
        	        	}
	                }
	                EndOfPost = (i == 0);
	            }
	        });
	        offset += BATCH_SIZE;
		}
		
		poolRepository.updateAll(localePostMap);
		//System.out.println(TAG+"end");
		currentPostMap = localePostMap;
	}
	
	private Double getScore(Long postLikeCount, Date postCreatedTime) {
		return postLikeCount.doubleValue();
	}
	
	@Override
	public void generateForUser(Long userId, boolean doClean) {
		if (currentPostMap != null) {
			if (doClean)
				poolRepository.cleanUser(userId);
			poolRepository.updateUser(userId, currentPostMap);
		}
	}	


}
