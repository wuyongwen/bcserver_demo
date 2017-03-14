package com.cyberlink.cosmetic.modules.look.dao.hibernate;


import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.look.dao.LookDao;
import com.cyberlink.cosmetic.modules.look.model.Look;

public class LookDaoHibernate extends AbstractDaoCosmetic<Look, Long> 
implements LookDao{

    @Override
	public PageResult<Look> findByUserId(Long userId, BlockLimit blockLimit) {
        PageResult<Look> result = new PageResult<Look>();
        if(userId == null)
            return result;
        final DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("userId", userId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        result = blockQuery(dc, blockLimit);
        return result;
    }

}
