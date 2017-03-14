package com.cyberlink.cosmetic.action.api.post;

import com.cyberlink.core.web.view.page.BlockLimit;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.post.model.Horoscope;
import com.cyberlink.cosmetic.modules.post.dao.HoroscopeDao;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;
import net.sourceforge.stripes.validation.Validate;

@UrlBinding("/api/v4.8/post/list-horoscope.action")
public class ListHoroscopeAction extends AbstractAction {

    @SpringBean("post.HoroscopeDao")
    private HoroscopeDao horoscopeDao;
    
	private Integer offset = 0;
	private Integer limit = 10;
    private String locale;
    
	@Validate(minvalue = 0, required = false, on = "route")
	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	@Validate(minvalue = 1, maxvalue = 20, required = false, on = "route")
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
	@DefaultHandler
	public Resolution route() {
		
		BlockLimit blockLimit = new BlockLimit(offset, limit);
		PageResult<Horoscope> pageResult = horoscopeDao.listByLocale(locale, blockLimit);
		return json(pageResult);
	}
}
