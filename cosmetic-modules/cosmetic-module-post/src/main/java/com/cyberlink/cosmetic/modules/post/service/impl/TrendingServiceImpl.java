package com.cyberlink.cosmetic.modules.post.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.hibernate.ScrollableResults;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostNewDao;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreTrendDao;
import com.cyberlink.cosmetic.modules.post.event.PostCategoryUpdateEvent;
import com.cyberlink.cosmetic.modules.post.event.TrendUserGroupUpdateEvent;
import com.cyberlink.cosmetic.modules.post.model.PostNew;
import com.cyberlink.cosmetic.modules.post.event.PersonalTrendEvent;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute;
import com.cyberlink.cosmetic.modules.post.model.PostScore.ResultType;
import com.cyberlink.cosmetic.modules.post.model.PostScoreTrend;
import com.cyberlink.cosmetic.modules.post.model.TrendPool;
import com.cyberlink.cosmetic.modules.post.model.TrendPool.AddWorker;
import com.cyberlink.cosmetic.modules.post.model.TrendPool.BacthAdd;
import com.cyberlink.cosmetic.modules.post.model.TrendPool.CommittedCallback;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolInfo;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolType;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.repository.TrendingRepository;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.post.service.TrendingService;
import com.google.common.collect.ImmutableList;

public class TrendingServiceImpl extends AbstractService implements 
    TrendingService {

    private TrendingRepository trendingRepository;
    private PostService postService;
    private PostNewDao postNewDao;
    private CircleTypeDao circleTypeDao;
    private LocaleDao localeDao;
    private TransactionTemplate transactionTemplate;
    private PostAttributeDao postAttributeDao;
    private PostScoreTrendDao postScoreTrendDao;
    private static final long TS_OF_2014 = 1388534400000l;
    private static final int BATCH_QUERY_LIKE_SIZE = 100;
    private static Map<String, Map<Long, String>> circleNameMap = null;
    private List<String> ignoreCircleType = ImmutableList.of("OTHER", "HOW-TO");
    
    public void setTrendingRepository(TrendingRepository trendingRepository) {
        this.trendingRepository = trendingRepository;
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }
    
    public void setPostNewDao(PostNewDao postNewDao) {
        this.postNewDao = postNewDao;
    }
    
    public void setCircleTypeDao(CircleTypeDao circleTypeDao) {
        this.circleTypeDao = circleTypeDao;
    }
    
    public void setLocaleDao(LocaleDao localeDao) {
        this.localeDao = localeDao;
    }
    
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public void setPostAttributeDao(PostAttributeDao postAttributeDao) {
		this.postAttributeDao = postAttributeDao;
	}

	public void setPostScoreTrendDao(PostScoreTrendDao postScoreTrendDao) {
		this.postScoreTrendDao = postScoreTrendDao;
	}

	@Override
    public Set<String> getTrendUserList(Long shardId) {
    	if(!Constants.getPersonalTrendEnable())
            return null;
        
        return trendingRepository.getUserList(shardId);
    }
    
    @Override
    public Boolean addPostCategory(Long userId, Long postId, String circleType) {
    	if(!Constants.getPersonalTrendEnable())
            return null;
        
    	if(userId == null || postId == null)
            return false;
    	
        List<PostNew> pns = postNewDao.findByPost(postId, false);
        for(PostNew pn : pns) {
            if (pn != null && circleTypeDao.exists(pn.getCircleTypeId())) {
            	circleType = circleTypeDao.findById(pn.getCircleTypeId()).getDefaultType();
            }
            
            if (circleType == null || circleType.isEmpty())
            	return false;
            
            //trendingRepository.addToUserList(userId);
            //trendingRepository.addPostCategory(userId, circleType);
            publishDurableEvent(new PostCategoryUpdateEvent(userId, circleType));
        }
        return true;
    }
    
    @Override
    public List<String> getPostCategoryList(Long userId) {
    	if(!Constants.getPersonalTrendEnable())
            return null;
    	
    	return trendingRepository.getCategoryList(userId);
    }
    
    @Override
    public Boolean updateUserGroup() {
        if(!Constants.getPersonalTrendEnable())
            return null;
        
        List<Long> shardList =  trendingRepository.getShardList();
        if (shardList == null || shardList.isEmpty()) {
        	logger.error("updateUserGroup error: shardList empty");
        	return false;
        }
        
        for (Long shardId : shardList) {
        	Set<String> users = trendingRepository.getUserList(shardId);
        	trendingRepository.removeUserList(shardId);
        	Iterator<String> it = users.iterator();

			while (it.hasNext()) {
				try {
					Long userId = Long.parseLong(it.next());
					List<String> categories = trendingRepository.getCategoryList(userId);
					String group = "null";
					
					if (categories.size() < 20)
						group = "null";
					else {
						Map<String, Integer> countMap = new HashMap<String, Integer>();
						int maxCount = 0;
						String maxType = "";
						
						for (String type : categories) {
							int count;
							if (countMap.containsKey(type))
								count = countMap.get(type) + 1;
							else
								count = 1;
							countMap.put(type, count);

							if (count > maxCount) {
								maxCount = count;
								maxType = type;
							}
						}
						
						if (maxCount > 20 * 0.5)
							group = getUserGroup(maxType, true);
						else if (maxCount > 20 * 0.15)
							group = getUserGroup(maxType, false);
						else
							group = "null";
					}
					//trendingRepository.updateUserGroup(userId, group);
					publishDurableEvent(new TrendUserGroupUpdateEvent(userId, group));
					
				} catch (Exception e) {
					logger.error("updateUserGroup error: " + e.getMessage());
				}
			}
        }
        return true;
    }

    @Override
    public String getUserGroup(Long userId) {
        if(!Constants.getPersonalTrendEnable())
            return null;
        
        String group = trendingRepository.getUserGroup(userId);
        if (group == null || group.isEmpty())
        	group = "null";
        return group;
    }

    @Override
    public void regenerateTrendList(String locale, List<TrendPoolType> pools, Date beginTime, Date endTime) {
        if(!Constants.getPersonalTrendEnable())
            return;
        TrendServiceCommittedCallback ccb = new TrendServiceCommittedCallback() {

            @Override
            public void committing(TrendPoolType type, String locale,
                    String circleKey, Map<Long, Double> val) {
                if(!contains(type, locale, circleKey))
                    return;
                publishDurableEvent(PersonalTrendEvent.CreateAddEvent(type, locale, circleKey, 1L, val));
            }

            @Override
            public void committed(TrendPoolType type, String locale,
                    String circleKey) {
                if(!contains(type, locale, circleKey))
                    return;
                publishDurableEvent(PersonalTrendEvent.CreateTrimEvent(type, locale, circleKey, 1L, 
                        getValue(type, locale, circleKey).maxSize));
                publishDurableEvent(PersonalTrendEvent.CreateSwapEvent(type, locale, circleKey, 
                        getValue(type, locale, circleKey).curIdx, 1L));
            }
        };
        listNewPostByCircle(locale, pools, ccb, beginTime, endTime);
    }
    
    @Override
    public void updateTrendList(String locale, List<TrendPoolType> pools, Date beginTime, Date endTime) {
        if(!Constants.getPersonalTrendEnable())
            return;
        TrendServiceCommittedCallback ccb = new TrendServiceCommittedCallback() {

            @Override
            public void committing(TrendPoolType type, String locale,
                    String circleKey, Map<Long, Double> val) {
                if(!contains(type, locale, circleKey))
                    return;
                publishDurableEvent(PersonalTrendEvent.CreateAddEvent(type, locale, circleKey, 0L, val));
            }

            @Override
            public void committed(TrendPoolType type, String locale,
                    String circleKey) {
                if(!contains(type, locale, circleKey))
                    return;
                publishDurableEvent(PersonalTrendEvent.CreateTrimEvent(type, locale, circleKey, 0L, 
                        getValue(type, locale, circleKey).maxSize));
                publishDurableEvent(PersonalTrendEvent.createMergeEvent(type, locale, circleKey, 0L, 
                        getValue(type, locale, circleKey).curIdx, getValue(type, locale, circleKey).nextIdx,
                        getValue(type, locale, circleKey).curCursor));
            }
            
        };
        List<TrendPoolType> updatablePools = new ArrayList<TrendPoolType>();
        for(TrendPoolType tp : pools) {
            if(tp.getUpdateMax() > 0L)
                updatablePools.add(tp);
        }
        listNewPostByCircle(locale, updatablePools, ccb, beginTime, endTime);
    }
    
    @Override
    public void updateTrendListCursor(List<TrendPoolType> pools) {
        if(!Constants.getPersonalTrendEnable())
            return;
        
        for(TrendPoolType type : pools) {
            if(type.getCursorStep() <= 0L)
                continue;
            Map<String, Map<Long, String>> refCirMap = getAllCircleNameMap();
            for(String locale : refCirMap.keySet()) {
                List<String> circleKeys = new ArrayList<String>(refCirMap.get(locale).values());
                circleKeys.add("null");
                for(String circleKey : circleKeys) {
                    TrendPoolInfo pInfo = trendingRepository.getTrendPoolInfo(type, locale, circleKey);
                    if(pInfo == null)
                        continue;
                    pInfo.stepCursor(type.getCursorStep());
                    Map<Long, Double> map = trendingRepository.shuffleTrendRange(type, locale, circleKey, pInfo.getPoolCursor(), 
                            type.getShuffleCount());
                    publishDurableEvent(PersonalTrendEvent.CreateAddEvent(type, locale, circleKey, pInfo.getIdx(), map));
                    publishDurableEvent(PersonalTrendEvent.CreateUpdateCursorEvent(type, locale, circleKey, pInfo.getPoolCursor()));
                }
            }
        }
    }
    
    @Override
    public Long listTrending(String sortBy, String locale, Long circleTypeId, String group, Long offset, Long limit, List<Long> result) {
        if(!Constants.getPersonalTrendEnable())
            return null;
        
        if("Popularity".equals(sortBy)) {
            return listTrendByGroup(locale, group, offset, limit, result);
        }

        return listTTrend(locale, circleTypeId, offset, limit, result);
    }
    
    private Long listTTrend(String locale, Long circleTypeId, Long offset, Long limit, List<Long> result) {
        if(circleTypeId == null)
            return trendingRepository.getTrendingList(TrendPoolType.TGen, locale, null, 1L, offset, limit, result);
        return trendingRepository.getTrendingList(TrendPoolType.TCat, locale, circleTypeId.toString(), 1L, offset, limit, result);
    }
    
    private Long listTrendByGroup(String locale, String group, Long offset, Long limit, List<Long> result) {
        if(!Constants.getPersonalTrendEnable())
            return null;
        
        String circleType = null;
        Boolean isHeavy = false;
        Triple<TrendPoolType, String, Double> [] tPoolToQuery = new Triple[2];
        Pair<String, Boolean> groupType = parseUserGroup(group);
        if(groupType != null) {
            circleType = groupType.getLeft();
            isHeavy = groupType.getRight();
        }

        if(circleType != null && !circleType.equalsIgnoreCase("null")) {
            if(isHeavy) {
                tPoolToQuery[0] = Triple.of(TrendPoolType.SGenCat, circleType, 0.33D);
                tPoolToQuery[1] = Triple.of(TrendPoolType.SCat, circleType, 0.67D);
            }
            else {
                tPoolToQuery[0] = Triple.of(TrendPoolType.SGenCat, circleType, 0.67D);
                tPoolToQuery[1] = Triple.of(TrendPoolType.SCat, circleType, 0.33D);
            }
        }
        else {
            tPoolToQuery[0] = Triple.of(TrendPoolType.SGen, null, 1.0D);
        }
        
        List<Long> [] resultList = new List[2];
        Long previousPoolSize = 0L;
        Double previousPoolRatio = 0.0D;
        Long fillForPrevious = 0L;
        for(int idx = tPoolToQuery.length - 1; idx >= 0; idx--) {
            resultList[idx] = new ArrayList<Long>();
            if(tPoolToQuery[idx] == null)
                continue;
            
            resultList[idx] = new ArrayList<Long>();
            Pair<Long, Long> tSz = getTrend(tPoolToQuery[idx].getLeft(), locale, tPoolToQuery[idx].getMiddle(), 
                    offset, limit, tPoolToQuery[idx].getRight(), previousPoolSize, previousPoolRatio, fillForPrevious, resultList[idx]);
            if(tSz == null)
                return null;
            fillForPrevious = tSz.getRight();
            previousPoolSize += tSz.getLeft();
            previousPoolRatio += tPoolToQuery[idx].getRight();
        }

        List<Long> ps = new ArrayList<Long>();
        interleaveArray(resultList[0], resultList[1], result);
        return (long) Integer.MAX_VALUE;
    }
    
    private Pair<Long, Long> getTrend(TrendPoolType type, String locale, String circleKey, Long offset, Long limit, 
            Double ratio, Long previousPoolSize, Double previousPoolRatio, Long fillForPrevious, List<Long> result) {
        TrendPoolInfo pInfo = trendingRepository.getTrendPoolInfo(type, locale, circleKey);
        if(pInfo == null)
            return null;
        Long allReturnedSize = (long) (offset * (ratio + previousPoolRatio));
        Long shouldReturnByPreviousPool = (long) (allReturnedSize * previousPoolRatio);
        Long padOffset = 0L;
        if(shouldReturnByPreviousPool > previousPoolSize)
            padOffset += shouldReturnByPreviousPool + previousPoolSize;
        Long toQueryOffset = padOffset + (long)(Math.round(offset * ratio));
        Long toQueryLimit = (long) (Math.round(limit * ratio)) + fillForPrevious;
        if(toQueryOffset > pInfo.getPoolSize())
            return Pair.of(pInfo.getPoolSize(), toQueryLimit);
        Long curPoolSize = trendingRepository.getTrendingList(type, locale, circleKey, pInfo.getIdx(), toQueryOffset, toQueryLimit, result); 
        return Pair.of(curPoolSize, toQueryLimit - result.size());
    }
    
    private void interleaveArray(List<Long> l1, List<Long> l2, List<Long> result) {
        if(result == null)
            return;
        
        if(l1 == null || l2 == null) {
            if(l1 == null) {
                result.addAll(l2);
                return;
            }
            result.addAll(l1);
            return;
        }
        
        List<Long> longerList;
        List<Long> shorterList;
        if(l1.size() >= l2.size()) {
            longerList = l1;
            shorterList = l2;
        }
        else {
            longerList = l2;
            shorterList = l1;
        }
            
        if(longerList.size() <= 0)
            return;
        if(shorterList.size() == 0) {
            result.addAll(longerList);
            return;
        }
        
        int step =  (int) Math.floor(longerList.size() / shorterList.size());
        for(int i = 0; i < longerList.size(); i++) {
            result.add(longerList.get(i));
            if((i + 1) % step == 0) {
                int si = (i / step) - 1;
                if(si < 0)
                    si = 0;
                if(shorterList.size() > si)
                    result.add(shorterList.get(si));
            }
        }
    }
    
    @Override
    public Boolean isInWhiteList(Long userId) {
        if(!Constants.getPersonalTrendEnable())
            return null;
        
        if(userId == null)
            return false;
        return trendingRepository.isInWhiteList(userId);
    }
    
    @Override
    public Long addToWhiteList(Long userId) {
        if(!Constants.getPersonalTrendEnable())
            return null;
        
        if(userId == null)
            return 0L;
        return trendingRepository.addToWhiteList(userId);
    }
    
    @Override
    public Set<String> getJoinerList() {
        if(!Constants.getPersonalTrendEnable())
            return null;
        
        return trendingRepository.getJoinerList();
    }
    
    @Override
    public Map<String, Map<Long, String>> getAllCircleNameMap() {
        if(circleNameMap != null)
            return circleNameMap;
        circleNameMap = new HashMap<String, Map<Long, String>>();
        Set<String> locales = localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE);
        for(String locale : locales) {
            List<CircleType> cts = circleTypeDao.listTypesByLocale(locale, true);
            for(CircleType ct : cts) {
                if(!circleNameMap.containsKey(ct.getLocale()))
                    circleNameMap.put(ct.getLocale(), new HashMap<Long, String>());
                if(circleNameMap.get(ct.getLocale()).containsKey(ct.getId()))
                    continue;
                if(ignoreCircleType.contains(ct.getDefaultType()))
                    continue;
                circleNameMap.get(ct.getLocale()).put(ct.getId(), ct.getDefaultType());
            }
        }
        return circleNameMap;
    }
    
    @Override
    @BackgroundJob
    public void importToPostScoreTrend(final String locale, final Long circleTypeId) {   	
    	TrendPoolInfo tpinfo = null;
    	String circleKey = null;
    	TrendPoolType tPooltype = TrendPoolType.SGen;
    	if (circleTypeId == null || circleTypeId == 0L) {
    		tpinfo = trendingRepository.getTrendPoolInfo(tPooltype, locale, null);
    	} else {
    		tPooltype = TrendPoolType.SCat;
    		circleKey = circleTypeDao.findById(circleTypeId).getDefaultType();
    		tpinfo = trendingRepository.getTrendPoolInfo(tPooltype, locale, circleKey);
    	}
    	if (tpinfo == null) {
    		return;
    	}
    	
    	long offset = 0;
    	long limit = 100;
    	do{
    		final List<Long> postIds = new ArrayList<Long>();
    		Long size = 0L;
	    	size = trendingRepository.getTrendingList(tPooltype, locale, circleKey, tpinfo.getIdx(), offset, limit, postIds);
	    	
	    	
	    	transactionTemplate.execute(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(TransactionStatus status) {
					
					for (Long postId : postIds) {
						try {
							PostAttribute paLike = postAttributeDao.findByTarget("Post", postId, PostAttrType.PostLikeCount);
							long likCount = 0l;
							if (paLike != null)
								likCount = paLike.getAttrValue();
							PostAttribute paCircleIn = postAttributeDao.findByTarget("Post", postId, PostAttrType.PostCircleInCount);
							long circleInCount = 0l;
							if (paCircleIn != null)
								circleInCount = paCircleIn.getAttrValue();
							int score = (int) (likCount + circleInCount * 3);
							
							PostScoreTrend pst = new PostScoreTrend();
							pst.setPostId(postId);
							pst.setPostLocale(locale);
							pst.setCircleTypeId(circleTypeId);
							pst.setScore(score);
							pst.setPoolType(PoolType.Trending);
							pst.setResultType(ResultType.Revive);
							pst.setReviewerId(1L);
							pst.setIsHandled(true);
							pst.setIsDeleted(false);
							postScoreTrendDao.create(pst);
						}catch (Exception e) {
							if (postId != null)
								logger.error("import to postScoreTrend fail, postId: " + String.valueOf(postId));
							else
								logger.error("import to postScoreTrend fail, postId is null");
							logger.error(e.getMessage());
							continue;
						}
					}
					return null;
				}      		
	    	});
	    	
	    	if (postIds.size() < limit)
	    		break;
	    	offset += limit;
	    	if (offset >= size)
	    		break;
	    	
    	} while(true);
    }
    
    private String getUserGroup(String circleType, Boolean isHeavy) {
        // circleType should be circleType.getDefaultType
        if(isHeavy)
            return "h_" + circleType;
        else
            return "l_" + circleType;
    }
    
    private Pair<String, Boolean> parseUserGroup(String group) {
        if(group == null)
            return null;

        String [] tokens = group.split("_");
        if(tokens.length < 2)
            return null;
        return Pair.of(tokens[1], tokens[0].equals("h_"));
    }
    
    private void bacthHandlePost(final AddWorker worker, final Map<Long, Object []> postIdMap) {
        Map<Long, Map<PostAttrType, Long>> postAttrMap = postService.listPostsAttr(
                new ArrayList<Long>(postIdMap.keySet()));
        for(Long pid : postIdMap.keySet()) {
            Object [] objs = postIdMap.get(pid);
            String locale = (String) objs[1];
            Long circleTypeId = (Long) objs[2];
            Long bonus = (Long) objs[3];
            Boolean forceHideInAll = (Boolean) objs[4];
            Long pop = 0L;
            if(postAttrMap.containsKey(pid))
                pop = getScore(postAttrMap.get(pid));
            Long timestamp = ((Date) objs[5]).getTime() - TS_OF_2014;
            if(forceHideInAll == null)
                forceHideInAll = false;
            if(bonus == null)
                bonus = 0L;
            worker.add(pid, locale, circleTypeId, bonus, forceHideInAll, pop, timestamp);
        }
        postIdMap.clear();
        postAttrMap.clear();
    }
    
    private void listNewPostByCircle(String locale, List<TrendPoolType> pools, CommittedCallback ccb, final Date beginTime, final Date endTime) {
        Map<String, Map<Long, String>> refCirMap = getAllCircleNameMap();
        Map<String, Map<Long, String>> singleLocMap = new HashMap<String, Map<Long, String>>();
        singleLocMap.put(locale, refCirMap.get(locale));
        TrendPool trendPool = new TrendPool(singleLocMap, pools);
        trendPool.bacthAddMergeTask(new BacthAdd() {

            @Override
            public void begin(final AddWorker worker) {
                postNewDao.doWithAllTrendPost(null, null, beginTime, endTime, new ScrollableResultsCallback() {
                    @Override
                    public void doInHibernate(ScrollableResults sr) {
                        Map<Long, Object []> postIdMap = new HashMap<Long, Object []>();
                        Long logIdx = 0L;
                        while (sr.next()) {
                            Object [] result = sr.get();
                            if(result.length < 6)
                                break;
                            Long postId = (Long) result[0];
                            postIdMap.put(postId, result);
                            if(postIdMap.keySet().size() >= BATCH_QUERY_LIKE_SIZE) {
                                bacthHandlePost(worker, postIdMap);
                                postNewDao.clear();
                            }
                            logIdx++;
                            if(logIdx % 10000L == 0) {
                                logger.error("Current adding : " + postId.toString());
                                logIdx = 0L;
                            }
                        }
                        if(postIdMap.keySet().size() > 0)
                            bacthHandlePost(worker, postIdMap);
                    }
                });
            }
        }, ccb);
    }
    
    private Long getScore(Map<PostAttrType, Long> postAttrMap) {
        Long score = 0L;
        if(postAttrMap == null)
            return score;
        
        if(postAttrMap.containsKey(PostAttrType.PostLikeCount))
            score += postAttrMap.get(PostAttrType.PostLikeCount);
        if(postAttrMap.containsKey(PostAttrType.PostCircleInCount))
            score += (3 * postAttrMap.get(PostAttrType.PostCircleInCount));
        return score;
    }

    private abstract class TrendServiceCommittedCallback implements CommittedCallback {
        private final Map<String, Info> poolMaxSize = new HashMap<String, Info>();
        protected class Info {
            final public Long maxSize;
            final public Long curIdx;
            final public Long nextIdx;
            final public Long curCursor;
            
            public Info(Long maxSize, Long curIdx, Long nextIdx, Long curCursor) {
                this.maxSize = maxSize;
                this.curIdx = curIdx;
                this.nextIdx = nextIdx;
                this.curCursor = curCursor;
            }
        }
        private String getKey(TrendPoolType type, String locale, String circleKey) {
            String k = type == null ? "null:" : type.toString() + ":";
            k += locale == null ? "null:" : locale + ":";
            k += circleKey == null ? "null:" : circleKey;
            return k;
        }
        
        protected Boolean contains(TrendPoolType type, String locale, String circleKey) {
            String k = getKey(type, locale, circleKey);
            return poolMaxSize.containsKey(k);
        }
        
        protected Info getValue(TrendPoolType type, String locale, String circleKey) {
            String k = getKey(type, locale, circleKey);
            if(poolMaxSize.containsKey(k))
                return poolMaxSize.get(k);
            
            Long dSize = type.getMaxPoolSize();
            Long dIdx = 1L;
            Long dNextIdx = 1L;
            Long dCurCursor = 0L;
            TrendPoolInfo info = trendingRepository.getTrendPoolInfo(type, locale, circleKey);
            if(info != null) {
                dSize = info.getPoolSize();
                dIdx = info.getIdx();
                dNextIdx = info.nextIdx();
                dCurCursor = info.getPoolCursor();
            }
            
            Info p = new Info(dSize, dIdx, dNextIdx, dCurCursor);
            poolMaxSize.put(k, p);
            return p;
        }
        
        @Override
        public Long getMaxPoolSize(TrendPoolType type, String locale, String circleKey,
                Long currentPoolSize) {
            return getValue(type, locale, circleKey).maxSize;
        }
        
        @Override
        public Long getScorePriority(TrendPoolType type, String locale,
                String circleKey) {
            if(type.getGroup() == 1)
                return 0L;
            Long ci = 100L - getValue(type, locale, circleKey).nextIdx;
            if(ci < 0L)
                ci = 0L;
            return ci;
        }
        
        @Override
        public abstract void committing(TrendPoolType type, String locale,
                String circleKey, Map<Long, Double> val);

        @Override
        public abstract void committed(TrendPoolType type, String locale,
                String circleKey);
    };
}