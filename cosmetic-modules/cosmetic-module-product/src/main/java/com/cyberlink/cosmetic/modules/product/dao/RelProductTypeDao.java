package com.cyberlink.cosmetic.modules.product.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.product.model.RelProductType;

public interface RelProductTypeDao extends GenericDao<RelProductType, Long>{
	public RelProductType findByProdIDTypeID(long PID, long typeID);
	public List<RelProductType> findByProdID(long PID);
}
