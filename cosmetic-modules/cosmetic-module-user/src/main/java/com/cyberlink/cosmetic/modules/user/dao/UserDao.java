package com.cyberlink.cosmetic.modules.user.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.dao.hibernate.ScrollableResultsCallback;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.user.model.GenderType;
import com.cyberlink.cosmetic.modules.user.model.User;
import com.cyberlink.cosmetic.modules.user.model.User.LookSource;
import com.cyberlink.cosmetic.modules.user.model.UserType;

public interface UserDao extends GenericDao<User, Long> {
    List<User> findByIds(Long... userIds);
    List<User> findByIdsWithOrder(Long... userIds);
    PageResult<User> findByUserType(List<UserType> userType, List<String> locale, Long offset, Long limit);
    PageResult<User> findByUserTypeAndLookSource(List<UserType> userType, List<LookSource> lookSource, List<String> locale, Long offset, Long limit);
    PageResult<Long> findIdByUserType(UserType userType, String locale, Long offset, Long limit);
    List<Long> findIdByUserType(UserType userType, List<String> locale);
    List<Long> findIdByUserTypeWithoutStatus(List<UserType> userType, List<String> locale);
    Map<Long, User> findUserMap(Set<Long> userIds);
    
    // Only Backend use
    PageResult<User> findUserByParameters(Long id, GenderType gender, UserType userType, String locale, Date startTime, Date endTime, Long offset, Long limit);
    PageResult<User> findUserByParameters(Long id, GenderType gender, UserType userType, String locale, Date startTime, Date endTime, Long offset, Long limit,Long access);
    PageResult<Long> findAllUserId(Long offset, Long limit);
    PageResult<User> findLastModifiedUser(Date startTime, Date endTime, Long offset, Long limit);

    PageResult<User> findUserWithoutAvatarUrl(Long limit);

    void doWithAllUser(String locale, BlockLimit blockLimit, ScrollableResultsCallback callback);
    
    public Long verifyUniqueId(String uniqueId);
}
