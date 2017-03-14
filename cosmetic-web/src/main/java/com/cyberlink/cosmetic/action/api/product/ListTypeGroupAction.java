package com.cyberlink.cosmetic.action.api.product;

import java.util.HashMap;
import java.util.Map;

import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.modules.product.dao.TypeGroupDao;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/product/list-typeGroup.action")
public class ListTypeGroupAction extends AbstractAction {

	@SpringBean("product.TypeGroupDao")
	protected TypeGroupDao groupDao;

	@DefaultHandler
    public Resolution route() {
    	final Map<String, Object> results = new HashMap<String, Object>();

    	results.put("results", groupDao.findAll());
    	return json(results);
    }
	
}
