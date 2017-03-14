package com.cyberlink.cosmetic.modules.circle.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.Cursor;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeGroupDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;
import com.cyberlink.cosmetic.modules.circle.model.CircleTypeGroup;
import com.cyberlink.cosmetic.modules.circle.repository.CircleFollowRepository;
import com.cyberlink.cosmetic.modules.circle.repository.CircleRepository;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;
import com.cyberlink.cosmetic.modules.user.dao.UserDao;
import com.cyberlink.cosmetic.redis.CursorCallback;

public class CircleServiceImpl extends AbstractService implements CircleService {

    private CircleTypeGroupDao circleTypeGroupDao;
    private CircleTypeDao circleTypeDao;
    private CircleDao circleDao;
    private UserDao userDao;
    private CircleRepository circleRepository;
    private CircleFollowRepository circleFollowRepository;

    public void setCircleRepository(CircleRepository circleRepository) {
        this.circleRepository = circleRepository;
    }

    public void setCircleFollowRepository(
            CircleFollowRepository circleFollowRepository) {
        this.circleFollowRepository = circleFollowRepository;
    }

    public void setCircleTypeGroupDao(CircleTypeGroupDao circleTypeGroupDao) {
        this.circleTypeGroupDao = circleTypeGroupDao;
    }
    
    public void setCircleTypeDao(CircleTypeDao circleTypeDao) {
        this.circleTypeDao = circleTypeDao;
    }
    
    public void setCircleDao(CircleDao circleDao) {
        this.circleDao = circleDao;
    }
    
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
        
    @Override
    public void deleteByUserId(final Long userId) {
        circleRepository.doWithCircleIds(userId,
                new CursorCallback<String>() {
                    @Override
                    public void doWithCursor(Cursor<String> cursor) {
                        while (cursor.hasNext()) {
                            circleFollowRepository.deleteByCircleId(Long
                                    .valueOf(cursor.next()));
                        }
                    }
                });
        circleRepository.deleteByUserId(userId);        
    }

    @Override
    public void deleteByCircleId(Long userId, Long circleId) {
        circleFollowRepository.deleteByCircleId(circleId);
        circleRepository.deleteCircle(userId, circleId);
    }
    
    @Override
    public List<Circle> getBcDefaultCircle(String region) {
    	return getBcDefaultCircle(region, true);
    }

    @Override
    public List<Circle> getBcDefaultCircle(String region, Boolean isVisible) {
        int circleTypeOffset = 0;
        int circleTypeLimit = 100;            
        List<String> locales = new ArrayList<String>();
        locales.add(region);
        List<Long> circleTypeIds = new ArrayList<Long>();
        do {
            PageResult<CircleType> circleTypeResults = circleTypeDao.listTypesByLocales(locales, isVisible, new BlockLimit(circleTypeOffset, circleTypeLimit));
            for(CircleType ct : circleTypeResults.getResults()) {
                circleTypeIds.add(ct.getId());
            }
            
            circleTypeOffset += circleTypeLimit;
            if(circleTypeOffset > circleTypeResults.getTotalSize())
                break;
        }while (true);
        
        List<Circle> result = new ArrayList<Circle>();
        if(circleTypeIds.size() <= 0) 
            return result;
        
        Long circleOffset = (long)0;
        Long circleLimit = (long)100;   
        do {
            PageResult<Circle> circleResults = circleDao.findBcDefaultCircleByCircleTypeIds(circleTypeIds, circleOffset, circleLimit);
            result.addAll(circleResults.getResults());            
            circleOffset += circleLimit;
            if(circleOffset > circleResults.getTotalSize())
                break;
        }while (true);
        return  result;
    }
    
    @Override
    public List<Circle> getUserDefaultCircle(Long userId, Boolean withDeleted) {
        List<Circle> result = new ArrayList<Circle>();
        Long circleOffset = (long)0;
        Long circleLimit = (long)100;   
        do {
            PageResult<Circle> circleResults = circleDao.findUserDefaultCircleByUserId(userId, withDeleted, circleOffset, circleLimit);
            result.addAll(circleResults.getResults());            
            circleOffset += circleLimit;
            if(circleOffset > circleResults.getTotalSize())
                break;
        }while (true);
        return  result;
    }

    @Override
    public PageResult<Circle> listUserCreatedCircle(Long userId, Boolean withSecret, BlockLimit blockLimit) {
        List<Long> userIds = new ArrayList<Long>();
        userIds.add(userId);
        if(withSecret)
            blockLimit.addOrderBy("isSecret", true);
        blockLimit.addOrderBy("lastModified", false);
        return circleDao.findByUserIds(userIds, withSecret, blockLimit);
    }
    
    @Override
    public PageResult<Circle> listUserCircle(Long userId, Boolean withSecret, String region, Boolean withDefault, BlockLimit blockLimit) {
        List<Long> userIds = new ArrayList<Long>();
        userIds.add(userId);
        PageResult<Circle> result = new PageResult<Circle>();
        List<Circle> bcDefaultCircle = null;
        if(withDefault)
            bcDefaultCircle = getBcDefaultCircle(region);
        else
            bcDefaultCircle = new ArrayList<Circle>();
        Map<String, Circle> circleTypeCircleMap = new LinkedHashMap<String, Circle>();
        for(Circle c : bcDefaultCircle) {
            c.setCircleCreatorId(userId);
            circleTypeCircleMap.put(c.getDefaultType(), c);
        }
        List<Object> userCircleAttrs = circleDao.getUserCircelAttr(userId);
        Long selfCreatedDefault = (long)0, selfDeletedDefault = (long)0, selfCreated = (long)0, selfCreatedHidden = (long)0, defaultToAdd = (long)0;
        for(Object obj : userCircleAttrs) {
            Object[] row = (Object[]) obj;
            String defaultType = (String)row[0];
            Long circleTypeId = (Long)row[1];
            Boolean isDeleted = (Boolean)row[2];
            Boolean isSecret = (Boolean)row[3];
            Long count = (Long)row[4];
            if(defaultType == null && !isDeleted && (!isSecret || isSecret == null))
                selfCreated += count;
            else if(defaultType == null && !isDeleted && isSecret)
                selfCreatedHidden += count;
            else if(defaultType != null && isDeleted) {
                selfDeletedDefault += 1;
                circleTypeCircleMap.remove(defaultType);
            }
            else if(defaultType != null && !isDeleted) {
                selfCreatedDefault += 1;
                circleTypeCircleMap.remove(defaultType);
            }
        }        

        defaultToAdd = (long) circleTypeCircleMap.size();
        /* New spec only sort by last modified date*/
        /*if(withSecret)
            blockLimit.addOrderBy("isSecret", true);
        if(withDefault)
            blockLimit.addOrderBy("defaultType", true);*/
        blockLimit.addOrderBy("lastModified", false);
        if(blockLimit.getOffset() < selfCreated + selfCreatedDefault) {
            PageResult<Circle> tmpResult = circleDao.findByUserIds(userIds, withSecret, blockLimit);
            List<Circle> toReturnCircle = new ArrayList<Circle>();
            Boolean isBcDefaultAdded = false;
            for(int cIdx = 0; cIdx < tmpResult.getResults().size(); cIdx++) {
                Circle cTmp = tmpResult.getResults().get(cIdx);
                if(!cTmp.getIsSecret()) {
                    toReturnCircle.add(cTmp);
                }
                else if(!isBcDefaultAdded){
                    for(String key : circleTypeCircleMap.keySet()) {
                        if(toReturnCircle.size() >= blockLimit.getSize())
                            break;
                        toReturnCircle.add(circleTypeCircleMap.get(key));
                    }
                    isBcDefaultAdded = true;
                    if(toReturnCircle.size() >= blockLimit.getSize())
                        break;
                    toReturnCircle.add(cTmp);
                }
                else {
                    if(toReturnCircle.size() >= blockLimit.getSize())
                        break;
                    toReturnCircle.add(cTmp);
                }
            }
            if(!isBcDefaultAdded) {
                for(String key : circleTypeCircleMap.keySet()) {
                    if(toReturnCircle.size() >= blockLimit.getSize())
                        break;
                    toReturnCircle.add(circleTypeCircleMap.get(key));
                }
                isBcDefaultAdded = true;
            }
            result.setResults(toReturnCircle);
            result.setTotalSize((int) (tmpResult.getTotalSize() + defaultToAdd));
        }
        else if (blockLimit.getOffset() >= selfCreated + selfCreatedDefault && blockLimit.getOffset() < selfCreated + selfCreatedDefault + defaultToAdd) {
            int defaultOffset = (int) (blockLimit.getOffset() - selfCreated - selfCreatedDefault);
            List<Circle> toReturnCircle = new ArrayList<Circle>();
            String[] circleTypeIds = circleTypeCircleMap.keySet().toArray(new String[circleTypeCircleMap.keySet().size()]);
            for(int dIdx = defaultOffset; dIdx < circleTypeCircleMap.size(); dIdx++) {
                if(toReturnCircle.size() >= blockLimit.getSize())
                    break;
                toReturnCircle.add(circleTypeCircleMap.get(circleTypeIds[dIdx]));
            }
            blockLimit.setOffset(blockLimit.getOffset() - defaultOffset);
            blockLimit.setSize(blockLimit.getSize() - toReturnCircle.size());
            PageResult<Circle> tmpResult = circleDao.findByUserIds(userIds, withSecret, blockLimit);
            for(int cIdx = 0; cIdx < tmpResult.getResults().size(); cIdx++) {
                if(toReturnCircle.size() >= blockLimit.getSize())
                    break;
                toReturnCircle.add(tmpResult.getResults().get(cIdx));
            }
            result.setResults(toReturnCircle);
            result.setTotalSize((int) (tmpResult.getTotalSize() + defaultToAdd));
        }
        else {
            List<Circle> toReturnCircle = new ArrayList<Circle>();
            blockLimit.setOffset((int) (blockLimit.getOffset() - defaultToAdd));
            PageResult<Circle> tmpResult = circleDao.findByUserIds(userIds, withSecret, blockLimit);
            for(int cIdx = 0; cIdx < tmpResult.getResults().size(); cIdx++) {
                if(toReturnCircle.size() >= blockLimit.getSize())
                    break;
                toReturnCircle.add(tmpResult.getResults().get(cIdx));
            }
            result.setResults(toReturnCircle);
            result.setTotalSize((int) (tmpResult.getTotalSize() + defaultToAdd));
        }
        return result;
    }
    
    @Override
    public Circle getUserAccessibleCircle(Circle relatedCircle, Long userId, Boolean createIfNotExist) {
    	Circle userCircle = null;
        if(relatedCircle.getDefaultType() != null && relatedCircle.getCreatorId() == null) {
            userCircle = circleDao.getUserCreateDefaultCircle(userId, relatedCircle.getDefaultType());
            if(userCircle != null) {
                return userCircle;
            }
            else if(createIfNotExist) {
                Circle tmp = new Circle();
                tmp.setCreatorId(userId);
                tmp.setDescription(relatedCircle.getDescription());
                tmp.setIconId(relatedCircle.getIconId());
                tmp.setIsSecret(relatedCircle.getIsSecret());
                tmp.setDefaultType(relatedCircle.getDefaultType());
                tmp.setCircleName(relatedCircle.getCircleName());
                tmp.setCricleTypeId(relatedCircle.getCricleTypeId());
                return circleDao.create(tmp);
                
            }
            else {
                return relatedCircle;
            }
        }
        else if(!relatedCircle.getCreatorId().equals(userId))
            return null;
        else {
            userCircle = relatedCircle;
        }
        return userCircle;
    }
    
    @Override
    public Map<String, Circle> getDefaultCircleByTypeGroupName(String typeGroupName) {
        Map<String, Circle> regionCircleMap = new HashMap<String, Circle>();
        CircleTypeGroup cirTypeGroup = circleTypeGroupDao.findByTypeGroupName(typeGroupName);
        if(cirTypeGroup == null)
            return regionCircleMap;
        List<CircleType> circleTypes = circleTypeDao.listTypesByTypeGroup(cirTypeGroup.getId(), null);
        List<Long> circleTypeIds = new ArrayList<Long>();
        Map<Long, String> circleTypeRegionMap = new HashMap<Long, String>();
        for(CircleType cT : circleTypes) {
            circleTypeIds.add(cT.getId());
            circleTypeRegionMap.put(cT.getId(), cT.getLocale());
        }
        
        Long offset = (long)0;
        Long limit = (long)100;
        do {
            PageResult<Circle> circleResult = circleDao.findBcDefaultCircleByCircleTypeIds(circleTypeIds, offset, limit);
            if(circleResult.getResults().size() <= 0)
                break;
            
            for(Circle c : circleResult.getResults()) {
                regionCircleMap.put(circleTypeRegionMap.get(c.getCricleTypeId()), c);
            }
            offset += limit;
            if(offset > circleResult.getTotalSize())
                break;
        } while(true);
        
        return regionCircleMap;
    }
    
    @Override
    public Long getCircleTypeByDefaultType(String circleTypeGroup, String locale) {
        Long circleTypeGroupId = circleTypeGroupDao.findByDefaultTypeName(circleTypeGroup);
        List<CircleType> circleTypes = circleTypeDao.listTypesByTypeGroup(circleTypeGroupId, locale);
        if(circleTypes == null || circleTypes.size() <= 0)
            return null;
        return circleTypes.get(0).getId();
    }
    
    @Override
    public List<CircleType> getCircleTypes(Long... ids) {
        return circleTypeDao.findByIds(ids);
    }
}
