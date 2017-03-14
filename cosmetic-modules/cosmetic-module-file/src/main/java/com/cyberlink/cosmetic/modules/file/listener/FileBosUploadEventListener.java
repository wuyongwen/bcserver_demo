package com.cyberlink.cosmetic.modules.file.listener;

import com.cyberlink.core.event.impl.AbstractEventListener;
import com.cyberlink.cosmetic.event.flie.FileBosUploadEvent;
import com.cyberlink.cosmetic.modules.file.service.BOSService;

public class FileBosUploadEventListener extends
		AbstractEventListener<FileBosUploadEvent> {

	private BOSService bosService;

	public void setBOSService(BOSService bosService) {
		this.bosService = bosService;
	}

	@Override
	public void onEvent(final FileBosUploadEvent e) {
		bosService.uploadFile(e.getUrl(), e.getPath(), e.getSize(), e.getMimeType());
	}

}