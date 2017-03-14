package com.cyberlink.cosmetic.modules.circle.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.Circle;

public interface CircleDao extends GenericDao<Circle, Long>{
	Circle create(String circleName);
	Circle findById(Long id, Boolean isDeleted);
    List<Circle> findByIds(Long... ids);
    List<Circle> listAllCircles();
    List<Circle> findByTypeId(Long typeId);
    PageResult<Circle> findByTypeId(Long typeId, Long offset, Long limit);
    PageResult<Circle> findByTypeIds(List<Long> typeIds, Long offset, Long limit);
    PageResult<Circle> findByUserIds(List<Long> userIds, Boolean withSecret, BlockLimit blockLimit);
    PageResult<Circle> findByCLUserIds(List<Long> userIds, Boolean onlyDefaultCircle, Boolean withSecret, BlockLimit blockLimit);
    PageResult<Circle> findByCLUserIds(List<Long> userIds, List<Long> circleTypeIds, Boolean withSecret, BlockLimit blockLimit);
    PageResult<Circle> findBcDefaultCircleByCircleTypeIds(List<Long> circleTypeIds, Long offset, Long limit);
    PageResult<Circle> findUserDefaultCircleByUserId(Long userId, Boolean withDeleted, Long offset, Long limit);
    List<Object> getUserCircelAttr(Long userId);
    Circle getUserCreateDefaultCircle(Long userId, String defaultType);
    Long getUserCircleCount(Long userId, Boolean withSecret);
    PageResult<Circle> findAllCircle(BlockLimit blockLimit);
    Map<Long, Circle> findCircleMap(Set<Long> circleIds);
    void deleteCircleByCreator(Long userIds);

    void doWithAllPublicCircle(ScrollableResultsCallback callback);
}
