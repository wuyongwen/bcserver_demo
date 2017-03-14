package com.cyberlink.cosmetic.action.backend.service;

public interface AutoCheckAndImportNewProductsService {

	void start();

	void stop();

	String getStatus();

	void exec();
	
	void onShelf();
	
	void offShelf();
	
	void setUploadFileNow();
	
	void setNotUploadFileNow();
	
	String getOnShelfStatus();
}
