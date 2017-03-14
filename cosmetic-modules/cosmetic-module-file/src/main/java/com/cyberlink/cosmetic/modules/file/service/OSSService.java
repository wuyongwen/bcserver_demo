package com.cyberlink.cosmetic.modules.file.service;

import java.io.FileNotFoundException;

import com.cyberlink.cosmetic.modules.file.model.FileItem;

public interface OSSService {
	
	void uploadFile(FileItem fileItem) throws FileNotFoundException;
	
	void downloadFile(FileItem fileItem);
}