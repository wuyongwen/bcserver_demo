package com.cyberlink.store;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.cyberlink.utility.*;

public class Yahoo {
	private static final String UTF8_CHARSET = "UTF-8";
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	private String yahooSecretKey = "e2d908cda06df43d03e18aa7bc3c32fc";
	private SecretKeySpec secretKeySpec = null;
	private Mac mac = null;
	private int requestCount = 0;
	private String record_Path;
	
	public Yahoo(String logFilePath) {	
		record_Path = logFilePath;
		byte[] secretyKeyBytes;
		try {
			secretyKeyBytes = yahooSecretKey.getBytes(UTF8_CHARSET);
			secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA1_ALGORITHM);
			mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(secretKeySpec);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {}		
	}
	
	public ArrayList<ProductInfo> search(ArrayList<String> index,String indexName, String brand, String categoryName, String key, String brandIndex, String fileFullPath) {
		
		// 6, 20
		ArrayList<ProductInfo> productList = new ArrayList<ProductInfo>();	
		for (String category:index) {
			ArrayList<ProductInfo> cateList = searchCategory(category,indexName, brand, categoryName, key, brandIndex, fileFullPath);
			productList.addAll(cateList);
        }
		return productList;
	}
	
	private ArrayList<ProductInfo> searchCategory(String category,String indexName, String brand, String categoryName, String key, String brandIndex, String fileFullPath)
	{
		String keyword = String.format("%s %s", brand, key);
		keyword = keyword.replace(" ", "+");
		
		String path = null ; 
		if(fileFullPath != null){
			path = fileFullPath;
		}else{
			path = Tool.pathJoin(record_Path,"_store","_searchProduct","zh_TW",indexName, brandIndex, brand , categoryName, key);
		}
		Tool.makeDir(path);
		
        //Debug.dprintf("keyword - %s", keyword);
		ArrayList<ProductInfo> productList = new ArrayList<ProductInfo>();
		
		int nPage = 1;
		int nCount = 0;
		do {
			String filePath = Tool.pathJoin(path ,String.format("%s-%s.xml", category, nPage));
			String sb = new String();
			if (Tool.isFileExist(filePath)) {
				sb = Tool.readFileToString(filePath);			
			}
			else {
				Map<String, String> params = new HashMap<String, String>();
				params.put("level_no", "1");
				params.put("no", category);
				params.put("p", keyword);
				params.put("ps", "50");
				params.put("page", Integer.toString(nPage));
				
				String url = sign(params);
				StringBuffer result = new StringBuffer();
				Tool.sendGet(url, result);
				//Debug.dprintf("%s", url);
				sb = result.toString();
		        Tool.writeStringToFile(filePath, sb, false);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
			
			XMLUtil xmlutil = new XMLUtil(sb);		
			NodeList itemNode = xmlutil.doc.getElementsByTagName("gd");
			nCount = itemNode.getLength();
			nPage++;
			//Debug.dprintf("count: " + nCount);
		    for (int i = 0; i < itemNode.getLength(); i++)
		    {
		    	ProductInfo info = new ProductInfo();
		    	Element itemElement = (Element) itemNode.item(i);
		    	String id = getID(itemElement, xmlutil);		    	
		    	String title = getTitle(itemElement, xmlutil);
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
		    	
		    	String extension = imgThumb.substring(imgThumb.lastIndexOf(".") + 1, imgThumb.length());
				if (!extension.equalsIgnoreCase("jpg")) {	
					continue;
				}
		    	
		    	String lwTitle = title.toLowerCase();
		    	String lwDesc = desc.toLowerCase();
		    	String lwBrand = brand.toLowerCase();
	    		if (!lwTitle.contains(lwBrand) && !lwDesc.contains(lwBrand)) {	    			
	    			continue;
	    		}
	    		
		    	productList.add(info);
		    }
		} while (nCount > 0);
		
		return productList;
	}
	
	private String getID(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("gd_id");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}
		return result;
	}
	
	private String getTitle(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("gd_name");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}			
		return result;
	}
	
	private String getDescription(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("gd_sdesc");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}
		return result;
	}	
	
	private String getSImage(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("gd_image");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}
		return result;
	}
	
	private String getLImage(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("gd_image");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}
		return result;
	}
	
	private String getLink(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("gd_url");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
			result += "&co_servername=37643de4cd429a0fd4c0547de27c5982";
		}
		return result;
	}
	
	private String getBrand(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("gd_brand");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}
		return result;
	}
	
	private String getPrice(Element itemElement, XMLUtil xmlutil) {
		String result = "";
		NodeList nodeList = itemElement.getElementsByTagName("gd_price");
		Element nodeElement = (Element) nodeList.item(0);
		if (nodeElement != null){
			result = xmlutil.getNodeData(nodeElement);
		}
		return result;
	}
	
	private StringBuffer urlGet(String url) {
		StringBuffer result = new StringBuffer();
		Tool.sendGet(url, result);
		//Debug.dprintf("%d", requestCount++);
		return result;
	}
	private String getCurTime() {
		StringBuffer result = urlGet("https://tw.partner.buy.yahoo.com/api/v1/getCurrTime");
		XMLUtil xmlutil = new XMLUtil(result.toString());
		String curTime = xmlutil.doc.getDocumentElement().getFirstChild().getNodeValue();
		return curTime;
	}
	
	private String sign(Map<String, String> params) {
		params.put("pkey", "65a515682d888ebe1ff246621d12ee5e");
		params.put("ts", getCurTime());
		SortedMap<String, String> sortedParamMap = new TreeMap<String, String>(params);
		String canonicalQS = canonicalize(sortedParamMap);
		String hmac = hmac(canonicalQS);		
		String sig = percentEncodeRfc3986(hmac);
		String url = "https://tw.partner.buy.yahoo.com/api/v1/getGdInfo?" + canonicalQS + "&signature=" + sig;
		return url;		
	}
	
	private String hmac(String stringToSign)
	{	
		byte[] bytes;
		Formatter formatter = new Formatter();
		bytes = mac.doFinal(stringToSign.getBytes());
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
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
}