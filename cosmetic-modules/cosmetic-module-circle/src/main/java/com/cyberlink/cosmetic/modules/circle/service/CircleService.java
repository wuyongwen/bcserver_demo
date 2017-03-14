package com.cyberlink.cosmetic.modules.circle.service;

import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;

public interface CircleService {

    List<Circle> getBcDefaultCircle(String region);
    List<Circle> getBcDefaultCircle(String region, Boolean isVisible);
    List<Circle> getUserDefaultCircle(Long userId, Boolean withDeleted);
    PageResult<Circle> listUserCircle(Long userId, Boolean withSecret, String region, Boolean withDefault, BlockLimit blockLimit);
    Circle getUserAccessibleCircle(Circle relatedCircle, Long userId, Boolean createIfNotExist);
    PageResult<Circle> listUserCreatedCircle(Long userId, Boolean withSecret, BlockLimit blockLimit);
    Map<String, Circle> getDefaultCircleByTypeGroupName(String typeGroupName);
    Long getCircleTypeByDefaultType(String circleTypeGroup, String locale);
    void deleteByUserId(Long userId);
    void deleteByCircleId(Long userId, Long circleId);
    List<CircleType> getCircleTypes(Long... circleTypeIds);
}
