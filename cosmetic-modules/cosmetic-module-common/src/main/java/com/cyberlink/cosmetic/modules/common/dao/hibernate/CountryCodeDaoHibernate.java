package com.cyberlink.cosmetic.modules.common.dao.hibernate;

import java.net.InetAddress;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.cyberlink.core.dao.hibernate.AbstractDaoCosmetic;
import com.cyberlink.cosmetic.modules.common.dao.CountryCodeDao;
import com.cyberlink.cosmetic.modules.common.model.CountryCode;

public class CountryCodeDaoHibernate extends
		AbstractDaoCosmetic<CountryCode, Long> implements CountryCodeDao {

	private Long ipAddressToLong(String ipAddress) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(ipAddress);
		} catch (Exception e) {
			return null;
		}

		byte[] address = addr.getAddress();
		Long ipnum = 0L;
		for (int i = 0; i < 4; ++i) {
			long y = address[i];
			if (y < 0) {
				y += 256;
			}
			ipnum += y << ((3 - i) * 8);
		}
		return ipnum;
	}

	@Override
	public String getCountryCode(String ipAddress) {
		Long ip = ipAddressToLong(ipAddress);
		if (ip == null)
			return null;
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.le("networkStart", ip));
		dc.add(Restrictions.ge("networkLast", ip));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.property("countryCode"));

		return uniqueResult(dc);
	}
	
	@Override
	public Long getShardId(String ipAddress) {
		Long ip = ipAddressToLong(ipAddress);
		if (ip == null)
			return null;
		
		DetachedCriteria dc = createDetachedCriteria();
		dc.add(Restrictions.le("networkStart", ip));
		dc.add(Restrictions.ge("networkLast", ip));
		dc.add(Restrictions.eq("isDeleted", Boolean.FALSE));
		dc.setProjection(Projections.property("shardId"));

		return uniqueResult(dc);
	}
}
