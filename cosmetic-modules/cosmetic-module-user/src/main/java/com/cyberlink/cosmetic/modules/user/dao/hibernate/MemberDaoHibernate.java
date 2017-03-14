package com.cyberlink.cosmetic.modules.user.dao.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoHibernate;
import com.cyberlink.cosmetic.modules.user.dao.MemberDao;
import com.cyberlink.cosmetic.modules.user.model.Member;

public class MemberDaoHibernate extends AbstractDaoHibernate<Member, Long> implements MemberDao{

	@Override
	public Member findByAccountId(Long accountId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("accountId", accountId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
	}

	@Override
	public Member findByCLMemberId(Long memberId) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("memberId", memberId));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
	}

	@Override
	public Member findByMemberId(Long id) {
        DetachedCriteria dc = createDetachedCriteria();
        dc.add(Restrictions.eq("id", id));
        dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        return uniqueResult(dc);
	}
}
