package com.cyberlink.cosmetic.modules.user.dao;

import java.util.List;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.cosmetic.modules.user.model.Attribute;
import com.cyberlink.cosmetic.modules.user.model.BlockDevice;

public interface BlockDeviceDao extends GenericDao<BlockDevice, Long> {
	Boolean isBlockedUuid(String uuid);
	Set<String> checkIsBlocked(Set<String> uuids); 
	List<BlockDevice> findByUuid(String uuid);
}
