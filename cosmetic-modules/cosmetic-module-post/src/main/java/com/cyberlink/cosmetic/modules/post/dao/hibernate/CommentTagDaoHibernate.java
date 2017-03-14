package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoHibernate;
import com.cyberlink.cosmetic.modules.post.dao.CommentTagDao;
import com.cyberlink.cosmetic.modules.post.model.CommentTag;

public class CommentTagDaoHibernate extends AbstractDaoHibernate<CommentTag, Long>
    implements CommentTagDao {

    @Override
    public List<CommentTag> listCommentTag(Long... commentIds) {
        DetachedCriteria d = createDetachedCriteria();
        d.add(Restrictions.in("commentId", commentIds));
        d.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return findByCriteria(d);
    }

}
