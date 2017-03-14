package com.cyberlink.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.cyberlink.utility.*;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.taobao.api.ApiException;
import com.taobao.api.Constants;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkItemInfoGetRequest;
import com.taobao.api.request.TbkItemsGetRequest;
import com.taobao.api.request.TbkMobileItemsConvertRequest;
import com.taobao.api.response.TbkItemInfoGetResponse;
import com.taobao.api.response.TbkItemsGetResponse;
import com.taobao.api.response.TbkMobileItemsConvertResponse;

public class Taobao {
	protected static String url = "http://gw.api.taobao.com/router/rest";       
	protected static String appkey = "23068432";      
	protected static String appSecret = "5b2ae992002a02e269d2542a14a28078";      
	protected static String sessionkey = "61022070cdca18818ffbe3e6c18a6837752dd30fa49e1352374554904";  
	private String record_Path;
	
	public Taobao(String logFilePath) {	
		record_Path = logFilePath;
	}
	
	public ArrayList<ProductInfo> search(ArrayList<String> index,String indexName, String brand, String categoryName, String key, String brandIndex, String productFilePath) {		
				
		ArrayList<ProductInfo> productList = new ArrayList<ProductInfo>();
		for (String category:index) {
			ArrayList<ProductInfo> cateList = searchCategory(Long.parseLong(category),indexName, brand, categoryName, key, brandIndex, productFilePath);
			productList.addAll(cateList);
        }
		//ArrayList<ProductInfo> cate1List = searchCategory(1801L, brand, key);
		//ArrayList<ProductInfo> cate2List = searchCategory(50010788L, brand, key);
		return productList;
	}
	
	private ArrayList<ProductInfo> searchCategory(Long category,String indexName, String brand,String categoryName, String key, String brandIndex, String productFilePath) 
	{
		String keyword = String.format("%s %s", brand, key);
		String path = null;
		if(productFilePath != null)
			path = productFilePath;
		else
			path = Tool.pathJoin(record_Path  , "_store" , "_searchProduct" ,"zh_CN",indexName, brandIndex, brand,categoryName, key);
		Tool.makeDir(path);
		
		ArrayList<ProductInfo> productList = new ArrayList<ProductInfo>();
		int nPage = 1;
		int nCount = 0;
		String result = new String();
		do {
			String filePath = Tool.pathJoin(path , String.format("%s-%s.xml", category, nPage));
			if (Tool.isFileExist(filePath)) {
				result = Tool.readFileToString(filePath);			
			}
			else {
				TaobaoClient taClient = new DefaultTaobaoClient(url, appkey, appSecret, Constants.FORMAT_XML, 15000, 15000);
				TbkItemsGetRequest req = new TbkItemsGetRequest();
				req.setFields("num_iid,title,price,pic_url,item_url,shop_url,discount_price");
				req.setCid(category); //1801, 50010788
				req.setKeyword(keyword);
				req.setMallItem("true");
				req.setPageNo(Long.valueOf(nPage));
				req.setPageSize(40L);
				TbkItemsGetResponse response;
				
				int nRetry = 0;
				int status = 404;
				do {
					try {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {}
						response = taClient.execute(req);
						String errorCode = response.getSubCode();
						if (errorCode != null && errorCode.equalsIgnoreCase("accesscontrol.limited-by-app-access-count"))
							return productList;
						result = response.getBody();					
						Tool.writeStringToFile(filePath, result, false);
						//Debug.dprintf(filePath);
						status = 200;
					} catch (Exception e) {
						nRetry ++;
						try {
							Thread.sleep(2000);
						} catch (InterruptedException interruptedException) {}
					}
				} while(status != 200 && nRetry < 3);
			}

			nPage++;
			if (result != null && !result.isEmpty()) {	
				XMLUtil xmlutil = new XMLUtil(result);		
				NodeList itemNode = xmlutil.doc.getElementsByTagName("tbk_item");
				nCount = itemNode.getLength();
				for (int i = 0; i < itemNode.getLength(); i++)
			    {
					ProductInfo info = new ProductInfo();
			    	Element itemElement = (Element) itemNode.item(i);
			    	String id = getID(itemElement, xmlutil);
			    	info.setId(id);
			    	info.setTitle(getTitle(itemElement, xmlutil));
			    	//info.setBrand(getBrand(itemElement, xmlutil));
			    	info.setImgThumb(getSImage(itemElement, xmlutil));
			    	info.setImgOriginal(getLImage(itemElement, xmlutil));
			    	info.setPrice(getPrice(itemElement, xmlutil));
			    	info.setDescription(getDescription(itemElement, xmlutil));
			    	if (info.getPrice().isEmpty() || info.getTitle().isEmpty() || 
			    		info.getImgThumb().isEmpty()) {			    		
			    		continue;
				    }			 
			    	
			    	productList.add(info);
			    }
			}
		}while (result != null && nCount > 0);
		
		
		String ids = new String();
		Map<String, String> urlinkMap = new HashMap<String, String>();				
		int nSum = 0;
		for (int nIdx = 0; nIdx < productList.size(); nIdx++) {			
			String id = productList.get(nIdx).getId();
			String filePath = Tool.pathJoin(path,String.format("%s.txt", id));
			if (Tool.isFileExist(filePath)) {
				String link = Tool.readFileToString(filePath);
				productList.get(nIdx).setLink(link);
			}
			else {				
				nSum ++;
				ids +=  id + ",";
				if (nSum == 40) {				
					Map<String, String> urllink = getLink(ids);
					urlinkMap.putAll(urllink);
					ids = "";
					nSum = 0;				
				}			
			}
		}
		
		if (!ids.isEmpty()) {
			Map<String, String> urllink = getLink(ids);
			urlinkMap.putAll(urllink);
		}
		
		if (!urlinkMap.isEmpty()) {
			for (int nIdx = 0; nIdx < productList.size(); nIdx++) {
				String id = productList.get(nIdx).getId();
				String filePath = Tool.pathJoin(path,String.format("%s.txt", id));
				String link = urlinkMap.get(id);						
				productList.get(nIdx).setLink(link);
				Tool.writeStringToFile(filePath, link, false);
			}
		}
		return productList;
	}
	
	private String getID(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("num_iid");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null) {
			result = xmlutil.getNodeData(nodeElement);
		}		
		return result;
	}
	
	private String getTitle(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("title");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null) {
			result = xmlutil.getNodeData(nodeElement);
		}		
		return result;
	}
	
	private String getDescription(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("title");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null) {
			result = xmlutil.getNodeData(nodeElement);
		}		
		return result;
	}	
	
	private String getSImage(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("pic_url");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null) {
			result = xmlutil.getNodeData(nodeElement);
		}		
		return result;
	}
	
	private String getLImage(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("pic_url");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null) {
			result = xmlutil.getNodeData(nodeElement);
		}		
		return result;
	}
	
	private Map<String, String> getLink(String id) {
		Map<String, String> idMap = new HashMap<String, String>();
		TaobaoClient taConvert = new DefaultTaobaoClient(url, appkey, appSecret, Constants.FORMAT_XML, 15000, 15000);
		TbkMobileItemsConvertRequest req=new TbkMobileItemsConvertRequest();
		req.setFields("click_url,num_iid");
		req.setNumIids(id);
		
		String result = null;
		TbkMobileItemsConvertResponse response;
		try {
			response = taConvert.execute(req);
			result = response.getBody();
		} catch (ApiException e) {}
		
		if (result != null) {
			XMLUtil xmlutil = new XMLUtil(result);		
			NodeList itemNode = xmlutil.doc.getElementsByTagName("tbk_item");
			for (int i = 0; i < itemNode.getLength(); i++)
		    {
				Element itemElement = (Element) itemNode.item(i);
				NodeList urlList = itemElement.getElementsByTagName("click_url");
				Element urlElement = (Element) urlList.item(0);
				String url = xmlutil.getNodeData(urlElement);
				
				NodeList idList = itemElement.getElementsByTagName("num_iid");
				Element idElement = (Element) idList.item(0);
				String key = xmlutil.getNodeData(idElement);
				idMap.put(key, url);
		    }
		}
		
		return idMap;
	}
	
	private String getBrand(Element itemElement, XMLUtil xmlutil) {
		String result = "";			
		return result;
	}
	
	private String getPrice(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("price");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null) {
			result = xmlutil.getNodeData(nodeElement);
		}		
		return result;
	}
	
	
	public static List<String> getDeleteStoreProdExIds(List<String> exprodIdsList) {
		List<String> deleteStoreProdExIdsList = new ArrayList<String>();
		if (exprodIdsList.isEmpty()) {
			return deleteStoreProdExIdsList;
		}
		String searchExtIds;
		int exprodIdsListSize = exprodIdsList.size();
		List<String> exprodIdsTempSublist = new ArrayList<String>();
		for (int index = 0; index < exprodIdsListSize; index++) {
			exprodIdsTempSublist.add(exprodIdsList.get(index));
			if (index % 40 == 0 || index == (exprodIdsListSize - 1)) {
				searchExtIds = "";
				for (String exprodId : exprodIdsTempSublist) {
					searchExtIds += (exprodId + ",");
				}
				TaobaoClient taClient = new DefaultTaobaoClient(url, appkey, appSecret, Constants.FORMAT_JSON, 15000,15000);
				TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
				req.setFields("num_iid,title");
				req.setNumIids(searchExtIds.substring(0, searchExtIds.length() - 1));
				TbkItemInfoGetResponse response;
				int nRetry = 0;
				int status = 404;
				do {
					try {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
						}
						response = taClient.execute(req);
						JsonObject responseObj = new JsonObject(response.getBody());
						JsonObject resultsJsonObject = (JsonObject)((JsonObject) responseObj.get("tbk_item_info_get_response")).get("results");
						if(resultsJsonObject.toString().length() > 2){
							JsonArray onShelfProducts = (JsonArray)(resultsJsonObject.get("n_tbk_item"));
							for (int searchObjIndex = 0; searchObjIndex < onShelfProducts.length(); searchObjIndex++) {
								exprodIdsTempSublist.remove(onShelfProducts.getJsonObject(searchObjIndex).getString("num_iid"));
							}
						}
						status = 200;
					} catch (Exception e) {
						nRetry++;
						try {
							Thread.sleep(2000);
						} catch (InterruptedException interruptedException) {
						}
					}
				} while (status != 200 && nRetry < 2);
				if (exprodIdsTempSublist.size() > 0) {
					for (String exprodId : exprodIdsTempSublist) {
						deleteStoreProdExIdsList.add(exprodId);
					}
				}
				exprodIdsTempSublist = new ArrayList<String>();
			}
		}
		return deleteStoreProdExIdsList;
	}
	
	public static Boolean isDeleteStoreProd(String exprodId){
		TaobaoClient taClient = new DefaultTaobaoClient(url, appkey, appSecret, Constants.FORMAT_JSON, 15000, 15000);
		TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
	    req.setFields("num_iid,title");
	    req.setNumIids(exprodId);
	    TbkItemInfoGetResponse response;
	    
		int nRetry = 0;
		int status = 404;
		do {
			try {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {}
				response = taClient.execute(req);
				JsonObject obj = new JsonObject(response.getBody());
				if(((JsonObject)obj.get("tbk_item_info_get_response")).get("results").toString().length() == 2)
				{
					return true; 
				};
				status = 200;
			} catch (Exception e) {
				nRetry ++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException interruptedException) {}
			}
			if(nRetry == 2){
				return true;
			}
		} while(status != 200 && nRetry < 2);
		return false;
	}
	
	public String getStoreLinkByExProductID(String exProductID){
		 return getLink(exProductID).get(exProductID);
	}

}
