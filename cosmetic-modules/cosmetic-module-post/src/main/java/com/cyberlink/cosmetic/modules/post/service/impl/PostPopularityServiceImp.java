package com.cyberlink.cosmetic.modules.post.service.impl;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.event.post.DiscoverPostCreateEvent;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostNewDao;
import com.cyberlink.cosmetic.modules.post.dao.PostNewPoolDao;
import com.cyberlink.cosmetic.modules.post.event.PersonalTrendEvent;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostNew;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool.NewPoolGroup;
import com.cyberlink.cosmetic.modules.post.model.PostScore.CreatorType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.model.PostStatus;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolType;
import com.cyberlink.cosmetic.modules.post.service.PostPopularityService;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.UserType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

public class PostPopularityServiceImp extends AbstractService implements PostPopularityService, InitializingBean {
    
    protected ImmutableMap<UserType, Integer> userTypeBonus;
    protected ReleaseSetting releaseSetting;
    protected PostNewDao postNewDao;
    protected PostNewPoolDao postNewPoolDao;
    protected PostDao postDao;
    protected CircleDao circleDao;
    protected ObjectMapper objectMapper;

    enum DestType {
        PostNew, PostNewPool;
    }
    
    public static class ReleaseSetting implements Serializable {
        
        private static final long serialVersionUID = -8367560298227613113L;
        
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
        public static abstract class CountByGroup {
            abstract public Long getReleaseCount(Long totalPoolCount, Calendar currentDate, Boolean mustPush);
        }
        
        public static class UgcCount extends CountByGroup {
            @JsonView(Views.Public.class)
            final private LinkedHashMap<Integer, Integer> normalReleaseDuration;
            
            public UgcCount(LinkedHashMap<Integer, Integer> normalReleaseDuration) {
                this.normalReleaseDuration = normalReleaseDuration;
            }

            @Override
            public Long getReleaseCount(Long totalPoolCount,
                    Calendar currentDate, Boolean mustPush) {
                if(totalPoolCount == null || totalPoolCount <= 0L)
                    return 0L;
                if(mustPush != null && mustPush)
                    return 1L;
                
                Integer minCount = null;
                for(Integer idx : normalReleaseDuration.keySet()) {
                    if(idx > totalPoolCount)
                        break;
                    minCount = idx;
                }
                if(minCount == null)
                    return 0L;
                Integer duration = normalReleaseDuration.get(minCount);
                Integer dayHour = currentDate.get(Calendar.HOUR_OF_DAY) * 100;     
                Integer dayMinute =  (int) Math.round((currentDate.get(Calendar.MINUTE) / 60.0) * 100);
                Boolean isTime = (dayHour + dayMinute) % duration == 0;
                if(isTime)
                    return 1L;
                return 0L;
            }
        }
        
        public static class PgcCount  extends CountByGroup {
            @JsonView(Views.Public.class)
            private Integer offset = null;
            
            @JsonView(Views.Public.class)
            private Integer step = 20;
            
            public PgcCount() {
                
            }
            
            public PgcCount(Integer offset, Integer step) {
                this.offset = offset;
                this.step = step;
            }
            @Override
            public Long getReleaseCount(Long totalPoolCount,
                    Calendar currentDate, Boolean mustPush) {
                if(totalPoolCount == null || totalPoolCount <= 0L)
                    return 0L;
                
                if(offset == null)
                    return 0L;
                
                if(mustPush != null && mustPush)
                    return 1L;
                
                Integer dayMinute =  currentDate.get(Calendar.MINUTE) + (currentDate.get(Calendar.HOUR_OF_DAY) * 60);
                Integer calOffset = (dayMinute-offset) % step;
                if(calOffset.compareTo(0) == 0)
                    return 1L;
                
                return 0L;
            }
            
        }
        
        @JsonView(Views.Public.class)
        private Map<NewPoolGroup, CountByGroup> groupMap = new HashMap<NewPoolGroup, CountByGroup>();
        
        public ReleaseSetting() {
            LinkedHashMap<Integer, Integer> normalReleaseDuration = new LinkedHashMap<Integer, Integer>();
            normalReleaseDuration.put(1, 300);
            normalReleaseDuration.put(11, 200);
            normalReleaseDuration.put(21, 100);
            normalReleaseDuration.put(31, 50);
            normalReleaseDuration.put(51, 25);
            normalReleaseDuration.put(91, 5);
            LinkedHashMap<Integer, Integer> normalCatReleaseDuration = new LinkedHashMap<Integer, Integer>();
            normalCatReleaseDuration.put(1, 15);
            normalCatReleaseDuration.put(31, 5);
            groupMap.put(NewPoolGroup.Normal, new UgcCount(normalReleaseDuration));
            groupMap.put(NewPoolGroup.Normal_Cat, new UgcCount(normalCatReleaseDuration));
            groupMap.put(NewPoolGroup.Publication, new PgcCount(0, 35));
            groupMap.put(NewPoolGroup.Publication_Cat, new PgcCount(0, 15));
            groupMap.put(NewPoolGroup.Beautyist, new PgcCount(5, 60));
            groupMap.put(NewPoolGroup.Beautyist_Cat, new PgcCount(5, 15));
            groupMap.put(NewPoolGroup.Brand, new PgcCount(10, 60));
            groupMap.put(NewPoolGroup.Brand_Cat, new PgcCount(10, 15));
            groupMap.put(NewPoolGroup.Scraped, new UgcCount(normalReleaseDuration));
            groupMap.put(NewPoolGroup.Scraped_Cat, new UgcCount(normalCatReleaseDuration));
        }
        
        public Long getReleaseCount(NewPoolGroup group, Long totalPoolCount, Calendar currentDate, Boolean mustPush) {
            if(!groupMap.containsKey(group))
                return 0L;
            if(totalPoolCount.compareTo(0L) <= 0)
                return 0L;
            
            return groupMap.get(group).getReleaseCount(totalPoolCount, currentDate, mustPush);
        }
        
    }
    
    public void setPostNewDao(PostNewDao postNewDao) {
        this.postNewDao = postNewDao;
    }
    
    public void setPostNewPoolDao(PostNewPoolDao postNewPoolDao) {
        this.postNewPoolDao = postNewPoolDao;
    }
    
    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }
    
    public void setCircleDao(CircleDao circleDao) {
		this.circleDao = circleDao;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public PostPopularityServiceImp() {
        userTypeBonus = ImmutableMap.<UserType, Integer>builder()
                .put(UserType.CL, 400)
                .put(UserType.Blogger, 400)
                .put(UserType.Celebrity, 400)
                .build();
        releaseSetting = new ReleaseSetting();
    }
    
    private Map<DestType, Boolean> releaseTo(Post post, List<Long> cirTypesUsed, Long basicSortBonus, 
            Boolean bForHide, Boolean publishedDiscover, Map<Long, PostNew> existPostNew, Map<DestType, Boolean> decision) {
        return releaseTo(post, cirTypesUsed, basicSortBonus, bForHide, existPostNew, decision, null, null, publishedDiscover);
    }
    
    private Map<DestType, Boolean> releaseTo(Post post, List<Long> cirTypesUsed, Long basicSortBonus, 
            Boolean bForHide, Map<Long, PostNew> existPostNew, Map<DestType, Boolean> decision, 
            Date createdTime, Boolean withMainType, Boolean publishedDiscover) {
        Map<DestType, Boolean> result = new HashMap<DestType, Boolean>();
        if("circle_in_posting".equalsIgnoreCase(post.getPostSource())) {
            for(DestType dt : decision.keySet())
                result.put(dt, false);
            return result;
        }
        
        Long mainTypeId = null;
        if(withMainType == null || withMainType)
            mainTypeId = cirTypesUsed.get(0);
        for(Long circleTypeId : cirTypesUsed) {
            Boolean isMainType = circleTypeId.equals(mainTypeId);
            if(decision.containsKey(DestType.PostNew) && decision.get(DestType.PostNew)) {
                try {
                    PostNew newPost;
                    if(existPostNew != null && existPostNew.containsKey(circleTypeId)) {
                        newPost = existPostNew.get(circleTypeId);
                        newPost.setCreatedTime(new Date());
                    }
                    else
                        newPost = new PostNew();
                    newPost.setPost(post);
                    newPost.setLocale(post.getLocale());
                    newPost.setBonus(basicSortBonus);
                    newPost.setCircleTypeId(circleTypeId);
                    newPost.setLookTypeId(post.getLookTypeId());
                    newPost.setMainType(isMainType);
                    newPost.setShowInAll(isMainType);
                    newPost.setForceHideInAll(bForHide);
                    PostNew createdPN = postNewDao.create(newPost);
                    if(createdTime != null) {
                        createdPN.setCreatedTime(createdTime);
                        postNewDao.update(createdPN);
                    }
                    if(Constants.getPersonalTrendEnable()) {
                        Map<Long, Double> scoreValueMap = new HashMap<Long, Double>();
                        scoreValueMap.put(post.getId(), Double.valueOf(newPost.getCreatedTime().getTime()));
                        if(bForHide == null || !bForHide)
                            publishDurableEvent(PersonalTrendEvent.CreateAddEvent(TrendPoolType.TGen, post.getLocale(), null, 
                                    1L, scoreValueMap));
                        if(circleTypeId != null) {
                            publishDurableEvent(PersonalTrendEvent.CreateAddEvent(TrendPoolType.TCat, post.getLocale(), circleTypeId.toString(), 
                                    1L, scoreValueMap));
                        }
                    }
                    result.put(DestType.PostNew, true);
                }
                catch(Exception e) {
                    result.put(DestType.PostNew, false);
                }
            }
        }
        
        if(publishedDiscover)
            publishDurableEvent(new DiscoverPostCreateEvent(post.getId(), post.getLocale(), cirTypesUsed,
                post.getCreatorId(), post.getPromoteScore(), bForHide, new Date()));
        
        if(decision.containsKey(DestType.PostNewPool) && decision.get(DestType.PostNewPool)) {
            try {
                PostNewPool newPostPool = new PostNewPool();
                newPostPool.setPostId(post.getId());
                newPostPool.setLocale(post.getLocale());
                newPostPool.setBonus(basicSortBonus);
                newPostPool.setCircleTypeId(cirTypesUsed.get(0));
                newPostPool.setCircleTypeIds(objectMapper.writeValueAsString(cirTypesUsed));
                newPostPool.setLookTypeId(post.getLookTypeId());
                newPostPool.setForceHideInAll(bForHide);
                UserType userType = post.getCreator().getUserType();
                if(bForHide) {
                    switch(userType) {
                    case Publisher: {
                        newPostPool.setGroup(NewPoolGroup.Publication_Cat);
                        break;
                    }
                    case Expert:
                    case Master: {
                        newPostPool.setGroup(NewPoolGroup.Beautyist_Cat);
                        break;
                    }
                    case Brand: {
                        newPostPool.setGroup(NewPoolGroup.Brand_Cat);
                        break;
                    }
                    case Blogger:{
                        newPostPool.setGroup(NewPoolGroup.Scraped_Cat);
                        break;
                    }
                    default: {
                        newPostPool.setGroup(NewPoolGroup.Normal_Cat);
                        break;
                    }
                    }
                }
                else {
                    switch(userType) {
                    case Publisher: {
                        newPostPool.setGroup(NewPoolGroup.Publication);
                        break;
                    }
                    case Expert:
                    case Master: {
                        newPostPool.setGroup(NewPoolGroup.Beautyist);
                        break;
                    }
                    case Brand: {
                        newPostPool.setGroup(NewPoolGroup.Brand);
                        break;
                    }
                    case Blogger:{
                        newPostPool.setGroup(NewPoolGroup.Scraped);
                        break;
                    }
                    default: {
                        newPostPool.setGroup(NewPoolGroup.Normal);
                        break;
                    }
                    }
                }
                postNewPoolDao.create(newPostPool);
                result.put(DestType.PostNewPool, true);
            }
            catch(Exception e) {
                result.put(DestType.PostNewPool, false);
            }
        }
        return result;
    }
    
    // Interface
    @Override
    public Post pushToNewImmediate(Post post, List<Long> circleTypeIds, Long basicSortBonus, Boolean forceHideInAll, Boolean skipPool, Boolean forceAdd) {
        Boolean frcAdd = forceAdd == null ? false : forceAdd;
        PostStatus status = post.getPostStatus();
        if(status == null || !status.getViewable())
            return post;
        
        if(circleTypeIds == null || circleTypeIds.size() <= 0 || basicSortBonus == null)
            return post;
        
        do {
            User postCreator = post.getCreator();
            if((postCreator == null || postCreator.getUserType() == null) && !frcAdd)
                break;
            if(basicSortBonus <= Long.valueOf(0) && !frcAdd)
                break;
            
            Boolean bForHide = forceHideInAll == null ? false : forceHideInAll;
            if(UserType.Blogger.equals(postCreator.getUserType()))
                bForHide = true;
            
            List<Post> toCheckPostList = new ArrayList<Post>();
            toCheckPostList.add(post);
            List<PostNew> postNews = postNewDao.getPostNewByPosts(toCheckPostList, null);
            Map<DestType, Boolean> dest = new HashMap<DestType, Boolean>();
            Boolean publishedDiscover = true;
            if(postNews == null || postNews.size() <= 0) {
                if(userTypeBonus.containsKey(postCreator.getUserType()))
                    dest.put(DestType.PostNew, true);
                else if(skipPool != null && skipPool)
                    dest.put(DestType.PostNew, true);
                else {
                    dest.put(DestType.PostNewPool, true);
                    publishedDiscover = false;
                }
            }
            releaseTo(post, circleTypeIds, basicSortBonus, bForHide, publishedDiscover, null, dest);
        } while (false);
        
        return post;
    }

    @Override
    public Boolean updatePostNew(Post post, List<PostNew> postNews, List<Long> circleTypeIds, 
            Long basicSortBonus, Boolean adjustDate, Boolean skipPool, Boolean forceHideInAll, 
            Boolean forceAdd) {
        if(adjustDate && !skipPool) {
            for(PostNew pn : postNews) {
                if(circleTypeIds.contains(pn.getCircleTypeId()) && !pn.getIsDeleted())
                    continue;
                pn.setIsDeleted(true);
                postNewDao.update(pn);
            }
            Map<DestType, Boolean> dest = new HashMap<DestType, Boolean>();
            dest.put(DestType.PostNewPool, true);
            releaseTo(post, circleTypeIds, basicSortBonus, forceHideInAll, false, null, dest);
            return true;
        }
        
        Boolean frcAdd = forceAdd == null ? false : forceAdd;
        PostStatus status = post.getPostStatus();
        if(status == null || !status.getViewable())
            return true;
        
        if(circleTypeIds == null || circleTypeIds.size() <= 0 || basicSortBonus == null)
            return true;
        
        if((basicSortBonus == null || basicSortBonus <= Long.valueOf(0)) && !frcAdd)
            return true;
        
        Long mainTypeId = circleTypeIds.get(0);
        Boolean mainTypeCreated = false;
        List<Post> toCheckPostList = new ArrayList<Post>();
        toCheckPostList.add(post);
        
        Date postNewDate = null;
        if(adjustDate)
            postNewDate = new Date();
        Boolean bForHide = forceHideInAll != null ? forceHideInAll : null;
        for(PostNew pn : postNews) {
            if(postNewDate == null)
                postNewDate = pn.getCreatedTime();
            if(bForHide == null)
                bForHide = pn.getForceHideInAll();
            if(!circleTypeIds.contains(pn.getCircleTypeId()) && !pn.getIsDeleted()) {
                pn.setIsDeleted(true);
                postNewDao.update(pn);
                continue;
            }
            circleTypeIds.remove(pn.getCircleTypeId());
            if(pn.getIsDeleted())
                pn.setIsDeleted(false);
            
            if(mainTypeId.equals(pn.getCircleTypeId())) {
                pn.setMainType(true);
                mainTypeCreated = true;
            }
            else
                pn.setMainType(false);
            
            pn.setCreatedTime(postNewDate);
            pn.setForceHideInAll(bForHide);
            postNewDao.update(pn);
        }
        
        bForHide = bForHide == null ? false : bForHide;
        if(adjustDate)
            postNewDate = null;
        if(circleTypeIds.size() > 0) {
            Map<DestType, Boolean> dest = new HashMap<DestType, Boolean>();
            dest.put(DestType.PostNew, true);
            releaseTo(post, circleTypeIds, basicSortBonus, bForHide, null, dest, postNewDate, !mainTypeCreated, true);
        }
        return true;
    }
    
    @Override
    public Integer releasePoolToNew(String locale, NewPoolGroup group, Calendar currentDate, Boolean mustPush, Map<String, Object> summary) {
        Integer addCount = 0;
        List<NewPoolGroup> toQueryGroups = new ArrayList<NewPoolGroup>();
        toQueryGroups.add(group);
        Long totalPoolCount = postNewPoolDao.getPostCountInPool(locale, toQueryGroups);
        Integer releaseCount = getReleaseCount(locale, group, totalPoolCount, currentDate, mustPush).intValue();                
        Map<DestType, Boolean> destMap = new HashMap<DestType, Boolean>();
        destMap.put(DestType.PostNew, true);
        List<Long> toDelPostNewPool = new ArrayList<Long>();

        Integer offset = 0;
        do {
            if(releaseCount <= 0)
                break;
            
            BlockLimit blockLimit = new BlockLimit(offset, releaseCount);
            blockLimit.addOrderBy("createdTime", true);
            PageResult<PostNewPool> pg = postNewPoolDao.getPostFromPool(locale, toQueryGroups, blockLimit);
            if(pg.getResults().size() <= 0)
                break;
            Map<Long, PostNewPool> postPNPMap = new LinkedHashMap<Long, PostNewPool>();
            for(PostNewPool pnp : pg.getResults()) {
                postPNPMap.put(pnp.getPostId(), pnp);
            }
            
            Map<Long, Post> postMap = new LinkedHashMap<Long, Post>();
            List<Post> availablePost = postDao.findByIds(postPNPMap.keySet().toArray(new Long[postPNPMap.size()]));
            for(Post p : availablePost) {
                Circle c = p.getCircle();
            	if(c == null || c.getIsSecret())
            		continue;
                postMap.put(p.getId(), p);
            }
                
            for(PostNewPool pnp : postPNPMap.values()) {
                if(!postMap.containsKey(pnp.getPostId())) {
                    toDelPostNewPool.add(pnp.getId());
                    continue;
                }
                Post p = postMap.get(pnp.getPostId());
                List<Long> cirTypesUsed = pnp.getCircleTypeIdsList();
                Map<Long, PostNew> existPostNew = null;
                if(NewPoolGroup.Scraped.equals(group) || NewPoolGroup.Scraped_Cat.equals(group)) {
                    List<PostNew> exPostNews = postNewDao.findByPost(p.getId(), null);
                    if(exPostNews != null && exPostNews.size() > 0) {
                        existPostNew = new HashMap<Long, PostNew>();
                        for(PostNew epn : exPostNews) {
                            existPostNew.put(epn.getCircleTypeId(), epn);
                        }
                    }
                }
                Map<DestType, Boolean> result = releaseTo(p, cirTypesUsed, pnp.getBonus(), pnp.getForceHideInAll(), 
                        true, existPostNew, destMap);
                if(result.get(DestType.PostNew)) {
                    addCount++;
                }
                toDelPostNewPool.add(pnp.getId());
            }
            offset += releaseCount;
            if(offset >= totalPoolCount)
                break;
        } while(addCount < releaseCount);
        
        int deletedCount = postNewPoolDao.batchSetDelete(toDelPostNewPool);
        if(summary != null) {
            Map<String, Object> info = new HashMap<String, Object>();
            info.put("TotalSize", totalPoolCount);
            info.put("MinAdd", releaseCount);
            info.put("Deleted", deletedCount);
            info.put("TotalAdd", addCount);
            summary.put(locale, info);
        }
        return addCount;
    }
    
    @Override
    public Integer getPostNewMinThreshold() {
        return 200;
    }

    @Override
    public Long getPostBasicSortBonus(Post post) {
        Long bonus = Long.valueOf(-1);
        UserType postCreatorType = post.getCreator().getUserType();
        if(userTypeBonus.containsKey(postCreatorType))
            bonus = userTypeBonus.get(postCreatorType).longValue();
        else if(post.getPromoteScore() != null)
            bonus = post.getPromoteScore();
        return bonus;
    }
    
    @Override
    public void setReleaseConfig(String content) {
        updateReleaseConfig(content);
    }
    
    @Override
    public String getReleaseConfig() {
        java.io.File confFile = getReleaseConfigFile(false);
        if(confFile == null)
            return null;
        
        return loadReleaseConfig(confFile);
    }
    
    @Override
    public Integer batchRealDelete(Date start, Date end) {
        return postNewPoolDao.batchRealDelete(start, end);
    }
    
    private Long getReleaseCount(String locale, NewPoolGroup group, Long totalPoolCount, Calendar currentDate, Boolean mustPush) {
        return releaseSetting.getReleaseCount(group, totalPoolCount, currentDate, mustPush);
    }
    
    private java.io.File getReleaseConfigFile(Boolean createIdNotExist) {
        String confDir = Constants.getPostScoreLogPath();
        if(confDir == null)
            return null;
        java.io.File dConfDir = new java.io.File(confDir);

        if (!dConfDir.exists())
        {
            if(!dConfDir.mkdir()) {
                logger.error("Failed to create directory :" + dConfDir);
                return null;
            }
        }
        
        java.io.File confFile = new java.io.File(confDir + "/post_new_pool.config");   
        if(confFile.exists())
            return confFile;
        else if(createIdNotExist) {
            try {
                confFile.createNewFile();
            } catch (IOException e) {
                return null;
            }
            return confFile;
        }
        return null;
    }
    
    
    private String createReleaseConfig() {
        java.io.File confFile = getReleaseConfigFile(true);
        if(confFile == null)
            return null;
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(confFile));
            output.write(objectMapper.writeValueAsString(releaseSetting));
            return confFile.getAbsolutePath();
        } catch (Exception e) {
            confFile.delete();
            return null;
        }
        finally {
            if ( output != null ) {
                try {
                    output.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private String loadReleaseConfig(java.io.File confFile) {
        String confContent = null;
        FileReader reader = null;
        try {
            reader = new FileReader(confFile);
            char[] chars = new char[(int) confFile.length()];
            reader.read(chars);
            confContent = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader !=null){
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return confContent;
    }

    private Boolean updateReleaseConfig(String content) {
        if(content == null)
            return false;
        
        try {
            releaseSetting = objectMapper.readValue(content, new TypeReference<ReleaseSetting>(){});
            createReleaseConfig();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        java.io.File config = getReleaseConfigFile(false);
        if(config == null) {
            createReleaseConfig();
        }
        else {
            updateReleaseConfig(loadReleaseConfig(config));
        }
    }
}
