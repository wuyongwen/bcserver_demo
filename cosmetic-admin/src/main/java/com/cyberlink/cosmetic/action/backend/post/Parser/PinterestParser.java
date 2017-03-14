package com.cyberlink.cosmetic.action.backend.post.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.cyberlink.cosmetic.action.backend.post.CreatePostAction.OgClass;
import com.cyberlink.cosmetic.modules.post.model.PostAutoArticle.ArticleType;
import com.cyberlink.cosmetic.modules.post.service.ArticleData;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

public class PinterestParser {
	private AbstractAction action = null;
	private String sreachUrl = "https://www.pinterest.com/search/pins/";
	private String userAgent;
	private String extUrl;
	private List<ArticleData> pinList = new ArrayList<ArticleData>();
	
	public PinterestParser (AbstractAction action) {
		this.action = action;
		this.userAgent = this.action.getServletRequest().getHeader("User-Agent");
	}
	
	public void clearList() {
		pinList.clear();
	}
	
	public List<ArticleData> getPinList(String keyWord) {
		try {
			sreachUrl += String.format("?q=%s&page_size=200", keyWord); 
			Document doc = Jsoup.connect(sreachUrl).userAgent(userAgent).get();
			Elements pinImageWrappers = doc.select("a.pinImageWrapper");
			int artidx = 0;
		    for(int idx = 0; idx < pinImageWrappers.size(); idx++){
		        String pinUrl = "https://www.pinterest.com" + pinImageWrappers.get(idx).attr("href");
		        ArticleData art = getExternalArticle(pinUrl);
		        if (art != null) {
	        		art.setIndex(artidx);
	        		pinList.add(art);
	        		artidx++;
	        	}
		        continue;
			}
		    
		    return pinList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<ArticleData> getPinListFromInputFile(InputStream istream) {
		try {
			Document doc = Jsoup.parse(istream, "UTF-8", "https://www.pinterest.com");
			
			Elements pinWrappers = doc.select("div.pinWrapper");
			if (pinWrappers == null || pinWrappers.isEmpty()){
				return null;
			} else {
				int artidx = 0;
				for (int idx = 0 ; idx < pinWrappers.size() ; idx++) {
					Elements navLinkOverlays = null;
					Elements pinImgs = null;
					Elements richPinGridTitles = null;
					
					richPinGridTitles = pinWrappers.get(idx).select("h3.richPinGridTitle");
					if (richPinGridTitles == null || richPinGridTitles.isEmpty())
						continue;
					pinImgs = pinWrappers.get(idx).select("img.pinImg.fullBleed.noFade");
					if (pinImgs == null || pinImgs.isEmpty()) {
						pinImgs = pinWrappers.get(idx).select("img.pinImg.fullBleed.loaded");
						if (pinImgs == null || pinImgs.isEmpty())
							continue;
					}
					navLinkOverlays = pinWrappers.get(idx).select("a.Button.Module.NavigateButton.borderless.hasText.pinNavLink.navLinkOverlay");
					if (navLinkOverlays == null || navLinkOverlays.isEmpty())
						continue;
					
					ArticleData article = new ArticleData();
					article.setTitle(richPinGridTitles.first().text());
					article.addImage(pinImgs.first().attr("src"));
					article.setContent(pinImgs.first().attr("alt"));
					article.setUrl(navLinkOverlays.first().attr("href"));
					article.setIndex(artidx);
	        		pinList.add(article);
	        		artidx++;
				}
				return pinList;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<ArticleData> getArticleListFromJson(JsonObject jsonObj, String importFile) {
		if (jsonObj.has("type") && jsonObj.getString("type").equals("google"))
			return getGoogleListFromJson(jsonObj, importFile);
		else
			return getPinListFromJson(jsonObj, importFile);
	}
	
	public List<ArticleData> getPinListFromJson(JsonObject jsonObj, String importFile) {
		if (jsonObj.has("results"))
			return getPinListFromJsonArray(jsonObj, importFile);
		
		Iterator<?> keys = jsonObj.keys();
		while( keys.hasNext() ) {
		    String key = (String)keys.next();
		    if ( jsonObj.get(key) instanceof JsonObject ) {
		    	JsonObject subObj = (JsonObject) jsonObj.get(key);
		    	if (subObj.has("title") && subObj.has("thumbnail") && subObj.has("link") && subObj.has("pin_id")) {
		    		String title = subObj.getString("title");
		    		String thumbnail = subObj.getString("thumbnail");
		    		String description = subObj.getString("description");
		    		String link = subObj.getString("link");
		    		String index = subObj.getString("index");
		    		String articleId = subObj.getString("pin_id");
		    		if (thumbnail.isEmpty() || link.isEmpty() || index.isEmpty() || articleId.isEmpty())
		    			continue;
		    		if (!description.isEmpty()) {
		    			Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
			    		boolean isHTML = htmlPattern.matcher(description).matches();
			    		if (isHTML)
			    			continue;
			    		if (title.isEmpty()) {
			    			if (description.length() <= 20)
			    				title = description;
			    			else 
			    				title = description.substring(0, 21) + "...";
			    		}
		    		}
		    		if (title.isEmpty())
		    			continue;
		    		
		    		ArticleData article = new ArticleData();
		    		article.setTitle(title);
		    		article.setUrl(link);
		    		article.setContent(description);
		    		article.addImage(thumbnail.replaceFirst("236x", "736x"));
		    		article.setOrder(Integer.parseInt(index));
		    		article.setArticleType(ArticleType.Pinterest);
		    		article.setArticleId(articleId);
		    		article.setImportFile(importFile);
		    		pinList.add(article);
		    	}
		    }
		}
		
		Collections.sort(pinList, new Comparator<ArticleData>(){
			@Override
			public int compare(ArticleData o1, ArticleData o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		
		int artidx = 0;
		for (ArticleData art : pinList) {
			art.setIndex(artidx);
			artidx++;
		}
		
		return pinList;
	}
	
	public List<ArticleData> getPinListFromJsonArray(JsonObject jsonObj, String importFile) {
		JsonArray jsonResults = jsonObj.getJsonArray("results");
		for (int i = 0 ; i < jsonResults.length() ; i++) {
			JsonObject subObj = jsonResults.getJsonObject(i);
	    	if (subObj.has("title") && subObj.has("thumbnail") && subObj.has("link") && subObj.has("pin_id")) {
	    		String title = subObj.getString("title");
	    		String thumbnail = subObj.getString("thumbnail");
	    		String description = subObj.getString("description");
	    		String link = subObj.getString("link");
	    		String index = subObj.getString("index");
	    		String articleId = subObj.getString("pin_id");
	    		if (thumbnail.isEmpty() || link.isEmpty() || index.isEmpty() || articleId.isEmpty())
	    			continue;	    		if (!description.isEmpty()) {
	    			Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
		    		boolean isHTML = htmlPattern.matcher(description).matches();
		    		if (isHTML)
		    			continue;
		    		if (title.isEmpty()) {
		    			if (description.length() <= 20)
		    				title = description;
		    			else 
		    				title = description.substring(0, 21) + "...";
		    		}
	    		}
	    		if (title.isEmpty())
	    			continue;
	    		ArticleData article = new ArticleData();
	    		article.setTitle(title);
	    		article.setUrl(link);
	    		article.setContent(description);
	    		article.addImage(thumbnail.replaceFirst("236x", "736x"));
	    		article.setOrder(Integer.parseInt(index));
	    		article.setArticleType(ArticleType.Pinterest);
	    		article.setArticleId(articleId);
	    		article.setImportFile(importFile);
	    		pinList.add(article);
	    	}
		}
		
		Collections.sort(pinList, new Comparator<ArticleData>(){
			@Override
			public int compare(ArticleData o1, ArticleData o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		
		int artidx = 0;
		for (ArticleData art : pinList) {
			art.setIndex(artidx);
			artidx++;
		}
		
		return pinList;
	}
	
	public List<ArticleData> getGoogleListFromJson(JsonObject jsonObj, String importFile) {
		JsonArray jsonResults = jsonObj.getJsonArray("results");
		for (int i = 0 ; i < jsonResults.length() ; i++) {
			JsonObject subObj = jsonResults.getJsonObject(i);
	    	if (subObj.has("title") && subObj.has("imgurl") && subObj.has("imgrefurl") && subObj.has("docid")) {
	    		String title = subObj.getString("title");
	    		String description = "";
	    		if (subObj.has("description"))
	    			description = subObj.getString("description");
	    		String thumbnail = subObj.getString("imgurl");
	    		String link = subObj.getString("imgrefurl");
	    		String index = subObj.getString("index");
	    		String articleId = subObj.getString("docid");
	    		if (thumbnail.isEmpty() || link.isEmpty() || index.isEmpty() || articleId.isEmpty())
	    			continue;
	    		if (!description.isEmpty()) {
	    			Pattern htmlPattern = Pattern.compile(".*\\<[^>]+>.*", Pattern.DOTALL);
		    		boolean isHTML = htmlPattern.matcher(description).matches();
		    		if (isHTML)
		    			description = "";
		    		if (title.isEmpty()) {
		    			if (description.length() <= 20)
		    				title = description;
		    			else 
		    				title = description.substring(0, 21) + "...";
		    		}
	    		}
	    		if (title.isEmpty())
	    			continue;	    		
	    		ArticleData article = new ArticleData();
	    		article.setTitle(title);
	    		article.setUrl(link);
	    		article.setContent(description);
	    		article.addImage(thumbnail);
	    		article.setOrder(Integer.parseInt(index));
	    		article.setArticleType(ArticleType.Google);
	    		article.setArticleId(articleId);
	    		article.setImportFile(importFile);
	    		pinList.add(article);
	    	}
		}
		
		Collections.sort(pinList, new Comparator<ArticleData>(){
			@Override
			public int compare(ArticleData o1, ArticleData o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		
		int artidx = 0;
		for (ArticleData art : pinList) {
			art.setIndex(artidx);
			artidx++;
		}
		
		return pinList;
	}
		
	public String getExternalLink(String pinUrl) {
		try {
			Document doc = Jsoup.connect(pinUrl).userAgent(userAgent).timeout(3000).get();
			Elements metas = doc.select("head meta");
			
			for (int idx = 0; idx < metas.size(); idx++){
				Element meta = metas.get(idx);
				String propertyAttr = meta.attr("property");
				if (propertyAttr.equalsIgnoreCase("og:see_also"))
					return meta.attr("content");
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public ArticleData getExternalArticle(String pinUrl) {
		try {
			Connection con =  Jsoup.connect(pinUrl).userAgent(userAgent).timeout(3000);
			Document doc = con.get();
			ArticleData article = new ArticleData();
			
			/*for (int idx = 0; idx < metas.size(); idx++){
				Element meta = metas.get(idx);
				String propertyAttr = meta.attr("property");
				if (propertyAttr.equalsIgnoreCase("og:see_also"))
					article.setUrl(meta.attr("content"));
				if (propertyAttr.equalsIgnoreCase("og:title"))
					article.setTitle(meta.attr("content"));
				if (propertyAttr.equalsIgnoreCase("og:image"))
					article.addImage(meta.attr("content"));
			}*/
			if (doc == null)
				return null;
			Elements richPinArticleSummarys = doc.select("div.richPinArticleSummary");
			if (richPinArticleSummarys == null || richPinArticleSummarys.isEmpty()){
				//Elements paddedPinLinks = doc.select("a.paddedPinLink");
				//article.setContent(paddedPinLinks.first().attr("title"));
				return null;
			} else 
				article.setContent(richPinArticleSummarys.first().text());
			Elements richPinNameLinks = doc.select("a.richPinNameLink");
			if (richPinNameLinks == null || richPinNameLinks.isEmpty()) {
				//Elements paddedPinLinks = doc.select("a.paddedPinLink");
				//article.setTitle(paddedPinLinks.first().attr("title"));
				//article.setUrl(paddedPinLinks.first().attr("href"));
				return null;
			} else {
				article.setTitle(richPinNameLinks.first().text());
				article.setUrl(richPinNameLinks.first().attr("href"));
			}
			Elements pinImages = doc.select("img.pinImage");
			if (pinImages == null || pinImages.isEmpty()) {
				return null;
			} else
				article.addImage(pinImages.first().attr("src"));
			
			return article;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArticleData getMetaTagFromUrl(String extUrl) {
		try {
            URL aURL = new URL(extUrl);
            Connection con = Jsoup.connect(extUrl).userAgent(userAgent).timeout(3000);
            org.jsoup.nodes.Document doc = con.get();
            Elements metas = doc.select("head meta");
            ArticleData article = new ArticleData();
            article.setUrl(extUrl);
            
            for(int idx = 0; idx < metas.size(); idx++) {
                org.jsoup.nodes.Element meta = metas.get(idx);
                String propertyAttr = meta.attr("property");
                if(propertyAttr.equalsIgnoreCase("og:title")) {
                	article.setTitle(meta.attr("content"));
                }
                else if(propertyAttr.equalsIgnoreCase("og:image")) {
                	article.addImage(meta.attr("content"));
                }
                else if(propertyAttr.equalsIgnoreCase("og:description")) {
                	article.setContent(meta.attr("content"));
                }
            }
            for(int idx = 0; idx < metas.size(); idx++) {
                org.jsoup.nodes.Element meta = metas.get(idx);
                String propertyAttr = meta.attr("name");
                if(propertyAttr.equalsIgnoreCase("description") && article.getContent().length() == 0) {
                	article.setContent(meta.attr("content"));
                }
            }
            if(article.getTitle().length() <= 0) {
                Elements title = doc.select("head title");
                if(title.size() > 0)
                	article.setTitle(title.get(0).text());
            }
            if(article.getImages().size() <= 0) {
                Elements imgs = doc.select("body img");
                for(int idx = 0; idx < imgs.size(); idx++) {
                    Element img = imgs.get(idx);
                    String imgSrc = img.attr("data-original");
                    if(imgSrc == null || imgSrc.length() <= 0)
                        imgSrc = img.attr("src");
                    if(imgSrc == null || imgSrc.length() <= 0)
                        continue;
                    if(imgSrc.startsWith("http"))
                    	article.addImage(imgSrc);
                    else if(imgSrc.startsWith("/"))
                    	article.addImage(aURL.getProtocol() + "://" + aURL.getAuthority() + imgSrc);
                    else if(imgSrc.startsWith("."))
                    	article.addImage(aURL.getProtocol() + "://" + aURL.getAuthority() + aURL.getPath().substring(0, aURL.getPath().lastIndexOf("/")) + imgSrc.substring(1));
                        
                }
            }
            return article;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
		return null;
	}
}