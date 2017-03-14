package com.cyberlink.cosmetic.modules.event.dao;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.event.model.BrandEvent;
import com.cyberlink.cosmetic.modules.event.model.EventType;
import com.cyberlink.cosmetic.modules.event.model.ServiceType;

public interface BrandEventDao extends GenericDao<BrandEvent, Long>{
	List<BrandEvent> listBrandEvent(String locale, ServiceType serviceType);
	PageResult<Pair<Long, BrandEvent>> findByEventIds(Long userId, Set<Long> eventIds, BlockLimit blockLimit);
	Boolean batchCreate(List<BrandEvent> list);
    int bacthDeleteByEventIds(Set<Long> eventIdSet);
    BrandEvent findByBrandEventId(Long id);
    List<Object> findImageSetListByBrandId(Long brandId);
    PageResult<BrandEvent> findBrandEventByType(String locale, List<ServiceType> serviceTypes, List<EventType> eventTypes, BlockLimit blockLimit);
}
