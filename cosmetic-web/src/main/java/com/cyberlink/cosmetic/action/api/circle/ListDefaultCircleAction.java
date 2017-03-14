package com.cyberlink.cosmetic.action.api.circle;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.error.ErrorDef;
import com.cyberlink.cosmetic.error.ErrorResolution;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.service.CircleService;

@UrlBinding("/api/v4.2/circle/list-default-circle.action")
public class ListDefaultCircleAction extends AbstractCircleAction {
    
	@SpringBean("circle.circleService")
	private CircleService circleService;
	
    private String locale ;
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@DefaultHandler
	public Resolution getDefaultCircle() {	
        if (locale == null)
            return new ErrorResolution(ErrorDef.BadRequest);
        
        List<Circle> defaultCircles = circleService.getBcDefaultCircle(locale);
        if(defaultCircles == null)
            return new ErrorResolution(ErrorDef.UnknownCircleError);
        
        PageResult<Circle> result = new PageResult<Circle>();
        result.setResults(defaultCircles);
        result.setTotalSize(defaultCircles.size());
        return json(result);
	}
}
