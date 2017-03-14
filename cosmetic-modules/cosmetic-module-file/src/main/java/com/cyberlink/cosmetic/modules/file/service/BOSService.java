package com.cyberlink.cosmetic.modules.file.service;

import net.sourceforge.stripes.action.FileBean;

public interface BOSService {
	
	String uploadFile(String fileUrl, String path, Long size, String mimeType);
	String uploadFileByFileBean(FileBean fileBean, String apkType, String bucket);
	public Boolean isObjectExist(String bucket, String path);
	
}