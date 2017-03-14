package com.cyberlink.cosmetic.modules.post.repository.redis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao;
import com.cyberlink.cosmetic.modules.common.dao.LocaleDao.LocaleType;
import com.cyberlink.cosmetic.modules.post.model.PostTargetType;
import com.cyberlink.cosmetic.modules.post.repository.LikeRepository;
import com.cyberlink.cosmetic.redis.AbstractRedisRepository;
import com.cyberlink.cosmetic.redis.CursorCallback;
import com.cyberlink.cosmetic.redis.KeyUtils;

public class LikeRepositoryRedis extends AbstractRedisRepository implements
    LikeRepository {

    private static final long TS_OF_2014 = 1388534400000l;
    private SimpleDateFormat promoLikeSlotFormatter = new SimpleDateFormat("yyyyMMddHH");
    
    @Override
    public List<Long> getLikes(Long userId, String targetType,
            List<Long> targetIds) {
        if(!Constants.getRedisLikeEnable())
            return null;
        
        if(!PostTargetType.POST.equals(targetType))
            return null;
        
        List<Long> result = new ArrayList<Long>();
        String userKey = KeyUtils.userLiked(targetType, userId);
        for(Long tId : targetIds) {
            Long r = opsForZSet().rank(userKey, tId.toString());
            if(r != null)
                result.add(tId);
        }
        return result;
    }

    @Override
    public PageResult<Long> getLikers(String targetType, Long targetId,
            BlockLimit blockLimit) {
        if(!Constants.getRedisLikeEnable())
            return null;
        
        if(!PostTargetType.POST.equals(targetType))
            return null;
        
        List<Long> result = new ArrayList<Long>();
        String postKey = KeyUtils.targetLikers(targetType, targetId);
        Set<String> uIds = opsForZSet().range(postKey, blockLimit.getOffset(), blockLimit.getOffset() + blockLimit.getSize());
        for(String uid : uIds) {
            result.add(Long.valueOf(uid));
        }
        Long totalSize = opsForZSet().zCard(postKey);
        PageResult<Long> pgResult = new PageResult<Long>();
        pgResult.setResults(result);
        pgResult.setTotalSize(totalSize.intValue());
        return pgResult;
    }

    @Override
    public PageResult<Long> getLikedTarget(Long userId,
            String targetType, BlockLimit blockLimit) {
        if(!Constants.getRedisLikeEnable())
            return null;
        
        if(!PostTargetType.POST.equals(targetType))
            return null;
        
        List<Long> result = new ArrayList<Long>();
        String userKey = KeyUtils.userLiked(targetType, userId);
        Set<String> tIds = opsForZSet().reverseRange(userKey, blockLimit.getOffset(), blockLimit.getOffset() + blockLimit.getSize());
        for(String tid : tIds) {
            result.add(Long.valueOf(tid));
        }
        Long totalSize = opsForZSet().zCard(userKey);
        PageResult<Long> pgResult = new PageResult<Long>();
        pgResult.setResults(result);
        pgResult.setTotalSize(totalSize.intValue());
        return pgResult;
    }

    @Override
    public void like(Long userId, String targetType, Long targetId, Long createdTime) {
        if(!PostTargetType.POST.equals(targetType))
            return;
        
        final long score = createdTime - TS_OF_2014;
        String postKey = KeyUtils.targetLikers(targetType, targetId);
        String userKey = KeyUtils.userLiked(targetType, userId);
        opsForZSet().add(postKey, userId.toString(), score);
        opsForZSet().add(userKey, targetId.toString(), score);
    }

    @Override
    public void unlike(Long userId, String targetType, Long targetId) {
        if(!PostTargetType.POST.equals(targetType))
            return;
        
        String postKey = KeyUtils.targetLikers(targetType, targetId);
        String userKey = KeyUtils.userLiked(targetType, userId);
        opsForZSet().remove(postKey, userId.toString());
        opsForZSet().remove(userKey, targetId.toString());
    }

    @Override
    public void deleteByTargetId(String targetType, Long targetId) {
        if(!PostTargetType.POST.equals(targetType))
            return;
        
        final String fTargetType = targetType;
        final Long fTargetId = targetId;
        
        zScan(KeyUtils.targetLikers(targetType, targetId).getBytes(), ScanOptions.NONE, new CursorCallback<TypedTuple<String>>() {
            @Override
            public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                while (cursor.hasNext()) {
                    final String likerId = cursor.next().getValue();
                    String userKey = KeyUtils.userLiked(fTargetType, Long.valueOf(likerId));
                    opsForZSet().remove(userKey, fTargetId.toString());
                }
            }
        });
        delete(KeyUtils.targetLikers(targetType, targetId));
    }
    
    @Override
    public void deleteByUserId(Long userId) {
        final String targetType = "Post";
        final Long fUserId = userId;
        zScan(KeyUtils.userLiked(targetType, userId).getBytes(), ScanOptions.NONE, new CursorCallback<TypedTuple<String>>() {
            @Override
            public void doWithCursor(Cursor<TypedTuple<String>> cursor) {
                while (cursor.hasNext()) {
                    final String postId = cursor.next().getValue();
                    String postKey = KeyUtils.targetLikers(targetType, Long.valueOf(postId));
                    opsForZSet().remove(postKey, fUserId.toString());
                }
            }
        });
        delete(KeyUtils.userLiked(targetType, userId));
    }
    
    @Override
    public void setServiceHost(String hostName) {
        String key = KeyUtils.promotionalLikeServiceHost();
        opsForValue().set(key, hostName);
    }
    
    @Override
    public String getServiceHost() {
        String key = KeyUtils.promotionalLikeServiceHost();
        return opsForValue().get(key);
    }
    
    @Override
    public void setPromotionalLikeTarget(String locale, Long postId,
            Date createdTime) {
        String key = KeyUtils.promotionalLikeTarget(locale, promoLikeSlotFormatter.format(createdTime));
        opsForSet().add(key, postId.toString());
        expire(key, 24L, TimeUnit.HOURS);
    }

    @Override
    public Map<Integer, String> getAvailablePromitionalLikeKey(String locale, Integer slotCount) {
        if(!Constants.getPromotionalLikeEnable())
            return null;

        Calendar cal = Calendar.getInstance();
        Map<Integer, String> result = new LinkedHashMap<Integer, String>();
        for(Integer i = 0; i < slotCount; i++) {
            cal.add(Calendar.HOUR, -1);
            Date curDate = cal.getTime();
            String key = KeyUtils.promotionalLikeTarget(locale, promoLikeSlotFormatter.format(curDate));
            result.put(i, key);
        }
        return result;
    }
    
    @Override
    public void doWithPromotionalLikeTargets(String key, CursorCallback<String> callback) {
        if(!Constants.getPromotionalLikeEnable())
            return;
        
        sScan(key.getBytes(), ScanOptions.NONE, callback);
    }
    
    @Override
    public void setPromotionalLikeCount(String postId, Integer incr) {
        String key = KeyUtils.promotionalLikeCount("Post", postId);
        opsForValue().increment(key, Long.valueOf(incr));
        expire(key, 10L, TimeUnit.DAYS);
    }
    
    @Override
    public Integer getPromotionalLikeCount(String postId) {
        if(!Constants.getPromotionalLikeEnable())
            return null;
        
        String key = KeyUtils.promotionalLikeCount("Post", postId);
        String value = opsForValue().get(key);
        if(value == null)
            return 0;
        return Integer.valueOf(value);
    }

    @Override
    public void setLastDayPostLikeCount(String locale, Integer totalPostCount, Integer totalLikeCount) {
        String key = KeyUtils.promotionalPostLikeCount(locale);
        String p = totalPostCount == null ? "0" : totalPostCount.toString();
        String l = totalLikeCount == null ? "0" : totalLikeCount.toString();
        opsForValue().set(key, p + ":" + l);
    }
    
    @Override
    public Pair<Integer, Integer> getLastDayPostLikeCount(String locale) {
        String key = KeyUtils.promotionalPostLikeCount(locale);
        String value = opsForValue().get(key);
        if(value == null)
            return null;
        String[] toks = value.split(":");
        if(toks.length < 2)
            return null;
        return Pair.of(Integer.valueOf(toks[0]), Integer.valueOf(toks[1]));
        
    }
    @Override
    public void cleanOldPromoteTarget(String locale) {
        SimpleDateFormat ymdFormatter = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        Date begin = DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH);
        cal.setTime(begin);
        for(Integer i = 12; i > 0; i--) {
            Date curDate = cal.getTime();
            String key = KeyUtils.promotionalLikeTarget(locale, ymdFormatter.format(curDate));
            delete(key);
            cal.add(Calendar.DATE, -1);
        }
    }
}
