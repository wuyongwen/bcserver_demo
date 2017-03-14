package com.cyberlink.cosmetic.action.backend;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/index.action")
public class IndexAction extends AbstractAction {
	
	@DefaultHandler
    public Resolution route() {
        return forward();
    }

}
