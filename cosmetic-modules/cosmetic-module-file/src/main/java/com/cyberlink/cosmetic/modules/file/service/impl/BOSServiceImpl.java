package com.cyberlink.cosmetic.modules.file.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.model.ObjectMetadata;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.cosmetic.modules.file.service.BOSService;

import net.sourceforge.stripes.action.FileBean;

public class BOSServiceImpl extends AbstractService implements BOSService {

	private BosClient client;

	private String bucket;
	
	@Override
	public String uploadFile(String fileUrl, String path, Long size, String mimeType) {
		try {
			URL url = new URL(fileUrl);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(10000);
			InputStream urlStream = connection.getInputStream();
			
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(size);
			meta.setContentType(mimeType);
			
			client.putObject(bucket, path, urlStream, meta);
			urlStream.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	@Override
	public String uploadFileByFileBean(FileBean fileBean, String apkType, String bucket) {
		InputStream is = null;
		try {
			is = fileBean.getInputStream();
			String prefix = "apk/" + apkType + "/";
			String fileName = fileBean.getFileName();
			String path = prefix + fileName;
			
			Boolean isExist = isObjectExist(bucket, path);
			if(isExist == null || isExist)
				return null;

			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(fileBean.getSize());
			meta.setContentType(fileBean.getContentType());
			
			client.putObject(bucket, path, is, meta);
			return "http://cdn.perfectcorp.cn/" + path;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				is.close();
				fileBean.delete();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		return null;
	}
	
	@Override
	public Boolean isObjectExist(String bucket, String path){
		try {
			ObjectMetadata meta = client.getObjectMetadata(bucket, path);
			if (meta != null)
				return true;
		} catch (Exception e) {
			if(e.getMessage().contains("Not Found"))
				return false;
			logger.error(e.getMessage());
		}
		return null;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public void setClient(BosClient client) {
		this.client = client;
	}

}