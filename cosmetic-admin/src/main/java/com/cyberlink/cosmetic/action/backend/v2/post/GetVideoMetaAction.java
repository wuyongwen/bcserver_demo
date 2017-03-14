package com.cyberlink.cosmetic.action.backend.v2.post;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.cyberlink.core.web.jackson.Views;
import com.cyberlink.cosmetic.action.backend.AbstractAction;
import com.fasterxml.jackson.annotation.JsonView;

@UrlBinding("/v2/post/get-video-meta.action")
public class GetVideoMetaAction extends AbstractAction {
	
    private String extUrl;
    
    public void setExtUrl(String extUrl) {
        this.extUrl = extUrl;
    }
    
    public class OgClass {
        @JsonView(Views.Simple.class)
        String title = "";
        
        @JsonView(Views.Simple.class)
        Set<String> images = new HashSet<String>();;
        
        @JsonView(Views.Simple.class)
        String content = "";
    }
    
    @DefaultHandler
    public Resolution route() {       
    	if (getCurrentUser() == null) {
    		return new ErrorResolution(403, "Need Login");
        }
        
        try {
            String userAgent = this.getServletRequest().getHeader("User-Agent");
            URL aURL = new URL(extUrl);
            Connection con = Jsoup.connect(extUrl).userAgent(userAgent).timeout(3000);
            org.jsoup.nodes.Document doc = con.get();
            Elements metas = doc.select("head meta");
            OgClass og = new OgClass();
            for(int idx = 0; idx < metas.size(); idx++) {
                org.jsoup.nodes.Element meta = metas.get(idx);
                String propertyAttr = meta.attr("property");
                if(propertyAttr.equalsIgnoreCase("og:title")) {
                    og.title = meta.attr("content");
                }
                else if(propertyAttr.equalsIgnoreCase("og:image")) {
                    og.images.add(meta.attr("content"));
                }
                else if(propertyAttr.equalsIgnoreCase("og:description")) {
                    og.content = meta.attr("content");
                }
            }
            for(int idx = 0; idx < metas.size(); idx++) {
                org.jsoup.nodes.Element meta = metas.get(idx);
                String propertyAttr = meta.attr("name");
                if(propertyAttr.equalsIgnoreCase("description") && og.content.length() == 0) {
                    og.content = meta.attr("content");
                }
            }
            if(og.title.length() <= 0) {
                Elements title = doc.select("head title");
                if(title.size() > 0)
                    og.title = title.get(0).text();
            }
            if(og.images.size() <= 0) {
                Elements imgs = doc.select("body img");
                for(int idx = 0; idx < imgs.size(); idx++) {
                    Element img = imgs.get(idx);
                    String imgSrc = img.attr("data-original");
                    if(imgSrc == null || imgSrc.length() <= 0)
                        imgSrc = img.attr("src");
                    if(imgSrc == null || imgSrc.length() <= 0)
                        continue;
                    if(imgSrc.startsWith("http"))
                        og.images.add(imgSrc);
                    else if(imgSrc.startsWith("/"))
                        og.images.add(aURL.getProtocol() + "://" + aURL.getAuthority() + imgSrc);
                    else if(imgSrc.startsWith("."))
                        og.images.add(aURL.getProtocol() + "://" + aURL.getAuthority() + aURL.getPath().substring(0, aURL.getPath().lastIndexOf("/")) + imgSrc.substring(1));
                        
                }
            }
            return json(og);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return new ErrorResolution(400, "Bad request");
    }
    
    public Resolution getDataUrl() {
    	if (getCurrentUser() == null) {
    		return new StreamingResolution("text/html", "Need Login");
        }
	    
        try {
            String mimeType;
            int pushbackLimit = 100;
            extUrl = URLDecoder.decode(extUrl, "UTF-8");
            URL url = new URL(extUrl);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent",  this.getServletRequest().getHeader("User-Agent"));
            InputStream urlStream = connection.getInputStream();
            PushbackInputStream pushUrlStream = new PushbackInputStream(urlStream, pushbackLimit);
            byte [] firstBytes = new byte[pushbackLimit];
            pushUrlStream.read(firstBytes);
            pushUrlStream.unread(firstBytes);

            ByteArrayInputStream bais = new ByteArrayInputStream(firstBytes);
            mimeType = URLConnection.guessContentTypeFromStream(bais);
            if (mimeType.startsWith("image/")) {
                BufferedImage inputImage = ImageIO.read(pushUrlStream);
                String imageType = mimeType.substring("image/".length());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write( inputImage, imageType, baos);
                baos.flush();
                Resolution result = json("data:" + mimeType + ";base64," + Base64.encodeBase64String(baos.toByteArray())); 
                baos.close();
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        
        
        return new ErrorResolution(400, "Bad request");
    }
}
