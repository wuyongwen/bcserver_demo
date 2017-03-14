package com.cyberlink.cosmetic.modules.circle.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.CircleType;

public interface CircleTypeDao extends GenericDao<CircleType, Long>{
	CircleType create(String circleTypeName, String locale);
    List<CircleType> listAllTypes();
	PageResult<CircleType> listTypesByLocale(String locale, Boolean isVisible, BlockLimit blockLimit);
	PageResult<CircleType> listTypesByLocales(List<String> locales, Boolean isVisible, BlockLimit blockLimit);
    List<CircleType> findByIds(Long... ids);
    List<CircleType> findByName(String circleTypeName);
    List<CircleType> listTypesByTypeGroup(Long circleTypeGroupId, String locale);   
    List<CircleType> listTypesByLocale(String locale, Boolean isVisible); 
	List<Long> listTypeIdsByLocale(String locale, Boolean isVisible);

}
