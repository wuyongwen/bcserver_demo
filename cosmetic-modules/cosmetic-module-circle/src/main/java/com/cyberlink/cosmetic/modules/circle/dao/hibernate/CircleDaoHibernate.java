package com.cyberlink.cosmetic.modules.circle.dao.hibernate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.dao.CircleDao;
import com.cyberlink.cosmetic.modules.circle.model.Circle;

public class CircleDaoHibernate extends AbstractDaoCosmetic<Circle, Long>
		implements CircleDao {
	private String regionOffindByTypeId = "com.cyberlink.cosmetic.modules.circle.model.Circle.query.findByTypeId";
	private String regionOffindDefaultCircleByUserIds = "com.cyberlink.cosmetic.modules.circle.model.Circle.query.findDefaultCircleByUserIds";
	private String regionOffindCLUserCircle = "com.cyberlink.cosmetic.modules.circle.model.Circle.query.regionOffindCLUserCircle";

	@Override
	public Circle create(String circleName) {
		Circle oCricleNew = new Circle();
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("circleName", circleName));
		List<Circle> listCircle = findByCriteria(dc);
		if (listCircle.size() > 0) {
			oCricleNew = listCircle.get(0);
			oCricleNew.setIsDeleted(false);
		} else {
			Circle oCricle = new Circle();
			oCricle.setCircleName(circleName);
			oCricleNew = create(oCricle);
		}
		return oCricleNew;
	}
	
	@Override
	public Circle findById(Long id, Boolean isDeleted){
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("id", id));
		if(!isDeleted)
			dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return uniqueResult(dc);
	}

	@Override
	public List<Circle> findByIds(Long... ids) {
		if (ids == null || ids.length == 0) {
			return Collections.emptyList();
		}

		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

		return findByCriteria(dc);
	}

	@Override
	public List<Circle> listAllCircles() {
		return findAll();
	}

	@Override
	public List<Circle> findByTypeId(Long typeId) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("cricleTypeId", typeId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return findByCriteria(dc);
	}

	@Override
	public PageResult<Circle> findByTypeId(Long typeId, Long offset, Long limit) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("cricleTypeId", typeId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return findByCriteria(dc, offset, limit, regionOffindByTypeId);
	}
	
	@Override
    public PageResult<Circle> findByTypeIds(List<Long> typeIds, Long offset, Long limit) {
	    PageResult<Circle> result = new PageResult<Circle>();
	    if(typeIds == null || typeIds.size() <= 0)
	        return result;
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("cricleTypeId", typeIds));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        result = findByCriteria(dc, offset, limit, regionOffindByTypeId);
        return result;
    }
    
    @Override
    public PageResult<Circle> findByUserIds(List<Long> userIds, Boolean withSecret, BlockLimit blockLimit) {
        PageResult<Circle> result = new PageResult<Circle>();
        if(userIds == null || userIds.size() <= 0)
            return result;
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("creatorId", userIds));
        if(!withSecret)
            dc.add(Restrictions.or(Restrictions.isNull("isSecret"), Restrictions.eq("isSecret", false)));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        result = blockQuery(dc, blockLimit);
        return result;
    }
    
    @Override
    public PageResult<Circle> findByCLUserIds(List<Long> userIds, Boolean onlyDefaultCircle, Boolean withSecret, BlockLimit blockLimit) {
        PageResult<Circle> result = new PageResult<Circle>();
        if(userIds == null || userIds.size() <= 0)
            return result;
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("creatorId", userIds));
        if(!withSecret)
            dc.add(Restrictions.or(Restrictions.isNull("isSecret"), Restrictions.eq("isSecret", false)));
        
        if(onlyDefaultCircle != null && onlyDefaultCircle.equals(true))
            dc.add(Restrictions.isNotNull("defaultType"));
        
        dc.createAlias("circleType", "circleType");
        dc.add(Restrictions.eq("circleType.isVisible", Boolean.TRUE));
        dc.createAlias("circleType.circleTypeGroup", "circleTypeGroup");      
        blockLimit.addOrderBy("circleTypeGroup.sortOrder", true);
        
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        result = blockQuery(dc, blockLimit, regionOffindCLUserCircle);
        return result;
    }
    
    @Override
    public PageResult<Circle> findByCLUserIds(List<Long> userIds, List<Long> circleTypeIds, Boolean withSecret, BlockLimit blockLimit) {
        PageResult<Circle> result = new PageResult<Circle>();
        if(userIds == null || userIds.size() <= 0)
            return result;
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("creatorId", userIds));
        if(!withSecret)
            dc.add(Restrictions.or(Restrictions.isNull("isSecret"), Restrictions.eq("isSecret", false)));
        
        if(circleTypeIds != null && circleTypeIds.size() > 0)
            dc.add(Restrictions.in("cricleTypeId", circleTypeIds));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        result = blockQuery(dc, blockLimit, regionOffindCLUserCircle);
        return result;
    }
    
    @Override
    public PageResult<Circle> findUserDefaultCircleByUserId(Long userId, Boolean withDeleted, Long offset, Long limit) {
        PageResult<Circle> result = new PageResult<Circle>();
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("creatorId", userId));
        dc.add(Restrictions.isNotNull("defaultType"));
        if(!withDeleted)
            dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        result = findByCriteria(dc, offset, limit, null);
        return result;
    }
    
    @Override
    public PageResult<Circle> findBcDefaultCircleByCircleTypeIds(List<Long> circleTypeIds, Long offset, Long limit) {
        PageResult<Circle> result = new PageResult<Circle>();
        if(circleTypeIds == null || circleTypeIds.size() <= 0)
            return result;
        final DetachedCriteria dc = createDetachedCriteria();
        if(circleTypeIds != null && circleTypeIds.size() > 0)
            dc.add(Restrictions.in("cricleTypeId", circleTypeIds));
        dc.add(Restrictions.isNull("creatorId"));
        dc.add(Restrictions.or(Restrictions.isNull("isSecret"), Restrictions.eq("isSecret", false)));
        dc.add(Restrictions.isNotNull("defaultType"));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        result = findByCriteria(dc, offset, limit, regionOffindDefaultCircleByUserIds);
        return result;
    }
    
    @Override
    public List<Object> getUserCircelAttr(Long userId) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("defaultType"))
                .add(Projections.groupProperty("cricleTypeId"))
                .add(Projections.groupProperty("isDeleted"))
                .add(Projections.groupProperty("isSecret"))
                .add(Projections.rowCount(), "count"));
        dc.add(Restrictions.eq("creatorId", userId));
        return findByCriteria(dc);
    }
    
    @Override
    public Circle getUserCreateDefaultCircle(Long userId, String defaultType) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("creatorId", userId));
        dc.add(Restrictions.eq("defaultType", defaultType));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
    }

	@Override
	public Long getUserCircleCount(Long userId, Boolean withSecret) {
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("creatorId", userId));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		if (!withSecret)
			dc.add(Restrictions.eq("isSecret", Boolean.FALSE));
		PageResult<Circle> page = findByCriteria(dc, Long.valueOf(0), Long.valueOf(0), null);
		return page.getTotalSize().longValue();
	}
	
	@Override
	public PageResult<Circle> findAllCircle(BlockLimit blockLimit) {
		PageResult<Circle> result = new PageResult<Circle>();
		final DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.isNotNull("creatorId"));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		result = blockQuery(dc, blockLimit);
        return result;
	}
	
	@Override
	public Map<Long, Circle> findCircleMap(Set<Long> circleIds) {
	    Map<Long, Circle> result = new HashMap<Long, Circle>();
        if(circleIds == null || circleIds.size() <= 0)
            return result;
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.in("id", circleIds));
        List<Circle> circles = findByCriteria(dc);
        for(Circle circle : circles)
            result.put(circle.getId(), circle);
        return result;
	}

	@Override
	public void deleteCircleByCreator(Long userId) {
		String format = "UPDATE `BC_CIRCLE` SET `IS_DELETED`= '1' WHERE `USER_ID` = '%s'";
		String sql = String.format(format, userId.toString());			
        SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
        sqlPostsQuery.executeUpdate();
        return;		
	}

    @Override
    public void doWithAllPublicCircle(ScrollableResultsCallback callback) {
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isSecret", Boolean.FALSE));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("creatorId"))
                .add(Projections.property("id")));
        final Criteria c = dc.getExecutableCriteria(getSession());

        final ScrollableResults sr = c.scroll(ScrollMode.FORWARD_ONLY);
        try {
            callback.doInHibernate(sr);
        } finally {
            sr.close();
        }
    }
}
