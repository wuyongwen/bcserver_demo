package com.cyberlink.cosmetic.modules.look.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;

public class LookTypeDaoHibernate extends AbstractDaoCosmetic<LookType, Long>
    implements LookTypeDao {

    private String regionOfListLookType = "com.cyberlink.cosmetic.modules.look.model.LookType.list";
    private String regionOfFindMapByCodeName = "com.cyberlink.cosmetic.modules.look.model.LookType.findMapByCodeName";
    
    @Override
    public List<LookType> listByLocale(String locale) {
        DetachedCriteria dc = createDetachedCriteria();
        if(locale != null)
            dc.add(Restrictions.eq("locale", locale));
        
        dc.add(Restrictions.eq("isVisible", Boolean.TRUE));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.addOrder(Order.desc("createdTime"));
        return findByCriteria(dc, regionOfListLookType);
    }

    @Override
    public Map<String, Long> findMapByCodeName(String codeName) {
        Map<String, Long> resultMap = new HashMap<String, Long>();
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.eq("isVisible", Boolean.TRUE));
        dc.add(Restrictions.eq("codeName", codeName));
        dc.setProjection(Projections.projectionList()
                .add(Projections.property("locale"))
                .add(Projections.property("id")));
        List<Object> resultList = findByCriteria(dc, regionOfFindMapByCodeName);
        for(Object obj : resultList) {
            Object[] row = (Object[]) obj;
            resultMap.put((String) row[0], (Long) row[1]);
        }
        return resultMap;
    }
    
    @Override
    public Map<Long, LookType> findMapByIds(Set<Long> lookTypeIds) {
        Map<Long, LookType> resultMap = new HashMap<Long, LookType>();
        if(lookTypeIds.size() <= 0)
            return resultMap;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.add(Restrictions.in("id", lookTypeIds));
        List<LookType> resultList = findByCriteria(dc);
        for(LookType lt : resultList) {
            resultMap.put(lt.getId(), lt);
        }
        return resultMap;
    }

}
