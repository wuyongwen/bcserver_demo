package com.cyberlink.cosmetic.modules.common.service.impl;

import javax.servlet.http.HttpServletRequest;

import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.geoip.LookupService;
import com.cyberlink.cosmetic.modules.common.service.GeoIPService;

public class GeoIPServiceImpl extends AbstractService implements GeoIPService {
	private LookupService lookupService;
	
	private LookupService getService() {
		if (lookupService == null) {
			try {
				lookupService = new LookupService(Constants.getGeoipPath(),
						LookupService.GEOIP_MEMORY_CACHE);
			} catch (Exception e) {
				logger.error(e.getMessage());
				return null;
			}
		}
		return lookupService;
	}

	@Override
	public String getCountryCode(String ipAddress) {
		if (getService() == null)
			return "";

		if (ipAddress == null || ipAddress.isEmpty())
			return "";
		
		try {
			return getService().getLocation(ipAddress).countryCode;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "";
		}
	}

	@Override
	public String getCountryCode(HttpServletRequest request) {
		if (getService() == null)
			return "";

		String ip = getIpAddr(request);
		if (ip == null || ip.isEmpty())
			return "";

		try {
			return getService().getLocation(ip).countryCode;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "";
		}
	}
	
	@Override
	public Long getShardIdByCountry(String countryCode) {
		if (countryCode != null && SHARDINGMAP.containsKey(countryCode))
			return SHARDINGMAP.get(countryCode);
		else
			return SHARD_OTHERS;
	}
	
	@Override
	public Long getShardIdByIp(String ipAddress) {
		String countryCode = getCountryCode(ipAddress);
		if (countryCode != null && SHARDINGMAP.containsKey(countryCode))
			return SHARDINGMAP.get(countryCode);
		else
			return SHARD_OTHERS;
	}

	@Override
	public String getIpAddr(HttpServletRequest request) {
		String ip = null;
		try {
			ip = request.getHeader("x-forwarded-for");
			if (ip != null && ip.length() != 0
					&& !"unknown".equalsIgnoreCase(ip)) {
				int idx = ip.indexOf(',');
				if (idx > -1) {
					ip = ip.substring(0, idx);
				}
			} else {
				ip = request.getHeader("Proxy-Client-IP");
			}

			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0
					|| "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {

		}

		if (ip == null)
			ip = "";
		return ip;
	}
}