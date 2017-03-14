package com.cyberlink.store;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.cyberlink.utility.*;

public class Amazon {
	private static final String UTF8_CHARSET = "UTF-8";
	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	private static final String REQUEST_URI = "/onca/xml";
	private static final String REQUEST_METHOD = "GET";	
	private String awsAccessKeyId = "AKIAIUBPNPT7B6ZKVQUQ";
	private String awsSecretKey = "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";
	private String awsAssociateTag = "";
	private SecretKeySpec secretKeySpec = null;
	private Mac mac = null;
	private String endpoint = ""; // must be lower case
	private Map<String, List<String>> endpointMap = new HashMap<String, List<String>>();	
	private String region;
	private String record_Path;
	
	public Amazon(String locale ,String logFilePath) {
		record_Path = logFilePath;
		region = locale;
		byte[] secretyKeyBytes;
		try {
			secretyKeyBytes = awsSecretKey.getBytes(UTF8_CHARSET);
			secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
			mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(secretKeySpec);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
			//e.printStackTrace();
		}		
		
		endpointMap.put("en_US", new ArrayList<String>(Arrays.asList("webservices.amazon.com", "amazaffiymk-20")));
		endpointMap.put("en_CA", new ArrayList<String>(Arrays.asList("webservices.amazon.ca", "perfcorp-20")));
		endpointMap.put("en_GB", new ArrayList<String>(Arrays.asList("webservices.amazon.co.uk", "amaaffymkuk00-21")));
		endpointMap.put("de_DE", new ArrayList<String>(Arrays.asList("webservices.amazon.de", "perfecaffilid-21")));
		endpointMap.put("fr_FR", new ArrayList<String>(Arrays.asList("webservices.amazon.fr", "cybe0b-21")));
		endpointMap.put("ja_JP", new ArrayList<String>(Arrays.asList("webservices.amazon.co.jp", "amaaffymkjpn-22")));
		
		if (endpointMap.containsKey(locale)) {
			endpoint = endpointMap.get(locale).get(0);
			awsAssociateTag = endpointMap.get(locale).get(1);
		}		
	}
	
	public ArrayList<ProductInfo> search(String index, String brand, String categoryName, String key, ArrayList<String> akaList ,String brandIndex,String fileFullPath)
	{
		if (endpoint.isEmpty() || awsAssociateTag.isEmpty())
			return null;
		
		String keyword = String.format("%s %s", brand, key);
		//Debug.dprintf("keyword - %s", keyword);
		String path = null;
		if(fileFullPath != null)
			path = fileFullPath;
		else
			path = Tool.pathJoin(record_Path , "_store" , "_searchProduct", region , index , brandIndex , brand, categoryName, key);
			
		Tool.makeDir(path);
		ArrayList<ProductInfo> productList = new ArrayList<ProductInfo>();
		
		int nPage = 1;
		int nCount = 0;
		do {
			try{
				String filePath = Tool.pathJoin(path , String.format("%d.xml", nPage));
				String sb = new String();
				if (Tool.isFileExist(filePath)) {				
					sb = Tool.readFileToString(filePath);			
				}
				else {				
					Map<String, String> params = new HashMap<String, String>();		
					params.put("Operation", "ItemSearch");
					params.put("Condition", "All");
					params.put("Keywords", keyword);				
					params.put("SearchIndex", index);
					params.put("Availability", "Available");		
					params.put("ResponseGroup", "Images,ItemAttributes,Offers");
					if (!index.equalsIgnoreCase("All"))
						params.put("Sort", "salesrank");
					params.put("Version", "2013-08-01");
					params.put("ItemPage", Integer.toString(nPage));				
					
					String url = sign(params);
					Debug.dprintf("url: %s", url);
					
					StringBuffer result = new StringBuffer();
					
					int nRetry = 0;
					int status = 200;
					do {
						status = Tool.sendGet(url, result);
						if (status != 200) {
							nRetry ++;
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {}
						}					
					} while(status != 200 && nRetry < 3);
					
					if (status != 200) {
						Debug.dprintf("status = %d, %s", status, result.toString());
						continue;	
					}
					
					sb = result.toString();
					Tool.writeStringToFile(filePath, sb, false);
				}
				
				XMLUtil xmlutil = new XMLUtil(sb);	
	 			NodeList itemNode = xmlutil.doc.getElementsByTagName("Code");
				int errorCount = itemNode.getLength();
				if (errorCount > 0) {
					String errorMsg = getCode((Element) itemNode.item(0), xmlutil);
					if (errorMsg.equalsIgnoreCase("RequestThrottled")) {					
						nCount = 1; // just for continue
						Tool.delFile(filePath);
						continue;
					}
				}
				
				nPage++;
				itemNode = xmlutil.doc.getElementsByTagName("Item");
				nCount = itemNode.getLength();
				//Debug.dprintf("count: " + nCount);
	
			    for (int i = 0; i < itemNode.getLength(); i++)
			    {
			    	ProductInfo info = new ProductInfo();
			    	Element itemElement = (Element) itemNode.item(i);
			    	String id = getID(itemElement, xmlutil);		    	
			    	String title = getTitle(itemElement, xmlutil);
			    	String prodBrand = getBrand(itemElement, xmlutil);
			    	String imgThumb = getSImage(itemElement, xmlutil);
			    	String imgOrig = getLImage(itemElement, xmlutil);
			    	String link = getLink(itemElement, xmlutil);
			    	String price = getPrice(itemElement, xmlutil);
			    	String desc = getDescription(itemElement, xmlutil);
			    	info.setId(id);
			    	info.setTitle(title);
			    	info.setImgThumb(imgThumb);		    	
			    	info.setImgOriginal(imgOrig);		    	
			    	info.setLink(link);		    	
			    	info.setPrice(price);		    	
			    	info.setDescription(desc);
	    	
			    	if (info.getPrice().isEmpty() || info.getPrice().equals("0") ||
			    		info.getTitle().isEmpty() || info.getLink().isEmpty() || 
			    		info.getImgThumb().isEmpty()) {
			    		continue;
			    		}
			    	
			    	String lwProdBrand = prodBrand.toLowerCase();
			    	String lwTitle = title.toLowerCase();
			    	String lwDesc = desc.toLowerCase();
			    	
			    	boolean akaExist = false;
			    	if(akaList != null){
				    	for (String aka: akaList) {
				    		String lwAka = aka.toLowerCase();
				    		if (!lwAka.isEmpty() && lwTitle.contains(lwAka)) {
				    			akaExist = true;
				    			break;
				    		}
				    	}
			    	}else{
			    		akaExist = true;
			    	}
			    	
			    	String lwBrand = brand.toLowerCase();
			    	String lwKey = key.toLowerCase();
			    	
			    	if ((akaExist || lwTitle.contains(lwBrand) || lwProdBrand.contains(lwBrand)) && 
		    		    (lwTitle.contains(lwKey) || lwDesc.contains(lwKey))) 
		    		{
			    		productList.add(info);
		    		}
			    }
			}catch(Exception e){nPage++;}//exist no word in file
		}while (nCount > 0);
		
		return productList;
	}
	
	private String getCode(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		if (itemElement != null){
			result = xmlutil.getNodeData(itemElement);
		}
		return result;
	}
	
	private String getID(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("ASIN");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}
		return result;
	}
	
	private String getTitle(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("Title");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}
		return result;
	}
	
	private String getDescription(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("Feature");
		for (int i = 0; i < nodeList.getLength(); i++)
		{ 
			Element nodeElement = (Element) nodeList.item(i);
			if (nodeElement != null){
				result += xmlutil.getNodeData(nodeElement);
				result += '\n';
			}
		}
		return result;
	}	
	
	private String getSImage(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("MediumImage");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			NodeList urlList = nodeElement.getElementsByTagName("URL");
			Element urlElement = (Element) urlList.item(0);
			if (urlElement != null){
				result = xmlutil.getNodeData(urlElement);
			}
		}
		return result;
	}
	
	private String getLImage(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("MediumImage");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			NodeList urlList = nodeElement.getElementsByTagName("URL");
			Element urlElement = (Element) urlList.item(0);
			if (urlElement != null){
				result = xmlutil.getNodeData(urlElement);
			}
		}
		return result;
	}
	
	private String getLink(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("DetailPageURL");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}
		return result;
	}
	
	private String getBrand(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("Brand");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}
		return result;
	}
	
	private String getPrice(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList priceList = null;
		NodeList nodeList = itemElement.getElementsByTagName("OfferListing");
		if (nodeList.getLength() != 0) {
			Element listElement = (Element) nodeList.item(0);
			NodeList idList = listElement.getElementsByTagName("OfferListingId");
			if (idList.getLength() > 0) {
				priceList = listElement.getElementsByTagName("Price");
			}
		}	

		if (priceList != null && priceList.getLength() > 0)
		{
			Element nodeElement = (Element) priceList.item(0);
			NodeList amontList = nodeElement.getElementsByTagName("Amount");
			Element amontElement = (Element) amontList.item(0);
			if (amontElement != null){ 
				result = xmlutil.getNodeData(amontElement);
			}
		}
		return result;
	}
	
	private String sign(Map<String, String> params) {
		params.put("Service", "AWSECommerceService");
		params.put("AWSAccessKeyId", awsAccessKeyId);
		params.put("AssociateTag", awsAssociateTag);
		params.put("Timestamp", timestamp());
		SortedMap<String, String> sortedParamMap = new TreeMap<String, String>(params);
		String canonicalQS = canonicalize(sortedParamMap);
		String toSign =
		REQUEST_METHOD + "\n"
		+ endpoint + "\n"
		+ REQUEST_URI + "\n"
		+ canonicalQS;
		String hmac = hmac(toSign);
		String sig = percentEncodeRfc3986(hmac);
		String url = "http://" + endpoint + REQUEST_URI + "?" + canonicalQS + "&Signature=" + sig;
		return url;
		}
	
	private String hmac(String stringToSign) {
		String signature = null;
		byte[] data;
		byte[] rawHmac;
		try {
			data = stringToSign.getBytes(UTF8_CHARSET);
			rawHmac = mac.doFinal(data);
			Base64 encoder = new Base64();
			signature = new String(encoder.encode(rawHmac));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
		}
		return signature;
	}
	
	private String timestamp() {
		String timestamp = null;
		Calendar cal = Calendar.getInstance();
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
		timestamp = dfm.format(cal.getTime());
		return timestamp;
	}
	
	private String canonicalize(SortedMap<String, String> sortedParamMap) {
		if (sortedParamMap.isEmpty()) {
			return "";
			}
		StringBuffer buffer = new StringBuffer();
		Iterator<Map.Entry<String, String>> iter =
		sortedParamMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> kvpair = iter.next();
			buffer.append(percentEncodeRfc3986(kvpair.getKey()));
			buffer.append("=");
			buffer.append(percentEncodeRfc3986(kvpair.getValue()));
			if (iter.hasNext()) {
				buffer.append("&");
			}
		}
		String canonical = buffer.toString();
		return canonical;
	}
	
	private String percentEncodeRfc3986(String s) {
		String out;
		try {
			out = URLEncoder.encode(s, UTF8_CHARSET)
					.replace("+", "%20")
					.replace("*", "%2A")
					.replace("%7E", "~");
		} catch (UnsupportedEncodingException e) {
			out = s;
		}
		return out;
	}
	
	public String getStoreLinkByExProductID(String exProductID){
		String resultXML ;
		String link = null;
		Map<String, String> params = new HashMap<String, String>();		
		params.put("Operation", "ItemLookup");
		params.put("Condition", "All");
		params.put("ItemId", exProductID);
		params.put("ResponseGroup", "ItemAttributes");
		params.put("Version", "2013-08-01");			
		
		String url = sign(params);
		Debug.dprintf("url: %s", url);
		
		StringBuffer result = new StringBuffer();
		
		int nRetry = 0;
		int status = 200;
		do {
			status = Tool.sendGet(url, result);
			if (status != 200) {
				nRetry ++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}					
		} while(status != 200 && nRetry < 3);
		
		if (status != 200) {
			Debug.dprintf("status = %d, %s", status, result.toString());
			return null;	
		}
		resultXML = result.toString();
		
		
		XMLUtil xmlutil = new XMLUtil(resultXML);	
			NodeList itemNode = xmlutil.doc.getElementsByTagName("Code");
		int errorCount = itemNode.getLength();
		if (errorCount > 0) {
			String errorMsg = getCode((Element) itemNode.item(0), xmlutil);
			if (errorMsg.equalsIgnoreCase("RequestThrottled")) {
				return null;
			}
		}
		itemNode = xmlutil.doc.getElementsByTagName("Item");
		
		if(itemNode != null){
			Element itemElement = (Element) itemNode.item(0);
			if(itemElement != null)
				link = getLink(itemElement, xmlutil);
		}
		return link;
	}
}