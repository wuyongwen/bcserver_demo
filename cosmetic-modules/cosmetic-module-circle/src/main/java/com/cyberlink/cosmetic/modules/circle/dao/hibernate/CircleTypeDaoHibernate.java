package com.cyberlink.cosmetic.modules.circle.dao.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.dao.CircleTypeDao;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;

public class CircleTypeDaoHibernate extends AbstractDaoCosmetic<CircleType, Long> implements CircleTypeDao{
	private String regionOflistTypesByLocale = "com.cyberlink.cosmetic.modules.circle.model.CircleType.query.listTypesByLocale";
	private String regionOflistTypesByLocales = "com.cyberlink.cosmetic.modules.circle.model.CircleType.query.listTypesByLocales";
	private String regionOflistTypeIdsByLocale = "com.cyberlink.cosmetic.modules.circle.model.CircleType.query.listTypeIdsByLocale";
	private String regionOflistTypeByTypeGroup = "com.cyberlink.cosmetic.modules.circle.model.CircleType.query.listTypesByTypeGroup";
	
    @Override
    public CircleType create(String circleTypeName, String locale) {    	
    	CircleType circleTypeNew = new CircleType();
    	DetachedCriteria dc = createDetachedCriteria();
    	dc.add(Restrictions.eq("circleTypeName", circleTypeName));
    	List<CircleType> circleTypeList = findByCriteria(dc);
    	if (circleTypeList.size() > 0) {
    		circleTypeNew = circleTypeList.get(0); 
    		circleTypeNew.setIsDeleted(false);
    	}
    	else {    		
    		CircleType circleType = new CircleType();
    		circleType.setCircleTypeName(circleTypeName);
    		circleType.setLocale(locale);
    		circleTypeNew = create(circleType);
    	}        
        return circleTypeNew;
    }
    
	@Override
    public List<CircleType> findByIds(Long... ids) {
        if (ids == null || ids.length == 0) {
            return Collections.emptyList();
        }

        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("id", ids));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }
    

    @Override
    public List<CircleType> listAllTypes() {
    	return findAll();	
    }

    @Override
    public PageResult<CircleType> listTypesByLocale(String locale, Boolean isVisible, BlockLimit blockLimit) {
    	DetachedCriteria dc = createDetachedCriteria();
		if (locale != null) 
			dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.createAlias("circleTypeGroup", "circleTypeGroup");
		if(isVisible != null) {
		    dc.add(Restrictions.eq( "isVisible" , isVisible)) ;
		}
		blockLimit.addOrderBy("circleTypeGroup.sortOrder", true);
		return blockQuery(dc, blockLimit, regionOflistTypesByLocale);	
    }
    
    @Override
    public PageResult<CircleType> listTypesByLocales(List<String> locales, Boolean isVisible, BlockLimit blockLimit) {
        DetachedCriteria dc = createDetachedCriteria();
        if (locales != null && locales.size() > 0) 
            dc.add(Restrictions.in( "locale" , locales )) ;
        if(isVisible != null) {
            dc.add(Restrictions.eq( "isVisible" , isVisible)) ;
        }
        dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
        return blockQuery(dc, blockLimit, regionOflistTypesByLocales);    
    }
    
    @Override
    public List<CircleType> findByName(String circleTypeName) {
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("circleTypeName", circleTypeName));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }
    
    @Override
    public List<CircleType> listTypesByTypeGroup(Long circleTypeGroupId, String locale) {
        List<CircleType> result = new ArrayList<CircleType>();
        if(circleTypeGroupId == null)
            return result;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq( "circleTypeGroupId" , circleTypeGroupId )) ;
        if(locale != null)
            dc.add(Restrictions.eq( "locale" , locale )) ;
            
        dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
        return findByCriteria(dc, regionOflistTypeByTypeGroup);   
    }

	@Override
	public List<Long> listTypeIdsByLocale(String locale, Boolean isVisible) {
    	DetachedCriteria dc = createDetachedCriteria();
		if (locale != null) 
			dc.add(Restrictions.eq( "locale" , locale )) ;
        if(isVisible != null) {
            dc.add(Restrictions.eq( "isVisible" , isVisible)) ;
        }
        dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.setProjection(Projections.property("id"));
		return findByCriteria(dc, regionOflistTypeIdsByLocale);
	}
	
	@Override
	public List<CircleType> listTypesByLocale(String locale, Boolean isVisible) {
    	DetachedCriteria dc = createDetachedCriteria();
		if (locale != null) 
			dc.add(Restrictions.eq( "locale" , locale )) ;
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		if(isVisible != null) {
		    dc.add(Restrictions.eq( "isVisible" , isVisible)) ;
		}

		return findByCriteria(dc);	
	}
}
