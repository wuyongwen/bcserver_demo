package com.cyberlink.cosmetic.action.api.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.action.api.AbstractAction;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.event.dao.BeautyInsightDao;
import com.cyberlink.cosmetic.modules.event.model.BeautyInsight;
import com.cyberlink.cosmetic.modules.event.model.result.BeautyInsightWrapper;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

@UrlBinding("/api/v4.6/event/getBeautyBuzzInfo.action")
public class GetBeautyInsightInfoAction  extends AbstractAction {

    private Long id;
    
	@SpringBean("event.beautyInsightDao")
	protected BeautyInsightDao beautyInsightDao;

	@DefaultHandler
    public Resolution route() {
		if( id == null){
			return new ErrorResolution(ErrorDef.InvalidId);
		}
		if(beautyInsightDao.exists(id)){
			return json("result", new BeautyInsightWrapper(beautyInsightDao.findById(id)));
		}else{
			return new ErrorResolution(ErrorDef.InvalidId);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
