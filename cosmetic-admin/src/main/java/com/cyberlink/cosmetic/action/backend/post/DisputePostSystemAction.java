package com.cyberlink.cosmetic.action.backend.post;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.service.BackendPostService;
import com.cyberlink.cosmetic.action.backend.service.BackendPostService.TransactionRunnable;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeGroupDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.circle.model.CircleTypeGroup;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.mail.service.MailInappropPostCommentService;
import com.cyberlink.cosmetic.modules.post.dao.PostCurateKeywordDao;
import com.cyberlink.cosmetic.modules.post.dao.PostNewPoolDao;
import com.cyberlink.cosmetic.modules.post.dao.PostReportedDao;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreDao;
import com.cyberlink.cosmetic.modules.post.dao.PostScoreTrendDao;
import com.cyberlink.cosmetic.modules.post.model.PostCurateKeyword;
import com.cyberlink.cosmetic.modules.post.model.PostScore;
import com.cyberlink.cosmetic.modules.post.model.PostNewPool.NewPoolGroup;
import com.cyberlink.cosmetic.modules.post.model.PostScore.CreatorType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.PoolType;
import com.cyberlink.cosmetic.modules.post.model.PostScore.ResultType;
import com.cyberlink.cosmetic.modules.post.model.PostScoreTrend;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolInfo;
import com.cyberlink.cosmetic.modules.post.model.TrendPoolType;
import com.cyberlink.cosmetic.modules.post.repository.TrendingRepository;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.Attachments;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.DisputePostView;
import com.cyberlink.cosmetic.modules.post.result.MainPostSimpleWrapper.File;
import com.cyberlink.cosmetic.modules.post.service.PostPopularityService;
import com.cyberlink.cosmetic.modules.post.service.PostService;
import com.cyberlink.cosmetic.modules.user.dao.SessionDao;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@UrlBinding("/post/DisputePostSystem.action")
public class DisputePostSystemAction extends AbstractPostAction {
    @SpringBean("post.PostService")
    private PostService postService;
    
    @SpringBean("circle.circleTypeGroupDao")
    private CircleTypeGroupDao circleTypeGroupDao;
    
    @SpringBean("mail.mailInappropPostCommentService")
    private MailInappropPostCommentService mailInappropPostCommentService;
    
    @SpringBean("user.SessionDao")
    private SessionDao sessionDao;
    
    @SpringBean("user.UserDao")
    private UserDao userDao;

    @SpringBean("post.PostReportedDao")
    private PostReportedDao postReportedDao;
    
    @SpringBean("common.localeDao")
    private LocaleDao localeDao;
    
    @SpringBean("circle.circleTypeDao") 
    private CircleTypeDao circleTypeDao;
    
    @SpringBean("web.objectMapper")
    private ObjectMapper objectMapper;
    
    @SpringBean("backend.BackendPostService")
    private BackendPostService backendPostService;
    
    @SpringBean("post.PostScoreDao")
    private PostScoreDao postScoreDao;
    
    @SpringBean("post.PostScoreTrendDao")
    private PostScoreTrendDao postScoreTrendDao;
    
    @SpringBean("post.PostCurateKeywordDao")
    private PostCurateKeywordDao postCurateKeywordDao;
    
    @SpringBean("post.PostNewPoolDao")
    private PostNewPoolDao postNewPoolDao;
    
    @SpringBean("post.PostPopularityService")
    private PostPopularityService postPopularityService;
    
    @SpringBean("post.trendingRepository")
	private TrendingRepository trendingRepository;
    
    // Route
    private PageResult<DisputePostWrapper> pageResult = new PageResult<DisputePostWrapper>();
    private int offset = 0;
    private int size = 10;
    private List<String> availableRegion = new ArrayList<String>(0);
    private String selRegion = "en_US";
    private Map<Long, String> availableCircleTypes = new LinkedHashMap<Long, String>();
    
    // getDispute
    private List<Integer> availablePageSize = null;    
    private Integer pageSize = 50;
    private ImmutableMap<String, String> localeTimeZoneMap = ImmutableMap.<String, String>builder()
            .put("en_US", "GMT-7")
            .put("de_DE", "GMT+2")
            .put("fr_FR", "GMT+2")
            .put("zh_CN", "GMT+8")
            .put("zh_TW", "GMT+8")
            .put("ja_JP", "GMT+9")
            .put("ko_KR", "GMT+9")
            .build();
    private ImmutableMap<String, Pair<String, Integer>> backwardCirIconMap = ImmutableMap.<String, Pair<String, Integer>>builder()
            .put("MAKEUP", Pair.of("./../common/theme/backend/images/ma_1.png", 0))
            .put("EYE_MAKEUP", Pair.of("./../common/theme/backend/images/ey_1.png", 1))
            .put("LIPS", Pair.of("./../common/theme/backend/images/li_1.png", 2))
            .put("HAIR", Pair.of("./../common/theme/backend/images/ha_1.png", 3))
            .put("NAILS", Pair.of("./../common/theme/backend/images/na_1.png", 4))
            .put("OUTFITS", Pair.of("./../common/theme/backend/images/ou_1.png", 5))
            .put("SKINCARE", Pair.of("./../common/theme/backend/images/sk_1.png", 6))
            .put("SELFIE", Pair.of("./../common/theme/backend/images/se_1.png", 7))
            .put("OTHER", Pair.of("./../common/theme/backend/images/ot_1.png", 8))
            .build();
    private ImmutableMap<String, Pair<String, Integer>> cirIconMap = ImmutableMap.<String, Pair<String, Integer>>builder()
            .put("MAKEUP", Pair.of("./../common/theme/backend/images/ma_1.png", 2))
            .put("EYE_MAKEUP", Pair.of("./../common/theme/backend/images/ey_1.png", 3))
            .put("LIPS", Pair.of("./../common/theme/backend/images/li_1.png", 4))
            .put("HAIR", Pair.of("./../common/theme/backend/images/ha_1.png", 6))
            .put("NAILS", Pair.of("./../common/theme/backend/images/na_1.png", 5))
            .put("OUTFITS", Pair.of("./../common/theme/backend/images/ou_1.png", 9))
            .put("SKINCARE", Pair.of("./../common/theme/backend/images/sk_1.png", 7))
            .put("SELFIE", Pair.of("./../common/theme/backend/images/se_1.png", 8))
            .put("BEAUTY_PRODUCTS", Pair.of("./../common/theme/backend/images/be_1.png", 1))
            .put("CELEBRITY", Pair.of("./../common/theme/backend/images/ce_1.png", 0))
            .put("SHOES", Pair.of("./../common/theme/backend/images/sh_1.png", 10))
            .put("BAGS", Pair.of("./../common/theme/backend/images/ba_1.png", 11))
            .put("GLASSES", Pair.of("./../common/theme/backend/images/gl_1.png", 12))
            .put("JEWELRY", Pair.of("./../common/theme/backend/images/je_1.png", 14))
            .put("WATCHES", Pair.of("./../common/theme/backend/images/wa_1.png", 13))
            .put("MEN", Pair.of("./../common/theme/backend/images/me_1.png", 16))
            .put("KIDS_FASHION", Pair.of("./../common/theme/backend/images/ki_1.png", 15))
            .put("OTHER", Pair.of("./../common/theme/backend/images/ot_1.png", 17))
            .build();
    private String timeFormat = "yyyy-MM-dd HH:mm";
    private Integer totalSize = null;
    private Map<String, Object> infoByLocale = new LinkedHashMap<String, Object>();
    private Long selCircleTypeId = 0L;
    private String selCreatorType = "All";
    
    // rescue
    private String rescueObjs = "";
    public String RESCUE_TASK_NAME = "com.cyberlink.cosmetic.action.backend.post.DisputePostSystem.RescueTask";
            
    // activity
	private Map<String, Map<String, Map<ResultType, Long>>> postScoreCountsMap = new LinkedHashMap<String, Map<String, Map<ResultType, Long>>>();
	private Map<String, List<String>> lastActivityRecord = new LinkedHashMap<String, List<String>>();
    private Map<String, List<String>> activitySummary = new LinkedHashMap<String, List<String>>();
    
    // addKeyword
    private String keyword;
    
    private PoolType poolType;

    public Long curUserId;
    
    private void loadAvailableRegion() {
        availableRegion.clear();
        availableRegion.addAll(localeDao.getAvailableLocaleByType(LocaleType.POST_LOCALE));
    }
    
    private void loadAvailableCircleType() {
        List<String> locales = new ArrayList<String>();
        locales.add(selRegion);
        List<CircleType> cTypes = circleTypeDao.listTypesByLocales(locales, null, new BlockLimit(0, 100)).getResults();
        for(CircleType ct : cTypes) {
            String cName = ct.getDefaultType(); 
            cName = cName.replace("_", " ");
            StringBuffer stringbf = new StringBuffer();
            Matcher m = Pattern.compile("([a-z])([a-z]*)",
            Pattern.CASE_INSENSITIVE).matcher(cName);
            while (m.find()) {
               m.appendReplacement(stringbf, 
               m.group(1).toUpperCase() + m.group(2).toLowerCase());
            }
            availableCircleTypes.put(ct.getId(), m.appendTail(stringbf).toString());
        }
    }
    
    private Map<String, Long> processRevivedCount(List<Object> curatedCounts) {
        Map<String, Long> countMap = new HashMap<String, Long>();
        countMap.put("C", 0L);
        countMap.put("CNT", 0L);
        countMap.put("KW", 0L);
        for(Object obj : curatedCounts) {
            Object[] row = (Object[]) obj;
            ResultType rt = (ResultType) row[0];
            Long count = (Long) row[1];
            String typeStr = null;
            switch(rt) {
            case Revive:
            case SelfieDiscover:
            case CatAndTrend:
                typeStr = "CNT";
                break;
            case Selfie:       
            case CatOnly:
            case SelfieOnly:
                typeStr = "C";
                break;
            case ChangeKeyWord:
                typeStr = "KW";
                break;
            case Abandon:
                break;
            default:
                break;
            }
            if(typeStr == null)
                continue;
            if(!countMap.containsKey(typeStr))
                continue;
            countMap.put(typeStr, countMap.get(typeStr) + count);
        }
        return countMap;
    }
    
    private Map<String, Long> processRetagCount(List<Object> curatedCounts) {
        Map<String, Long> countMap = new HashMap<String, Long>();
        countMap.put("C", 0L);
        countMap.put("CNT", 0L);
        countMap.put("KW", 0L);
        for(Object obj : curatedCounts) {
            Object[] row = (Object[]) obj;
            ResultType rt = (ResultType) row[0];
            Long count = (Long) row[1];
            String typeStr = null;
            switch(rt) {
            case Revive:
            case SelfieDiscover:
            case CatAndTrend:
            case Selfie:       
            case CatOnly:
            case SelfieOnly:
                typeStr = "KW";
                break;
            case ChangeKeyWord:
            case Abandon:
            default:
                break;
            }
            if(typeStr == null)
                continue;
            if(!countMap.containsKey(typeStr))
                continue;
            countMap.put(typeStr, countMap.get(typeStr) + count);
        }
        return countMap;
    }
    
    private void loadInfoByLocale(String locale, PoolType type) {
        String timeZoneId = "GMT+8";
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        SimpleDateFormat customDateFormatter = new SimpleDateFormat(timeFormat);
        customDateFormatter.setTimeZone(timeZone);

        List<ResultType> resultTypes = new ArrayList<ResultType>();
        if(poolType.getRevived() && !poolType.getEnableNewInfo()) {
            resultTypes.add(ResultType.ChangeKeyWord);
        }
        else {
            resultTypes.add(ResultType.CatAndTrend);
            resultTypes.add(ResultType.CatOnly);
            resultTypes.add(ResultType.SelfieOnly);
            resultTypes.add(ResultType.Abandon);
        }
        
        if (PoolType.Trending.equals(poolType)) {
            PostScoreTrend lastRecord = postScoreTrendDao.getLastHandledRecord(selRegion, poolType, resultTypes);
            if(lastRecord != null) {
                infoByLocale.put("Last Modified", String.valueOf(customDateFormatter.format(lastRecord.getLastModified())));
                User curator = userDao.findById(lastRecord.getReviewerId());
                infoByLocale.put("Last Modified By", curator.getDisplayName());
                infoByLocale.put("Sepatare Date", lastRecord.getLastModified());
            }
            else {
                infoByLocale.put("Last Modified", "");
                infoByLocale.put("Last Modified By", "");
            }
        }
        else {
            PostScore lastRecord = postScoreDao.getLastHandledRecord(selRegion, poolType, resultTypes);
            if(lastRecord != null) {
                infoByLocale.put("Last Modified", String.valueOf(customDateFormatter.format(lastRecord.getLastModified())));
                User curator = userDao.findById(lastRecord.getReviewerId());
                infoByLocale.put("Last Modified By", curator.getDisplayName());
                infoByLocale.put("Sepatare Date", lastRecord.getLastModified());
            }
            else {
                infoByLocale.put("Last Modified", "");
                infoByLocale.put("Last Modified By", "");
            }
        }
        
        Map<String, Map<String, Long>> postScoreCount = new LinkedHashMap<String, Map<String, Long>>();
        SimpleDateFormat ymdFormatter = new SimpleDateFormat("yyyy-MM-dd");      
        ymdFormatter.setTimeZone(timeZone);
        Calendar cal = Calendar.getInstance(timeZone);
        Date end = cal.getTime();
        cal.add(Calendar.HOUR, -cal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date begin = cal.getTime();
        if(poolType.getRevived() && !poolType.getEnableNewInfo()) {
            List<Object> curatedCounts3;
            if (PoolType.Trending.equals(poolType))
                curatedCounts3 = postScoreTrendDao.getHandledPostScoreCountByDate(selRegion, poolType, resultTypes, null, null);
            else
                curatedCounts3 = postScoreDao.getHandledPostScoreCountByDate(selRegion, poolType, resultTypes, null, null);
            postScoreCount.put("Total Retag Count", processRevivedCount(curatedCounts3));
        }
        else {
            List<NewPoolGroup> gs = new ArrayList<NewPoolGroup>();
            if(PoolType.Pgc.equals(poolType)) {
                gs.add(NewPoolGroup.Publication);
                gs.add(NewPoolGroup.Publication_Cat);
                gs.add(NewPoolGroup.Beautyist);
                gs.add(NewPoolGroup.Beautyist_Cat);
                gs.add(NewPoolGroup.Brand);
                gs.add(NewPoolGroup.Brand_Cat);
            }
            else if(PoolType.RetagScraped.equals(poolType)) {
                gs.add(NewPoolGroup.Scraped);
                gs.add(NewPoolGroup.Scraped_Cat);
            }
            else {
                gs.add(NewPoolGroup.Normal);
                gs.add(NewPoolGroup.Normal_Cat);
            }
            
            Map<NewPoolGroup, Long> poolCount = postNewPoolDao.getPostCountInPoolPerGroup(selRegion, gs);
            Map<String, Map<String, String>> poolCountMap = new LinkedHashMap<String, Map<String, String>>();
            for(NewPoolGroup npg : gs) {
                String [] groupingKey = npg.toString().split("_");
                if(!poolCountMap.containsKey(groupingKey[0])) {
                    Map<String, String> defaultValue = new LinkedHashMap<String, String>();
                    defaultValue.put("T", "0");
                    defaultValue.put("C", "0");
                    poolCountMap.put(groupingKey[0], defaultValue);
                }
            }
            for(NewPoolGroup pg : poolCount.keySet()) {
                String [] groupingKey = pg.toString().split("_");
                if(!poolCountMap.containsKey(groupingKey[0]))
                    continue;
                
                Map<String, String> cMap = poolCountMap.get(groupingKey[0]);
                if(groupingKey.length > 1)
                    cMap.put("C", poolCount.get(pg).toString());
                else
                    cMap.put("T", poolCount.get(pg).toString());
            }
            String poolInfo = "";
            String infoFormat = "%s(T:%s, C:%s)";
            if(gs.size() > 2) {
                for(String g : poolCountMap.keySet()) {
                    if(poolInfo.length() > 0)
                        poolInfo += ", ";
                    String cCount = "0", tCount = "0";
                    Map<String, String> cMap = poolCountMap.get(g);
                    if(cMap.containsKey("C"))
                        cCount = cMap.get("C");
                    if(cMap.containsKey("T"))
                        tCount = cMap.get("T");
                    poolInfo += String.format(infoFormat, g.toString(), tCount, cCount);
                }
            }
            else if(gs.size() > 0) {
                for(String g : poolCountMap.keySet()) {
                    if(poolInfo.length() > 0)
                        poolInfo += ", ";
                    String cCount = "0", tCount = "0";
                    Map<String, String> cMap = poolCountMap.get(g);
                    if(cMap.containsKey("C"))
                        cCount = cMap.get("C");
                    if(cMap.containsKey("T"))
                        tCount = cMap.get("T");
                    poolInfo += String.format(infoFormat, "", tCount, cCount);
                }
            }
            infoByLocale.put("Pool Size", poolInfo);
        }
        
        if(!poolType.getEnableNewInfo()){ // last 2 days records
	        List<Object> curatedCounts1;
	        if (PoolType.Trending.equals(poolType))
	            curatedCounts1 = postScoreTrendDao.getHandledPostScoreCountByDate(selRegion, poolType, resultTypes, begin, end);
	        else
	            curatedCounts1 = postScoreDao.getHandledPostScoreCountByDate(selRegion, poolType, resultTypes, begin, end);
	        
	        if(!poolType.getRevived())
	            postScoreCount.put(ymdFormatter.format(begin) + " Revived Count", processRevivedCount(curatedCounts1));
	        else
	            postScoreCount.put(ymdFormatter.format(begin) + " Retag Count", processRevivedCount(curatedCounts1));
	        
	        end = begin;
	        cal.add(Calendar.DATE, -1);
	        begin = cal.getTime();
	        List<Object> curatedCounts2;
	        if (PoolType.Trending.equals(poolType))
	            curatedCounts2 = postScoreTrendDao.getHandledPostScoreCountByDate(selRegion, poolType, resultTypes, begin, end);
	        else
	            curatedCounts2 = postScoreDao.getHandledPostScoreCountByDate(selRegion, poolType, resultTypes, begin, end);
	        if(!poolType.getRevived())
	            postScoreCount.put(ymdFormatter.format(begin) + " Revived Count", processRevivedCount(curatedCounts2));
	        else
	            postScoreCount.put(ymdFormatter.format(begin) + " Retag Count", processRevivedCount(curatedCounts2));
        }
        else { // last 8 days records
            Calendar cals = Calendar.getInstance(timeZone);
            Date endTime = cals.getTime();
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            dateFormater.setTimeZone(timeZone);
        	List<String> dateList = new ArrayList<String>();
        	for(int i=0; i<8; i++){
				String date = dateFormater.format(cals.getTime());
	        	dateList.add(date);
	        	cals.add(Calendar.DATE, -1);
        	}
            Date beginTime = cals.getTime();

            List<Object> objects = postScoreDao.getHandledPostScoreCounts(selRegion, poolType, resultTypes, beginTime, endTime);
            Map<String, List<Object>> postScoreCountTemp = new LinkedHashMap<String, List<Object>>();
     		for(Object obj : objects){
     			Object[] row = (Object[]) obj;
     			String date = ((Date) row[1]).toString();
     			
     			if(!postScoreCountTemp.containsKey(date)) 
     				postScoreCountTemp.put(date, new ArrayList<Object>());
     			
                if(postScoreCountTemp.containsKey(date)) {
                	Object[] ob = new Object[] {row[2], row[3]};
                	postScoreCountTemp.get(date).add(ob);
                }
     		}
        	String countString = " Revived Count";
     		for(String date : dateList) {
     			if(postScoreCountTemp.containsKey(date))
     				postScoreCount.put(date + countString, processRevivedCount(postScoreCountTemp.get(date)));
     			else
     				postScoreCount.put(date + countString, processRevivedCount(new ArrayList<Object>()));
     		}
     		
     		listUnCuratedPostCount();
        }

        for(String msgKey : postScoreCount.keySet()) {
            Map<String, Long> tmp = postScoreCount.get(msgKey);
            String msgInfo;
            if(!poolType.getRevived() || poolType.getEnableNewInfo()) {
                msgInfo = tmp.containsKey("CNT") ? tmp.get("CNT").toString() : "0";
                msgInfo += " (Trending), ";
                msgInfo += tmp.containsKey("C") ? tmp.get("C").toString() : "0";
                msgInfo += " (Category Only)";
            }
            else {
                msgInfo = tmp.containsKey("KW") ? tmp.get("KW").toString() : "0";
            }
            infoByLocale.put(msgKey, msgInfo);
        }
        infoByLocale.put("revived", poolType.getRevived());
        infoByLocale.put("reviewed", poolType.getReviewed());
        infoByLocale.put("preSelectQuality", poolType.getPreSelectQuality());
        infoByLocale.put("preSelectCircle", poolType.getPreSelectCircle());
        
        if(!type.getMultiCategory()) {
            backwardCompatibleLoadInfoByLocale(locale);
            return;
        }
        
        loadInfoByLocale(locale);
    }
    
	private void listUnCuratedPostCount() {
    	loadAvailableRegion();
        
		List<Object> unCuratedPostCounts = postScoreDao.listUnCuratedPostCounts(availableRegion, null, poolType);
		Map<String, Long> unCuratedPostCount = new HashMap<String, Long>();
		Map<String, Date> oldestUnCuratedPost = new HashMap<String, Date>();
		for (Object obj : unCuratedPostCounts) {
			Object[] row = (Object[]) obj;
			unCuratedPostCount.put((String) row[0], (Long) row[1]);
			oldestUnCuratedPost.put((String) row[0], (Date) row[2]);
		}
        
		List<Map<String, String>> unCuratedCount = new ArrayList<Map<String, String>>();
		for (String loc : availableRegion) {
			Map<String, String> countMap = new HashMap<String, String>();
			countMap.put("locale", loc);
			if (unCuratedPostCount.containsKey(loc))
				countMap.put("count", unCuratedPostCount.get(loc).toString());
			else
				countMap.put("count", "0");
			
			if (oldestUnCuratedPost.get(loc) != null) {
				Date oldestPostTime = oldestUnCuratedPost.get(loc);
				SimpleDateFormat timeFormater = new SimpleDateFormat(timeFormat);
				timeFormater.setTimeZone(TimeZone.getTimeZone("GMT+8"));
				countMap.put("oldestPost", timeFormater.format(oldestPostTime));
			} else
				countMap.put("oldestPost", "N/A");
			unCuratedCount.add(countMap);
		}
		infoByLocale.put("unCuratedSummary", unCuratedCount);
	}
    
    private void loadInfoByLocale(String locale) {
        List<String> locales = new ArrayList<String>();
        locales.add(locale);
        PageResult<CircleType> circleTypes = circleTypeDao.listTypesByLocales(locales, null, new BlockLimit(0, 100));
        List<Map<String, String>> circles = new ArrayList<Map<String, String>>();
        for(CircleType c : circleTypes.getResults()) {
            if("HOW-TO".equals(c.getDefaultType()))
                continue;
            if(!cirIconMap.containsKey(c.getDefaultType()))
                continue;
            Map<String, String> cMap = new HashMap<String, String>();
            cMap.put("circleTypeId", c.getId().toString());
            cMap.put("circleName", c.getCircleTypeName());
            cMap.put("defaultType", c.getDefaultType());
            cMap.put("iconUrl", cirIconMap.get(c.getDefaultType()).getLeft());
            circles.add(cMap);
            String iconUrl = c.getIconUrl();
            if(iconUrl == null)
                iconUrl = "./../common/theme/backend/images/ico_ot.png";
        }
        Collections.sort(circles, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return Integer.compare(cirIconMap.get(o1.get("defaultType")).getRight(), cirIconMap.get(o2.get("defaultType")).getRight());
            }
        });
        infoByLocale.put("circles", circles);
        
        List<Map<String, String>> keywords = getKeywords(locale);
        infoByLocale.put("keywords", keywords);
    }
    
    private List<Map<String, String>> getKeywords(String locale) {
        List<Map<String, String>> keywords = new ArrayList<Map<String, String>>();
        List<PostCurateKeyword> pgKeyword = postCurateKeywordDao.listAllByLocale(locale, null);
        if(pgKeyword != null && pgKeyword.size() > 0) {
            for(PostCurateKeyword k : pgKeyword) {
                Map<String, String> kMap = new HashMap<String, String>();
                kMap.put("id", k.getId().toString());
                kMap.put("keyword", k.getKeyword());
                keywords.add(kMap);
            }
        }
        return keywords;
    }
    
    private void backwardCompatibleLoadInfoByLocale(String locale) {        
        List<String> locales = new ArrayList<String>();
        locales.add(locale);
        PageResult<CircleType> circleTypes = circleTypeDao.listTypesByLocales(locales, null, new BlockLimit(0, 100));
        List<Map<String, String>> circles = new ArrayList<Map<String, String>>();
        Map<String, Pair<String, Integer>> kwImgMap = new HashMap<String, Pair<String, Integer>>();
        List<Map<String, String>> extraCircles = new ArrayList<Map<String, String>>();
        for(CircleType c : circleTypes.getResults()) {
            if("HOW-TO".equals(c.getDefaultType()))
                continue;
            Map<String, String> cMap = new HashMap<String, String>();
            cMap.put("circleTypeId", c.getId().toString());
            cMap.put("circleName", c.getCircleTypeName());
            cMap.put("defaultType", c.getDefaultType());
            if(!backwardCirIconMap.containsKey(c.getDefaultType())) {
                extraCircles.add(cMap);
            }
            else {
                cMap.put("iconUrl", backwardCirIconMap.get(c.getDefaultType()).getLeft());
                circles.add(cMap);
                String iconUrl = c.getIconUrl();
                if(iconUrl == null)
                    iconUrl = "./../common/theme/backend/images/ico_ot.png";
                kwImgMap.put(c.getDefaultType(), Pair.of(iconUrl, backwardCirIconMap.get(c.getDefaultType()).getRight()));
            }
        }
        Collections.sort(circles, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return Integer.compare(backwardCirIconMap.get(o1.get("defaultType")).getRight(), backwardCirIconMap.get(o2.get("defaultType")).getRight());
            }
        });
        Collections.sort(extraCircles, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                return o1.get("circleName").compareTo(o2.get("circleName"));
            }
        });
        infoByLocale.put("circles", circles);
        infoByLocale.put("extraCircles", extraCircles);
        
        List<Map<String, String>> keywords = new ArrayList<Map<String, String>>();
        List<PostCurateKeyword> pgKeyword = postCurateKeywordDao.listAllByLocale(locale, null);
        PostCurateKeyword [] imgKeyWords = new PostCurateKeyword[kwImgMap.size()];
        if(pgKeyword != null && pgKeyword.size() > 0) {
            for(PostCurateKeyword k : pgKeyword) {
                if(k.getDefaultType() != null && kwImgMap.containsKey(k.getDefaultType())) {
                    imgKeyWords[kwImgMap.size() - 1 - kwImgMap.get(k.getDefaultType()).getRight()] = k;
                    continue;
                }
                Map<String, String> kMap = new HashMap<String, String>();
                kMap.put("id", k.getId().toString());
                kMap.put("keyword", k.getKeyword());
                keywords.add(kMap);
            }
            for(int idx = 0; idx < imgKeyWords.length; idx++) {
                PostCurateKeyword ikw = imgKeyWords[idx];
                if(ikw == null)
                    continue;
                Map<String, String> kMap = new HashMap<String, String>();
                kMap.put("id", ikw.getId().toString());
                kMap.put("keyword", ikw.getKeyword());
                kMap.put("imgUrl", kwImgMap.get(ikw.getDefaultType()).getLeft());
                keywords.add(0, kMap);
            }
        }
        infoByLocale.put("keywords", keywords);
    }
    
    public List<String> getAvailableRegion() {
        return availableRegion;
    }
    
    public String getSelRegion() {
        return selRegion;
    }
    
    public void setSelRegion(String selRegion) {
        this.selRegion = selRegion;
    }
    
    public void setOffset(int offset){
        this.offset = offset;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public void setSize(int size){
        this.size = size;
    }
    
    public int getSize() {
        return size;
    }
    
    public List<String> getCreatorType() {
        List<String> r = new ArrayList<String>();
        r.add("All");
        for(CreatorType ct : CreatorType.values())
            r.add(ct.toString());
        return r;
    }
    
    public void setpageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    
    public Integer getPageSize() {
        return this.pageSize;
    }
    
    public List<Integer> getAvailablePageSize() {
        if(availablePageSize == null) {
            if("Dev".equalsIgnoreCase(Constants.getPostRegion()))
                availablePageSize = new ArrayList<Integer>(Arrays.asList( 1, 10, 20, 50, 100)); 
            else
                availablePageSize = new ArrayList<Integer>(Arrays.asList( 10, 20, 50, 100));
        }
        return availablePageSize;
    }
    
    public Map<Long, String> getAvailableCircleTypes() {
        return availableCircleTypes;
    }
    
    public PageResult<DisputePostWrapper> getPageResult() {
        return pageResult;
    }
    
    public void setRescueObjs(String rescueObjs) {
        this.rescueObjs = rescueObjs;
    }
    
	public Map<String, Map<String, Map<ResultType, Long>>> getPostScoreCountsMap() {
		return postScoreCountsMap;
	}
	
    public Map<String, List<String>> getLastActivityRecord() {
        return lastActivityRecord;
    }
    
    public Map<String, List<String>> getActivitySummary() {
        return activitySummary;
    }    
    
    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }
    
    public Long getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(Long curUserId) {
		this.curUserId = curUserId;
	}

	public Map<String, Object> getInfoByLocale() {
        return infoByLocale;
    }

    public void setInfoByLocale(Map<String, Object> infoByLocale) {
        this.infoByLocale = infoByLocale;
    }
    
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public PoolType getPoolType() {
        return poolType;
    }
    
    public void setPoolType(PoolType poolType) {
        this.poolType = poolType;
    }
    
	@DefaultHandler
    public Resolution route() {  
        if (!getCurrentUserAdmin() && !getAccessControl().getReportManagerAccess()) {
            return new ErrorResolution(403, "Need to login");
        }
        this.curUserId = getCurrentUserId();
        loadAvailableRegion();
        loadAvailableCircleType();
        return forward();
    }
    
	public Resolution getLastHandledTime() {
        String timeZoneId = "GMT+0";
        if(localeTimeZoneMap.containsKey(selRegion))
            timeZoneId = localeTimeZoneMap.get(selRegion);
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        SimpleDateFormat customDateFormatter = new SimpleDateFormat(timeFormat);
        customDateFormatter.setTimeZone(timeZone);
        
        String lastModifyDate = "";
        String lastModifyDuration = "";
        Map<String, String> lastModifiedMap = getLastModified(poolType, customDateFormatter);
        Date lastHandledTime = null;
        if(lastModifiedMap != null && lastModifiedMap.containsKey("Last submission time")) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
            Date now = cal.getTime();
            try {
                lastHandledTime = customDateFormatter.parse(lastModifiedMap.get("Last submission time"));
                Period dif = new Period(lastHandledTime.getTime(), now.getTime(), PeriodType.dayTime());
                PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix(" day ")
                .appendHours().appendSuffix(" hr ")
                .appendMinutes().appendSuffix(" min ago")
                .toFormatter();
                String lmdTmp = formatter.print(dif);
                if(lmdTmp != null && lmdTmp.length() > 0)
                    lastModifyDuration = lmdTmp;
                
                String twTimeZoneId = "GMT+8";
                TimeZone twTimeZone = TimeZone.getTimeZone(twTimeZoneId);
                SimpleDateFormat twCustomDateFormatter = new SimpleDateFormat(timeFormat);
                twCustomDateFormatter.setTimeZone(twTimeZone);
                lastModifyDate += twCustomDateFormatter.format(lastHandledTime) + " (TW)";
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else
            lastHandledTime = new Date();
        String curator = "";
        if(lastModifiedMap.containsKey("Curator"))
            curator = lastModifiedMap.get("Curator");
        if(curator.equals("none"))
            curator = "";
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("lastModified", lastModifyDate);
        resultMap.put("handler", curator);
        resultMap.put("lastHandled", lastHandledTime);
        resultMap.put("duration", lastModifyDuration);
        return json(resultMap);
    }
    
    public Resolution getLocaleInfo() {
        loadInfoByLocale(selRegion, poolType);    
        return json(infoByLocale);
    }
    
    public Resolution getDisputeJson() {
        BlockLimit blockLimit = new BlockLimit(offset, pageSize);
        blockLimit.addOrderBy("postCreateDate", false);
        processGetDispute(poolType, blockLimit);
        return json(pageResult, DisputePostView.class);
    }
    
    public Resolution initOtherKeyword() {
        List<CircleType> circleTypes = circleTypeDao.listTypesByTypeGroup(8L, null);
        List<PostCurateKeyword> toCreateKeywords = new ArrayList<PostCurateKeyword>();
        for(CircleType cy : circleTypes) {
            PostCurateKeyword newKeyword = new PostCurateKeyword();
            newKeyword.setKeyword(cy.getCircleTypeName());
            newKeyword.setLocale(cy.getLocale());
            newKeyword.setFrequency(0L);
            newKeyword.setDefaultType(cy.getDefaultType());
            toCreateKeywords.add(newKeyword);
        }
        postCurateKeywordDao.batchCreate(toCreateKeywords);
        return json("Completed");
    }
    
    public int processGetDispute(PoolType poolType, BlockLimit blockLimit) {
        long startTime = System.currentTimeMillis();
        if (!getCurrentUserAdmin() && !getAccessControl().getReportManagerAccess()) {
            return 403;
        }
        
        List<CircleTypeGroup> bcDefaiultCircles = circleTypeGroupDao.findAll();
        Map<Long, String> defaultTypeNameMap = new HashMap<Long, String>();
        for(CircleTypeGroup dc : bcDefaiultCircles) {
            defaultTypeNameMap.put(dc.getId(), dc.getGroupName());
        }
        
        PageResult<Object[]> posts = new PageResult<Object[]>();
        Boolean withSize = totalSize == null;
        if (PoolType.TrendingTest.equals(poolType))
        	posts = getTrendingPost(selRegion, selCircleTypeId, withSize, blockLimit);
        else if (PoolType.Trending.equals(poolType))
        	posts = postScoreTrendDao.getRevivedDisputePostIds(selRegion, selCircleTypeId, poolType, null, null, withSize, blockLimit);
        else if (PoolType.Pgc.equals(poolType)) {
            CreatorType toQueryType = null;
            switch(selCreatorType) {
            case "Publication":
                toQueryType = CreatorType.Publication;
                break;
            case "Beautyist":
                toQueryType = CreatorType.Beautyist;
                break;
            case "Brand":
                toQueryType = CreatorType.Brand;
                break;
            case "All":
                default:
                    break;
            }
            posts = postScoreDao.getDisputePostIdsOrderByScore(selRegion, selCircleTypeId, poolType, null, null, toQueryType, withSize, blockLimit);
        }
        else if(!poolType.getRevived() || poolType.getMultiCategory())
            posts = postScoreDao.getDisputePostIds(selRegion, selCircleTypeId, poolType, null, null, null, withSize, blockLimit);
        else
            posts = postScoreDao.getRevivedDisputePostIds(selRegion, selCircleTypeId, poolType, null, null, withSize, blockLimit);
        totalSize = withSize ? posts.getTotalSize() : totalSize;
        posts.setTotalSize(totalSize);

        Map<Long, Triple<String, Long, Date>> postScoreMap = new LinkedHashMap<Long, Triple<String, Long, Date>>();
        Map<Long, Long> popularityMap = new HashMap<Long, Long>();
        Map<Long, String> descCirMap = new HashMap<Long, String>();
        Map<Long, String> violatedMap = new HashMap<Long, String>();
        for(Object[] objs : posts.getResults()) {
            postScoreMap.put((Long)objs[0], Triple.of((String)objs[1], ((Integer)objs[2]).longValue(), (Date)objs[3]));
            if(objs.length >= 5) {
                String jInfo = (String)objs[4];
                if(jInfo != null) {
                    JsonNode info = null;
                    try {
                        info = objectMapper.readValue(jInfo, JsonNode.class);
                    } catch (IOException e) {
                    }
                    if(info == null)
                        continue;
                    JsonNode descCirStr = info.get("descCirName");
                    if(descCirStr != null)
                        descCirMap.put((Long)objs[0], descCirStr.asText());    
                    JsonNode violatedStr = info.get("violated");
                    if(violatedStr != null)
                        violatedMap.put((Long)objs[0], violatedStr.toString());    
                }
            }
            if(objs.length >= 6)
                popularityMap.put((Long)objs[0], (Long)objs[5]);
        }
        PageResult<DisputePostWrapper> postWrappers = postIdToPostView(posts.getTotalSize(), new ArrayList<Long>(postScoreMap.keySet()), defaultTypeNameMap, timeFormat);
        //return json(postWrappers);
        Map<String, List<Long>> md5Map = new HashMap<String, List<Long>>();
        for (final DisputePostWrapper pw : postWrappers.getResults()) {
            Boolean isRepeat = false;
            Triple<String, Long, Date> psAttr = postScoreMap.get(pw.getPostId());
            if(pw.getExtLookUrl() != null && pw.getExtLookUrl().length() > 0 || pw.getLookType() != null) {
                List<String> noticeUrls = new ArrayList<String>();
                if("YMK".equals(psAttr.getLeft()))
                    noticeUrls.add("./../common/theme/backend/images/ymk.png");
                else if("YCN".equals(psAttr.getLeft()))
                    noticeUrls.add("./../common/theme/backend/images/ycn.png");
                pw.setNoticeIconUrls(noticeUrls);
            }
            pw.setScore(psAttr.getMiddle());
            pw.setProcessScoreDate(psAttr.getRight());
            Long popularity = null;
            if(popularityMap.containsKey(pw.getPostId()))
                popularity = popularityMap.get(pw.getPostId());
            popularity = popularity == null ? 0L : popularity;
            pw.setPopularity(popularity);
            if(descCirMap.containsKey(pw.getPostId()))
                pw.setDescCirName(descCirMap.get(pw.getPostId()));
            if(violatedMap.containsKey(pw.getPostId())) {
                String voilated = violatedMap.get(pw.getPostId());
                if(voilated != null) {
                    List<String> noticeUrls = new ArrayList<String>();
                    if(voilated.contains("ForbiddenWord"))
                        noticeUrls.add("./../common/theme/backend/images/forbidden.png");
                    if(voilated.contains("Porn"))
                        noticeUrls.add("./../common/theme/backend/images/porn.png");
                    if(voilated.contains("Violence"))
                        noticeUrls.add("./../common/theme/backend/images/violence.png");
                    pw.setNoticeIconUrls(noticeUrls);
                }
            }
            Attachments attach = pw.getAttachments();
            if(attach != null) {
                List<File> files = attach.getFiles();
                if(files != null) {
                    String md5 = null;
                    for(File file : files) {
                        String fileType = file.getFileType();
                        if(fileType == null || !fileType.equals("Photo"))
                            continue;
                        md5 = file.getMd5();
                        if(!md5Map.containsKey(md5)) {
                            List<Long> postIds = new ArrayList<Long>();
                            md5Map.put(md5, postIds);
                        }
                        else
                            isRepeat = true;
                        if(file.getRedirectUrl() != null && file.getRedirectUrl().length() > 0)
                            pw.setExtPostLink(file.getRedirectUrl());
                    }
                    if(md5 != null)
                        md5Map.get(md5).add(pw.getPostId());
                }
            }
     
            if(!isRepeat)
                pageResult.add(pw);
        }
        
        if (!PoolType.Trending.equals(poolType)) {
	        List<Long> repeatedPost = new ArrayList<Long>();
	        for(String tmpMd5 : md5Map.keySet()) {
	            List<Long> ids = md5Map.get(tmpMd5);
	            if(ids.size() <= 1)
	                continue;
	            ids.remove(0);
	            if(ids.size() > 0)
	                repeatedPost.addAll(ids);
	        }
	        
	        if(repeatedPost.size() > 0) {
	            RunnableRemoveRepeatPost rrrp = new RunnableRemoveRepeatPost(repeatedPost);
	            backendPostService.addTask(rrrp);
	        }
        }
        pageResult.setTotalSize(posts.getTotalSize());
        long endTime = System.currentTimeMillis();
        logger.debug("ProcessTime " + (endTime - startTime) + " milliseconds");
        return 200;
    }
    
    public Resolution reTag() {
        if((rescueObjs == null || rescueObjs.length() <= 0))
            return json("Complete");
        
        JsonNode rescueNode = null;
        try {
            rescueNode = objectMapper.readValue(rescueObjs, JsonNode.class);
        } catch (IOException e) {
            return new ErrorResolution(400, "Parsing Json Format Error");
        }
        
        if(!rescueNode.isArray())
            return new ErrorResolution(400, "Json In Not ");
        
        List<Map<String, Object>> rescueTask = new ArrayList<Map<String, Object>>();
        List<Long> rescueTaskList = new ArrayList<Long>();
        
        int rescueIdx = 0;
        while(rescueNode.has(rescueIdx)) {
            JsonNode rNode = rescueNode.get(rescueIdx++);
            JsonNode postIdStr = rNode.get("postId");
            if(postIdStr == null)
                continue;
            Long postId = Long.valueOf(postIdStr.asText());
            JsonNode rType = rNode.get("type");
            if(rType == null)
                continue;
            
            ResultType rescueType = getResultType(rType.asText());
            if(rescueType == null)
                continue;
            Map<String, Object> nMap = new HashMap<String, Object>();
            JsonNode descCirIdStr = rNode.get("descCirId");
            if(descCirIdStr != null)
                nMap.put("descCirId", Long.valueOf(descCirIdStr.asText()));
            JsonNode keywordsStr = rNode.get("keywords");
            if(keywordsStr != null) {
                List<Map<String, Object>> ks = new ArrayList<Map<String, Object>>();
                for(JsonNode k : keywordsStr) {
                    Map<String, Object> tMap = new HashMap<String, Object>();
                    JsonNode kid = k.get("id");
                    if(kid == null)
                        continue;
                    tMap.put("kid", Long.valueOf(kid.asText()));
                    JsonNode kWord = k.get("word");
                    if(kWord == null)
                        continue;
                    tMap.put("kword", kWord.asText());
                    ks.add(tMap);
                }
                nMap.put("keywords", ks);
            }
            JsonNode qualityStr = rNode.get("quality");
            if(qualityStr != null)
                nMap.put("quality", qualityStr.asInt());
            nMap.put("postId", postId);
            nMap.put("type", rescueType);
            rescueTask.add(nMap);
            rescueTaskList.add(postId);            
        }
              
        if(rescueTaskList.size() > 0) {
            Long rescueTaskId = (new Date()).getTime();
            String taskLogPath = createPostScoreLog(rescueTaskId, rescueTask, poolType);
            if(taskLogPath == null)
                return new ErrorResolution(400, "Could not create task log");
            RunnableRescueLog rrrp3 = new RunnableRescueLog(1L, selRegion, taskLogPath, rescueTask);
            backendPostService.addTask(rrrp3);
        }
        return json("Completed");
    }
    
    public Resolution rescue() {
        long startTime = System.currentTimeMillis();
        if (!getCurrentUserAdmin() && !getAccessControl().getReportManagerAccess()) {
            return new ErrorResolution(403, "Need to login");
        }
        
        if((rescueObjs == null || rescueObjs.length() <= 0))
            return json("Complete");
        
        JsonNode rescueNode = null;
        try {
            rescueNode = objectMapper.readValue(rescueObjs, JsonNode.class);
        } catch (IOException e) {
            return new ErrorResolution(400, "Parsing Json Format Error");
        }
        
        if(!rescueNode.isArray())
            return new ErrorResolution(400, "Json In Not ");
        
        List<Map<String, Object>> rescueTask = new ArrayList<Map<String, Object>>();
        List<Long> rescueTaskList = new ArrayList<Long>();
        
        int rescueIdx = 0;
        while(rescueNode.has(rescueIdx)) {
            JsonNode rNode = rescueNode.get(rescueIdx++);
            JsonNode postIdStr = rNode.get("postId");
            if(postIdStr == null)
                continue;
            Long postId = Long.valueOf(postIdStr.asText());
            JsonNode rType = rNode.get("type");
            if(rType == null)
                continue;
            
            ResultType rescueType = getResultType(rType.asText());
            if(rescueType == null)
                continue;
            Map<String, Object> nMap = new HashMap<String, Object>();
            Long bwcTypeId = null;
            List<Long> typeIds = new ArrayList<Long>();
            JsonNode descCirIdStr = rNode.get("descCirId");
            if(descCirIdStr != null) {
                bwcTypeId = Long.valueOf(descCirIdStr.asText());
                nMap.put("descCirId", bwcTypeId);
                typeIds.add(bwcTypeId);
            }
            JsonNode descCirIdsStr = rNode.get("descCirIds");
            if(descCirIdsStr != null) {
                for(JsonNode ci : descCirIdsStr)
                    typeIds.add(Long.valueOf(ci.asText()));
            }
            nMap.put("descCirIds", typeIds);
            JsonNode keywordsStr = rNode.get("keywords");
            if(keywordsStr != null) {
                List<Map<String, Object>> ks = new ArrayList<Map<String, Object>>();
                for(JsonNode k : keywordsStr) {
                    Map<String, Object> tMap = new HashMap<String, Object>();
                    JsonNode kid = k.get("id");
                    if(kid == null)
                        continue;
                    tMap.put("kid", Long.valueOf(kid.asText()));
                    JsonNode kWord = k.get("word");
                    if(kWord == null)
                        continue;
                    tMap.put("kword", kWord.asText());
                    ks.add(tMap);
                }
                nMap.put("keywords", ks);
            }
            JsonNode qualityStr = rNode.get("quality");
            if(qualityStr != null)
                nMap.put("quality", qualityStr.asInt());
            
            JsonNode hiInAlStr = rNode.get("hiInAl");
            if(hiInAlStr != null)
                nMap.put("hiInAl", hiInAlStr.asBoolean());
            
            JsonNode skipStr = rNode.get("skip");
            if(skipStr != null)
                nMap.put("skip", skipStr.asBoolean());
            
            nMap.put("postId", postId);
            nMap.put("type", rescueType);
            rescueTask.add(nMap);
            rescueTaskList.add(postId);            
        }
      
        Long reviewerId = getCurrentUserId();        
        if(rescueTaskList.size() > 0) {
        	if (PoolType.Trending.equals(poolType)) {
        		if (selCircleTypeId == null)
        			selCircleTypeId = 0L;
        		postScoreTrendDao.markToHandle(reviewerId, null, rescueTaskList);
        	}
        	else
        		postScoreDao.markToHandle(reviewerId, null, rescueTaskList);
            Long rescueTaskId = (new Date()).getTime();
            String taskLogPath = createPostScoreLog(rescueTaskId, rescueTask, poolType);
            if(taskLogPath == null)
                return new ErrorResolution(400, "Could not create task log");
            RunnableRescueLog rrrp3 = new RunnableRescueLog(reviewerId, selRegion, taskLogPath, rescueTask, poolType, selCircleTypeId);
            backendPostService.addTask(rrrp3);
        }
        long endTime = System.currentTimeMillis();
        logger.debug("ProcessTime " + (endTime - startTime) + " milliseconds");
        return json("Completed");
    }
    
    public Resolution addKeyword() {
        if(selRegion == null || keyword == null)
            return new ErrorResolution(400, "Bad request");
        
        PostCurateKeyword newKeyword = postCurateKeywordDao.findByKeyword(selRegion, keyword);
        if(newKeyword == null) {
            PostCurateKeyword tmpKeyword = new PostCurateKeyword();
            tmpKeyword.setKeyword(keyword);
            tmpKeyword.setLocale(selRegion);
            tmpKeyword.setFrequency(0L);
            newKeyword = postCurateKeywordDao.create(tmpKeyword);
        }
        
        if(newKeyword == null)
            return new ErrorResolution(400, "Unknown error");
        
        Map<String, String> kMap = new HashMap<String, String>();
        kMap.put("id", newKeyword.getId().toString());
        kMap.put("keyword", newKeyword.getKeyword());
        return json(kMap);
    }
    
    public Resolution loadKeyword() {
        return json(getKeywords(selRegion));
    }
    
    private String createPostScoreLog(Long taskId, Object taskList, PoolType poolType) {
        String logDir = Constants.getPostScoreLogPath();
        if(logDir == null)
            return null;
        java.io.File dLogDir = new java.io.File(logDir);

        if (!dLogDir.exists())
        {
            if(!dLogDir.mkdir()) {
                logger.error("Failed to create directory :" + dLogDir);
                return null;
            }
        }
        
        String filePath = "";
        if (PoolType.Trending.equals(poolType))
        	filePath = logDir + "/" + String.valueOf(taskId) + "_" + poolType + ".json";
        else
        	filePath = logDir + "/" + String.valueOf(taskId) + ".json";
        java.io.File logFile = new java.io.File(filePath);        
        BufferedWriter output = null;
        try {
            if (!logFile.exists())
                logFile.createNewFile();
            output = new BufferedWriter(new FileWriter(logFile));
            output.write(objectMapper.writeValueAsString(taskList));
            return logFile.getAbsolutePath();
        } catch (Exception e) {
            logFile.delete();
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
    
    private ResultType getResultType(String resultType) {
        if(resultType == null)
            return null;
        switch(resultType) {
        case "CAT_TREND":
            return ResultType.CatAndTrend;
        case "SEL_CIR":
            return ResultType.SelfieOnly;
        case "CAT":
            return ResultType.CatOnly;
        case "ABANDON":
            return ResultType.Abandon;
        case "CHANGE_KEYWORD":
            return ResultType.ChangeKeyWord;
        case "REMOVE":
            return ResultType.Remove;
        case "UNDECIDED":
            return ResultType.Reviewed;
            default:
                return null;
        }
    }
    
    private Map<ResultType, Long> getSummary() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+00"));
        cal.add(Calendar.DATE, -2);
        Date beginDate = cal.getTime();

		ImmutableList<ResultType> rescueTypeOrder = ImmutableList.of(ResultType.Revive, ResultType.Selfie, ResultType.Abandon);
		Map<ResultType, Long> summary = postScoreDao.getHandledPostScoreCountBetween(selRegion, rescueTypeOrder, beginDate, null);
		Map<ResultType, Long> result = new LinkedHashMap<ResultType, Long>();
		for (ResultType rT : rescueTypeOrder) {
			Long value = (long) 0;
			if (summary.containsKey(rT))
				value = summary.get(rT);
			result.put(rT, value);
		}
		return result;
	}

    private void getAllSummary() {
    	TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
        Calendar cals = Calendar.getInstance(timeZone);
        Date endTime = cals.getTime();
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        dateFormater.setTimeZone(timeZone);
    	List<String> dateList = new ArrayList<String>();
    	for(int i=0; i<30; i++){
			String date = dateFormater.format(cals.getTime());
        	dateList.add(date);
        	cals.add(Calendar.DATE, -1);
    	}
        Date beginTime = cals.getTime();

        List<ResultType> resultTypes = Arrays.asList(ResultType.CatAndTrend, ResultType.CatOnly);
 		List<Object> objects = postScoreDao.getHandledPostScoreCounts(null, null, resultTypes, beginTime, endTime);
 		
 		for(String locale: availableRegion){
 			postScoreCountsMap.put(locale, new LinkedHashMap<String, Map<ResultType, Long>>());
 			for(String date : dateList){
 				postScoreCountsMap.get(locale).put(date, new LinkedHashMap<ResultType, Long>());
 				for(ResultType rt : resultTypes){
 					postScoreCountsMap.get(locale).get(date).put(rt, 0L);
 				}
 			}
 		}
 		
 		for(Object obj : objects){
 			Object[] row = (Object[]) obj;
 			String locale = (String) row[0];
 			Date date = (Date) row[1];
            ResultType rt = (ResultType) row[2];
            Long count = (Long) row[3];
            if(postScoreCountsMap.containsKey(locale)) {
            	if(postScoreCountsMap.get(locale).containsKey(date.toString()))
            		postScoreCountsMap.get(locale).get(date.toString()).put(rt, count);
            }
 		}
    }
    
    private PageResult<Object[]> getTrendingPost(String locale, Long circleTypeId, Boolean withTotalSize, BlockLimit blockLimit) {
    	PageResult<Object[]> posts = new PageResult<Object[]>();
    	List<Long> postIds = new ArrayList<Long>();
    	Long size = 0L;
    	if(!Constants.getPersonalTrendEnable()) {
    	    posts.setTotalSize((int) (long) size);
    	    return posts;
    	}
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
    	if (tpinfo == null)
    		return posts;
    	
    	size = trendingRepository.getTrendingList(tPooltype, locale, circleKey, tpinfo.getIdx(), (long) blockLimit.getOffset(), (long) blockLimit.getSize(), postIds);
    	
    	List<Object[]> postObjs = new ArrayList<Object[]>();
    	for (Long pId : postIds) {
    		Object[] obj = new Object[6];
    		obj[0] = pId;
    		obj[1] = null;
    		obj[2] = 0;
    		obj[3] = null;
    		obj[4] = null;
    		obj[5] = 0L;
    		postObjs.add(obj);
    	}
    	posts.setResults(postObjs);
    	if (size != null)
    		posts.setTotalSize((int) (long) size);
    	return posts;
    }
    
    public Map<String, String> getLastModified(PoolType poolType, SimpleDateFormat customDateFormatter) {
        PostScore lastRecord = postScoreDao.getLastHandledRecord(selRegion, poolType, null);
        Map<String, String> lastRecordMap = new LinkedHashMap<String, String>();
        if(lastRecord != null) {
            Date endDate = lastRecord.getLastModified();
            Date beginDate = DateUtils.addHours(endDate, -1);
			User curator = lastRecord.getCurator();
			Long count = postScoreDao.getPostScoreCountBetween(lastRecord.getReviewerId(), selRegion, beginDate, endDate);
            String curatorName = "null";
            if(curator != null)
                curatorName = curator.getDisplayName();
            lastRecordMap.put("Curator", curatorName);
            lastRecordMap.put("Last submission time", String.valueOf(customDateFormatter.format(lastRecord.getLastModified())));
            lastRecordMap.put("Revived post", String.valueOf(count));
        }
        else {
            lastRecordMap.put("Curator", "none");
            lastRecordMap.put("Last submission time", "none");
            lastRecordMap.put("Revived post", "none");
        }
        return lastRecordMap;
    }
    
    public Resolution activity() {
        if (!getCurrentUserAdmin() && !getAccessControl().getReportAuditorAccess()) {
            return new ErrorResolution(403, "Need to login");
        }
        
        loadAvailableRegion();
        getAllSummary();
        
/* previous activity summary page
        String timeZoneId = "GMT+8";
        //  if(localeTimeZoneMap.containsKey(selRegion))
        //      timeZoneId = localeTimeZoneMap.get(selRegion);
        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        SimpleDateFormat customDateFormatter = new SimpleDateFormat(timeFormat);
        customDateFormatter.setTimeZone(timeZone);

        for(String region : availableRegion) {
            selRegion = region;
            Map<String, String> lastRecordMap = getLastModified(poolType, customDateFormatter);
            Map<ResultType, Long> activitySummaryMap = getSummary();
            List<String> lARs = new ArrayList<String>();
            List<String> lASs = new ArrayList<String>();
            for(String key : lastRecordMap.keySet()) {
                lARs.add(key + " : " + lastRecordMap.get(key));
            }
            for(ResultType keyType : activitySummaryMap.keySet()) {
                lASs.add(keyType.toString() + " : " + activitySummaryMap.get(keyType));
            }
            lastActivityRecord.put(region, lARs);
            activitySummary.put(region, lASs);
        }
*/
        return forward();
    }

    public Long getSelCircleTypeId() {
        return selCircleTypeId;
    }

    public void setSelCircleTypeId(Long selCircleTypeId) {
        this.selCircleTypeId = selCircleTypeId;
    }

    public String getSelCreatorType() {
        return selCreatorType;
    }

    public void setSelCreatorType(String selCreatorType) {
        this.selCreatorType = selCreatorType;
    }

    private class RunnableRescueLog extends TransactionRunnable {

        private Long handlerId;
        private String taskLogPath;
        private List<Map<String, Object>> rescueTask;
        private String locale;
        private PoolType poolType;
        private Long circleTypeId;
        
        public RunnableRescueLog(Long handlerId, String locale, String taskLogPath, List<Map<String, Object>> rescueTask) {
            this.handlerId = handlerId;
            this.taskLogPath = taskLogPath;
            this.rescueTask = rescueTask;
            this.locale = locale;
        }
        
        public RunnableRescueLog(Long handlerId, String locale, String taskLogPath, List<Map<String, Object>> rescueTask, PoolType poolType, Long circleTypeId) {
        	this(handlerId, locale, taskLogPath, rescueTask);
        	this.poolType = poolType;
        	this.circleTypeId = circleTypeId;
        }
        
        private void errorHandle(String subject, Object... errors) {
            String content = "";
            try {
                for(Object error : errors) {
                    content += objectMapper.writeValueAsString(error) + "\n";
                }
            } catch (JsonProcessingException e) {
            }
            mailInappropPostCommentService.directSend("Victor_Chew@PerfectCorp.com", subject + " - " +  Constants.getWebsiteDomain() + ":" + taskLogPath, content);
        }
        
        public void run() {
            List<Throwable> errs = null;
            try {
                if (taskLogPath != null){
                    for(Map<String, Object> rt : rescueTask) {
                        try {
                            errs = postService.handleTrendRescueTask(handlerId, rt, circleTypeId);
                            if(errs.size() > 0)
                                errorHandle("Rescue Error", errs, rescueTask);
                        }
                        catch(Exception e) {
                            errorHandle("Rescue Error", e, rescueTask);
                        }
                    }
                }
            }
            catch(Exception error) {
                if(errs == null)
                    errs = new ArrayList<Throwable>();
                errs.add(error);
            }
            if(errs != null && errs.size() > 0) {
                String subject = "Rescue Error - " + Constants.getWebsiteDomain();
                String content = "";
                for(Throwable e : errs) {
                    content += e.getMessage() + "\n";
                }
                mailInappropPostCommentService.directSend("Victor_Chew@PerfectCorp.com", subject, content);
            }
            else {
                if(taskLogPath != null) {
                    java.io.File logFile = new java.io.File(taskLogPath);
                    if(logFile.exists())
                        logFile.delete();
                }
            }
        }

        @Override
        protected void doInTransaction(TransactionTemplate transactionTemplate) {
            if (PoolType.Trending.equals(poolType)) {
                run();
                return;
            }
            
            List<Throwable> errs = new ArrayList<Throwable>();

            if (taskLogPath != null){
                for(final Map<String, Object> rTask : rescueTask) {
                    try {
                        List<Throwable> execError = transactionTemplate.execute(new TransactionCallback<List<Throwable>>(){

                            @Override
                            public List<Throwable> doInTransaction(
                                    TransactionStatus status) {
                                return postService.handleRescueTask(handlerId, rTask);
                            }
                            
                        });
                        
                        if(execError != null && execError.size() > 0)
                            errs.addAll(execError);
                    }
                    catch(Exception e) {
                        errs.add(e);
                    }
                }
            }
        
            if(errs != null && errs.size() > 0) {
                String subject = "Rescue Error - " + Constants.getWebsiteDomain();
                try {
                    mailInappropPostCommentService.directSend("Victor_Chew@PerfectCorp.com", subject, objectMapper.writeValueAsString(errs));
                } catch (JsonProcessingException e) {
                    logger.error("", errs);
                }
            }
            else {
                if(taskLogPath != null) {
                    java.io.File logFile = new java.io.File(taskLogPath);
                    if(logFile.exists())
                        logFile.delete();
                }
            }
            
        }
    }
    
    private class RunnableRemoveRepeatPost extends TransactionRunnable {

        private List<Long> removeIdList;
        
        public RunnableRemoveRepeatPost(List<Long> removeIdList) {
            this.removeIdList = removeIdList;
        }
        
        @Override
        protected void doInTransaction(TransactionTemplate transactionTemplate) {
            transactionTemplate.execute(new TransactionCallback<Boolean>() {

                @Override
                public Boolean doInTransaction(TransactionStatus status) {
                    run();
                    return true;
                }
                
            });
            
        }

        @Override
        public void run() {
            postScoreDao.removeRepeatId(removeIdList);
        }

    }

}
