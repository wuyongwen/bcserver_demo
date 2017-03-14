package com.cyberlink.cosmetic.modules.circle.dao;

import java.util.Date;
import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.CircleSubscribe;
import com.cyberlink.cosmetic.modules.user.model.User;

public interface CircleSubscribeDao extends GenericDao<CircleSubscribe, Long>{
    
    List<CircleSubscribe> getCircleSubscribe(Long userId, Long circleId);
    PageResult<Circle> findByUserId(Long userId, BlockLimit blockLimit);
    List<CircleSubscribe> findSubscribeByUserId(Long userId);
    List<Long> findByUserId(Long userId);
    PageResult<User> findByCircleId(Long circleId, BlockLimit blockLimit);
    List<Long> listSubcribeCircle(Long userId, List<Circle> circles);
    Long getSubscribeCountByCircleCreator(Long userId, Long circleCreatorId);
    PageResult<CircleSubscribe> findAllCircleSubscribe(Date startTime, Date endTime, BlockLimit blockLimit);
    List<CircleSubscribe> listCircleSubcribe(Long userId, Long creatorId);
    Integer bacthDeleteSubscribe(Long circleId);
    void bacthDeleteByCircleCreator(Long userId);
    List<Long> listByCircleId(Long circleId);
    List<Long> listUserIdByCircleId(Long circleId);
    void doWithAllCircleSubscribe(
            ScrollableResultsCallback scrollableResultsCallback);
    void doWithAllCircleSubscribeBetween(
            Date begin, Date end, ScrollableResultsCallback callback);
}
