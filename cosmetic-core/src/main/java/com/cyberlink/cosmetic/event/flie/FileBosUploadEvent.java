package com.cyberlink.cosmetic.event.flie;

import com.cyberlink.core.event.DurableEvent;

public class FileBosUploadEvent extends DurableEvent {

	private static final long serialVersionUID = -5841573419013123945L;

	private String url;

	private String path;
	
	private Long size;
	
	private String mimeType;

	public FileBosUploadEvent() {
		super(new Object());
	}

	public FileBosUploadEvent(String url, String path, Long size, String mimeType) {
		super(path);
		this.url = url;
		this.path = path;
		this.size = size;
		this.mimeType = mimeType;
	}

	public String getUrl() {
		return url;
	}

	public String getPath() {
		return path;
	}

	public Long getSize() {
		return size;
	}

	public String getMimeType() {
		return mimeType;
	}

	@Override
    public Boolean isGlobal() {
        return false;
    }
}