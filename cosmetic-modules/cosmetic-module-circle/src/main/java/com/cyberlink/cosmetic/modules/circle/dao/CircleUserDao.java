package com.cyberlink.cosmetic.modules.circle.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.CircleUser;

public interface CircleUserDao extends GenericDao<CircleUser, Long>{
    List<CircleUser> findByIds(Long... ids);
    List<CircleUser> findByUserId(Long id);
    List<CircleUser> findByCircleId(Long id);
    PageResult<CircleUser> findByCircleId(Long id, Long offset, Long limit);
    PageResult<CircleUser> findByUserId(Long id, Long offset, Long limit);
}
