package com.cyberlink.cosmetic.action.api.user;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.ScrollableResults;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeGroupDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.circle.model.CircleTypeGroup;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.post.dao.PostAttributeDao;
import com.cyberlink.cosmetic.modules.post.dao.PostDao;
import com.cyberlink.cosmetic.modules.post.dao.PostNewDao;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendGroupDao;
import com.cyberlink.cosmetic.modules.post.model.PostAttribute.PostAttrType;
import com.cyberlink.cosmetic.modules.post.model.PsTrend.PsTrendKey;
import com.cyberlink.cosmetic.modules.post.model.PsTrend;
import com.cyberlink.cosmetic.modules.post.model.PsTrendGroup;
import com.cyberlink.cosmetic.modules.post.model.PsTrendHeat;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool.PsTrendPoolKey;
import com.cyberlink.cosmetic.modules.post.repository.PsTrendHeatRepository;
import com.cyberlink.cosmetic.modules.post.service.PsTrendService;
import com.cyberlink.cosmetic.modules.post.service.PsTrendService.ScanResultCallback;
import com.cyberlink.cosmetic.modules.user.dao.UserAttrDao;
import com.cyberlink.cosmetic.modules.user.model.UserHeat;
import com.cyberlink.cosmetic.modules.user.repository.UserHeatRepository;
import com.cyberlink.cosmetic.utils.IdGenerator;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/user/inint-badge.action")
public class InitBadgeAction extends AbstractAction{
   
    @SpringBean("post.PostDao")
    private PostDao postDao;
    
    @SpringBean("user.userAttrDao")
    private UserAttrDao userAttrDao;
    
    @SpringBean("post.PostAttributeDao")
    private PostAttributeDao postAttributeDao;
    
    @SpringBean("post.PostNewDao")
    private PostNewDao postNewDao;
    
    @SpringBean("user.userHeatRepository")
    private UserHeatRepository userHeatRepository;
    
    @SpringBean("post.psTrendHeatRepository")
    private PsTrendHeatRepository psTrendHeatRepository;

    @SpringBean("post.psTrendGroupDao")
    private PsTrendGroupDao psTrendGroupDao;

    @SpringBean("circle.circleTypeDao") 
    private CircleTypeDao circleTypeDao;
    
    @SpringBean("circle.circleTypeGroupDao") 
    private CircleTypeGroupDao circleTypeGroupDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;

    @SpringBean("post.psTrendService")
    private PsTrendService psTrendService;
    
    @SpringBean("core.jdbcTemplate")
    private TransactionTemplate transactionTemplate;
    
    private Date startTime;
    
    private Date endTime;
    
    private List<String> locales;
    
    private Integer BATCH_QUERY_SIZE = 100;
    
    private void doUserPostCount() {
        postDao.getPostCountPerUser(startTime, endTime, new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                Long logIdx = 0L;
                List<UserHeat> uhs = new ArrayList<UserHeat>();
                
                while (sr.next()) {
                    Object [] result = sr.get();
                    if(result.length < 4)
                        break;
                    Long usrId = (Long) result[0];
                    String usrRegion = (String) result[1];
                    Date usrCreatedTime = (Date) result[2];
                    Integer postCount = (Integer) result[3];
                    UserHeat uh = new UserHeat();
                    uh.setId(usrId.toString());
                    uh.setLoc(usrRegion);
                    uh.setDate(usrCreatedTime);
                    uh.setPosts(postCount);
                    uh.setCirIns(0);
                    uh.setFollowers(0);
                    uh.setLikes(0);
                    uhs.add(uh);

                    if(uhs.size() >= BATCH_QUERY_SIZE) {
                        userHeatRepository.batchCreateOrUpdate(uhs);
                        uhs.clear();
                        postDao.clear();
                    }
                    logIdx++;
                    if(logIdx % 10000L == 0) {
                        logger.error("Current adding posts : " + usrId.toString());
                        logIdx = 0L;
                    }
                }
                if(uhs.size() > 0)
                    userHeatRepository.batchCreateOrUpdate(uhs);
            }
        });
    }
    
    private void doUserLikesCount() {
        postAttributeDao.getLikeCirInCountPerUser(PostAttrType.PostLikeCount, startTime, endTime, new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                Long logIdx = 0L;
                List<UserHeat> uhs = new ArrayList<UserHeat>();
                
                while (sr.next()) {
                    Object [] result = sr.get();
                    if(result.length < 4)
                        break;
                    Long usrId = (Long) result[0];
                    String usrRegion = (String) result[1];
                    Date usrCreatedTime = (Date) result[2];
                    Integer likes = (Integer) result[3];
                    UserHeat uh = new UserHeat();
                    uh.setId(usrId.toString());
                    uh.setLoc(usrRegion);
                    uh.setDate(usrCreatedTime);
                    uh.setLikes(likes);
                    uhs.add(uh);

                    if(uhs.size() >= BATCH_QUERY_SIZE) {
                        userHeatRepository.batchCreateOrUpdate(uhs);
                        uhs.clear();
                        postAttributeDao.clear();
                    }
                    logIdx++;
                    if(logIdx % 10000L == 0) {
                        logger.error("Current adding likes : " + usrId.toString());
                        logIdx = 0L;
                    }
                }
                if(uhs.size() > 0)
                    userHeatRepository.batchCreateOrUpdate(uhs);
            }
        });
    }
    
    private void doUserCirInCount() {
        postAttributeDao.getLikeCirInCountPerUser(PostAttrType.PostCircleInCount, startTime, endTime, new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                Long logIdx = 0L;
                List<UserHeat> uhs = new ArrayList<UserHeat>();
                
                while (sr.next()) {
                    Object [] result = sr.get();
                    if(result.length < 4)
                        break;
                    Long usrId = (Long) result[0];
                    String usrRegion = (String) result[1];
                    Date usrCreatedTime = (Date) result[2];
                    Integer cirIns = (Integer) result[3];
                    UserHeat uh = new UserHeat();
                    uh.setId(usrId.toString());
                    uh.setLoc(usrRegion);
                    uh.setDate(usrCreatedTime);
                    uh.setCirIns(cirIns);
                    uhs.add(uh);

                    if(uhs.size() >= BATCH_QUERY_SIZE) {
                        userHeatRepository.batchCreateOrUpdate(uhs);
                        uhs.clear();
                        postAttributeDao.clear();
                    }
                    logIdx++;
                    if(logIdx % 10000L == 0) {
                        logger.error("Current adding cirin : " + usrId.toString());
                        logIdx = 0L;
                    }
                }
                if(uhs.size() > 0)
                    userHeatRepository.batchCreateOrUpdate(uhs);
            }
        });
    }
    
    private void doUserFollowerCount() {
        userAttrDao.getFollowerCountPerUser(startTime, endTime, new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                Long logIdx = 0L;
                List<UserHeat> uhs = new ArrayList<UserHeat>();
                
                while (sr.next()) {
                    Object [] result = sr.get();
                    if(result.length < 4)
                        break;
                    Long usrId = (Long) result[0];
                    String usrRegion = (String) result[1];
                    Date usrCreatedTime = (Date) result[2];
                    Integer followers = (Integer) result[3];
                    UserHeat uh = new UserHeat();
                    uh.setId(usrId.toString());
                    uh.setLoc(usrRegion);
                    uh.setDate(usrCreatedTime);
                    uh.setFollowers(followers);
                    uhs.add(uh);

                    if(uhs.size() >= BATCH_QUERY_SIZE) {
                        userHeatRepository.batchCreateOrUpdate(uhs);
                        uhs.clear();
                        userAttrDao.clear();
                    }
                    logIdx++;
                    if(logIdx % 10000L == 0) {
                        logger.error("Current adding followers : " + usrId.toString());
                        logIdx = 0L;
                    }
                }
                if(uhs.size() > 0)
                    userHeatRepository.batchCreateOrUpdate(uhs);
            }
        });
    }
    
    private void doTrendPostLike() {
        postNewDao.getLikeCirInCountPerPost(PostAttrType.PostLikeCount, startTime, endTime, new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                Long logIdx = 0L;
                List<PsTrendHeat> psths = new ArrayList<PsTrendHeat>();
                
                while (sr.next()) {
                    Object [] result = sr.get();
                    if(result.length < 5)
                        break;
                    Long postId = (Long) result[0];
                    String locale = (String) result[1];
                    Long circleTypeId = (Long) result[2];
                    Date createdTime = (Date) result[3];
                    Integer likes = (Integer) result[4];
                    PsTrendHeat psth = new PsTrendHeat();
                    psth.setId(postId.toString());
                    psth.setLoc(locale);
                    List<String> cirTypes = new ArrayList<String>();
                    cirTypes.add(circleTypeId.toString());
                    psth.setCirTypes(cirTypes);
                    psth.setLikes(likes);
                    psth.setDate(createdTime);
                    psths.add(psth);

                    if(psths.size() >= BATCH_QUERY_SIZE) {
                        psTrendHeatRepository.batchCreateOrUpdate(psths);
                        psths.clear();
                        postNewDao.clear();
                    }
                    logIdx++;
                    if(logIdx % 10000L == 0) {
                        logger.error("Current adding trend likes : " + postId.toString());
                        logIdx = 0L;
                    }
                }
                if(psths.size() > 0) {
                    psTrendHeatRepository.batchCreateOrUpdate(psths);
                    psths.clear();
                    postNewDao.clear();
                }
            }
        });
    }
    
    private void doTrendPostCirIn() {
        postNewDao.getLikeCirInCountPerPost(PostAttrType.PostCircleInCount, startTime, endTime, new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                Long logIdx = 0L;
                List<PsTrendHeat> psths = new ArrayList<PsTrendHeat>();
                
                while (sr.next()) {
                    Object [] result = sr.get();
                    if(result.length < 5)
                        break;
                    Long postId = (Long) result[0];
                    String locale = (String) result[1];
                    Long circleTypeId = (Long) result[2];
                    Date createdTime = (Date) result[3];
                    Integer cirIns = (Integer) result[4];
                    PsTrendHeat psth = new PsTrendHeat();
                    psth.setId(postId.toString());
                    psth.setLoc(locale);
                    List<String> cirTypes = new ArrayList<String>();
                    cirTypes.add(circleTypeId.toString());
                    psth.setCirTypes(cirTypes);
                    psth.setCirIns(cirIns);
                    psth.setDate(createdTime);
                    psths.add(psth);

                    if(psths.size() >= BATCH_QUERY_SIZE) {
                        psTrendHeatRepository.batchCreateOrUpdate(psths);
                        psths.clear();
                        postNewDao.clear();
                    }
                    logIdx++;
                    if(logIdx % 10000L == 0) {
                        logger.error("Current adding trend circleIn : " + postId.toString());
                        logIdx = 0L;
                    }
                }
                if(psths.size() > 0) {
                    psTrendHeatRepository.batchCreateOrUpdate(psths);
                    psths.clear();
                    postNewDao.clear();
                }
            }
        });
    }

    private void doGenTrend(final Boolean promoted, final String locale) {
        postNewDao.getGenPerPost(locale, promoted, startTime, endTime, new ScrollableResultsCallback() {
            @Override
            public void doInHibernate(ScrollableResults sr) {
                Long logIdx = 0L;
                Map<Long, Pair<Long, Date>> postMap = new HashMap<Long, Pair<Long, Date>>();
                
                while (sr.next()) {
                    Object [] result = sr.get();
                    if(result.length < 1)
                        break;
                    Long postId = (Long) result[0];
                    Date createdTime = (Date) result[1];
                    Long promoteScore = (Long)result[2];
                    postMap.put(postId, Pair.of(promoteScore, createdTime));
                    
                    if(postMap.size() >= BATCH_QUERY_SIZE) {
                        psTrendService.addGeneralPosts(locale, postMap);
                        postMap.clear();
                        postNewDao.clear();
                    }
                    logIdx++;
                    if(logIdx % 10000L == 0) {
                        logger.error("Current generate trend : " + postId.toString());
                        logIdx = 0L;
                    }
                }
                if(postMap.size() > 0) {
                    psTrendService.addGeneralPosts(locale, postMap);
                    postMap.clear();
                    postNewDao.clear();
                }
            }
        });
    }
    
    private void doTopCatTrend(String locale) {
        List<CircleType> circleTypes = circleTypeDao.listTypesByLocale(locale, true);
        List<String> circleTypeIds = new ArrayList<String>();
        for(CircleType ct : circleTypes) {
            circleTypeIds.add(ct.getId().toString());
        }
        final int bucketId = PsTrendPool.getBucketId(new Date());
        final Date newDisplayDate = new Date();
        psTrendService.doWithBestPsTrend(locale, startTime, endTime, new ScanResultCallback<Map<String, Map<String, Date>>>() {

            @Override
            public void doWith(Map<String, Map<String, Date>> results) {
                for(String ctId : results.keySet()) {
                    Map<String, Date> pMap = results.get(ctId);
                    final List<PsTrendPool> toAddCatTrends = new ArrayList<PsTrendPool>();
                    for(String pId : pMap.keySet()) {
                        PsTrendPool tp = new PsTrendPool();
                        PsTrendPoolKey key = new PsTrendPoolKey();
                        key.setpId(Long.valueOf(pId));
                        key.setBucket(bucketId);
                        key.setCircleTypeId(Long.valueOf(ctId));
                        tp.setId(key);
                        tp.setDisplayTime(pMap.get(pId));
                        toAddCatTrends.add(tp);
                    }
                    transactionTemplate.execute(new TransactionCallback<Boolean>() {

                        @Override
                        public Boolean doInTransaction(TransactionStatus status) {
                            return psTrendService.batchCreateTrendPool(newDisplayDate, toAddCatTrends, null);
                        }
                        
                    });
                    
                }
            }
            
        });
    }
    
    public Resolution initUser() {
        if(startTime == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2014);
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DATE, 1);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startTime = cal.getTime();
            cal.add(Calendar.MONTH, 1);
            endTime = cal.getTime();
        }
        
        logger.error(startTime.toString() + "~" + endTime.toString());
        doUserPostCount();
        doUserLikesCount();
        doUserCirInCount();
        doUserFollowerCount();
        
        if(endTime.after(new Date())) {
            logger.error("All User Complete");
            return json("All User Complete");
        }
        
        startTime = endTime;
        Calendar nCal = Calendar.getInstance();
        nCal.setTime(startTime);
        nCal.add(Calendar.MONTH, 1);
        endTime = nCal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Connection conn = Jsoup.connect("http://" + getServletRequest().getServerName() + ":" + getServletRequest().getServerPort() + "/api/user/inint-badge.action");
        conn.data("startTime", sdf.format(startTime));
        conn.data("endTime", sdf.format(endTime));
        try {
            conn.ignoreContentType(true).post();
        } catch (IOException e) {
            return json("Stop");
        }
        return json("Complete");
    }
    
    @DefaultHandler
    public Resolution listTrendGroup() {
        return json(psTrendService.getPsTrendGroupMap());
    }
    
    // PsTrend : Step4
    public Resolution initTopCatTrend() {     
        if(locales == null) {
            locales = new ArrayList<String>();
            locales.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
        }
        
        if(startTime == null) {
            Calendar cal = Calendar.getInstance();
            endTime = cal.getTime();
            cal.add(Calendar.DATE, -60);
            startTime = cal.getTime();
        }
        
        logger.error(locales.get(0) + " : " + startTime.toString() + "~" + endTime.toString());
        doTopCatTrend(locales.get(0));
        
        locales.remove(0);
        if(locales.size() <= 0) {
            logger.error("All Post Complete");
            return json("All Post Complete");
        }
        
        Connection conn = Jsoup.connect("http://" + getServletRequest().getServerName() + ":" + getServletRequest().getServerPort() + "/api/user/inint-badge.action");
        conn.data("initTopCatTrend", "");
        for(String l : locales) {
            conn.data("locales", l);
        }
        try {
            conn.ignoreContentType(true).post();
        } catch (IOException e) {
            return json("Stop");
        }
        return json("Complete");
    
    }
    
    public Resolution purgeTopCatTrend() {     
        psTrendService.purgeExpiredTrendPool(new Date());
        return json("Complete");
    }
    
    // PsTrend : Step3
    public Resolution initTrendPostHeat() {
        if(startTime == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -60);
            startTime = cal.getTime();
            cal.add(Calendar.DATE, 1);
            endTime = cal.getTime();
        }
        
        logger.error(startTime.toString() + "~" + endTime.toString());
        doTrendPostLike();
        doTrendPostCirIn();
        
        if(endTime.after(new Date())) {
            logger.error("All Post Complete");
            return json("All Post Complete");
        }
        
        startTime = endTime;
        Calendar nCal = Calendar.getInstance();
        nCal.setTime(startTime);
        nCal.add(Calendar.DATE, 1);
        endTime = nCal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Connection conn = Jsoup.connect("http://" + getServletRequest().getServerName() + ":" + getServletRequest().getServerPort() + "/api/user/inint-badge.action");
        conn.data("initTrendPostHeat", "");
        conn.data("startTime", sdf.format(startTime));
        conn.data("endTime", sdf.format(endTime));
        try {
            conn.ignoreContentType(true).post();
        } catch (IOException e) {
            return json("Stop");
        }
        return json("Complete");
    }
    
    // PsTrend : Step1
    public Resolution createTrendGroup() {
        List<CircleTypeGroup> circleTypeGroup = circleTypeGroupDao.findAll();
        List<String> conCircleTypeGroup = new ArrayList<String>();
        Map<String, Long> customIdMap = new HashMap<String, Long>();
        for(int i = 0; i < circleTypeGroup.size(); i++) {
            String curICTG = circleTypeGroup.get(i).getId().toString();
            conCircleTypeGroup.add("," + curICTG + ",");
            Long gId1 = IdGenerator.generate(Constants.getShardId());
            customIdMap.put("," + curICTG + ",", gId1);
            for(int j = i + 1; j < circleTypeGroup.size(); j++) {
                String curJCTG = circleTypeGroup.get(j).getId().toString();
                conCircleTypeGroup.add("," + curICTG + "," + curJCTG + ",");
                Long gId2 = IdGenerator.generate(Constants.getShardId());
                customIdMap.put("," + curICTG + "," + curJCTG + ",", gId2);
            }
        }
        circleTypeGroup.clear();
        Set<String> locales = localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE);
        Map<String, Map<String, String>> maps = new HashMap<String, Map<String, String>>();
        for(String l : locales) {
            maps.put(l, new HashMap<String, String>());
            for(String g : conCircleTypeGroup) {
                maps.get(l).put(g, "");
            }
        }
        locales.clear();
        
        List<CircleType> circleTypes = circleTypeDao.listAllTypes();
        for(CircleType ct : circleTypes) {
            if(!maps.containsKey(ct.getLocale()))
                continue;
            Map<String, String> gs = maps.get(ct.getLocale());
            for(String g : gs.keySet()) {
                if(!g.contains("," + ct.getCircleTypeGroupId().toString() + ","))
                    continue;
                gs.put(g, gs.get(g) + ct.getId().toString() + ",");
            }
        }
        
        List<PsTrendGroup> psTGroups = new ArrayList<PsTrendGroup>();
        String defaultPointer = String.valueOf((PsTrendGroup.DEFAULT_POINTER).getTime());
        for(String l : maps.keySet()) {
            Map<String, String> m = maps.get(l);
            for(String g : m.keySet()) {
                PsTrendGroup pstg = new PsTrendGroup();
                PsTrendGroup.PsTrendGroupKey key = new PsTrendGroup.PsTrendGroupKey();
                key.setgId(customIdMap.get(g));
                key.setLocale(l);
                pstg.setId(key);
                String trimG = StringUtils.strip(g, ",");
                pstg.setGroups(trimG);
                String trimT = StringUtils.strip(m.get(g), ",");
                pstg.setTypes(trimT);
                if(StringUtils.countMatches(trimG, ",") == 0) {
                    pstg.setStep(20);
                    pstg.setPointers(defaultPointer);
                }
                else {
                    pstg.setStep(30);
                    pstg.setPointers(defaultPointer + "," + defaultPointer);
                }
                psTGroups.add(pstg);
            }
        }
        psTrendGroupDao.batchInsert(psTGroups);
        return null;
    }
    
    // PsTrend : Step2
    public Resolution initGenTrend() {
        if(locales == null) {
            locales = new ArrayList<String>();
            locales.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
        }
        
        if(startTime == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -60);
            startTime = cal.getTime();
            cal.add(Calendar.DATE, 1);
            endTime = cal.getTime();
        }
        
        logger.error(locales.get(0) + " : " + startTime.toString() + "~" + endTime.toString());
        doGenTrend(false, locales.get(0));
        
        String nextStart = null;
        String nextEnd = null;
        if(endTime.after(new Date())) {
            locales.remove(0);
            if(locales.size() <= 0) {
                logger.error("All Post Complete");
                return json("All Post Complete");
            }
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startTime = endTime;
            Calendar nCal = Calendar.getInstance();
            nCal.setTime(startTime);
            nCal.add(Calendar.DATE, 1);
            endTime = nCal.getTime();
            nextStart = sdf.format(startTime);
            nextEnd = sdf.format(endTime);
        }

        Connection conn = Jsoup.connect("http://" + getServletRequest().getServerName() + ":" + getServletRequest().getServerPort() + "/api/user/inint-badge.action");
        conn.data("initGenTrend", "");
        if(nextStart != null)
            conn.data("startTime", nextStart);
        if(nextEnd != null)
            conn.data("endTime", nextEnd);
        for(String l : locales) {
            conn.data("locales", l);
        }
        try {
            conn.ignoreContentType(true).post();
        } catch (IOException e) {
            return json("Stop");
        }
        return json("Complete");
    }
    
 // PsTrend : Step2.5
    public Resolution initPromotedGenTrend() {
        if(locales == null) {
            locales = new ArrayList<String>();
            locales.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
        }
        
        if(startTime == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -60);
            startTime = cal.getTime();
            cal.add(Calendar.DATE, 1);
            endTime = cal.getTime();
        }
        
        logger.error(locales.get(0) + " : " + startTime.toString() + "~" + endTime.toString());
        doGenTrend(true, locales.get(0));
        
        String nextStart = null;
        String nextEnd = null;
        if(endTime.after(new Date())) {
            locales.remove(0);
            if(locales.size() <= 0) {
                logger.error("All Post Complete");
                return json("All Post Complete");
            }
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startTime = endTime;
            Calendar nCal = Calendar.getInstance();
            nCal.setTime(startTime);
            nCal.add(Calendar.DATE, 1);
            endTime = nCal.getTime();
            nextStart = sdf.format(startTime);
            nextEnd = sdf.format(endTime);
        }

        Connection conn = Jsoup.connect("http://" + getServletRequest().getServerName() + ":" + getServletRequest().getServerPort() + "/api/user/inint-badge.action");
        conn.data("initPromotedGenTrend", "");
        if(nextStart != null)
            conn.data("startTime", nextStart);
        if(nextEnd != null)
            conn.data("endTime", nextEnd);
        for(String l : locales) {
            conn.data("locales", l);
        }
        try {
            conn.ignoreContentType(true).post();
        } catch (IOException e) {
            return json("Stop");
        }
        return json("Complete");
    }
    
    public Resolution releaseFromPool() {
        PsTrendGroup group = psTrendGroupDao.findAll().get(0);
        List<PsTrendGroup> groups = new ArrayList<PsTrendGroup>();
        groups.add(group);
        final Date newDisplayDate = new Date();
        psTrendService.releaseFromTrendPool(groups, new ScanResultCallback<Map<PsTrendKey, Long>>() {

            @Override
            public void doWith(Map<PsTrendKey, Long> r) {
                psTrendService.batchAddTrendPost(newDisplayDate, r, false, null);
            }
            
        }, new ScanResultCallback<List<PsTrendGroup>>() {

            @Override
            public void doWith(List<PsTrendGroup> r) {
                for(PsTrendGroup g : r)
                    psTrendGroupDao.update(g);
            }
            
        });
        return json("Complete");
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public void setLocales(List<String> locales) {
        this.locales = locales;
    }
}
