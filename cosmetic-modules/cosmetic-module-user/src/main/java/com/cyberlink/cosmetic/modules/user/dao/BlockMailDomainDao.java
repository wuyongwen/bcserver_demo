package com.cyberlink.cosmetic.modules.user.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.user.model.BlockMailDomain;

public interface BlockMailDomainDao extends GenericDao<BlockMailDomain, Long> {
	Boolean isBlockedDomain(String domain);
}