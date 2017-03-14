package com.cyberlink.cosmetic.modules.file.service.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.file.model.FileItem;
import com.cyberlink.cosmetic.modules.file.service.OSSService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class OSSServiceImpl extends AbstractService implements OSSService {
	
	private OSSClient client;
	
	private String bucket;

	@Override
	public void uploadFile(FileItem fileItem) throws FileNotFoundException {		
		InputStream targetStream = new FileInputStream(fileItem.getLocalFilePath());
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(fileItem.getFileSize());
		metadata.setContentType(fileItem.getContentType());
		client.putObject(bucket, fileItem.getFilePath(), targetStream, metadata);
	}
	
	@Override
	public void downloadFile(FileItem fileItem) {
		File targetFile =  new File(fileItem.getLocalFilePath());
		targetFile.getParentFile().mkdirs();
		client.getObject(new GetObjectRequest(bucket, fileItem.getFilePath()), targetFile);
    }

	public OSSClient getClient() {
		return client;
	}

	public void setClient(OSSClient client) {
		this.client = client;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

}