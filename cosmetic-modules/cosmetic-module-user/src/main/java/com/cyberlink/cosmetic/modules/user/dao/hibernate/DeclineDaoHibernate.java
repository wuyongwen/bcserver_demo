package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.dao.AccountDao;
import com.cyberlink.cosmetic.modules.user.dao.DeclineDao;
import com.cyberlink.cosmetic.modules.user.model.*;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeclineDaoHibernate extends AbstractDaoCosmetic<Decline, Long> implements DeclineDao{
    private String regionOfFindBySourceAndReference = "com.cyberlink.cosmetic.modules.user.model.BCAccount.query.findBySourceAndReference";

    @Override
    public Decline findByTypeAndDeclineid(String type, String declineid) {
    	DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("type", type));
        dc.add(Restrictions.eq("declineid", declineid));
        return uniqueResult(dc);
    }
}
