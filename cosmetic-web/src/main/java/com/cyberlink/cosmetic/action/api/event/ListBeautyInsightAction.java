package com.cyberlink.cosmetic.action.api.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


@UrlBinding("/api/v4.6/event/listBeautyBuzz.action")
public class ListBeautyInsightAction extends AbstractAction {

    private Long offset = Long.valueOf(0);
    private Long limit = Long.valueOf(10);
    private String locale = "en_US";
    
	@SpringBean("event.beautyInsightDao")
	protected BeautyInsightDao beautyInsightDao;

	@DefaultHandler
    public Resolution route() {
		if( offset < 0 ){
    		return new ErrorResolution(ErrorDef.InvalidOffset);
    	}
		if( limit < 0 || limit > 20 ){
    		return new ErrorResolution(ErrorDef.InvalidLimit);
    	}
		final Map<String, Object> results = new HashMap<String, Object>();
		List<BeautyInsight> beautyInsightResult = beautyInsightDao.listBeautyInsightByLocale(locale,offset,limit).getResults();
		
		List<BeautyInsightWrapper>  wrapperList = new ArrayList<BeautyInsightWrapper>();
		for( BeautyInsight beautyInsight: beautyInsightResult){
			wrapperList.add(new BeautyInsightWrapper(beautyInsight));
		}
		results.put("results", wrapperList);
		results.put("totalSize", beautyInsightResult.size());
		return json(results);
	}
	
	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

}
