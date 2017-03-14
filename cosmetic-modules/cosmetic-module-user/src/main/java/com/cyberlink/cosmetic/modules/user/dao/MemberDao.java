package com.cyberlink.cosmetic.modules.user.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.user.model.Member;

public interface MemberDao extends GenericDao<Member, Long>{
	Member findByAccountId(Long accountId);
	Member findByCLMemberId(Long memberId);
	Member findByMemberId(Long id);	
}
