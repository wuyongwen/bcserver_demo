package com.cyberlink.cosmetic.modules.circle.dao.hibernate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.modules.circle.dao.CircleAttributeDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute;
import com.cyberlink.cosmetic.modules.circle.model.CircleAttribute.CircleAttrType;

public class CircleAttributeDaoHibernate extends AbstractDaoCosmetic<CircleAttribute, Long>
    implements CircleAttributeDao {

    private String regionOfFindDefaultCircleAttr = "com.cyberlink.cosmetic.modules.circle.model.CircleAttribute.query.getDefaultCircleAttribute";
    
    @Override
    public List<Circle> getCircleAttribute(List<Circle> circles, Long curUserId, List<Long> subcribedCircleIds) {
        List<Circle> result = new ArrayList<Circle>();
        if(circles == null || circles.size() <= 0)
            return result;
        Map<Long, Circle> circleIdMap = new LinkedHashMap<Long, Circle>();
        for(Circle cir : circles) {
            circleIdMap.put(cir.getId(), cir);
        }
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("circle", circles));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        BlockLimit blockLimit = new BlockLimit(0, circles.size() * CircleAttribute.maxCircleAttrSize);
        blockLimit.addOrderBy("lastModified", false);
        blockLimit.addOrderBy("id", false);
        PageResult<CircleAttribute> circleAttributes =  blockQuery(dc, blockLimit);
        for(CircleAttribute cirAttr : circleAttributes.getResults()) {
            Circle tmp = circleIdMap.get(cirAttr.getCircle().getId());
            switch(cirAttr.getAttrType()) {
            case PostCount:
                tmp.setPostCount(Long.valueOf(cirAttr.getAttrValue()));
                break;
            case FollowerCount:
                tmp.setFollowerCount(Long.valueOf(cirAttr.getAttrValue()));
                break;
            case Thumbnail:
                tmp.setPostThumbnails(cirAttr.getAttrValue());
                break;
            default:
                break;
            }
        }
        
        for(Long cirId : circleIdMap.keySet()) {
            Circle cir = circleIdMap.get(cirId);
            Long userId = cir.getCreatorId();
            if(userId == null)
                userId = cir.getCircleCreatorId();
            if(subcribedCircleIds != null && subcribedCircleIds.contains(cirId))
                cir.setIsFollowed(true);
            
            cir.setCurUserId(curUserId);
            result.add(cir);
        }
        return result;
    }
    
    @Override
    public List<Circle> getBcDefaultCircleAttribute(List<Circle> circles) {
        List<Circle> result = new ArrayList<Circle>();
        if(circles == null || circles.size() <= 0)
            return result;
        Map<Long, Circle> circleIdMap = new LinkedHashMap<Long, Circle>();
        for(Circle cir : circles) {
            circleIdMap.put(cir.getId(), cir);
        }
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("circle", circles));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        BlockLimit blockLimit = new BlockLimit(0, circles.size() * CircleAttribute.maxCircleAttrSize);
        blockLimit.addOrderBy("lastModified", false);
        PageResult<CircleAttribute> circleAttributes =  blockQuery(dc, blockLimit, regionOfFindDefaultCircleAttr);
        for(CircleAttribute cirAttr : circleAttributes.getResults()) {
            Circle tmp = circleIdMap.get(cirAttr.getCircle().getId());
            switch(cirAttr.getAttrType()) {
            case PostCount:
                tmp.setPostCount(Long.valueOf(cirAttr.getAttrValue()));
                break;
            case FollowerCount:
                tmp.setFollowerCount(Long.valueOf(cirAttr.getAttrValue()));
                break;
            case Thumbnail:
                tmp.setPostThumbnails(cirAttr.getAttrValue());
                break;
            default:
                break;
            }
        }
        
        for(Long cirId : circleIdMap.keySet()) {
            Circle cir = circleIdMap.get(cirId);
            cir.setIsEditable(true);
            cir.setIsFollowed(null);
            result.add(cir);
        }
        return result;
    }
    
    @Override
    public List<CircleAttribute> findCircleAttribute(String region, Circle circle, CircleAttrType attrType) {
        final DetachedCriteria dc = createDetachedCriteria();
        if(region != null)
            dc.add(Restrictions.eq("region", region));
        dc.add(Restrictions.eq("circle", circle));
        if(attrType != null)
            dc.add(Restrictions.eq("attrType", attrType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        BlockLimit blockLimit = new BlockLimit(0, CircleAttribute.maxCircleAttrSize);
        blockLimit.addOrderBy("lastModified", false);
        PageResult<CircleAttribute> circleAttributes =  blockQuery(dc, blockLimit);
        return circleAttributes.getResults();
    }
    
    @Override
    public CircleAttribute createOrUpdateCircleAttr(Circle circle, CircleAttrType attrType, String value, Boolean createIfNotExist) {
        String region = Constants.getPostRegion();
        List<CircleAttribute> cirAttrt = findCircleAttribute(region, circle, attrType);
        CircleAttribute tmp = null;
        if(cirAttrt != null && cirAttrt.size() > 0) {
            tmp = cirAttrt.get(0);
            tmp.setAttrValue(attrType.getNewValue(tmp.getAttrValue(), value));
        }
        else if(createIfNotExist){
            tmp = new CircleAttribute();
            tmp.setRegion(region);
            tmp.setCircle(circle);
            tmp.setAttrType(attrType);
            tmp.setAttrValue(attrType.getNewValue(null, value));
        }
        else
            return null;
        return update(tmp);
    }
}
