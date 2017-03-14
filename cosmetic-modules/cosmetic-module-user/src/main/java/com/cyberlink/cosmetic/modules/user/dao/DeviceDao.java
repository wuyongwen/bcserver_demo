package com.cyberlink.cosmetic.modules.user.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.Device;
import com.cyberlink.cosmetic.modules.user.model.DeviceType;

public interface DeviceDao extends GenericDao<Device, Long>{
	Device findDeviceInfo(Long userId, String uuid, DeviceType deviceType, String app);
	Device findDeviceInfo(String uuid);
	Map<Long, Device> findNotifyDeviceByUserIds(Set<Long> userIds);
	// only for backend used
	List<String> findByUserId(Long userId);
	List<String> findDistinctByUserId(Long userId);
	PageResult<String> findDeviceUuidByUserId(Long userId, Long offset, Long limit);
}
