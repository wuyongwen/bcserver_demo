package com.cyberlink.cosmetic.modules.circle.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.CircleTagGroup;

public interface CircleTagGroupDao extends GenericDao<CircleTagGroup, Long>{
	CircleTagGroup create(String circleTagGroupName);
	List<CircleTagGroup> listAllTagGroups();
    List<CircleTagGroup> findByIds(Long... ids);
    List<CircleTagGroup> findByCircleId(Long id);
    PageResult<CircleTagGroup> findByCircleId(Long id, Long offset, Long limit);    
}
