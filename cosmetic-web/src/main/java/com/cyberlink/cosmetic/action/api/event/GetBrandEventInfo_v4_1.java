package com.cyberlink.cosmetic.action.api.event;

import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.cyberlink.cosmetic.modules.event.model.BrandEvent.InfoBrandEventView_v4_1;

@UrlBinding("/api/v4.1/event/get-brand-event-info.action")
public class GetBrandEventInfo_v4_1 extends GetBrandEventInfo {
	
	@DefaultHandler
    public Resolution route() {
	    final Map<String, Object> results = getEventInfo(brandEventId, curUserId);
        return json(results, InfoBrandEventView_v4_1.class);
	}
}