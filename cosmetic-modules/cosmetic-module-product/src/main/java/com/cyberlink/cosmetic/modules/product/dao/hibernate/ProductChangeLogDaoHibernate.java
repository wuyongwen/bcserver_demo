package com.cyberlink.cosmetic.modules.product.dao.hibernate;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.product.dao.ProductChangeLogDao;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLog;
import com.cyberlink.cosmetic.modules.product.model.ProductChangeLogType;
import com.cyberlink.cosmetic.modules.user.model.User;

public class ProductChangeLogDaoHibernate extends AbstractDaoCosmetic<ProductChangeLog, Long>
	implements ProductChangeLogDao{

	public PageResult<ProductChangeLog> listProdChangeLog(Long userId,
			ProductChangeLogType itemtype, Long itemId, Long offset, Long limit) {
		DetachedCriteria dc = createDetachedCriteria();
		if( userId != null ){
			dc.add(Restrictions.eq("user.id", userId));
		}
		if( itemtype != null ){
			dc.add(Restrictions.eq("refType", itemtype));
		}
		if( itemId != null ){
			dc.add(Restrictions.eq("refId", itemId));
		}
		/*Date kerker = new Date();
		DateFormat df = DateFormat.getDateInstance();
		try {
			kerker = df.parse("2015/2/5");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dc.add(Restrictions.eq("createdTime", kerker));*/
		dc.add(Restrictions.eq( "isDeleted" , Boolean.FALSE )) ;
		dc.addOrder(Order.desc("lastModified"));
		return findByCriteria(dc, offset, limit, "com.cyberlink.cosmetic.modules."
				+ "product.dao.hibernate.ProductChangeLogDaoHibernate.listProdChangeLog");
	}

	public List<User> findUserList() {
		DetachedCriteria dc = createDetachedCriteria();
		dc.setProjection(Projections.property("user"));
		dc.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return findByCriteria(dc);
	}

}

