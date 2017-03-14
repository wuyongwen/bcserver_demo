package com.cyberlink.cosmetic.modules.event.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.event.model.BeautyInsight;

public interface BeautyInsightDao extends GenericDao<BeautyInsight, Long>{
	
	PageResult<BeautyInsight> listAllBeautyInsight(Long offset, Long limit);
	PageResult<BeautyInsight> listBeautyInsightByLocale(String locale, Long offset,Long limit);
	List<BeautyInsight> listAllBeautyInsightByLocale(String locale);
}
