package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.post.dao.PostDefaultTagDao;
import com.cyberlink.cosmetic.modules.post.model.PostDefaultTag;

public class PostDefaultTagDaoHibernate extends AbstractDaoCosmetic<PostDefaultTag, Long>
    implements PostDefaultTagDao {

    private String regionOfListPostDefaultTag = "com.cyberlink.cosmetic.modules.post.model.PostDefaultTag.list";
    
    @Override
    public List<PostDefaultTag> listByLocale(String locale) {
        DetachedCriteria dc = createDetachedCriteria();
        if(locale != null)
            dc.add(Restrictions.eq("locale", locale));
        
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        dc.addOrder(Order.desc("createdTime"));
        return findByCriteria(dc, regionOfListPostDefaultTag);
    }

}
