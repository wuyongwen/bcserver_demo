package com.cyberlink.cosmetic.action.api.circle;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.modules.circle.model.Circle;
import com.cyberlink.cosmetic.modules.circle.model.Circle.ListCicleView;

@UrlBinding("/api/v4.2/circle/list-circle.action")
public class ListCircleAction_v4_2 extends ListCircleAction {

	@DefaultHandler
	public Resolution route() {
	    PageResult<Circle> results = getCircle();
        return json(results, ListCicleView.class);
	}
}
