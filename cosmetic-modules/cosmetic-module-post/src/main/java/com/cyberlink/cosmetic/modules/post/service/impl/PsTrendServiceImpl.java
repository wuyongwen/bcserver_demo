package com.cyberlink.cosmetic.modules.post.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.core.repository.EsRepository.EsResult;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendDao;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendGroupDao;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendPoolDao;
import com.cyberlink.cosmetic.modules.post.dao.PsTrendUserDao;
import com.cyberlink.cosmetic.modules.post.model.PsTrend;
import com.cyberlink.cosmetic.modules.post.model.PsTrend.PsTrendKey;
import com.cyberlink.cosmetic.modules.post.model.PsTrendGroup;
import com.cyberlink.cosmetic.modules.post.model.PsTrendHeat;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool;
import com.cyberlink.cosmetic.modules.post.model.PsTrendUser;
import com.cyberlink.cosmetic.modules.post.model.PsTrendPool.PsTrendPoolKey;
import com.cyberlink.cosmetic.modules.post.repository.PsTrendHeatRepository;
import com.cyberlink.cosmetic.modules.post.repository.PsTrendHeatRepository.LoopCallback;
import com.cyberlink.cosmetic.modules.post.service.PsTrendService;

public class PsTrendServiceImpl extends AbstractService implements 
    PsTrendService {

    private PsTrendGroupDao psTrendGroupDao;
    private PsTrendDao psTrendDao;
    private PsTrendHeatRepository psTrendHeatRepository;
    private PsTrendPoolDao psTrendPoolDao;
    private PsTrendUserDao psTrendUserDao; 
    private CircleTypeDao circleTypeDao;    
    private Map<String, Map<Long, Set<Long>>> psTrendGroupMap = null;
    
    @Override
    public Set<Long> getRelatedPsTrendGroup(String locale, Long circleTypeId) {
        if(!getPsTrendGroupMap().containsKey(locale))
            return null;
        
        Set<Long> groupIds = new HashSet<Long>();
        Map<Long, Set<Long>> typeMap = getPsTrendGroupMap().get(locale);
        for(Long gId : typeMap.keySet()) {
            if(typeMap.get(gId).contains(circleTypeId))
                groupIds.add(gId);
        }
        return groupIds;
    }
    
    @Override
    public Map<String, Map<Long, Set<Long>>> getPsTrendGroupMap() {
        if(psTrendGroupMap == null) {
            psTrendGroupMap = new HashMap<String, Map<Long, Set<Long>>>();
            List<PsTrendGroup> psTGs = psTrendGroupDao.findAll();
            for(PsTrendGroup pstg : psTGs) {
                if(!psTrendGroupMap.containsKey(pstg.getId().getLocale()))
                    psTrendGroupMap.put(pstg.getId().getLocale(), new HashMap<Long, Set<Long>>());
                
                Map<Long, Set<Long>> ctMap = psTrendGroupMap.get(pstg.getId().getLocale());
                if(!ctMap.containsKey(pstg.getId().getgId()))
                    ctMap.put(pstg.getId().getgId(), new HashSet<Long>());
                
                String [] ctids = pstg.getTypes().split(",");
                for(String ctid : ctids) {
                    if(!NumberUtils.isNumber(ctid))
                        continue;
                    ctMap.get(pstg.getId().getgId()).add(Long.valueOf(ctid));
                }
                
            }
        }
        return psTrendGroupMap;
    }

    @Override
    public List<PsTrendGroup> findGroupsByStep(Integer step) {
        return psTrendGroupDao.findByStep(step);
    }
    
    @Override
    public Boolean addGeneralPost(String locale, Long promoteScore, Date displayDate, Long postId) {
        Map<Long, Pair<Long, Date>> postMap = new HashMap<Long, Pair<Long, Date>>();
        postMap.put(postId, Pair.of(promoteScore, displayDate));
        Map<Long, Pair<Long, Date>> failedList = addGeneralPosts(locale, postMap);
        return failedList.size() <= 0 ? true : false;
    }
    
    @Override
    public Map<Long, Pair<Long, Date>> addGeneralPosts(String locale, Map<Long, Pair<Long, Date>> postMap) {
        Map<Long, Pair<Long, Date>> failedList = new HashMap<Long, Pair<Long, Date>>();
        Map<Long, Set<Long>> allGroup = getPsTrendGroupMap().get(locale);
        if(allGroup == null)
            return failedList;
        
        Set<Long> addedGroupId = new HashSet<Long>();
        for(Long postId : postMap.keySet()) {
            Pair<Long, Date> postInfo = postMap.get(postId);
            Map<PsTrendKey, Long> toAddPsTrend = new HashMap<PsTrendKey, Long>();
            Date newDisplayDate = new Date();
            for(Long groupId : allGroup.keySet()) {
                PsTrendKey key = new PsTrendKey();
                key.setGroups(groupId);
                key.setPid(postId);
                key.setLocale(locale);
                toAddPsTrend.put(key, postInfo.getLeft());
                addedGroupId.add(groupId);
            }
            try {
                batchAddTrendPost(newDisplayDate, toAddPsTrend, true, null);
            }
            catch(Exception e) {
                logger.error("", e);
                failedList.put(postId, postInfo);
            }
        }
        return failedList;
    }
    
    @Override
    public Boolean addTrendPost(Long postId, String locale, List<Long> circleTypeIds, Long promoteScore, Boolean hideInAll, Date displayDate) {
        if(!hideInAll) {
            addGeneralPost(locale, promoteScore, displayDate, postId);
        }
        
        createTrendHeat(postId, locale, circleTypeIds, displayDate);
        return true;
    }
    
    @Override
    public String listPsTrend(String uuid, String groupIdVal, String locale, List<Long> resultList, BlockLimit blockLimit) {
        Long groupId = null;
        if(groupIdVal == null) {
            if(blockLimit.getOffset() == 0)
                groupId = psTrendUserDao.findGroupByUuid(uuid);
            else
                return null;
        }
        else if(NumberUtils.isNumber(groupIdVal))
            groupId = Long.valueOf(groupIdVal);
        if(groupId == null)
            return null;
        
        psTrendDao.listPostByGroup(locale, groupId, resultList, blockLimit);
        return groupId.toString();
    }
    
    @Override
    public void doWithBestPsTrend(String locale, Date startTime, Date endTime, final ScanResultCallback<Map<String, Map<String, Date>>> callback) {
        if(callback == null)
            return;
        
        List<CircleType> circleTypes = circleTypeDao.listTypesByLocale(locale, true);
        List<String> circleTypeIds = new ArrayList<String>();
        for(CircleType ct : circleTypes) {
            circleTypeIds.add(ct.getId().toString());
        }
        psTrendHeatRepository.findTopTrend(circleTypeIds, 0.5, startTime, endTime, new LoopCallback<Map<String, Map<String, Date>>>() {
    
            @Override
            public void doWith(Map<String, Map<String, Date>> results) {
                callback.doWith(results);                
            }
            
        });
    }
    
    @Override
    public Boolean batchCreateTrendPool(Date newDisplayDate, List<PsTrendPool> list, Map<String, Object> info) {
        Map<PsTrendPoolKey, PsTrendPool> map = new HashMap<PsTrendPoolKey, PsTrendPool>();
        for(PsTrendPool tp : list) {
            map.put(tp.getId(), tp);
        }
        List<PsTrendPool> exTrendPools = psTrendPoolDao.findByIds(new ArrayList<PsTrendPoolKey>(map.keySet()));
        for(PsTrendPool p: exTrendPools) {
            p.setDisplayTime(newDisplayDate);
            psTrendPoolDao.update(p);
            map.remove(p.getId());
        }
        
        psTrendPoolDao.batchInsert(new ArrayList<PsTrendPool>(map.values()));
        if(info != null) {
            info.put("Total", list.size());
            info.put("Updated", exTrendPools.size());
            info.put("Created", map.size());
        }
        return true;
    }
    
    @Override
    public void purgeExpiredTrendPool(Date displayDate) {
        psTrendPoolDao.batchDelete(PsTrendPool.getBucketId(displayDate));
    }
    
    @Override
    public Boolean releaseFromTrendPool(List<PsTrendGroup> groups, ScanResultCallback<Map<PsTrendKey, Long>> psTrendCallback, ScanResultCallback<List<PsTrendGroup>> psTGroupCallback) {
        if(groups == null || psTrendCallback == null || psTGroupCallback == null)
            return false;
        
        Map<PsTrendKey, Long> toAddPsTrend = new HashMap<PsTrendKey, Long>();
        Map<Long, List<PsTrendPool>> cachePsTrendPool = new HashMap<Long, List<PsTrendPool>>();
        BlockLimit blockLimit = new BlockLimit(0, 1);
        Integer bucket = PsTrendPool.getBucketId(new Date());
        List<PsTrendGroup> batchUpdateTGroup = new ArrayList<PsTrendGroup>();
        for(PsTrendGroup group : groups) {
            String circleTypesVal = group.getTypes();
            if(circleTypesVal == null || circleTypesVal.length() <= 0)
                continue;
            
            String pointers = group.getPointers();
            if(pointers == null || pointers.length() <= 0)
                continue;
            
            String [] cirTypeTok = circleTypesVal.split(",");
            String [] pointerTok = pointers.split(",");
            if(cirTypeTok.length != pointerTok.length)
                continue;
            
            Date [] newPointer = new Date[cirTypeTok.length];
            for(int idx = 0; idx < cirTypeTok.length; idx++) {
                Date from = new Date(Long.valueOf(pointerTok[idx]));
                newPointer[idx] = PsTrendGroup.DEFAULT_POINTER;
                Long circleTypeId = Long.valueOf(cirTypeTok[idx]);
                List<PsTrendPool> psPools = null;
                if(!cachePsTrendPool.containsKey(circleTypeId)) {
                    PageResult<PsTrendPool> pools = psTrendPoolDao.findByCircleType(bucket, circleTypeId, from, blockLimit);
                    psPools = pools.getResults();
                    cachePsTrendPool.put(circleTypeId, psPools);
                }
                else
                    psPools = cachePsTrendPool.get(circleTypeId);
                if(psPools == null)
                    continue;
                for(PsTrendPool p : psPools) {
                    PsTrendKey key = new PsTrendKey();
                    key.setPid(p.getId().getpId());
                    key.setLocale(group.getId().getLocale());
                    key.setGroups(group.getId().getgId());
                    toAddPsTrend.put(key, null);
                    if(toAddPsTrend.size() >= 100) {
                        psTrendCallback.doWith(toAddPsTrend);
                        toAddPsTrend.clear();
                    }
                    newPointer[idx] = p.getDisplayTime();
                }
            }
            String newPointerVal = "";
            for(Date np : newPointer) {
                newPointerVal += String.valueOf(np.getTime()) + ",";
            }
            if(newPointerVal.length() > 0) {
                newPointerVal = newPointerVal.substring(0, newPointerVal.length() - 1);
                group.setPointers(newPointerVal);
                batchUpdateTGroup.add(group);
                if(batchUpdateTGroup.size() >= 100) {
                    psTGroupCallback.doWith(batchUpdateTGroup);
                    batchUpdateTGroup.clear();
                }
            }
        }
        
        if(toAddPsTrend.size() > 0) {
            psTrendCallback.doWith(toAddPsTrend);
            toAddPsTrend.clear();
        }
        if(batchUpdateTGroup.size() > 0) {
            psTGroupCallback.doWith(batchUpdateTGroup);
            batchUpdateTGroup.clear();
        }
        return true;
    }
    
    @Override
    public Boolean batchAddTrendPost(Date newDisplayDate, Map<PsTrendKey, Long> toAddPsTrend, Boolean updatePromoteScore, Map<String, Object> info) {
        Integer total = toAddPsTrend.size();
        List<PsTrend> exPsTrends = psTrendDao.findByIds(new ArrayList<PsTrendKey>(toAddPsTrend.keySet()));
        for(PsTrend pt : exPsTrends) {
            if(updatePromoteScore && toAddPsTrend.containsKey(pt.getId()))
                pt.setPromoteScore(toAddPsTrend.get(pt.getId()));
            else
                pt.setDisplayTime(newDisplayDate);
            psTrendDao.update(pt);
            toAddPsTrend.remove(pt.getId());
        }
        
        List<PsTrend> toCreateList = new ArrayList<PsTrend>();
        for(PsTrendKey key : toAddPsTrend.keySet()) {
            PsTrend pt = new PsTrend();
            pt.setId(key);
            pt.setDisplayTime(newDisplayDate);
            pt.setPromoteScore(toAddPsTrend.get(pt.getId()));
            toCreateList.add(pt);
        }
        psTrendDao.batchInsert(toCreateList);
        if(info != null) {
            info.put("Total", total);
            info.put("Updated", exPsTrends.size());
            info.put("Created", toCreateList.size());
        }
        return true;
    }
    
    @Override
    public Boolean updateTrendGroups(List<PsTrendGroup> trendGroups) {
        for(PsTrendGroup g : trendGroups)
            psTrendGroupDao.update(g);
        return true;
    }
    
    @Override
    public Boolean createOrUpdateUserGroup(Map<String, String> userGroupMap) {
        Map<String, Long> availableGIds = psTrendGroupDao.getAvailableId();
        List<PsTrendUser> exTrendUser = psTrendUserDao.findGroupByUuids(new ArrayList<String>(userGroupMap.keySet()));
        for(PsTrendUser pstu : exTrendUser) {
            if(!availableGIds.containsKey(userGroupMap.get(pstu.getUuid())))
                continue;
            pstu.setGroups(availableGIds.get(userGroupMap.get(pstu.getUuid())));
            psTrendUserDao.update(pstu);
            userGroupMap.remove(pstu.getUuid());
        }
        if(userGroupMap.size() > 0) {
            for(String uuid : userGroupMap.keySet()) {
                if(!availableGIds.containsKey(userGroupMap.get(uuid)))
                    continue;
                PsTrendUser newPstu = new PsTrendUser();
                newPstu.setUuid(uuid);
                newPstu.setGroups(availableGIds.get(userGroupMap.get(uuid)));
                psTrendUserDao.create(newPstu);
            }
        }
        return true;
    }
    
    private Boolean createTrendHeat(Long postId, String locale, List<Long> circleTypeIds, Date displayDate) {
        List<String> cts = new ArrayList<String>();
        for(Long ctId : circleTypeIds) {
            cts.add(ctId.toString());
        }
        
        PsTrendHeat newTrend = new PsTrendHeat();
        newTrend.setId(postId.toString());
        newTrend.setLoc(locale);
        newTrend.setCirTypes(cts);
        newTrend.setCirIns(0);
        newTrend.setLikes(0);
        newTrend.setDate(displayDate);
        EsResult<Boolean> result = psTrendHeatRepository.create(newTrend);
        return result.error == null ? true : false;
    }
    
    public void setPsTrendGroupDao(PsTrendGroupDao psTrendGroupDao) {
        this.psTrendGroupDao = psTrendGroupDao;
    }
    
    public void setPsTrendDao(PsTrendDao psTrendDao) {
        this.psTrendDao = psTrendDao;
    }
    
    public void setPsTrendHeatRepository(PsTrendHeatRepository psTrendHeatRepository) {
        this.psTrendHeatRepository = psTrendHeatRepository;
    }

    public void setPsTrendPoolDao(PsTrendPoolDao psTrendPoolDao) {
        this.psTrendPoolDao = psTrendPoolDao;
    }

    public void setPsTrendUserDao(PsTrendUserDao psTrendUserDaoHibernate) {
        this.psTrendUserDao = psTrendUserDaoHibernate;
    }
    
    public void setCircleTypeDao(CircleTypeDao circleTypeDao) {
        this.circleTypeDao = circleTypeDao;
    }

}