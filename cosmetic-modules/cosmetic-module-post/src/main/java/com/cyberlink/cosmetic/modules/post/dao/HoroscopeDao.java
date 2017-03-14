package com.cyberlink.cosmetic.modules.post.dao;

import java.util.List;

import com.cyberlink.core.dao.GenericDao;
import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.post.model.Horoscope;

public interface HoroscopeDao extends GenericDao<Horoscope, Long>{
    
	List<Horoscope> listByLocale(String locale);
    PageResult<Horoscope> listByLocale(String locale, BlockLimit blockLimit);
    
}
