package com.cyberlink.cosmetic.modules.common.dao;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.common.model.CountryCode;

public interface CountryCodeDao extends GenericDao<CountryCode, Long> {

	String getCountryCode(String ipAddress);

	Long getShardId(String ipAddress);
}