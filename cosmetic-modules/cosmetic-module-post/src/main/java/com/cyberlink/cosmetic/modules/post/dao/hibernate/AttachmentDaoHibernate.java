package com.cyberlink.cosmetic.modules.post.dao.hibernate;

import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.dao.AttachmentDao;
import com.cyberlink.cosmetic.modules.post.model.Attachment;

public class AttachmentDaoHibernate extends AbstractDaoCosmetic<Attachment, Long>
    implements AttachmentDao {

    @Override
    public Attachment getBcAttachment(Long refId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("refId", refId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
    }

    @Override
    public PageResult<Attachment> getAttachmentByPostId(Set<Long> postIds, BlockLimit blockLimit) {
        PageResult<Attachment> result = new PageResult<Attachment>();
        if(postIds == null || postIds.size() <= 0)
            return result;
        
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.in("postId", postIds.toArray(new Long[postIds.size()])));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        result = blockQuery(dc, blockLimit);
        return result;
    }
}
