package com.cyberlink.cosmetic.action.api.look;

import java.util.List;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.look.dao.LookTypeDao;
import com.cyberlink.cosmetic.modules.look.model.LookType;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/v4.4/look/list-look-type.action")
public class ListLookTypeAction extends AbstractAction {

    @SpringBean("look.LookTypeDao")
    private LookTypeDao lookTypeDao;
    
    private String locale;
    
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
	@DefaultHandler
	public Resolution route() {
	    List<LookType> results = lookTypeDao.listByLocale(locale);
	    PageResult<LookType> pageResult = new PageResult<LookType>();
	    pageResult.setResults(results);
	    pageResult.setTotalSize(results.size());
		return json(pageResult);
	}
}
