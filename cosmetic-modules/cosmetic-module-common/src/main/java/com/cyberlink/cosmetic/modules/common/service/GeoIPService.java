package com.cyberlink.cosmetic.modules.common.service;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

public interface GeoIPService {
	
	public static final Map<String, Long> SHARDINGMAP = new TreeMap<String, Long>(
			String.CASE_INSENSITIVE_ORDER) {
		private static final long serialVersionUID = 3625471505013677953L;
		{
			put("US", 11L);
			put("BR", 31L);
			put("MX", 51L);
			put("FR", 71L);
			put("DE", 91L);
			put("GB", 111L);
			put("IT", 131L);
			put("CN", 151L);
			put("TW", 171L);
			put("JP", 191L);
			put("KR", 211L);
			put("TR", 231L);
			put("RU", 251L);
			put("AZ", 271L);
			put("IQ", 291L);
			put("JO", 311L);
			put("SA", 331L);
			put("PK", 351L);
			put("IN", 371L);
			put("EG", 391L);
		}
	};
	public static final Long SHARD_OTHERS = 1001L;
	
	String getCountryCode(String ipAddress);
	
	String getCountryCode(HttpServletRequest request);
	
	String getIpAddr(HttpServletRequest request);
	
	Long getShardIdByCountry(String countryCode);
	
	Long getShardIdByIp(String ipAddress);
}
