package com.cyberlink.cosmetic.action.backend.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.helper.StringUtil;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cyberlink.core.scheduling.quartz.annotation.BackgroundJob;
import com.cyberlink.core.service.AbstractService;
import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.core.web.view.page.PageResult;
import com.cyberlink.cosmetic.Constants;
import com.cyberlink.cosmetic.action.backend.service.AutoCheckAndImportNewProductsService;
import com.cyberlink.cosmetic.modules.product.dao.BackendProductDao;
import com.cyberlink.cosmetic.modules.product.dao.BrandDao;
import com.cyberlink.cosmetic.modules.product.dao.BrandIndexDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductDao;
import com.cyberlink.cosmetic.modules.product.dao.ProductTypeDao;
import com.cyberlink.cosmetic.modules.product.dao.StoreDao;
import com.cyberlink.cosmetic.modules.product.dao.StorePriceRangeDao;
import com.cyberlink.cosmetic.modules.product.model.BackendProduct;
import com.cyberlink.cosmetic.modules.product.model.Brand;
import com.cyberlink.cosmetic.modules.product.model.BrandIndex;
import com.cyberlink.cosmetic.modules.product.model.Product;
import com.cyberlink.cosmetic.modules.product.model.ProductType;
import com.cyberlink.cosmetic.modules.product.model.Store;
import com.cyberlink.cosmetic.modules.product.model.StorePriceRange;
import com.cyberlink.cosmetic.modules.product.model.result.BrandIndexWrapper;
import com.cyberlink.cosmetic.modules.product.model.result.BrandWrapper;
import com.cyberlink.cosmetic.modules.product.model.result.ProductTypeWrapper;
import com.cyberlink.cosmetic.modules.product.service.BrandIndexService;
import com.cyberlink.cosmetic.modules.product.service.BrandService;
import com.cyberlink.cosmetic.modules.product.service.ProductService;
import com.cyberlink.cosmetic.modules.product.service.ProductTypeService;
import com.cyberlink.cosmetic.modules.product.service.RelProductTypeService;
import com.cyberlink.utility.*;
import com.cyberlink.store.ProductInfo;
import com.cyberlink.store.Amazon;
import com.cyberlink.store.Taobao;
import com.cyberlink.store.Yahoo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sourceforge.stripes.action.Resolution;

/**
 * @author Ben_Chen
 *
 */
public class AutoCheckAndImportNewProductsServiceImpl extends AbstractService
		implements AutoCheckAndImportNewProductsService {

	private TransactionTemplate transactionTemplate;
	protected ProductDao productDao;
	protected StoreDao storeDao;
	protected StorePriceRangeDao storePriceRangeDao;
	protected ProductService productService;
	protected RelProductTypeService relProductTypeService;
	protected BackendProductDao backendProductDao;
	protected BrandDao brandDao;
	protected BrandIndexDao brandIndexDao;
	protected ObjectMapper objectMapper;
	protected BrandService brandService;
	protected BrandIndexService brandIndexService;
	protected ProductTypeDao productTypeDao;
	protected ProductTypeService productTypeService;

	private String filePath = "";
	private String logFileFullPath = null;
	private String prodJsonFilePath = null;
	private String prodRecordFilePath = null;
	
	static final String CRONEXPRESSION = "0 0 7 * * ? *";
	static private Boolean isRunning = Boolean.TRUE;
	static private Boolean isUploadFileNow = Boolean.FALSE;
	static private String onShelf = "true";

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * Map<BrandIndex, Map<BrandName, BrandID>>
	 */
	Map<String, Map<String, String>> brandMap = null;
	/**
	 * Map<typeName ,typeID>
	 */
	Map<String, String> typeMap = null;
	
	@Override
	public void onShelf() {
		onShelf = "true";
	}
	
	@Override
	public void offShelf() {
		onShelf = "false";
	}
	
	@Override
	public String getOnShelfStatus(){
		return onShelf;
	}
	
	@Override
	public void start() {
		isRunning = Boolean.TRUE;
	}

	@Override
	public void stop() {
		isRunning = Boolean.FALSE;
	}
	
	@Override
	public void setUploadFileNow(){
		isUploadFileNow = true;
	};
	
	@Override
	public void setNotUploadFileNow(){
		isUploadFileNow = false;
	};

	@Override
	public String getStatus() {
		if (!isRunning)
			return "AutoCheckAndImportNewProductsService isn't running";
		else
			return "AutoCheckAndImportNewProductsService is running";
	}

	@Override
	@BackgroundJob(cronExpression = CRONEXPRESSION)
	public void exec() {
		if (!isRunning) {
			logger.info("AutoCheckAndImportNewProductsService isn't running");
			return;
		} else
			logger.info("AutoCheckAndImportNewProductsService is running");
		do{
			int count = 0;
			if(isUploadFileNow){
				try {
					Thread.sleep(180000);
					count++;
				} catch (InterruptedException e) {}
				if(count >= 3){
					isUploadFileNow = Boolean.FALSE;
					logger.info("upload file is too late");
					return;
				}
			}
		}while(isUploadFileNow);

		filePath = Constants.getUploadJsonStringPath();
		logFileFullPath = Tool.pathJoin(filePath, "productScraperLog.txt");
		prodJsonFilePath = Tool.pathJoin(filePath, "_store", "_product");
		prodRecordFilePath = Tool.pathJoin(filePath, "_store", "_record");
		Tool.makeDir(prodJsonFilePath);
		Tool.makeDir(prodRecordFilePath);
		
		//Complete the unfinished work
		if(hasProdInfoFile())
			createOrUpdateProdFromFileToDB();
		compareAndPrintWithSearchProdInfo();
		createOrUpdateProdFromFileToDB();

		logger.info("AutoCheckAndImportNewProductsService completed");
	}
	
	private Boolean hasProdInfoFile(){
		if(Tool.isFileExist(prodJsonFilePath)){
			return (new File(prodJsonFilePath).listFiles().length > 0);
		}else{
			Tool.makeDir(prodJsonFilePath);
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void compareAndPrintWithSearchProdInfo() {
		File rootFile = new File(Tool.pathJoin(filePath, "_store", "_searchProduct"));
		if (rootFile.exists()) {
			File[] storeFiles = rootFile.listFiles();
			if (storeFiles.length > 0) {
				for (File storeFile : storeFiles) {
					Date localStoreStartTime = new Date();

					//Initial parameters
					//Map<typeName ,typeID>
					typeMap = null;
					//Map<BrandIndex, Map<BrandName, BrandID>>
					brandMap = null;
					JSONObject jsonCreateProductInfo = new JSONObject();
					JSONObject jsonUpdateProductInfo = new JSONObject();
					Tool.delAllFiles(new File(Tool.pathJoin(filePath, "_store", "_brand")));
					Tool.delAllFiles(new File(Tool.pathJoin(filePath, "_store", "_prodType")));
					long lCreateItemNum = 0;
					long lUpdateItemNum = 0;

					String locale = storeFile.getName();
					String storeId = "";
					
					//because can't use brand name or type name be file name,encode in tool and decode in backend service
					Map<String,String> indexMap = new HashMap<String,String>();
					Map<String,String> brandIndexMap = new HashMap<String,String>();
					Map<String,String> brandNameMap = new HashMap<String,String>();
					Map<String,String> typeNameMap = new HashMap<String,String>();
					Map<String,String> keyNameMap = new HashMap<String,String>();
					if(!readProductRelated(locale,indexMap,brandIndexMap,brandNameMap,typeNameMap,keyNameMap)){
						logger.info("readProductRelated fail, locale = " + locale);
						continue;
					}

					switch (locale) {
					case "zh_TW":
						storeId = "1";
						break;
					case "zh_CN":
						storeId = "2";
						break;
					case "en_US":
						storeId = "3";
						break;
					case "de_DE":
						storeId = "4";
						break;
					case "fr_FR":
						storeId = "5";
						break;
					case "en_GB":
						storeId = "6";
						break;
					case "en_CA":
						storeId = "7";
						break;
					case "ja_JP":
						storeId = "8";
						break;
					default:
						storeId = "1";
						break;
					}

					Amazon an = new Amazon(locale, filePath);
					Yahoo yo = new Yahoo(filePath);
					Taobao ta = new Taobao(filePath);

					File[] IndexFiles = storeFile.listFiles();
					if (IndexFiles.length <= 0) 
						continue;
					for (File indexFile : IndexFiles) {
						String indexNameId = indexFile.getName();
						String indexName = indexMap.get(indexNameId);
						if(indexName == null){
							logger.info("getIndexName fail, indexName = " + indexName + " , locale = " + locale);
							continue;
						}
						ArrayList<String> indexNameList = new ArrayList<>();
						Collections.addAll(indexNameList, indexName.split("\\s*,\\s*"));

						File[] brandIndexFiles = indexFile.listFiles();
						if (brandIndexFiles.length <= 0) 
							continue;
						for (File brandIndexFile : brandIndexFiles) {
							String brandIndexId = brandIndexFile.getName();
							String brandIndex = brandIndexMap.get(brandIndexId);
							if(brandIndex == null){
								logger.info("getBrandIndex fail, brandIndex = " + brandIndex + " , locale = " + locale);
								continue;
							}
							File[] brandFiles = brandIndexFile.listFiles();
							for (File brandFile : brandFiles) {
								String brandNameId = brandFile.getName();
								String brandName = brandNameMap.get(brandNameId);
								String brandId = getBrandId(locale, brandIndex, brandName);
								if(brandId == null || brandName == null){
									logger.info("getBrandId fail, brandName = " + brandName + " , locale = " + locale);
									continue;
								}
								File[] typeFiles = brandFile.listFiles();
								if (typeFiles.length <= 0)
									continue;
								for (File typeFile : typeFiles) {
									String categoryNameId = typeFile.getName();
									String categoryName = typeNameMap.get(categoryNameId);
									String typeId = getTypeId(locale, categoryName);
									if(typeId == null || categoryName == null){
										logger.info("getTypeId fail, typeName = " + categoryName + " , locale = " + locale);
										continue;
									}
									Map<String, Map<String, String>> prodMap = new HashMap<String, Map<String, String>>();
									getServerProdList(prodMap, locale, brandId, typeId);
									deleteNotSaleProdFromDB(locale, prodMap, brandId, typeId);
									
									File[] typeKeyFiles = typeFile.listFiles();
									if (typeKeyFiles.length <= 0) 
										continue;
									for (File typeKeyFile : typeKeyFiles) {
										String typeKeyNameId = typeKeyFile.getName();
										String typeKeyName = keyNameMap.get(typeKeyNameId);
										if(typeKeyName == null){
											logger.info("getTypeKeyName fail, typeKeyName = " + typeKeyName + " , locale = " + locale);
											continue;
										}
										ArrayList<ProductInfo> infoList = new ArrayList<ProductInfo>();
										String productFilePath = Tool.pathJoin(filePath , "_store" , "_searchProduct", locale , indexNameId , brandIndexId , brandNameId, categoryNameId, typeKeyNameId);
										if (locale.equalsIgnoreCase("en_US")|| locale.equalsIgnoreCase("de_DE")
												|| locale.equalsIgnoreCase("fr_FR")|| locale.equalsIgnoreCase("en_GB")
												|| locale.equalsIgnoreCase("en_CA")|| locale.equalsIgnoreCase("ja_JP")) {
											infoList = an.search(indexName, brandName, categoryName, typeKeyName, null, brandIndex, productFilePath);
											if (infoList.size() <= 0) 
												continue;
											for (ProductInfo info : infoList) {
												String extProdId = info.getId();
												int newPrice = Integer.valueOf(info.getPrice());
												String price = String.format("%.2f",(float) newPrice / 100);
												if (!prodMap.containsKey(extProdId)) {
													if (isWrongProdInfo(locale, false, null, info, brandId, typeId))
														continue;
													info.setPrice(price);
													info.setBrandID(brandId);
													info.setTypeID(typeId);
													info.setStoreID(storeId);
													info.setPkID("");
													lCreateItemNum++;
													jsonCreateProductInfo.putAll(getProdMapObjFromProdInfo(lCreateItemNum,locale, info, onShelf));
												} else if (prodMap.get(extProdId).get("isDeleted").equals("false")) {
													if (isWrongProdInfo(locale, true,prodMap.get(extProdId), null, brandId, typeId))
														continue;
													BigDecimal bd = new BigDecimal(prodMap.get(extProdId).get("price"));
													bd = bd.multiply(new BigDecimal("100"));
													int oldPrice = bd.intValue();
													if (oldPrice != newPrice) {
														lUpdateItemNum++;
														info.setPkID(prodMap.get(extProdId).get("productId"));
														info.setPrice(price);
														info.setBrandID(brandId);
														info.setTypeID(typeId);
														info.setStoreID(storeId);
														jsonUpdateProductInfo.putAll(getProdMapObjFromProdInfo(lUpdateItemNum, locale,info, onShelf));
													}
												}
											} // for infoList
										} else {
											//infoList sourced from productScraper tool upload file
											if (locale.equalsIgnoreCase("zh_TW")) {
												infoList = yo.search(indexNameList,indexName, brandName, categoryName,typeKeyName, brandIndex, productFilePath);
											} else if (locale.equalsIgnoreCase("zh_CN")) {
												infoList = ta.search(indexNameList,indexName, brandName, categoryName,typeKeyName, brandIndex, productFilePath);
											}
											if (infoList.size() <= 0)
												continue;
											for (ProductInfo info : infoList) {
												String extProdId = info.getId();
												BigDecimal bdNew = new BigDecimal(info.getPrice());
												int newPrice = bdNew.intValue();
												String price = String.valueOf(newPrice);
												if (!prodMap.containsKey(info.getId())) {
													if (isWrongProdInfo(locale, false, null, info, brandId, typeId))
														continue;
													info.setPrice(price);
													info.setBrandID(brandId);
													info.setTypeID(typeId);
													info.setStoreID(storeId);
													lCreateItemNum++;
													info.setPkID("");
													jsonCreateProductInfo.putAll(getProdMapObjFromProdInfo(lCreateItemNum,locale, info, onShelf));
												} else if (prodMap.get(extProdId).get("isDeleted").equals("false")) {
													if (isWrongProdInfo(locale, true,prodMap.get(extProdId), null, brandId, typeId))
														continue;
													BigDecimal bdOld = new BigDecimal(prodMap.get(extProdId).get("price"));
													int oldPrice = bdOld.intValue();
													if (oldPrice != newPrice) {
														lUpdateItemNum++;
														info.setPkID(prodMap.get(extProdId).get("productId"));
														info.setPrice(price);
														info.setBrandID(brandId);
														info.setTypeID(typeId);
														info.setStoreID(storeId);
														jsonUpdateProductInfo.putAll(getProdMapObjFromProdInfo(lUpdateItemNum, locale,info, onShelf));
													}
												}
											}
										}
									} //for typeKeyFiles
								} //for typeFiles
							}
						}
					}
					writeJasonObjListToFile("Create-" + locale , jsonCreateProductInfo,prodJsonFilePath);
					writeJasonObjListToFile("Update-" + locale , jsonUpdateProductInfo,prodJsonFilePath);
					Date localStoreEndTime = new Date();
					Tool.delFile(Tool.pathJoin(filePath, locale + "_product.zip"));
					Tool.delAllFiles(new File(Tool.pathJoin(filePath,"_store","_searchProduct",locale )));
					Tool.delFile(Tool.pathJoin(filePath, "_store", "_searchProductRelated",String.format("%s.txt", locale)));
					String message = "Create product number:" + lCreateItemNum + ";Update product number:" + lUpdateItemNum + "\r\n";
					Debug.printInfoDiffTimeAndWriteToFile(message + locale +" store compare product data spend time", localStoreStartTime,localStoreEndTime, logFileFullPath);
				}
			}
		}
	}
	
	private Boolean readProductRelated(String locale, Map<String,String> indexMap,Map<String,String> brandIndexMap,Map<String,String> brandNameMap,
			Map<String,String> typeNameMap,Map<String,String> keyNameMap){
		String productRelatedInfoFullPath = Tool.pathJoin(filePath, "_store", "_searchProductRelated", String.format("%s.txt", locale));
		if(!Tool.isFileExist(productRelatedInfoFullPath))
			return false;
		String productRelatedInfo = Tool.readFileToString(productRelatedInfoFullPath);
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = new JSONObject();
		try {jsonObject = (JSONObject) jsonParser.parse(productRelatedInfo);
		} catch (ParseException e) {}
		setMapByJSONObject("index",indexMap,jsonObject);
		setMapByJSONObject("brandIndex",brandIndexMap,jsonObject);
		setMapByJSONObject("brandName",brandNameMap,jsonObject);
		setMapByJSONObject("typeName",typeNameMap,jsonObject);
		setMapByJSONObject("keyName",keyNameMap,jsonObject);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void setMapByJSONObject(String targetName,Map<String,String> targetMap,JSONObject jsonObject){
		JSONObject targetObject =(JSONObject)jsonObject.get(targetName);
		Set<String> targetkeySet = targetObject.keySet();
		if(targetkeySet.size() > 0){
			for(String targetkey: targetkeySet){
				targetMap.put(targetObject.get(targetkey).toString(), targetkey);
			}
		}
	}
	
	private void createOrUpdateProdFromFileToDB(){
		List<String> creatProductFilePathList = new LinkedList<String>();
		List<String> updateProductFileNameList = new LinkedList<String>();
		for (File prodFile : new File(prodJsonFilePath).listFiles()) {
			if (prodFile.getName().split("-")[0].equals("Create"))
			{
				creatProductFilePathList.add(prodFile.getAbsolutePath());
			}
			else if (prodFile.getName().split("-")[0].equals("Update"))
			{
				updateProductFileNameList.add(prodFile.getAbsolutePath());
			}
		}
		String result = null;
		String messageLog = null;
		
		for(String creatProductFilePath : creatProductFilePathList){
			Date createProdStartTime = new Date();
			String region = creatProductFilePath.substring(creatProductFilePath.lastIndexOf("-")+1,creatProductFilePath.lastIndexOf("."));
			List<Brand> brandList = brandDao.listAllBrandByLocale(region);
			//Map<BrandID,Brand>
			final Map<String,Brand> brandMap = new HashMap<String,Brand>();
			for(Brand brand: brandList){
				brandMap.put(brand.getId().toString(), brand);
			}
			List<Store> storeList = storeDao.findAll();
			//Map<StoreID,Store>
			final Map<String,Store> storeMap = new HashMap<String,Store>();
			for(Store store: storeList){
				storeMap.put(store.getId().toString(), store);
			}
			final List<StorePriceRange> storePriceRangeList = storePriceRangeDao.listAllPriceRangeByLocale(region);
			result = Tool.readFileToString(creatProductFilePath);
			List<ProductInfo> createProductInfoList = getProductInfoListByJsonString(result);
			int createProdTotalNum = createProductInfoList.size();
			//get record create count
			int createCount = getRecordCount(region,"create");
			//if record number > 0 , subList by record count
			Map<String,Map<String,List<ProductInfo>>> createProductInfoMap = null;
			if(createCount == 0)
				createProductInfoMap = getProdInfoListToMap(createProductInfoList);
			else 
				createProductInfoMap = getProdInfoListToMap(createProductInfoList.subList(createCount,createProductInfoList.size()));

			for(String brandID : createProductInfoMap.keySet()){
				for(String typeID : createProductInfoMap.get(brandID).keySet()){
					List<Product> productList = createProduct(createProductInfoMap.get(brandID).get(typeID),brandMap,storeMap,storePriceRangeList);
					//updateProductDataInTxt
					Map<String, Map<String, String>> prodMap = new HashMap<String, Map<String, String>>();
					getServerProdList(prodMap, region, brandID, typeID);
					String productInfoFullPath = Tool.pathJoin(filePath, "_store", "_searchServerProduct",region,brandID,typeID,"productInfo.txt");
					saveNewProductToDB(productInfoFullPath,prodMap ,productList);
					//updateCount
					createCount += createProductInfoMap.get(brandID).get(typeID).size();
					saveRecordCount(region,"create",createCount);
					
				}
			}
			//delete record count
			deleteRecordCount(region,"create");
			
			Date createProdEndTime = new Date();
			messageLog = creatProductFilePath.split("_")[3] + " store create number:" + createProdTotalNum + "\r\n";
			Debug.printInfoDiffTimeAndWriteToFile(messageLog + " spend time", createProdStartTime,createProdEndTime, logFileFullPath);
			deleteFile(creatProductFilePath);
		}
		
		for(String UpdateProductFilePath : updateProductFileNameList){
			Date updateProdStartTime = new Date();
			String region = UpdateProductFilePath.substring(UpdateProductFilePath.lastIndexOf("-")+1,UpdateProductFilePath.lastIndexOf("."));
			final List<StorePriceRange> storePriceRangeList = storePriceRangeDao.listAllPriceRangeByLocale(region);
			result = Tool.readFileToString(UpdateProductFilePath);
			List<ProductInfo> updateProductInfoList = getProductInfoListByJsonString(result);
			int updateProdNum = updateProductInfoList.size();
			//get record create count
			int updateCount = getRecordCount(region,"update");
			//if record number > 0 , subList by record count
			Map<String,Map<String,List<ProductInfo>>> updateProductInfoMap = null;
			if(updateCount == 0)
				updateProductInfoMap = getProdInfoListToMap(updateProductInfoList);
			else 
				updateProductInfoMap = getProdInfoListToMap(updateProductInfoList.subList(updateCount,updateProductInfoList.size()));
			for(String brandID : updateProductInfoMap.keySet()){
				for(String typeID : updateProductInfoMap.get(brandID).keySet()){
					updateProduct(updateProductInfoList,storePriceRangeList);
					//UpdateProductDataInTxt
					Map<String, Map<String, String>> prodMap = new HashMap<String, Map<String, String>>();
					getServerProdList(prodMap, region, brandID, typeID);
					String productInfoFullPath = Tool.pathJoin(filePath, "_store", "_searchServerProduct",region,brandID,typeID,"productInfo.txt");
					updateProductToDB(productInfoFullPath,prodMap ,updateProductInfoMap.get(brandID).get(typeID));
					//UpdateCount
					updateCount += updateProductInfoMap.get(brandID).get(typeID).size();
					saveRecordCount(region,"update",updateCount);
				}
			}
			//delete record count
			deleteRecordCount(region,"update");
			
			Date updateProdEndTime = new Date();
			messageLog = UpdateProductFilePath.split("_")[3] + " store update number:" + updateProdNum + "\r\n";
			Debug.printInfoDiffTimeAndWriteToFile(messageLog + " spend time", updateProdStartTime,updateProdEndTime, logFileFullPath);
			deleteFile(UpdateProductFilePath);
		}
	}
	
	private int getRecordCount(String region,String mode){
		 String strCount = Tool.readFileToString(Tool.pathJoin(prodRecordFilePath,mode + "_" + region + ".txt"));
		 if(strCount.isEmpty())
			 return 0;
		 else
			 return Integer.parseInt(strCount);
	}
	
	private void saveRecordCount(String region ,String mode,int count){
		Tool.writeStringToFile(Tool.pathJoin(prodRecordFilePath,mode + "_" + region + ".txt"), String.valueOf(count), false);
	}
	
	private void deleteRecordCount(String region ,String mode){
		Tool.delFile(Tool.pathJoin(prodRecordFilePath,mode + "_" + region + ".txt"));
	}
	
	private void saveNewProductToDB(String fullFilePath,Map<String, Map<String, String>> prodMap ,List<Product> productList){
		if(productList.size() == 0)
			return;
		StringBuffer productData = new StringBuffer();  
		for(String exprodID : prodMap.keySet()){
			Map<String, String> tempProdInfo = prodMap.get(exprodID);
			productData.append(tempProdInfo.get("productId")).append("&&")
			.append(tempProdInfo.get("price")).append("&&")
			.append(tempProdInfo.get("displayTitle")).append("&&")
			.append(tempProdInfo.get("isDeleted")).append("&&")
			.append(tempProdInfo.get("productStoreLink")).append("&&")
			.append(tempProdInfo.get("imgThumbnail")).append("&&")
			.append(tempProdInfo.get("extPID")).append("@@");
		}
		for(Product product : productList){
			productData.append(product.getId()).append("&&")
			.append(product.getPrice()).append("&&")
			.append(product.getProductTitle()).append("&&")
			.append(product.getIsDeleted()).append("&&")
			.append(product.getProductStoreLink()).append("&&")
			.append(product.getImg_thumbnail()).append("&&")
			.append(product.getExtProdID()).append("@@");
		}
		Tool.writeStringToFile(fullFilePath,productData.substring(0,productData.length()-2).toString(),false);
	}
	
	private void updateProductToDB(String fullFilePath,Map<String, Map<String, String>> prodMap ,List<ProductInfo> productInfoList){
		if(productInfoList.size() == 0)
			return;
		for(ProductInfo productInfo : productInfoList){
			prodMap.get(productInfo.getId()).put("price", productInfo.getPrice());
		}
		StringBuffer productData = new StringBuffer();  
		for(String exprodID : prodMap.keySet()){
			Map<String, String> tempProdInfo = prodMap.get(exprodID);
			productData.append(tempProdInfo.get("productId")).append("&&")
			.append(tempProdInfo.get("price")).append("&&")
			.append(tempProdInfo.get("displayTitle")).append("&&")
			.append(tempProdInfo.get("isDeleted")).append("&&")
			.append(tempProdInfo.get("productStoreLink")).append("&&")
			.append(tempProdInfo.get("imgThumbnail")).append("&&")
			.append(tempProdInfo.get("extPID")).append("@@");
		}
		Tool.writeStringToFile(fullFilePath,productData.substring(0,productData.length()-2).toString(),false);
	}
	
	
	private void deleteProductsFromLocaleDB(List<String> prodExtIdsList,String locale,String brandId,String typeId){
		if(prodExtIdsList.isEmpty())
			return;
		try{
			String productInfoFullPath = Tool.pathJoin(filePath, "_store", "_searchServerProduct",locale,brandId,typeId,"productInfo.txt");
			Map<String, Map<String, String>> prodMapInTXT = new HashMap<String, Map<String, String>>();
			getServerProdList(prodMapInTXT, locale, brandId, typeId);
			for(String prodExtId : prodExtIdsList){
				prodMapInTXT.remove(prodExtId);
			}
			StringBuffer productData = new StringBuffer();  
			for(String exprodID : prodMapInTXT.keySet()){
				Map<String, String> tempProdInfo = prodMapInTXT.get(exprodID);
				productData.append(tempProdInfo.get("productId")).append("&&")
				.append(tempProdInfo.get("price")).append("&&")
				.append(tempProdInfo.get("displayTitle")).append("&&")
				.append(tempProdInfo.get("isDeleted")).append("&&")
				.append(tempProdInfo.get("productStoreLink")).append("&&")
				.append(tempProdInfo.get("imgThumbnail")).append("&&")
				.append(tempProdInfo.get("extPID")).append("@@");
			}
			String prodInfo = "";
			if(productData.length() > 2)
				prodInfo = productData.substring(0,productData.length()-2).toString();
			Tool.writeStringToFile(productInfoFullPath, prodInfo, false);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Combine prodInfo List To Map
	 * @param productInfoList
	 * @return Map<brandID,Map<typeID,List<ProductInfo>>>
	 */
	public Map<String,Map<String,List<ProductInfo>>> getProdInfoListToMap(List<ProductInfo> productInfoList){
		//Map<BrandID,Map<String,ProductInfo>>
		Map<String,Map<String,List<ProductInfo>>> productInfoMap = new HashMap<String,Map<String,List<ProductInfo>>>();
		for(ProductInfo productInfo : productInfoList){
			String brandID = productInfo.getBrandID();
			String typeID = productInfo.getTypeID();
			if(!productInfoMap.containsKey(brandID))
				productInfoMap.put(brandID,new HashMap<String,List<ProductInfo>>());
			if(!productInfoMap.get(brandID).containsKey(typeID))
				productInfoMap.get(brandID).put(typeID,new LinkedList<ProductInfo>());
			productInfoMap.get(brandID).get(typeID).add(productInfo);
		}
		return productInfoMap;
	}
	
	private void deleteFile(String productFilePath){
		try{
			File file = new File(productFilePath);
			if(!file.delete())
				logger.error("Delete operation is failed.Path:"+productFilePath);
		}catch(Exception e){
			logger.error("Delete operation is failed.Path:"+productFilePath);
		}
	}

	public String getTypeId(String locale, String typeName) {
		String typeId = null;
		if (typeMap == null) {
			typeMap = new HashMap<String, String>();
			typeMap = getServerTypeList(locale);
		}
		if (typeMap.containsKey(typeName))
			typeId = typeMap.get(typeName);
		return typeId;
	}

	public void createProdType(String locale, String typeName) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		productTypeService.createOrUpdate(typeName, locale);
	}

	@SuppressWarnings("rawtypes")
	private Map<String, String> getServerTypeList(String locale) {

		String path = Tool.pathJoin(filePath, "_store", "_prodType");
		Tool.makeDir(path);
		String filePath = Tool.pathJoin(path, String.format("%s.json", locale));

		String result = "error";

		if (Tool.isFileExist(filePath)) {
			result = Tool.readFileToString(filePath);
		} else {
			result = getTypeByJson(locale);
			Tool.writeStringToFile(filePath, result, false);
		}

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = (JSONObject) jsonParser.parse(result);
		} catch (ParseException e) {
		}
		JSONArray lang = (JSONArray) jsonObject.get("results");
		Iterator bi = lang.iterator();
		while (bi.hasNext()) {
			JSONObject biObj = (JSONObject) bi.next();
			String key = biObj.get("typeName").toString();
			String value = biObj.get("typeId").toString();
			typeMap.put(key, value);
		}
		return typeMap;
	}

	private String getTypeByJson(String locale) {
		final Map<String, Object> results = new HashMap<String, Object>();
		List<ProductType> typeSearchResult = productTypeDao.listAllProdTypeByLocale(locale);
		List<ProductTypeWrapper> wrapperList = new ArrayList<ProductTypeWrapper>();
		for (ProductType type : typeSearchResult) {
			wrapperList.add(new ProductTypeWrapper(type));
		}
		results.put("totalSize", wrapperList.size());
		results.put("results", wrapperList);
		try {
			return objectMapper.writerWithView(Views.Public.class).writeValueAsString(results);
		} catch (JsonProcessingException e) {
		}
		return null;
	}

	private List<Product> createProduct(List<ProductInfo> productInfoList,final Map<String,Brand> brandMap ,final Map<String,Store> storeMap ,final List<StorePriceRange> storePriceRangeList) {
		final List<Product> newProductList = new ArrayList<Product>();
		int offset = 0;
		int limit = 100;
		do {
			if (productInfoList.size() <= 0)
				break;
			final List<ProductInfo> productInfos = productInfoList.subList(offset,Math.min(offset + limit, productInfoList.size()));
			try {
				transactionTemplate.execute(new TransactionCallback<Resolution>() {
					@Override
					public Resolution doInTransaction(TransactionStatus status) {
						for (ProductInfo productInfo : productInfos) {
							Product ExistProduct = productDao.findByBrandIdExtProdID_StoreID(Long.parseLong(productInfo.getBrandID()), productInfo.getId(),
									Long.parseLong(productInfo.getStoreID()), new Long[] {Long.parseLong(productInfo.getTypeID())} );
							Product newProduct = ExistProduct;
							if(ExistProduct == null){
								newProduct = new Product();
								newProduct.setLocale(productInfo.getLocale());							//locale
								newProduct.setBrand(brandMap.get(productInfo.getBrandID()));			//brandID
								newProduct.setStore(storeMap.get(productInfo.getStoreID()));			//storeID
								newProduct.setTypeGroupId(0L);											//typeGroupID
								newProduct.setProductName(null);										//ProductName
								newProduct.setProductTitle(productInfo.getTitle());						//ProductTitle
								newProduct.setProductDescription(productInfo.getDescription());			//Description
								newProduct.setImg_original(productInfo.getImgOriginal());				//Img_original
								newProduct.setImg_thumbnail(productInfo.getImgThumb());					//Img_thumbnail
								newProduct.setBarCode(0L);												//barcode
								newProduct.setProductStoreLink(productInfo.getLink());					//productStoreLink
								newProduct.setPrice(Float.parseFloat(productInfo.getPrice()));			//price
								newProduct.setExtProdID(productInfo.getId());							//extProdID
								newProduct.setOnShelf(Boolean.parseBoolean(productInfo.getOnShelf()));	//OnShelf
								newProduct.setTrialOnYCMakeUp(null);									//TrialOnYMK
								setPriceString(newProduct);
								for(StorePriceRange storePriceRange :storePriceRangeList){
									if(storePriceRange.isInPriceRange(Float.parseFloat(productInfo.getPrice()))){
										newProduct.setPriceRange(storePriceRange);							//StorePriceRange
										break;
									}
								}
								newProduct = productDao.create(newProduct);
								newProductList.add(newProduct);
							}
							relProductTypeService.createOrUpdate(newProduct.getId(),Long.parseLong(productInfo.getTypeID()));
						}
						return null;
					}
				});
			} catch (Exception e) {
				StringBuffer errorMsg = new StringBuffer(" Fail create extProdID:");
				for (ProductInfo productInfo : productInfos) {
					errorMsg.append(productInfo.getId() + "_");
				}
				logger.error(e.getMessage() + errorMsg.toString());
			}
			offset += limit;
			if (offset > productInfoList.size())
				break;
		} while (true);
		return newProductList;
	}

	private void updateProduct(List<ProductInfo> productInfoList,final List<StorePriceRange> storePriceRangeList) {
		int offset = 0;
		int limit = 100;
		do {
			if (productInfoList.size() <= 0)
				break;
			final List<ProductInfo> productInfos = productInfoList.subList(offset,
					Math.min(offset + limit, productInfoList.size()));
			try {
				transactionTemplate.execute(new TransactionCallback<Resolution>() {
					@Override
					public Resolution doInTransaction(TransactionStatus status) {
						for (ProductInfo productInfo : productInfos) {
							if (productInfo.getPrice() != null && !productInfo.getPrice().trim().equals("")) {
								Float price = Float.parseFloat(productInfo.getPrice());
								Product existProduct = productDao.findById(Long.parseLong(productInfo.getPkID()));
								existProduct.setPrice(price);
								for(StorePriceRange storePriceRange :storePriceRangeList){
									if(storePriceRange.isInPriceRange(Float.parseFloat(productInfo.getPrice()))){
										existProduct.setPriceRange(storePriceRange);							//StorePriceRange
										break;
									}
								}
								setPriceString(existProduct);
								productDao.update(existProduct);
							}
						}
						return null;
					}
				});
			} catch (Exception e) {
				StringBuffer errorMsg = new StringBuffer(" Fail update productID:");
				for (ProductInfo productInfo : productInfos) {
					errorMsg.append(productInfo.getId() + "_");
				}
				logger.error(e.getMessage() + errorMsg.toString());
			}
			offset += limit;
			if (offset > productInfoList.size())
				break;
		} while (true);
	}

	public void setPriceString(Product prodItem) {
		switch (prodItem.getLocale()) {
		case "de_DE":
			prodItem.setPriceString("ab EUR " + String.format(Locale.GERMANY, "%,.2f", prodItem.getPrice()));
			break;
		case "fr_FR":
			prodItem.setPriceString("à partir de EUR " + String.format(Locale.GERMANY, "%,.2f", prodItem.getPrice()));
			break;
		case "en_GB":
			prodItem.setPriceString("from \u00A3" + String.format(Locale.UK, "%.2f", prodItem.getPrice()));
			break;
		case "ja_JP":
			prodItem.setPriceString("\u00A5 " + String.format("%,.0f", prodItem.getPrice()) + "より");
			break;
		case "zh_CN":
			prodItem.setPriceString("\u00A5" + String.format("%.0f", prodItem.getPrice()));
			break;
		case "zh_TW":
			prodItem.setPriceString("$" + String.format("%,.0f", prodItem.getPrice()));
			break;
		case "en_CA":
			prodItem.setPriceString("from CDN$ " + String.format("%.2f", prodItem.getPrice()));
			break;
		case "en_US":
		default:
			prodItem.setPriceString("from $" + String.format("%.2f", prodItem.getPrice()));
			break;
		}
	}
	
	public String getBrandId(String locale, String brandIdx, String brandName) {
		String brandId = null;
		if (brandMap == null) {
			brandMap = new HashMap<String, Map<String, String>>();
			brandMap = getServerBrandList(locale);
		}
		if (brandMap.containsKey(brandIdx)) {
			Map<String, String> brand = brandMap.get(brandIdx);
			if (brand.containsKey(brandName)) {
				brandId = brand.get(brandName);
			}
		}
		return brandId;
	}

	public void createBrand(String locale, String brandIndex, String brandName) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		BrandIndex newBrandIndex = brandIndexService.createOrUpdate(brandIndex, locale);
		brandService.createOrUpdate(brandName, newBrandIndex, locale);
	}

	/**
	 * First determine whether exist records brand JSON file 1. Yes, read JSON
	 * file and convert the text back pass result 2. No，get Brand data by
	 * listBrandIndex.action and weite back JSON file and return result
	 * 
	 * @param locale
	 * @return Map<BrandIndex, Map<BrandName, BrandID>>
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, Map<String, String>> getServerBrandList(String locale) {

		String path = Tool.pathJoin(filePath, "_store", "_brand");
		Tool.makeDir(path);
		String filePath = Tool.pathJoin(path, String.format("%s.json", locale));

		String result = "error";

		if (Tool.isFileExist(filePath)) {
			result = Tool.readFileToString(filePath);
		} else {
			result = getBrandByJson(locale);
			Tool.writeStringToFile(filePath, result, false);
		}

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = (JSONObject) jsonParser.parse(result);
		} catch (ParseException e) {
		}

		JSONArray lang = (JSONArray) jsonObject.get("results");
		Iterator bi = lang.iterator();
		while (bi.hasNext()) {
			JSONObject biObj = (JSONObject) bi.next();
			String key = biObj.get("index").toString();
			JSONArray value = (JSONArray) biObj.get("brandList");
			Iterator b = value.iterator();
			Map<String, String> brand = new HashMap<String, String>();
			while (b.hasNext()) {
				JSONObject bObj = (JSONObject) b.next();
				brand.put(bObj.get("brandName").toString(), bObj.get("id").toString());
			}
			brandMap.put(key, brand);
		}
		return brandMap;
	}

	private String getBrandByJson(String locale) {

		final Map<String, Object> results = new HashMap<String, Object>();
		List<BrandIndex> brandIndexList = brandIndexDao.listAllIndexByLocale(locale);
		List<BrandIndexWrapper> brandIndexWrapper = new ArrayList<BrandIndexWrapper>();
		for (BrandIndex bIndex : brandIndexList) {
			BrandIndexWrapper newBrandIndexWrapper = new BrandIndexWrapper(bIndex);
			List<Brand> currentIndexedBrandList = brandDao
					.listBrandByLocale(bIndex.getId(), locale, Long.valueOf(0), Long.MAX_VALUE).getResults();
			List<BrandWrapper> currentIndexedBrandWrapperList = new ArrayList<BrandWrapper>();
			for (Brand currentIndexedBrand : currentIndexedBrandList) {
				currentIndexedBrandWrapperList.add(new BrandWrapper(currentIndexedBrand));
			}
			newBrandIndexWrapper.setBrandList(currentIndexedBrandWrapperList);
			brandIndexWrapper.add(newBrandIndexWrapper);
		}
		results.put("results", brandIndexWrapper);
		results.put("totalSize", brandIndexWrapper.size());

		try {
			return objectMapper.writerWithView(Views.Public.class).writeValueAsString(results);
		} catch (JsonProcessingException e) {
		}
		return null;
	}

	public void deleteNotSaleProdFromDB(String locale, Map<String, Map<String, String>> prodsMap,String brandId,String typeId) {
		if(prodsMap.isEmpty() || StringUtil.isBlank(locale) || StringUtil.isBlank(brandId) || StringUtil.isBlank(typeId))
			return;
		
		String title = "";
		String prodId = "";
		String link = "";
		String image = "";
		String extId = "";
		
		Set<String> prodExtIds = new HashSet<String>(prodsMap.keySet());
		List<String> deleteProdExIdslist = new ArrayList<String>();
		List<String> deleteProdIdsList = new ArrayList<String>();
		
		if (locale.equals("zh_TW")) {
			for(String prodExtId : prodExtIds){
				try{
					Map<String, String> prodMap = prodsMap.get(prodExtId);
					title = String.valueOf(prodMap.get("displayTitle"));
					prodId = String.valueOf(prodMap.get("productId"));
					link = String.valueOf(prodMap.get("productStoreLink"));
					image = String.valueOf(prodMap.get("imgThumbnail"));
					extId = String.valueOf(prodMap.get("extPID"));
					
					if (StringUtils.isBlank(link) || StringUtils.isBlank(image) 
							|| StringUtils.isBlank(extId) || StringUtils.isBlank(title)){
						deleteProdExIdslist.add(extId);
						deleteProdIdsList.add(prodId);
						prodsMap.remove(extId);
					}
					
					StringBuffer result = new StringBuffer();
					String extension = image.substring(image.lastIndexOf(".") + 1, image.length());
					if (!extension.equalsIgnoreCase("jpg")) {
						deleteProdExIdslist.add(extId);
						deleteProdIdsList.add(prodId);
						prodsMap.remove(extId);
					} else {
						Tool.sendGet(link, result);
						Pattern postPtn = Pattern.compile("<span class=\"desc\">(.*?)</span></button>");
						Matcher postMer = postPtn.matcher(result);
						if (postMer.find()) {
							String sPost = postMer.group(1);
							if (sPost.equalsIgnoreCase("已停售")) {
								deleteProdExIdslist.add(extId);
								deleteProdIdsList.add(prodId);
								prodsMap.remove(extId);
							}
						}
						String format = String.format("<div class=\"gdid\">賣場編號 <span class=\"number\">%s</span></div>", extId);
						postPtn = Pattern.compile(format);
						postMer = postPtn.matcher(result);
						if (!postMer.find()) {
							deleteProdExIdslist.add(extId);
							deleteProdIdsList.add(prodId);
							prodsMap.remove(extId);
						}
					}
				} catch (Exception e){}
			}
		}
		if(locale.equals("zh_CN")){
			List<String> prodExtIdsList = new ArrayList<String>();
			prodExtIdsList.addAll(prodExtIds);
			deleteProdExIdslist = Taobao.getDeleteStoreProdExIds(prodExtIdsList);
			deleteProdIdsList = new ArrayList<String>();
			if(!deleteProdExIdslist.isEmpty()){
				for(String deleteProdExId : deleteProdExIdslist){
					deleteProdIdsList.add(prodsMap.get(deleteProdExId).get("productId"));
					prodsMap.remove(deleteProdExId);
				}
			}
			
		}
		if(!deleteProdExIdslist.isEmpty()){
			deleteProducts(deleteProdIdsList);
			deleteProductsFromLocaleDB(deleteProdExIdslist, locale, brandId, typeId);
		}
		return;
	}
	
	public Boolean isWrongProdInfo(String locale, Boolean isDBContainProduct, Map<String, String> prodMap,
			ProductInfo prodInfo,String brandId,String typeId) {

		String title = "";
		String link = "";
		String image = "";
		String extId = "";

		if (isDBContainProduct) {
			title = String.valueOf(prodMap.get("displayTitle"));
			link = String.valueOf(prodMap.get("productStoreLink"));
			image = String.valueOf(prodMap.get("imgThumbnail"));
			extId = String.valueOf(prodMap.get("extPID"));
		} else {
			title = prodInfo.getTitle();
			link = prodInfo.getLink();
			image = prodInfo.getImgThumb();
			extId = prodInfo.getId();
		}
		if(StringUtil.isBlank(image) || StringUtil.isBlank(extId) || StringUtil.isBlank(title) || StringUtil.isBlank(link))
			return true;
		return false;
	}
	
	public void deleteProducts(List<String> deleteProdIdsList) {
		if(deleteProdIdsList.isEmpty())
			return;
		final List<String> deleteProdIdsFianlList = deleteProdIdsList;
		try {
			transactionTemplate.execute(new TransactionCallback<Resolution>() {
				@Override
				public Resolution doInTransaction(TransactionStatus status) {
					for(String prodId : deleteProdIdsFianlList){
							productDao.delete(Long.parseLong(prodId));
					}
					return null;
				}
			});
		} catch (Exception e) {
			StringBuffer errorMsg = new StringBuffer(" Fail delete ProductId:");
			for (String ProdId : deleteProdIdsFianlList) {
				errorMsg.append(ProdId).append("_");
			}
			logger.error(errorMsg.toString());
		}
	}
	
	public void deleteProduct(String prodId) {
		List<String> prodIdList = new ArrayList<String>();
		prodIdList.add(prodId);
		deleteProducts(prodIdList);
	}

	/**
	 * 
	 * @param prodMap Map<extPID, Map<key, value>>
	 * @param locale
	 * @param brandId
	 * @param typeId
	 */
	private void getServerProdList(Map<String, Map<String, String>> prodMap, String locale, String brandId,String typeId){
		prodMap.clear();
		String productInfoPath = Tool.pathJoin(filePath, "_store", "_searchServerProduct",locale,brandId,typeId);
		Tool.makeDir(productInfoPath);
		String productInfoFilePath = Tool.pathJoin(productInfoPath,"productInfo.txt");
		if(new File(productInfoFilePath).exists()){
			String productInfos = Tool.readFileToString(productInfoFilePath);
			if(productInfos.length() <= 0)
				return;
			String[] productInfosAry = productInfos.split("@@");
			for(String productInfo:productInfosAry){
				try{
					String[] productInfoElements = productInfo.split("&&");
					Map<String, String> prod = new HashMap<String, String>();
					prod.put("productId", productInfoElements[0]);
					prod.put("price", productInfoElements[1]);
					prod.put("displayTitle", productInfoElements[2]);
					prod.put("isDeleted", productInfoElements[3]);
					prod.put("productStoreLink", productInfoElements[4]);
					prod.put("imgThumbnail", productInfoElements[5]);
					prod.put("extPID", productInfoElements[6]);
					prodMap.put(productInfoElements[6],prod);
				}catch(Exception e){}//ignore wrong product data
			}
		}
		else
			getProdListFromServer(prodMap, locale, brandId, typeId, productInfoFilePath);
	}
	
	private void getProdListFromServer(Map<String, Map<String, String>> prodMap, String locale, String brandId,String typeId,String productInfoFilePath){
		int size = 0;
		int offset = 0;
		int limit = 100;
		StringBuffer productInfoSB = new StringBuffer();
		do {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
    		PageResult<BackendProduct> productResult = new PageResult<BackendProduct>();
    		productResult = backendProductDao.findProdByParams(locale, Long.parseLong(brandId), Long.parseLong(typeId),
    				null, new Long(offset), new Long(limit), null, Long.valueOf(0) );
    		for( BackendProduct product: productResult.getResults()){
    			String key = String.valueOf(product.getExtProdID());
				Map<String, String> prod = new HashMap<String, String>();
				String value = "";
				value = String.valueOf(product.getId());
				productInfoSB.append(value).append("&&");
				prod.put("productId", value);//ID
				value = String.valueOf(product.getPrice());
				productInfoSB.append(value).append("&&");
				prod.put("price", value);//5.53 price
				value = String.valueOf(product.getProductTitle());
				productInfoSB.append(value).append("&&");
				prod.put("displayTitle", value);//title
				value = String.valueOf(product.getIsDeleted());
				productInfoSB.append(value).append("&&");
				prod.put("isDeleted", value);
				value = String.valueOf(product.getProductStoreLink());
				productInfoSB.append(value).append("&&");
				prod.put("productStoreLink", value);
				value = String.valueOf(product.getImg_thumbnail());
				productInfoSB.append(value).append("&&");
				prod.put("imgThumbnail", value);
				value = String.valueOf(product.getExtProdID());
				productInfoSB.append(value).append("@@");
				prod.put("extPID", value);
				prodMap.put(key, prod);
    		}
			offset += 100;
			size = productResult.getResults().size();
		} while (size > 0);
		if(productInfoSB.length()>0)
			Tool.writeStringToFile(productInfoFilePath,productInfoSB.substring(0,productInfoSB.length()-2).toString(),false);
		else
			Tool.writeStringToFile(productInfoFilePath,"",false);
	}
	

	/**
	 * ProductInfo turn into JasonObject
	 * 
	 * @param locale
	 * @param info
	 * @return
	 */
	public Map<String, List<String>> getProdMapObjFromProdInfo(long itemIndex, String locale, ProductInfo productInfo,
			String onShelf) {
		Map<String, List<String>> productInfoMapObject = new HashMap<String, List<String>>();
		List<String> productInfolist = new LinkedList<String>();
		productInfolist.add(productInfo.getTitle()); // displayTitle
		productInfolist.add(productInfo.getDescription()); // description
		productInfolist.add(productInfo.getImgOriginal()); // img_original
		productInfolist.add(productInfo.getImgThumb()); // img_thumbnail
		productInfolist.add(productInfo.getLink()); // productStoreLink
		productInfolist.add(productInfo.getId()); // extProdID
		productInfolist.add(productInfo.getBrandID()); // brandID
		productInfolist.add(productInfo.getTypeID()); // typeID
		productInfolist.add(locale); // locale
		productInfolist.add(onShelf); // onShelf
		productInfolist.add(productInfo.getStoreID()); // storeID
		productInfolist.add(productInfo.getPrice()); // price
		productInfolist.add(productInfo.getPkID()); // pkID
		productInfoMapObject.put(String.valueOf(itemIndex), productInfolist);
		return productInfoMapObject;
	}
	
	/**
	 * List<JasonObj> write into File
	 * 
	 * @param fileName
	 * @param jasonObj
	 */
	private void writeJasonObjListToFile(String fileName, JSONObject jasonObj ,String jsonFilePath) {
		String jsonFileFullPath = Tool.pathJoin(jsonFilePath, fileName + ".json");
		String result = jasonObj.toJSONString();
		Tool.writeStringToFile(jsonFileFullPath, result, false);
	}
	
	@SuppressWarnings("rawtypes")
	private List<ProductInfo> getProductInfoListByJsonString(String JsonString) {
		try {
			Map<Integer,ProductInfo> productInfoMap = new HashMap<Integer,ProductInfo>();
			List<ProductInfo> productOrderList = new ArrayList<ProductInfo>();
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(JsonString);
			Iterator iter = jsonObject.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				JSONArray array = (JSONArray) entry.getValue();
				Iterator iterContent = array.iterator();
				try {
					while (iterContent.hasNext()) {
						ProductInfo productInfo = new ProductInfo();
						productInfo.setTitle(String.valueOf(iterContent.next()));
						productInfo.setDescription(String.valueOf(iterContent.next()));
						productInfo.setImgOriginal(String.valueOf(iterContent.next()));
						productInfo.setImgThumb(String.valueOf(iterContent.next()));
						productInfo.setLink(String.valueOf(iterContent.next()));
						productInfo.setId(String.valueOf(iterContent.next()));
						productInfo.setBrandID(String.valueOf(iterContent.next()));
						productInfo.setTypeID(String.valueOf(iterContent.next()));
						productInfo.setLocale(String.valueOf(iterContent.next()));
						productInfo.setOnShelf(String.valueOf(iterContent.next()));
						productInfo.setStoreID(String.valueOf(iterContent.next()));
						productInfo.setPrice(String.valueOf(iterContent.next()));
						productInfo.setPkID(String.valueOf(iterContent.next()));
						productInfoMap.put(Integer.parseInt(entry.getKey().toString()), productInfo);
					}
				} catch (Exception e) {}//avoid a data wrong
			}
			int productInfoMapSize = productInfoMap.size();
			for(int num = 1; num <= productInfoMapSize ; num++){
				productOrderList.add(productInfoMap.get(num));
			}
			return productOrderList;
		} catch (ParseException e) {}
		return null;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public RelProductTypeService getRelProductTypeService() {
		return relProductTypeService;
	}

	public void setRelProductTypeService(RelProductTypeService relProductTypeService) {
		this.relProductTypeService = relProductTypeService;
	}

	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	public StorePriceRangeDao getStorePriceRangeDao() {
		return storePriceRangeDao;
	}

	public void setStorePriceRangeDao(StorePriceRangeDao storePriceRangeDao) {
		this.storePriceRangeDao = storePriceRangeDao;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public BrandDao getBrandDao() {
		return brandDao;
	}

	public void setBrandDao(BrandDao brandDao) {
		this.brandDao = brandDao;
	}

	public BrandIndexDao getBrandIndexDao() {
		return brandIndexDao;
	}

	public void setBrandIndexDao(BrandIndexDao brandIndexDao) {
		this.brandIndexDao = brandIndexDao;
	}

	public BrandService getBrandService() {
		return brandService;
	}

	public void setBrandService(BrandService brandService) {
		this.brandService = brandService;
	}

	public BrandIndexService getBrandIndexService() {
		return brandIndexService;
	}

	public void setBrandIndexService(BrandIndexService brandIndexService) {
		this.brandIndexService = brandIndexService;
	}

	public ProductTypeDao getProductTypeDao() {
		return productTypeDao;
	}

	public void setProductTypeDao(ProductTypeDao productTypeDao) {
		this.productTypeDao = productTypeDao;
	}

	public ProductTypeService getProductTypeService() {
		return productTypeService;
	}

	public void setProductTypeService(ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}
	
	public BackendProductDao getBackendProductDao() {
		return backendProductDao;
	}

	public void setBackendProductDao(BackendProductDao backendProductDao) {
		this.backendProductDao = backendProductDao;
	}

	public StoreDao getStoreDao() {
		return storeDao;
	}

	public void setStoreDao(StoreDao storeDao) {
		this.storeDao = storeDao;
	}
	
}
