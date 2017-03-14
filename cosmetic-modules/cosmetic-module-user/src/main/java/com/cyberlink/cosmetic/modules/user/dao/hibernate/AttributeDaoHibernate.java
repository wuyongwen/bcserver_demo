package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.dao.AttributeDao;
import com.cyberlink.cosmetic.modules.user.model.AttributeType;
import com.cyberlink.cosmetic.modules.user.model.Attribute;

public class AttributeDaoHibernate extends AbstractDaoCosmetic<Attribute, Long> implements AttributeDao{
    //private String regionOfFindByRefIdAndName = "com.cyberlink.cosmetic.modules.user.model.BCAttribute.query.findByRefIdAndName";
	private String regionOfFindByRefType = "com.cyberlink.cosmetic.modules.user.model.BCAttribute.query.findByRefType";
	
    @Override
    public List<Attribute> findByRefIdAndNames(AttributeType type, Long refId,
            String ... attrNames) {
    	if (attrNames == null || attrNames.length == 0) {
    		return new ArrayList<Attribute>();
    	}
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("refId", refId));
        dc.add(Restrictions.eq("refType", type));
        dc.add(Restrictions.in("attrName", attrNames));        
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }

    @Override
    public List<Attribute> findByNameAndRefIds(AttributeType type, String attrName, Long... refIds) {
    	if (refIds == null || refIds.length == 0) {
    		return new ArrayList<Attribute>();
    	}
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("refId", refIds));
        dc.add(Restrictions.eq("refType", type));
        dc.add(Restrictions.eq("attrName", attrName));        
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }
    
	@Override
	public List<Attribute> findByRefType(AttributeType type) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("refType", type));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.projectionList()
	        	.add(Projections.property("id"))
	        	.add(Projections.property("attrName"))
	        	.add(Projections.property("attrValue")));
		
		List<Object> objs = findByCriteria(dc, regionOfFindByRefType);
		List<Attribute> attrList = new ArrayList<Attribute>();
		for(Object obj : objs){
			Object[] row = (Object[]) obj;
			Attribute attr = new Attribute();
			attr.setId((Long) row[0]);
			attr.setAttrName((String) row[1]);
			attr.setAttrValue((String) row[2]);
			attrList.add(attr);
		}
		return attrList;
	}
    
    @Override
    public List<Attribute> findByRefId(AttributeType type, Long refId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("refId", refId));
        dc.add(Restrictions.eq("refType", type));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return findByCriteria(dc);
    }

	@Override
	public Attribute findOneByRefIdAndName(AttributeType type, Long refId,
			String attrName) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("refId", refId));
        dc.add(Restrictions.eq("refType", type));
        dc.add(Restrictions.eq("attrName", attrName));        
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));

        return uniqueResult(dc);
	}

	public List<Attribute> listAllBackendUserAttrs(AttributeType type,
			String attrName) {
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.eq("refType", type));
		dc.add(Restrictions.eq("attrName", attrName));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		return findByCriteria(dc);
	}

	@Override
	public List<Long> findAttributeUser(Long offset, Long limit) {
		List<Long> result = new ArrayList<Long>();
		DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("refType", AttributeType.User));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
    	dc.setProjection(Projections.groupProperty("refId"));
        
        PageResult<Object> list = findByCriteriaWithoutCount(dc, offset, limit, null);
        for (Object obj : list.getResults()) {
        	result.add((Long) obj);
        }
        return result;
	}

	@Override
	public void deleteUserAttribute(List<Long> list) {
		String sql = "UPDATE `BC_ATTR` SET `IS_DELETED`= '1' WHERE `REF_TYPE` = 'User' AND `REF_ID` IN (";
		for (int i = 0; i < list.size(); i++) {
			sql += list.get(i).toString();
			if (i < list.size() -1) {
				sql += ", ";
			} else {
				sql += ")";
			}
		}
		SQLQuery sqlPostsQuery = getSession().createSQLQuery(sql);
		sqlPostsQuery.executeUpdate();
	}

}
