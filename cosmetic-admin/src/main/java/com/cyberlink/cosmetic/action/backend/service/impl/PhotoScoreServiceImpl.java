package com.cyberlink.cosmetic.action.backend.service.impl;

import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.ScrollableResults;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.service.PhotoScoreService;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.file.service.PhotoProcessService;
import com.cyberlink.cosmetic.modules.mail.service.MailInappropPostCommentService;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostRescueDao;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreDao;
import com.cyberlink.cosmetic.modules.post.dao.PostViewDao;
import com.cyberlink.cosmetic.modules.post.model.Post;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool.NewPoolGroup;
import com.cyberlink.cosmetic.modules.post.model.PostScore;
import com.cyberlink.cosmetic.modules.post.model.PostScore.CreatorType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.model.PostView;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Attachments;
import com.cyberlink.cosmetic.modules.post.service.PostPopularityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PhotoScoreServiceImpl extends AbstractService 
    implements PhotoScoreService, InitializingBean {
    
    private PostDao postDao;
    private PostViewDao postViewDao;
    private PostRescueDao postRescueDao;
    private PostScoreDao postScoreDao;
    private CircleDao circleDao;
    private CircleTypeDao circleTypeDao;
    private LocaleDao localeDao;
    private PhotoProcessService photoProcessService;
    private PostPopularityService postPopularityService;
    private MailInappropPostCommentService mailInappropPostCommentService;
    private ObjectMapper objectMapper;
    private TransactionTemplate transactionTemplate;
    static private Boolean running = false;
    static private Boolean enable = false;
    static private Boolean pause = false;
    static private Boolean releasingUgc = false;
    static private Boolean releasingPgc = false;
    private int BATCH_UPDATE_SIZE = 100;
    private int SCORE_THRESHOLD = 70;
    private int CLEAN_OLD_RECORD_TIME = 5;
    private int CLEAN_OLD_POOL_TIME = 6;
    private List<Long> nailCircleTypeIds = null;
    private Long NAIL_CIRCLE_TYPE_GROUP_ID = 5L;
    private String [] logReceivers;
    
    @Override
    public Throwable runFor(Date beginDate, Date endDate, Long minBasicScore, final Boolean checkPostRescue, final Map<String, Object> summary, BlockLimit blockLimit) {
        if(!getEnable() || getRunning())
            return new Throwable("Not enable or running");
        if(getPause())
            return new Throwable("Pausing");
        
        setRunning(true);
        try {
            postDao.doWithPotentialDiscoverPosts(beginDate, endDate, Long.valueOf(200), blockLimit, new ScrollableResultsCallback() {
                @Override
                public void doInHibernate(ScrollableResults sr) {
                    Long startTime = System.currentTimeMillis();
                    int i = 0;
                    Map<Long, Post> postMap = new LinkedHashMap<Long, Post>();
                    Set<Long> circleIds = new HashSet<Long>();
                    while (sr.next()) {
                        final Post post = (Post) sr.get()[0];
                        postMap.put(post.getId(), post);
                        Long circleId = post.getCircleId();
                        if(circleId != null)
                            circleIds.add(circleId);
                        if ((++i) % BATCH_UPDATE_SIZE == 0) {
                            if(postMap.size() > 0)
                                HandlePosts(postMap, circleIds, checkPostRescue);
                            postMap.clear();
                            circleIds.clear();
                        }
                    }
                    
                    if(postMap.size() > 0)
                        HandlePosts(postMap, circleIds, checkPostRescue);
                    if(summary != null) {
                        Long spent = System.currentTimeMillis() - startTime;
                        summary.put("ProcessTime", spent);
                        summary.put("ProcessCount", i);
                    }
                    
                }
            });
        }
        catch(Exception e) {
            summary.put("Error", e.getMessage());
        }
        finally {
            setRunning(false);
        }
        return null;
    }
    
    @Override
    public void start() {
        setPause(false);
    }
    
    @Override
    public void stop() {
        setPause(true);
    }

    @Override
    public Map<String, Object> getStatus() {
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Running", getRunning());
        results.put("Enable", getEnable());
        results.put("Pausing", getPause());
        return results;
    }

    @Override
    //@BackgroundJob(cronExpression = "0 30 * * * ? *")
    public void exec() {
        if(!getEnable() || getRunning())
            return;
        if(getPause())
            return;
        
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
        Boolean cleanOlderRecord = cal.get(Calendar.HOUR_OF_DAY) == CLEAN_OLD_RECORD_TIME;
        Boolean cleanOlderPoolRecord = cal.get(Calendar.HOUR_OF_DAY) == CLEAN_OLD_POOL_TIME;
        long markTime;
        long lapTime;
        Map<String, Object> summary = new HashMap<String, Object>();
        /* Temporary disable create postScore for every 30 hour 
         * We are create postScore right after create post. We 
         * could remove these code if no any problems was found. */
        /*int duration = 3;
        Date endDate = cal.getTime();
        cal.add(Calendar.HOUR, -duration);
        Date startDate = cal.getTime();
        markTime = System.currentTimeMillis();
        Throwable error = runFor(startDate, endDate, 0L, false, summary, null);   
        lapTime = System.currentTimeMillis();
        summary.put("ToPostScore", lapTime - markTime);
        if(error != null) {
            summary.put("PhotoScoreService Failed", error.getMessage());
        }
        summary.put("DurationBegin", startDate.toString());
        summary.put("DurationEnd", endDate.toString());*/
        if(cleanOlderRecord) {
            markTime = System.currentTimeMillis();
            CleanOldRecord();
            lapTime = System.currentTimeMillis();
            summary.put("CleanOldRecord", String.valueOf(lapTime - markTime) + "ms");
        }
        if(cleanOlderPoolRecord) {
            markTime = System.currentTimeMillis();
            CleanOldPoolRecord();
            lapTime = System.currentTimeMillis();
            summary.put("CleanOldPoolRecord", String.valueOf(lapTime - markTime) + "ms");
        }        
        if(summary.keySet().size() > 0)
            SendMail("PhotoScoreService End", summary);
    }
    
    @Override
    //@BackgroundJob(cronExpression = "0 0/3 * 1/1 * ? *")
    public void releaseUgcPostNew() {
        if(getReleasingUgc())
            return;
        
        try {
            setReleasingUgc(true);
            NewPoolGroup [] ugcGroups = new NewPoolGroup[]{
                NewPoolGroup.Normal, NewPoolGroup.Normal_Cat,
                NewPoolGroup.Scraped, NewPoolGroup.Scraped_Cat
            };
            Map<String, Object> summary = new LinkedHashMap<String, Object>();
            Integer added = 0;
            long markTime = System.currentTimeMillis();
            for(NewPoolGroup g : ugcGroups) {
                Map<String, Object> gMap = new LinkedHashMap<String, Object>();
                added += releaseFromPoolToNew(g, false, gMap);
                summary.put(g.toString(), gMap);
            }
            if(added > 0) {
                long lapTime = System.currentTimeMillis();
                summary.put("Spend", String.valueOf(lapTime - markTime) + "ms");        
                summary.put("DateNow", (new Date()).toString());
                SendMail("PhotoScoreService Release End", summary);
            }
        }
        finally {
            setReleasingUgc(false);
        }
    }
    
    @Override
    //@BackgroundJob(cronExpression = "0 0/5 * 1/1 * ? *")
    public void releasePgcPostNew() {
        if(getReleasingPgc())
            return;
        
        try {
			setReleasingPgc(true);
            Map<String, Object> summary = new LinkedHashMap<String, Object>();
            long markTime = System.currentTimeMillis();
            Integer added = 0;
            NewPoolGroup [] pgcGroups = new NewPoolGroup[]{
                    NewPoolGroup.Publication,  NewPoolGroup.Publication_Cat,  
                    NewPoolGroup.Beautyist,  NewPoolGroup.Beautyist_Cat,  
                    NewPoolGroup.Brand,  NewPoolGroup.Brand_Cat
            };
            for(NewPoolGroup g : pgcGroups) {
                Map<String, Object> releaseCountMap = new LinkedHashMap<String, Object>();
                added += releaseFromPoolToNew(g, false, releaseCountMap);
                if(added <= 0)
                    continue;
                summary.put(g.toString(), releaseCountMap);
            }
            if(summary.keySet().size() > 0) {
                long lapTime = System.currentTimeMillis();
                summary.put("Spend", String.valueOf(lapTime - markTime) + "ms");                
                summary.put("DateNow", (new Date()).toString());
                SendMail("PhotoScoreService Release End", summary);
            }
        }
        finally {
            setReleasingPgc(false);
        }
    }
    
    @Override
    public void CleanOldRecord() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date endDate = cal.getTime();
        int deletedCount = postScoreDao.batchDelete(null, endDate);
        SendMail("CleanOldRecord End", endDate.toString() + " : " + String.valueOf(deletedCount));
    }
    
    private void CleanOldPoolRecord() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date endDate = cal.getTime();
        int deletedCount = postPopularityService.batchRealDelete(null, endDate);
        SendMail("CleanOldRecord End", endDate.toString() + " : " + String.valueOf(deletedCount));
    }
    
    @Override
    public Integer releaseFromPoolToNew(NewPoolGroup group, Boolean mustPush, Map<String, Object> releaseCountMap) {        
        Set<String> availableLocale = localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE);
        Integer added = 0;
        Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
        for(String loc : availableLocale)
            added += postPopularityService.releasePoolToNew(loc, group, currentDate, mustPush, releaseCountMap);
        
        return added;
    }
    
    @Override
    public void HandleUnhandledPostScore(Date begin, Date end) {
        // Not Implemented
        return;
    }
    
    private void SendMail(String subject, Object object) {
        subject += " - " + Constants.getWebsiteDomain();
        String content;
        try {
            content = objectMapper.writerWithView(Views.Public.class).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            content = e.getMessage();
        }
        
        mailInappropPostCommentService.directSend(logReceivers, subject, content);
    }
    
    @SuppressWarnings("unused")
    private void schedHandleUnhandledPostScore() {
        int duration = 3;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
        cal.add(Calendar.HOUR, -1);
        Date endDate = cal.getTime();
        cal.add(Calendar.HOUR, -duration);
        Date startDate = cal.getTime();
        HandleUnhandledPostScore(startDate, endDate);
    }
    
    private PostScore HandlePost(Post post, PostView postView, Circle circle, List<Long> exPostScoreIds, List<Long> exPostRescueIds, Set<String> extMd5s) {
        PostScore postScore = null;
        if(post == null || postView == null || circle == null)
            return postScore;
        
        if(post.getIsDeleted() || circle.getIsDeleted() || circle.getIsSecret())
            return postScore;
        
        if(exPostScoreIds.contains(post.getId()))
            return postScore;
        if(exPostRescueIds != null && exPostRescueIds.contains(post.getId()))
            return postScore;
        
        try {
            MainPostSimpleWrapper tmp = objectMapper.readValue(postView.getMainPost(), MainPostSimpleWrapper.class);
            Attachments attch = tmp.getAttachments();
            if(attch.files.size() <= 0)
                return postScore;
            com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.File f = attch.files.get(0);
            ObjectNode actualObj = (ObjectNode)objectMapper.readTree(f.getMetadata());
            if(actualObj == null)
                return postScore;

            JsonNode attrNode = actualObj.get("md5");
            if(attrNode != null) {
                String md5 = attrNode.asText();
                if(extMd5s.contains(md5))
                    return postScore;
                extMd5s.add(md5);
            }
            
            attrNode = actualObj.get("originalUrl");
            if(attrNode == null)
                return postScore;
            
            Long score = (long)(GetScore(attrNode.asText()));
            postScore = new PostScore();
            postScore.setPostId(post.getId());
            postScore.setPostLocale(post.getLocale());
            postScore.setAppName(post.getAppName().toString());
            postScore.setPostCreateDate(post.getCreatedTime());
            postScore.setCircleTypeId(circle.getCircleTypeId());
            PoolType poolType = PoolType.Disqualified;

            switch (post.getCreator().getUserType()) {
                case Publisher: {
                    poolType = PoolType.Pgc;
                    postScore.setCreatorType(CreatorType.Publication);
                    break;
                }
                case Expert:
                case Master: {
                    poolType = PoolType.Pgc;
                    postScore.setCreatorType(CreatorType.Beautyist);
                    break;
                }
                case Brand: {
                    poolType = PoolType.Pgc;
                    postScore.setCreatorType(CreatorType.Brand);
                    break;
                }
                case Normal: 
                default :{
                if(score >= SCORE_THRESHOLD) {
                    if(nailCircleTypeIds.contains(circle.getCircleTypeId()))
                        poolType = PoolType.QualifiedNail;
                    else
                        poolType = PoolType.Qualified;
                }
                break;
                }
            }
            postScore.setPoolType(poolType);
            postScore.setScore(score.intValue());
        }
        catch(Exception e) {
        }
        return postScore;
    }
    
    private void HandlePosts(Map<Long, Post> postMap, Set<Long> circleIds, Boolean checkPostRescue) {
        if(postMap == null || postMap.size() <= 0)
            return;
        List<PostScore> results = new ArrayList<PostScore>();
        List<Long> postIds = new ArrayList<Long>(postMap.keySet());
        List<Circle> circles = circleDao.findByIds(circleIds.toArray(new Long[circleIds.size()]));
        Map<Long, Circle> circleMap = new HashMap<Long, Circle>();
        for(Circle c : circles) {
            circleMap.put(c.getId(), c);
        }
        Map<Long, PostView> postViewMap = postViewDao.getViewMapByPostIds(postIds);
        List<Long> exPostScoreIds = postScoreDao.findExPostIds(postIds, null, null, null);
        List<Long> exPostRescueIds = null;
        if(checkPostRescue)
            exPostRescueIds = postRescueDao.findExPostIds(postIds, null, null);
        Set<String> extMd5s = new HashSet<String>(); 
        for(Long postId : postMap.keySet()) {
            if(!postViewMap.containsKey(postId))
                continue;
            PostView postView = postViewMap.get(postId);
            Post post = postMap.get(postId);
            Long circleId = post.getCircleId();
            if(circleId == null)
                continue;
            Circle circle = circleMap.get(circleId);
            PostScore ps = HandlePost(post, postView, circle, exPostScoreIds, exPostRescueIds, extMd5s);
            if(ps != null)
                results.add(ps);
        }
        postScoreDao.batchCreate(results);
    }
    
    private float GetScore(String url) throws Exception {
        Pair<BufferedImage, Integer> pImgInfo = photoProcessService.getBufferAndLengthFromUrl(url);
        Float result = photoProcessService.GetScore(pImgInfo); 
        if(result == null)
            return 0F;
        return result;
    }
    
    public String doPost(String sURL, String data, String cookie, String referer, String charset) { 
        java.io.BufferedWriter wr = null; 
        String result = "";
        try { 
            URL url = new URL(sURL); 
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
            conn.setDoOutput(true); 
            conn.setDoInput(true); 
            conn.setRequestMethod("POST"); 
            conn.setUseCaches(false); 
            conn.setAllowUserInteraction(true);  
            conn.setInstanceFollowRedirects(true); 
       
            conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-TW; rv:1.9.1.2) " + "Gecko/20090729 Firefox/3.5.2 GTB5 (.NET CLR 3.5.30729)"); 
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"); 
            conn.setRequestProperty("Accept-Language", "zh-tw,en-us;q=0.7,en;q=0.3"); 
            conn.setRequestProperty("Accept-Charse", "Big5,utf-8;q=0.7,*;q=0.7"); 
            if (cookie != null) 
                conn.setRequestProperty("Cookie", cookie); 
            if (referer != null) 
                conn.setRequestProperty("Referer", referer); 
      
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            conn.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length)); 
              
            java.io.DataOutputStream dos = new java.io.DataOutputStream(conn.getOutputStream()); 
            dos.writeBytes(data); 
       
            java.io.BufferedReader rd = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream(), charset)); 
            String line; 
            while ((line = rd.readLine()) != null) { 
                result += line; 
            }
            rd.close(); 
        } catch (java.io.IOException e) {   
            e.printStackTrace();
        } finally { 
            if (wr != null) { 
                try { 
                    wr.close(); 
                } catch (java.io.IOException ex) { 
                } 
                wr = null; 
            } 
        } 
          
        return result; 
    } 
    
    private void Init() {
        if(postDao == null || postViewDao == null || postRescueDao == null || postScoreDao == null || circleDao == null || photoProcessService == null || objectMapper == null || transactionTemplate == null)
            return;
        setEnable(true);
        nailCircleTypeIds = new ArrayList<Long>();
        List<CircleType> cts = circleTypeDao.listTypesByTypeGroup(NAIL_CIRCLE_TYPE_GROUP_ID, null);
        for(CircleType ct : cts) {
            nailCircleTypeIds.add(ct.getId());
        }
    }
    
    public PostDao getPostDao() {
        return postDao;
    }

    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }

    public PostViewDao getPostViewDao() {
        return postViewDao;
    }

    public void setLocaleDao(LocaleDao localeDao) {
        this.localeDao = localeDao;
    }
    
    public void setPostViewDao(PostViewDao postViewDao) {
        this.postViewDao = postViewDao;
    }
    
    public void setPostRescueDao(PostRescueDao postRescueDao) {
        this.postRescueDao = postRescueDao;
    }
    
    public void setPostScoreDao(PostScoreDao postScoreDao) {
        this.postScoreDao = postScoreDao;
    }
    
    public void setCircleDao(CircleDao circleDao) {
        this.circleDao = circleDao;
    }
    
    public void setCircleTypeDao(CircleTypeDao circleTypeDao) {
        this.circleTypeDao = circleTypeDao;
    }
    
    public PhotoProcessService getPhotoProcessService() {
        return photoProcessService;
    }

    public void setPhotoProcessService(PhotoProcessService photoProcessService) {
        this.photoProcessService = photoProcessService;
    }

    public void setMailInappropPostCommentService(MailInappropPostCommentService mailInappropPostCommentService) {
        this.mailInappropPostCommentService = mailInappropPostCommentService;
    }
    
    public void setPostPopularityService(PostPopularityService postPopularityService) {
        this.postPopularityService = postPopularityService;
    }
    
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
    
    private Boolean getRunning() {
        Boolean r;
        synchronized(running) {
            r = running;
        }
        return r;
    }

    private void setRunning(Boolean running) {
        synchronized(running) {
            PhotoScoreServiceImpl.running = running;
        }
    }

    public Boolean getEnable() {
        Boolean e;
        synchronized(enable) {
            e = enable;
        }
        return e;
    }

    public void setEnable(Boolean enable) {
        synchronized(running) {
            PhotoScoreServiceImpl.enable = enable;
        }
    }

    public Boolean getPause() {
        Boolean p;
        synchronized(pause) {
            p = pause;
        }
        return p;
    }

    public void setPause(Boolean pause) {
        synchronized(pause) {
            PhotoScoreServiceImpl.pause = pause;
        }
    }

    public Boolean getReleasingUgc() {
        Boolean r;
        synchronized(releasingUgc) {
            r = releasingUgc;
        }
        return r;
    }
    
    public void setReleasingUgc(Boolean inReleasingUgc) {
        synchronized(releasingUgc) {
            PhotoScoreServiceImpl.releasingUgc = inReleasingUgc;
        }
    }
    
    public Boolean getReleasingPgc() {
        Boolean r;
        synchronized(releasingPgc) {
            r = releasingPgc;
        }
        return r;
    }
    
    public void setReleasingPgc(Boolean inReleasingPgc) {
        synchronized(releasingPgc) {
            PhotoScoreServiceImpl.releasingPgc = inReleasingPgc;
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Init();
        if("Dev".equalsIgnoreCase(Constants.getNotifyRegion()))
            logReceivers = new String[] {"Victor_Chew@PerfectCorp.com"};
        else
            logReceivers = new String[] {"Victor_Chew@PerfectCorp.com", "Frank_Chuang@PerfectCorp.com"};
    }
    
}
