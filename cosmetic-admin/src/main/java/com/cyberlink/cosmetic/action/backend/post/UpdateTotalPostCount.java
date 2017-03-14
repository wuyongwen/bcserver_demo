package com.cyberlink.cosmetic.action.backend.post;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.integration.spring.SpringBean;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.service.PostTotalCountService;

@UrlBinding("/post/updateTotalPostCount.action")
public class UpdateTotalPostCount extends AbstractAction {

	@SpringBean("backend.PostTotalCountService")
	private PostTotalCountService postTotalCountService;

	@DefaultHandler
	public Resolution route() {
		if (!getCurrentUserAdmin()) {
			return new StreamingResolution("text/html", "Need to login");
		}
		try {
			postTotalCountService.exec();
			return new StreamingResolution("text/html", "success");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
	}

	public Resolution start() {
		if (!getCurrentUserAdmin()) {
			return new StreamingResolution("text/html", "Need to login");
		}
		try {
			postTotalCountService.start();
			return new StreamingResolution("text/html",
					"PostTotalCountService start success");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
	}

	public Resolution stop() {
		if (!getCurrentUserAdmin()) {
			return new StreamingResolution("text/html", "Need to login");
		}
		try {
			postTotalCountService.stop();
			return new StreamingResolution("text/html",
					"PostTotalCountService stop success");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
	}

	public Resolution status() {
		if (!getCurrentUserAdmin()) {
			return new StreamingResolution("text/html", "Need to login");
		}
		try {
			return new StreamingResolution("text/html",
					postTotalCountService.getStatus());
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new StreamingResolution("text/html", e.getMessage());
		}
	}
}